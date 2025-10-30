import { defineStore } from 'pinia'
import { api } from 'src/boot/axios'
import { Notify } from 'quasar'

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    allTasks: [], // For paginated table
    dashboardTasks: [], // Latest tasks for dashboard
    loading: false,
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0,
  }),

  getters: {
    completedTasks: (state) => state.allTasks.filter((t) => t.status === 'COMPLETED').length,
    pendingTasks: (state) => state.allTasks.filter((t) => t.status === 'PENDING').length,
  },

  actions: {
    // Fetch latest tasks for dashboard
    async fetchDashboardTasks(latestCount = 5) {
      this.loading = true
      try {
        const res = await api.get('/tasks', { params: { page: 0, size: 1000 } })
        const tasks = res.data?.data?.content || []

        // ✅ Update global task data for dashboard cards
        this.allTasks = tasks
        this.totalElements = tasks.length

        this.dashboardTasks = tasks
          .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
          .slice(0, latestCount)
      } catch (err) {
        console.error('Error fetching dashboard tasks:', err)
      } finally {
        this.loading = false
      }
    },
    // Fetch tasks for table with frontend sorting & pagination
    async fetchTasks(page = 0, size = 10) {
      this.loading = true
      try {
        // Fetch all tasks
        const res = await api.get('/tasks', { params: { page: 0, size: 1000 } })
        let tasks = res.data?.data?.content || []

        // Sort by createdAt descending (latest first)
        tasks = tasks.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

        // Slice for current page
        const start = page * size
        const end = start + size
        this.allTasks = tasks.slice(start, end)

        // Update pagination info
        this.totalElements = tasks.length
        this.totalPages = Math.ceil(tasks.length / size)
        this.page = page
        this.size = size
      } catch (err) {
        console.error('Error fetching tasks:', err)
        Notify.create({ type: 'negative', message: 'Error fetching tasks' })
      } finally {
        this.loading = false
      }
    },

    async deleteTask(taskId) {
      try {
        await api.delete(`/tasks/${taskId}`)
        this.allTasks = this.allTasks.filter((t) => t.id !== taskId)
        this.dashboardTasks = this.dashboardTasks.filter((t) => t.id !== taskId)
        this.totalElements--
        Notify.create({ type: 'positive', message: 'Task deleted successfully' })
      } catch (err) {
        console.error('Error deleting task:', err)
        Notify.create({ type: 'negative', message: 'Error deleting task' })
      }
    },

    async addTask(newTask) {
      try {
        const res = await api.post('/tasks', newTask)
        const task = res.data?.data
        if (task) {
          this.allTasks.unshift(task)
          this.dashboardTasks.unshift(task)
          this.totalElements++
          Notify.create({ type: 'positive', message: 'Task added successfully' })
        }
      } catch (err) {
        console.error('Error adding task:', err)
        Notify.create({ type: 'negative', message: 'Error adding task' })
      }
    },

    async updateTask(taskId, updatedTask) {
      try {
        const res = await api.patch(`/tasks/${taskId}`, updatedTask)
        const task = res.data?.data
        if (task) {
          this.allTasks = this.allTasks.map((t) => (t.id === taskId ? task : t))
          this.dashboardTasks = this.dashboardTasks.map((t) => (t.id === taskId ? task : t))
          Notify.create({ type: 'positive', message: 'Task updated successfully' })
        }
      } catch (err) {
        console.error('Error updating task:', err)
        Notify.create({ type: 'negative', message: 'Error updating task' })
      }
    },
  },
})
