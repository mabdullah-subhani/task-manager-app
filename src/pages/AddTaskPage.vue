<template>
  <q-page class="q-pa-md">
    <div class="text-h5 q-mb-md">Add New Task</div>

    <q-card flat bordered class="q-pa-md">
      <q-form @submit.prevent="saveTask" class="q-gutter-md">
        <!-- Title -->
        <q-input v-model="newTask.title" label="Title" filled required />

        <!-- Description -->
        <q-input
          v-model="newTask.description"
          label="Description"
          type="textarea"
          filled
          required
        />

        <!-- Status -->
        <q-select
          v-model="newTask.status"
          :options="['PENDING', 'COMPLETED']"
          label="Status"
          filled
        />

        <!-- Due Date & Time -->
        <q-input
          v-model="newTask.dueDateTime"
          label="Due Date & Time"
          type="datetime-local"
          filled
          required
        />

        <!-- Actions -->
        <div class="row justify-end q-gutter-sm">
          <q-btn flat label="Cancel" color="negative" @click="$router.push('/dashboard/tasks')" />
          <q-btn type="submit" label="Save Task" color="primary" />
        </div>
      </q-form>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useTaskStore } from 'src/stores/task-store'

const router = useRouter()
const $q = useQuasar()
const taskStore = useTaskStore()

// Task state
const newTask = ref({
  title: '',
  description: '',
  status: 'PENDING',
  dueDateTime: '', // yyyy-MM-ddTHH:mm from datetime-local input
})

// Helper: format backend datetime
const formatDueDateTime = (datetime) => {
  if (!datetime) return null
  return datetime + ':00.000' // add seconds + ms
}

// Save task
const saveTask = async () => {
  if (!newTask.value.title || !newTask.value.description || !newTask.value.dueDateTime) {
    $q.notify({
      type: 'negative',
      message: 'Please fill all required fields',
    })
    return
  }

  // ✅ Frontend validation: block past dates instantly
  const selectedDate = new Date(newTask.value.dueDateTime)
  const now = new Date()
  if (selectedDate < now) {
    $q.notify({
      type: 'negative',
      message: 'Due date cannot be in the past',
    })
    return
  }

  const payload = {
    title: newTask.value.title,
    description: newTask.value.description,
    status: newTask.value.status,
    dueDate: formatDueDateTime(newTask.value.dueDateTime),
  }

  try {
    await taskStore.addTask(payload)

    $q.notify({
      type: 'positive',
      message: 'Task created successfully!',
    })

    // ✅ Redirect only on success
    router.push('/dashboard/tasks')
  } catch (err) {
    console.error('Error creating task:', err)

    // ✅ Show backend error message if available
    const errorMessage = err.response?.data?.message || 'Failed to create task. Please try again.'

    $q.notify({
      type: 'negative',
      message: errorMessage,
    })
  }
}
</script>
