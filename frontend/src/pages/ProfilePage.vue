<template>
  <q-page class="q-pa-md">
    <div class="text-h5 q-mb-md">My Profile</div>

    <q-card flat bordered class="q-pa-md" style="max-width: 600px; margin: auto">
      <q-card-section>
        <div class="text-h6">Profile Information</div>
      </q-card-section>

      <q-form class="q-gutter-md" @submit.prevent="updateProfile">
        <!-- User ID (readonly) -->
        <q-input v-model="form.id" label="User ID" outlined dense readonly />

        <!-- Username (editable) -->
        <q-input
          v-model="form.username"
          label="Username"
          outlined
          dense
          :rules="[(val) => !!val || 'Username is required']"
        />

        <!-- Email (editable) -->
        <q-input
          v-model="form.email"
          label="Email"
          type="email"
          outlined
          dense
          :rules="[(val) => !!val || 'Email is required']"
        />

        <!-- Password (optional) -->
        <q-input
          v-model="form.password"
          label="New Password"
          :type="hidePassword ? 'password' : 'text'"
          outlined
          dense
          hint="Leave empty if you don’t want to change"
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

        <!-- Password Strength -->
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

        <!-- Confirm Password -->
        <q-input
          v-model="form.confirmPassword"
          label="Confirm New Password"
          :type="hidePassword ? 'password' : 'text'"
          outlined
          dense
          :disable="!form.password"
          hint="Re-enter new password"
        />

        <!-- Error Message -->
        <div v-if="error" class="text-negative text-caption">{{ error }}</div>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" color="grey" @click="resetForm" />
          <q-btn color="primary" type="submit" label="Save Changes" />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import { useUserStore } from 'src/stores/user-store'
import { validatePassword, getPasswordStrength } from 'src/utils/validators'

const $q = useQuasar()
const userStore = useUserStore()

// form holds current input state
const form = ref({
  id: userStore.user?.id || '',
  username: userStore.user?.username || '',
  email: userStore.user?.email || '',
  password: '',
  confirmPassword: '',
})

const error = ref(null)
const hidePassword = ref(true)
const strength = ref({ label: '', color: 'grey', value: 0 })

// Reset form back to store values
const resetForm = () => {
  form.value = {
    id: userStore.user?.id || '',
    username: userStore.user?.username || '',
    email: userStore.user?.email || '',
    password: '',
    confirmPassword: '',
  }
  error.value = null
  strength.value = { label: '', color: 'grey', value: 0 }
}

// Update strength meter
const updateStrength = () => {
  strength.value = getPasswordStrength(form.value.password)
}

// Save updates
const updateProfile = async () => {
  error.value = null

  if (form.value.password) {
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
  }

  try {
    const payload = {}
    if (form.value.username !== userStore.user.username) payload.username = form.value.username
    if (form.value.email !== userStore.user.email) payload.email = form.value.email
    if (form.value.password) payload.password = form.value.password

    await userStore.updateProfile(payload)

    $q.notify({
      type: 'positive',
      message: 'Profile updated successfully!',
    })
    resetForm()
  } catch (err) {
    $q.notify({
      type: 'negative',
      message: err.response?.data?.message || 'Failed to update profile',
    })
  }
}
</script>
