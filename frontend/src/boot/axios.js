// src/boot/axios.js
import { boot } from 'quasar/wrappers'
import axios from 'axios'
import { useUserStore } from 'src/stores/user-store'

// Create axios instance
const api = axios.create({
  baseURL: 'http://localhost:9093', // <-- your Spring Boot backend URL
  timeout: 10000,
})

export default boot(({ app }) => {
  // Attach JWT token to every request
  api.interceptors.request.use((config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  })

  // Make Axios globally available in Vue components
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
})

export { api }
