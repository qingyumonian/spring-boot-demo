<template>
  <div class="navbar">
    <div class="left-menu">
      <div class="hamburger-container" @click="toggleSidebar">
        <el-icon :size="20">
          <Fold v-if="appStore.sidebar.opened" />
          <Expand v-else />
        </el-icon>
      </div>
      <el-breadcrumb separator="/">
        <transition-group name="breadcrumb">
          <el-breadcrumb-item
            v-for="(item, index) in breadcrumbs"
            :key="item.path"
          >
            <span v-if="index === breadcrumbs.length - 1" class="no-redirect">
              {{ item.meta.title }}
            </span>
            <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
          </el-breadcrumb-item>
        </transition-group>
      </el-breadcrumb>
    </div>

    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="32" :icon="UserFilled" />
          <span class="username">{{ username }}</span>
          <el-icon class="el-icon--right"><CaretBottom /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/">
              <el-dropdown-item>Dashboard</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              Logout
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { UserFilled, CaretBottom, SwitchButton, Fold, Expand } from '@element-plus/icons-vue'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const breadcrumbs = ref([])

const username = computed(() => {
  return userStore.userInfo?.username || 'Admin'
})

const getBreadcrumbs = () => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  breadcrumbs.value = matched
}

watch(
  () => route.path,
  () => {
    getBreadcrumbs()
  },
  { immediate: true }
)

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const handleLink = (item) => {
  if (item.redirect) {
    router.push(item.redirect)
  } else {
    router.push(item.path)
  }
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;
}

.left-menu {
  display: flex;
  align-items: center;

  .hamburger-container {
    height: 100%;
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 0 15px 0 0;
    transition: background 0.3s;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }
}

.right-menu {
  display: flex;
  align-items: center;

  .avatar-container {
    cursor: pointer;

    .avatar-wrapper {
      display: flex;
      align-items: center;

      .username {
        margin-left: 8px;
        font-size: 14px;
        color: #333;
      }
    }
  }
}

.el-breadcrumb {
  font-size: 14px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}

.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.5s;
}

.breadcrumb-enter-from,
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
