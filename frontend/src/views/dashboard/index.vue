<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">Total Users</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon :size="32"><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">Total Roles</div>
              <div class="stat-value">{{ stats.roleCount }}</div>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon :size="32"><UserFilled /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">Total Menus</div>
              <div class="stat-value">{{ stats.menuCount }}</div>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <el-icon :size="32"><Menu /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">Online Users</div>
              <div class="stat-value">{{ stats.onlineCount }}</div>
            </div>
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
              <el-icon :size="32"><Connection /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>System Information</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="Project Name">Admin Panel</el-descriptions-item>
            <el-descriptions-item label="Version">1.0.0</el-descriptions-item>
            <el-descriptions-item label="Frontend">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="Backend">Spring Boot 2.3.6</el-descriptions-item>
            <el-descriptions-item label="Database">MySQL + Redis</el-descriptions-item>
            <el-descriptions-item label="ORM">MyBatis-Plus</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>Quick Actions</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/system/user')">
              <el-icon><User /></el-icon>
              Manage Users
            </el-button>
            <el-button type="success" @click="$router.push('/system/role')">
              <el-icon><UserFilled /></el-icon>
              Manage Roles
            </el-button>
            <el-button type="warning" @click="$router.push('/system/menu')">
              <el-icon><Menu /></el-icon>
              Manage Menus
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { User, UserFilled, Menu, Connection } from '@element-plus/icons-vue'

const stats = reactive({
  userCount: 0,
  roleCount: 0,
  menuCount: 0,
  onlineCount: 1
})

onMounted(() => {
  // Simulate loading stats - in real app, fetch from API
  setTimeout(() => {
    stats.userCount = 5
    stats.roleCount = 3
    stats.menuCount = 10
  }, 300)
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 10px 0;
}

.stat-card {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  .stat-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
  }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.mt-20 {
  margin-top: 20px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .el-button {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
