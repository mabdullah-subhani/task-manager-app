import { defineStore } from 'pinia'
import { api } from 'src/boot/axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
  },

  actions: {
    async login(credentials) {
      try {
        const res = await api.post('/auth/login', credentials)

        if (res.data && res.data.success) {
          this.token = res.data.data.token
          this.user = {
            id: res.data.data.userId,
            username: res.data.data.username,
            email: res.data.data.email,
            roles: res.data.data.roles,
          }
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.user))
        } else {
          throw res.data.message || 'Login failed'
        }
      } catch (err) {
        throw err.response?.data?.message || err.message || 'Login failed'
      }
    },

    async register(userData) {
      try {
        const res = await api.post('/auth/register', userData)
        if (res.data && res.data.success) {
          return true
        } else {
          throw res.data.message || 'Registration failed'
        }
      } catch (err) {
        throw err.response?.data?.message || err.message || 'Registration failed'
      }
    },

    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    loadFromStorage() {
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')
      if (token && user) {
        this.token = token
        this.user = JSON.parse(user)
      }
    },
    async updateProfile(payload) {
      const userId = this.user.id

      // Track the updated fields
      if (payload.username) {
        await api.patch(`/auth/update/username/${userId}`, { username: payload.username })
        this.user.username = payload.username
      }

      if (payload.email) {
        await api.patch(`/auth/update/email/${userId}`, { email: payload.email })
        this.user.email = payload.email
      }

      if (payload.password) {
        await api.patch(`/auth/update/password/${userId}`, { password: payload.password })
        // ⚠️ Do NOT store password in localStorage
      }

      // Save updated user back to localStorage
      localStorage.setItem('user', JSON.stringify(this.user))

      return this.user
    },
  },
})
