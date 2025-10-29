<template>
  <q-page class="q-pa-md">
    <div class="text-h5 q-mb-md">My Tasks</div>

    <q-table
      :rows="displayedTasks"
      :columns="columns"
      row-key="id"
      flat
      bordered
      title="Task List"
      v-model:pagination="pagination"
      :loading="taskStore.loading"
      @request="onRequest"
    >
      <!-- Title -->
      <template v-slot:body-cell-title="props">
        <q-td :props="props" style="white-space: pre-line; text-align: center; max-width: 300px">
          {{ formatTitle(props.row.title) }}
        </q-td>
      </template>

      <!-- Description -->
      <template v-slot:body-cell-description="props">
        <q-td :props="props" style="white-space: pre-line; text-align: left; max-width: 500px">
          {{ formatDescription(props.row.description) }}
        </q-td>
      </template>

      <!-- Status -->
      <template v-slot:body-cell-status="props">
        <q-td :props="props" align="center">
          <q-badge
            :color="props.row.status === 'COMPLETED' ? 'green' : 'orange'"
            class="q-pa-sm text-white q-mx-sm"
            rounded
          >
            {{ props.row.status }}
          </q-badge>
        </q-td>
      </template>

      <!-- Actions -->
      <template v-slot:body-cell-actions="props">
        <q-td align="right">
          <q-btn dense flat color="primary" icon="edit" @click="openEditDialog(props.row)" />
          <q-btn
            dense
            flat
            color="negative"
            icon="delete"
            @click="
              async () => {
                await taskStore.deleteTask(props.row.id)
                await fetchTasks()
              }
            "
          />
        </q-td>
      </template>
    </q-table>

    <q-btn label="Add Task" color="primary" class="q-mt-md" @click="addTask" />

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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useTaskStore } from 'src/stores/task-store'
import { formatDueDate, toDateTimeLocal } from 'src/utils/date-utils'

const taskStore = useTaskStore()
const router = useRouter()
const $q = useQuasar()

// Table Columns
const columns = [
  { name: 'title', label: 'Title', field: 'title', sortable: true, align: 'center' },
  { name: 'description', label: 'Description', field: 'description', align: 'center' },
  { name: 'status', label: 'Status', field: 'status', sortable: true, align: 'center' },
  { name: 'dueDate', label: 'Due Date', field: 'dueDate', sortable: true, align: 'center' },
  { name: 'actions', label: 'Actions', field: 'actions', align: 'center' },
]

// Pagination state
const pagination = ref({
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0,
})

// Fetch tasks for the current page
const fetchTasks = async () => {
  const pageIndex = pagination.value.page - 1 // backend pages are 0-indexed
  const size = pagination.value.rowsPerPage <= 0 ? 1000 : pagination.value.rowsPerPage
  await taskStore.fetchTasks(pageIndex, size)
  pagination.value.rowsNumber = taskStore.totalElements
}

// Handle table pagination request
const onRequest = async (props) => {
  pagination.value.page = props.pagination.page
  pagination.value.rowsPerPage = props.pagination.rowsPerPage
  await fetchTasks()
}

// No need to slice locally; backend returns correct page
const displayedTasks = computed(() => taskStore.allTasks)

// Format helpers
const formatDescription = (text, wordsPerLine = 15) => {
  if (!text) return ''
  const words = text.split(' ')
  return words.map((w, i) => ((i + 1) % wordsPerLine === 0 ? w + '\n' : w)).join(' ')
}

const formatTitle = (text, wordsPerLine = 6) => {
  if (!text) return ''
  const words = text.split(' ')
  return words.map((w, i) => ((i + 1) % wordsPerLine === 0 ? w + '\n' : w)).join(' ')
}

// Edit dialog
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

  await taskStore.updateTask(editTaskData.value.id, payload)
  editDialog.value = false
  await fetchTasks()
}

// Add Task
const addTask = () => router.push('/dashboard/tasks/add')

// Initial fetch
onMounted(fetchTasks)
</script>

<style scoped>
.q-td {
  white-space: pre-line !important;
  word-break: break-word !important;
  line-height: 1.5;
}
</style>
