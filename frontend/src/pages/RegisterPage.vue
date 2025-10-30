<template>
  <q-page class="flex flex-center">
    <q-card class="q-pa-md shadow-2 rounded-borders" style="width: 400px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6 text-center">Create Account</div>
      </q-card-section>

      <q-form @submit.prevent="handleRegister" class="q-gutter-md">
        <q-input v-model="form.username" label="Username" outlined required />
        <q-input v-model="form.email" label="Email" type="email" outlined required />

        <!-- Password input -->
        <q-input
          v-model="form.password"
          label="Password"
          :type="hidePassword ? 'password' : 'text'"
          outlined
          required
          @input="updateStrength"
        >
          <template v-slot:append>
            <q-icon
              :name="hidePassword ? 'visibility_off' : 'visibility'"
              class="cursor-pointer"
              @click="hidePassword = !hidePassword"
            />
          </template>
        </q-input>

        <!-- Password Strength Meter -->
        <q-linear-progress
          v-if="strength.label"
          :value="strength.value"
          :color="strength.color"
          size="10px"
          class="rounded-borders q-mt-xs"
        />
        <div
          v-if="strength.label"
          class="text-caption text-center"
          :class="`text-${strength.color}`"
        >
          {{ strength.label }}
        </div>

        <q-input
          v-model="form.confirmPassword"
          label="Confirm Password"
          :type="hidePassword ? 'password' : 'text'"
          outlined
          required
        />

        <div v-if="error" class="text-negative text-caption">{{ error }}</div>

        <q-btn label="Register" type="submit" color="primary" class="full-width" />
      </q-form>

      <q-card-section class="text-center q-mt-md">
        <span>Already have an account? </span>
        <q-btn flat color="primary" label="Login" @click="$router.push('/login')" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from 'src/stores/user-store'
import { validatePassword, getPasswordStrength } from 'src/utils/validators'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const userStore = useUserStore()
const router = useRouter()

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const error = ref(null)
const hidePassword = ref(true)
const strength = ref({ label: '', color: 'grey', value: 0 })

const updateStrength = () => {
  strength.value = getPasswordStrength(form.value.password)
}

const handleRegister = async () => {
  error.value = null

  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Passwords do not match'
    $q.notify({ type: 'negative', message: error.value, timeout: 3000 })
    return
  }

  if (!validatePassword(form.value.password)) {
    error.value = 'Password must include uppercase, lowercase, number, and special character'
    $q.notify({ type: 'negative', message: error.value, timeout: 3000 })
    return
  }

  try {
    await userStore.register({
      username: form.value.username,
      email: form.value.email,
      password: form.value.password,
    })

    $q.notify({
      type: 'positive',
      message: 'Registration successful! You can now login.',
      timeout: 3000,
    })

    router.push('/login')
  } catch (err) {
    error.value = err
    $q.notify({ type: 'negative', message: err || 'Registration failed', timeout: 3000 })
  }
}
</script>
