<template>
  <q-layout view="lHh Lpr lFf">
    <!-- App Bar -->
    <q-header elevated>
      <q-toolbar>
        <q-btn flat dense round icon="menu" @click="toggleDrawer" aria-label="Menu" />
        <q-toolbar-title>{{ pageTitle }}</q-toolbar-title>
      </q-toolbar>
    </q-header>

    <!-- Side Drawer -->
    <q-drawer v-model="drawerOpen" show-if-above bordered>
      <q-list padding>
        <q-item-label header>Navigation</q-item-label>

        <q-item clickable v-ripple to="/dashboard" tag="router-link">
          <q-item-section avatar>
            <q-icon name="dashboard" />
          </q-item-section>
          <q-item-section>Dashboard</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/dashboard/tasks" tag="router-link">
          <q-item-section avatar>
            <q-icon name="task" />
          </q-item-section>
          <q-item-section>My Tasks</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/dashboard/tasks/add" tag="router-link">
          <q-item-section avatar>
            <q-icon name="add" />
          </q-item-section>
          <q-item-section>Add Task</q-item-section>
        </q-item>

        <q-item clickable v-ripple to="/dashboard/profile" tag="router-link">
          <q-item-section avatar>
            <q-icon name="person" />
          </q-item-section>
          <q-item-section>Profile</q-item-section>
        </q-item>

        <q-separator spaced />

        <q-item clickable v-ripple @click="confirmLogout">
          <q-item-section avatar>
            <q-icon name="logout" />
          </q-item-section>
          <q-item-section>Logout</q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <!-- Page Container -->
    <q-page-container>
      <!-- 👇 key ensures re-render when route changes -->
      <router-view :key="route.fullPath" />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from 'src/stores/user-store'

const drawerOpen = ref(true)
const toggleDrawer = () => (drawerOpen.value = !drawerOpen.value)

const $q = useQuasar()
const route = useRoute() // 👈 IMPORTANT: make sure route is reactive
const router = useRouter()
const userStore = useUserStore()

// Dynamic AppBar Title
const pageTitle = ref('Task Manager')

const updateTitle = () => {
  switch (route.path) {
    case '/dashboard':
      pageTitle.value = 'Dashboard'
      break
    case '/dashboard/tasks':
      pageTitle.value = 'My Tasks'
      break
    case '/dashboard/tasks/add':
      pageTitle.value = 'Add Task'
      break
    case '/dashboard/profile':
      pageTitle.value = 'Profile'
      break
    default:
      pageTitle.value = 'Task Manager'
  }
}

watch(() => route.path, updateTitle, { immediate: true })

// Logout with confirmation and clean-up
const confirmLogout = () => {
  $q.dialog({
    title: 'Confirm Logout',
    message: 'Are you sure you want to logout?',
    cancel: true,
    persistent: true,
  }).onOk(() => {
    userStore.logout()
    $q.notify({ type: 'positive', message: 'You have been logged out successfully' })

    // Use router push instead of hard reload
    router.push('/login')
  })
}
</script>
