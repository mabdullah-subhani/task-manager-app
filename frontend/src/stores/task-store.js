import { defineStore } from 'pinia'
import { api } from 'src/boot/axios'
import { Notify } from 'quasar'

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    tasks: [],
    loading: false,
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0,
  }),

  getters: {
    completedTasks: (state) => state.tasks.filter((t) => t.status === 'COMPLETED').length,
    pendingTasks: (state) => state.tasks.filter((t) => t.status === 'PENDING').length,
  },

  actions: {
    async fetchTasks(page = this.page, size = this.size) {
      this.loading = true
      try {
        const res = await api.get('/tasks', { params: { page, size } })
        if (res.data?.data?.content) {
          this.tasks = res.data.data.content
          this.page = res.data.data.pageable.pageNumber
          this.size = res.data.data.pageable.pageSize
          this.totalPages = res.data.data.totalPages
          this.totalElements = res.data.data.totalElements
        } else {
          this.tasks = []
        }
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
        this.tasks = this.tasks.filter((t) => t.id !== taskId)
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
        if (res.data?.data) {
          this.tasks.unshift(res.data.data)
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
        if (res.data?.data) {
          this.tasks = this.tasks.map((t) => (t.id === taskId ? res.data.data : t))
          Notify.create({ type: 'positive', message: 'Task updated successfully' })
        }
      } catch (err) {
        console.error('Error updating task:', err)
        Notify.create({ type: 'negative', message: 'Error updating task' })
      }
    },
  },
})
