package com.lxf.demo.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> records;
    private long total;
    private long size;
    private long current;
    private long pages;

    public static <T> PageResult<T> of(List<T> records, long total, long size, long current) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setSize(size);
        result.setCurrent(current);
        result.setPages((total + size - 1) / size);
        return result;
    }
}
