<template>
  <div :class="{ 'has-logo': showLogo }">
    <div v-if="showLogo" class="sidebar-logo-container" :class="{ collapse: isCollapse }">
      <transition name="sidebarLogoFade">
        <router-link v-if="isCollapse" key="collapse" class="sidebar-logo-link" to="/">
          <el-icon :size="32" class="sidebar-logo"><Management /></el-icon>
        </router-link>
        <router-link v-else key="expand" class="sidebar-logo-link" to="/">
          <el-icon :size="32" class="sidebar-logo"><Management /></el-icon>
          <h1 class="sidebar-title">Admin Panel</h1>
        </router-link>
      </transition>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :active-text-color="variables.menuActiveText"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>Dashboard</template>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>System</span>
          </template>
          <el-menu-item index="/system/user">
            <el-icon><User /></el-icon>
            <template #title>Users</template>
          </el-menu-item>
          <el-menu-item index="/system/role">
            <el-icon><UserFilled /></el-icon>
            <template #title>Roles</template>
          </el-menu-item>
          <el-menu-item index="/system/menu">
            <el-icon><Menu /></el-icon>
            <template #title>Menus</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { House, Setting, User, UserFilled, Menu, Management } from '@element-plus/icons-vue'
import { useAppStore } from '@/store/app'

const route = useRoute()
const appStore = useAppStore()

const showLogo = true

const variables = {
  menuBg: '#304156',
  menuText: '#bfcbd9',
  menuActiveText: '#409EFF'
}

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

const isCollapse = computed(() => !appStore.sidebar.opened)
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    .sidebar-logo {
      color: #fff;
    }

    .sidebar-title {
      display: inline-block;
      margin: 0 0 0 12px;
      color: #fff;
      font-weight: 600;
      font-size: 16px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo-link {
      .sidebar-title {
        display: none;
      }
    }
  }
}

.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter-from,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

:deep(.el-menu) {
  border-right: none;
}
</style>
