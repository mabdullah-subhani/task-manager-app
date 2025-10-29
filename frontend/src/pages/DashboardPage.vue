<template>
  <q-page class="q-pa-md">
    <!-- Welcome Header -->
    <div class="row items-center q-mb-lg">
      <div class="col">
        <h4>Welcome, {{ user?.username || 'User' }}</h4>
        <p class="text-subtitle2">Here’s a summary of your tasks</p>
      </div>
    </div>

    <!-- Quick Stats Cards -->
    <div class="row q-mb-lg justify-center q-gutter-xl">
      <q-card class="col-12 col-sm-6 col-md-3 bg-primary text-white text-center">
        <q-card-section>
          <div class="text-h6">Total Tasks</div>
          <div class="text-h4">{{ taskStore.totalElements }}</div>
        </q-card-section>
      </q-card>

      <q-card class="col-12 col-sm-6 col-md-3 bg-positive text-white text-center">
        <q-card-section>
          <div class="text-h6">Completed</div>
          <div class="text-h4">{{ taskStore.completedTasks }}</div>
        </q-card-section>
      </q-card>

      <q-card class="col-12 col-sm-6 col-md-3 bg-warning text-white text-center">
        <q-card-section>
          <div class="text-h6">Pending</div>
          <div class="text-h4">{{ taskStore.pendingTasks }}</div>
        </q-card-section>
      </q-card>
    </div>

    <!-- Recent Tasks Table -->
    <q-card flat bordered>
      <q-card-section>
        <div class="row items-center justify-between q-mb-md">
          <div class="text-h6">Recent Tasks (Latest 5)</div>
          <q-btn
            color="primary"
            flat
            label="View All"
            icon-right="arrow_forward"
            @click="$router.push('/dashboard/tasks')"
          />
        </div>

        <q-table
          :rows="taskStore.dashboardTasks"
          :columns="columns"
          row-key="id"
          flat
          bordered
          hide-bottom
        >
          <template v-slot:body-cell-title="props">
            <q-td
              :props="props"
              style="white-space: pre-line; text-align: center; max-width: 250px"
            >
              {{ formatTitle(props.row.title) }}
            </q-td>
          </template>

          <template v-slot:body-cell-description="props">
            <q-td :props="props" style="white-space: pre-line; text-align: left; max-width: 400px">
              {{ formatDescription(props.row.description) }}
            </q-td>
          </template>

          <template v-slot:body-cell-status="props">
            <q-td :props="props" class="status-cell text-center">
              <q-badge
                :color="props.row.status === 'COMPLETED' ? 'green' : 'orange'"
                class="q-pa-sm text-white"
                rounded
              >
                {{ props.row.status }}
              </q-badge>
            </q-td>
          </template>

          <template v-slot:body-cell-actions="props">
            <q-td align="right">
              <q-btn dense flat color="primary" icon="edit" @click="openEditDialog(props.row)" />
              <q-btn dense flat color="negative" icon="delete" @click="deleteTask(props.row.id)" />
            </q-td>
          </template>
        </q-table>
      </q-card-section>
    </q-card>

    <!-- Edit Task Dialog -->
    <q-dialog v-model="editDialog">
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">Edit Task</div>
        </q-card-section>

        <q-card-section class="q-gutter-md">
          <q-input v-model="editTaskData.title" label="Title" filled />
          <q-input v-model="editTaskData.description" label="Description" type="textarea" filled />
          <q-select
            v-model="editTaskData.status"
            :options="['PENDING', 'COMPLETED']"
            label="Status"
            filled
          />
          <q-input
            v-model="editTaskData.dueDateTime"
            label="Due Date & Time"
            type="datetime-local"
            filled
            required
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancel" v-close-popup />
          <q-btn color="primary" label="Save" @click="saveTaskUpdate" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from 'src/stores/user-store'
import { useTaskStore } from 'src/stores/task-store'
import { useQuasar } from 'quasar'
import { formatDueDate, toDateTimeLocal } from 'src/utils/date-utils'

const userStore = useUserStore()
const user = userStore.user
const taskStore = useTaskStore()
const $q = useQuasar()

const columns = [
  { name: 'title', label: 'Title', field: 'title', align: 'center' },
  { name: 'description', label: 'Description', field: 'description', align: 'center' },
  { name: 'status', label: 'Status', field: 'status', align: 'center' },
  { name: 'dueDate', label: 'Due Date', field: 'dueDate', align: 'center' },
  { name: 'actions', label: 'Actions', field: 'actions', align: 'center' },
]

const formatTitle = (text, wordsPerLine = 6) => {
  if (!text) return ''
  const words = text.split(' ')
  return words.map((w, i) => ((i + 1) % wordsPerLine === 0 ? w + '\n' : w)).join(' ')
}

const formatDescription = (text, wordsPerLine = 15) => {
  if (!text) return ''
  const words = text.split(' ')
  return words.map((w, i) => ((i + 1) % wordsPerLine === 0 ? w + '\n' : w)).join(' ')
}

const editDialog = ref(false)
const editTaskData = ref({})

const openEditDialog = (task) => {
  editTaskData.value = { ...task }
  if (editTaskData.value.dueDate) {
    editTaskData.value.dueDateTime = toDateTimeLocal(editTaskData.value.dueDate)
  }
  editDialog.value = true
}

const saveTaskUpdate = async () => {
  const selectedDate = new Date(editTaskData.value.dueDateTime)
  if (selectedDate < new Date()) {
    $q.notify({ type: 'negative', message: 'Due date cannot be in the past' })
    return
  }

  const payload = {
    title: editTaskData.value.title,
    description: editTaskData.value.description,
    status: editTaskData.value.status,
    dueDate: formatDueDate(editTaskData.value.dueDateTime),
  }

  try {
    await taskStore.updateTask(editTaskData.value.id, payload)
    editDialog.value = false
    await taskStore.fetchDashboardTasks()
  } catch (err) {
    console.error('Error updating task:', err)
  }
}

const deleteTask = async (id) => {
  try {
    await taskStore.deleteTask(id)
    await taskStore.fetchDashboardTasks()
  } catch (err) {
    console.error('Error deleting task:', err)
    $q.notify({ type: 'negative', message: 'Failed to delete task' })
  }
}

onMounted(async () => {
  await taskStore.fetchDashboardTasks()
})
</script>

<style scoped>
.q-td {
  white-space: pre-line !important;
  word-break: break-word !important;
  line-height: 1.5;
}
.status-cell {
  text-align: center !important;
}
</style>
