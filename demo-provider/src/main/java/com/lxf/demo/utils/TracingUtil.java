package com.lxf.demo.utils;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 追踪工具类 - 异步上报异常到 SigNoz
 */
@Component
public class TracingUtil {

    private static final Logger log = LoggerFactory.getLogger(TracingUtil.class);

    private static TracingUtil instance;
    private static Tracer tracer;
    private ThreadPoolExecutor executor;

    @Value("${spring.application.name:SpringBootDemo}")
    private String applicationName;

    @PostConstruct
    public void init() {
        instance = this;
        // 从 Java Agent 初始化的全局 OpenTelemetry 实例获取 Tracer
        tracer = GlobalOpenTelemetry.getTracer(applicationName, "1.0.0");
        // 独立线程池：核心2，最大4，队列100，空闲60秒回收
        executor = new ThreadPoolExecutor(
                2, 4, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                r -> {
                    Thread t = new Thread(r, "tracing-pool-" + System.currentTimeMillis());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        log.info("TracingUtil initialized, tracer available: {}", tracer != null);
    }

    @PreDestroy
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 异步记录异常到 SigNoz
     *
     * @param spanName  span名称（如 "GlobalExceptionHandler"）
     * @param throwable 异常
     */
    public static void recordExceptionAsync(String spanName, Throwable throwable) {
        recordExceptionAsync(spanName, throwable, null);
    }

    /**
     * 异步记录异常到 SigNoz（带额外信息）
     *
     * @param spanName  span名称
     * @param throwable 异常
     * @param extraInfo 额外信息（如请求路径、用户ID等）
     */
    public static void recordExceptionAsync(String spanName, Throwable throwable, String extraInfo) {
        if (instance == null || instance.tracer == null || instance.executor == null) {
            return;
        }

        // 捕获当前上下文
        Context currentContext = Context.current();

        instance.executor.execute(() -> {
            try {
                Span span = instance.tracer.spanBuilder(spanName)
                        .setParent(currentContext)
                        .setSpanKind(SpanKind.INTERNAL)
                        .setAttribute("exception.type", throwable.getClass().getName())
                        .setAttribute("exception.message", throwable.getMessage() != null ? throwable.getMessage() : "")
                        .startSpan();

                if (extraInfo != null) {
                    span.setAttribute("extra.info", extraInfo);
                }

                span.setStatus(StatusCode.ERROR, throwable.getMessage());
                span.recordException(throwable);
                span.end();
            } catch (Exception e) {
                log.warn("Failed to record exception to SigNoz: {}", e.getMessage());
            }
        });
    }

    /**
     * 异步创建自定义 span
     *
     * @param spanName span名称
     * @param message  消息
     */
    public static void recordEventAsync(String spanName, String message) {
        if (instance == null || instance.tracer == null || instance.executor == null) {
            return;
        }

        Context currentContext = Context.current();

        instance.executor.execute(() -> {
            try {
                Span span = instance.tracer.spanBuilder(spanName)
                        .setParent(currentContext)
                        .setSpanKind(SpanKind.INTERNAL)
                        .setAttribute("event.message", message)
                        .startSpan();

                span.setStatus(StatusCode.OK);
                span.end();
            } catch (Exception e) {
                log.warn("Failed to record event to SigNoz: {}", e.getMessage());
            }
        });
    }
}
