<template>
  <div class="login-container">
    <div class="login-card">
      <div class="title-container">
        <h3 class="title">Admin Panel Login</h3>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        autocomplete="off"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="Username"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="Password"
            :prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item class="remember-me">
          <el-checkbox v-model="loginForm.rememberMe">Remember me</el-checkbox>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          size="large"
          class="login-button"
          @click="handleLogin"
        >
          <span v-if="!loading">Login</span>
          <span v-else>Logging in...</span>
        </el-button>

        <div class="divider">
          <span>Or login with</span>
        </div>

        <el-button
          type="default"
          size="large"
          class="keycloak-button"
          @click="handleKeycloakLogin"
        >
          <svg class="keycloak-icon" viewBox="0 0 24 24" width="20" height="20">
            <path fill="currentColor" d="M12 2L2 7v10l10 5 10-5V7L12 2zm0 2.18l6.9 3.45L12 11.09 5.1 7.63 12 4.18zM4 8.81l7 3.5v6.88l-7-3.5V8.81zm9 10.38v-6.88l7-3.5v6.88l-7 3.5z"/>
          </svg>
          <span>Keycloak SSO</span>
        </el-button>

        <el-button
          type="default"
          size="large"
          class="cas-button"
          @click="handleCasLogin"
        >
          <svg class="cas-icon" viewBox="0 0 24 24" width="20" height="20">
            <path fill="currentColor" d="M12 2l9 4.5v11L12 22l-9-4.5v-11L12 2zm0 2.18L6 6.09v7.82l6 3.91 6-3.91V6.09L12 4.18zm-1 3.32h2v6h-2v-6zm0 7h2v2h-2v-2z"/>
          </svg>
          <span>CAS SSO</span>
        </el-button>

        <div class="tips">
          <span>Default account: admin / 123456</span>
        </div>
      </el-form>
    </div>

    <div class="footer">
      <span>Copyright 2024 Admin Panel</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  rememberMe: true
})

const loginRules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 5, max: 20, message: 'Password length should be 5-20 characters', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password
      })
      ElMessage.success('Login successful')

      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (error) {
      console.error('Login failed:', error)
    } finally {
      loading.value = false
    }
  })
}

const handleKeycloakLogin = () => {
  // Redirect to Keycloak OAuth2 authorization endpoint
  const baseUrl = getBaseUrl()
  window.location.href = `${baseUrl}/oauth2/authorization/keycloak`
}

const handleCasLogin = () => {
  const baseUrl = getBaseUrl()
  window.location.href = `${baseUrl}/login/cas`
}

const getBaseUrl = () => {
  return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8888'
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1d3557 0%, #457b9d 50%, #a8dadc 100%);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  }
}

.login-card {
  position: relative;
  width: 400px;
  padding: 35px 35px 30px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.title-container {
  position: relative;
  text-align: center;
  margin-bottom: 30px;

  .title {
    font-size: 26px;
    color: #1d3557;
    font-weight: 600;
    margin: 0;
  }
}

.login-form {
  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px #dcdfe6 inset;

    &:hover {
      box-shadow: 0 0 0 1px #409eff inset;
    }
  }

  :deep(.el-input__inner) {
    height: 44px;
    line-height: 44px;
  }
}

.remember-me {
  margin-bottom: 20px;

  :deep(.el-form-item__content) {
    justify-content: flex-start;
  }
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #1d3557 0%, #457b9d 100%);
  border: none;

  &:hover {
    background: linear-gradient(135deg, #457b9d 0%, #1d3557 100%);
  }
}

.divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
  color: #909399;
  font-size: 13px;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: #dcdfe6;
  }

  span {
    padding: 0 15px;
  }
}

.keycloak-button {
  width: 100%;
  height: 44px;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1px solid #dcdfe6;
  background: #fff;
  color: #606266;

  &:hover {
    border-color: #409eff;
    color: #409eff;
    background: #ecf5ff;
  }

  .keycloak-icon {
    flex-shrink: 0;
  }
}

.cas-button {
  width: 100%;
  height: 44px;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;

  .cas-icon {
    flex-shrink: 0;
  }
}

.tips {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  font-size: 13px;
  text-align: center;
  color: #909399;
}

.footer {
  position: absolute;
  bottom: 20px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
}
</style>
