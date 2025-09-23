<template>
  <q-page class="flex flex-center">
    <q-card class="q-pa-md shadow-2 rounded-borders" style="width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6 text-center">Login</div>
      </q-card-section>

      <q-form @submit.prevent="handleLogin" class="q-gutter-md">
        <q-input v-model="form.login" label="Username" outlined required />

        <!-- Password input with toggle -->
        <q-input
          v-model="form.password"
          label="Password"
          :type="hidePassword ? 'password' : 'text'"
          outlined
          required
        >
          <template v-slot:append>
            <q-icon
              :name="hidePassword ? 'visibility_off' : 'visibility'"
              class="cursor-pointer"
              @click="hidePassword = !hidePassword"
            />
          </template>
        </q-input>

        <div v-if="error" class="text-negative text-caption">{{ error }}</div>

        <q-btn label="Login" type="submit" color="primary" class="full-width" />
      </q-form>

      <q-card-section class="text-center q-mt-md">
        <span>Don’t have an account? </span>
        <q-btn flat color="primary" label="Sign Up" @click="$router.push('/register')" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from 'src/stores/user-store'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const form = ref({ login: '', password: '' })
const error = ref(null)
const hidePassword = ref(true) // reactive toggle

const handleLogin = async () => {
  error.value = null
  try {
    await userStore.login(form.value)
    router.push('/dashboard') // Redirect after login
  } catch (err) {
    error.value = err
  }
}
</script>
