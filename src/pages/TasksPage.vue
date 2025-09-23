<template>
  <q-page class="q-pa-md">
    <div class="text-h5 q-mb-md">My Tasks</div>

    <q-table
      :rows="taskStore.tasks"
      :columns="columns"
      row-key="id"
      flat
      bordered
      title="Task List"
    >
      <template v-slot:body-cell-actions="props">
        <q-td align="right">
          <q-btn dense flat color="primary" icon="edit" @click="openEditDialog(props.row)" />
          <q-btn
            dense
            flat
            color="negative"
            icon="delete"
            @click="taskStore.deleteTask(props.row.id)"
          />
        </q-td>
      </template>
      <template v-slot:body-cell-status="props">
        <q-td :props="props">
          <q-badge
            :color="props.row.status === 'COMPLETED' ? 'green' : 'orange'"
            align="center"
            class="q-pa-sm text-white"
            rounded
          >
            {{ props.row.status }}
          </q-badge>
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
          <!-- Due Date & Time -->
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
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useTaskStore } from 'src/stores/task-store'
import { formatDueDate, toDateTimeLocal } from 'src/utils/date-utils'

const taskStore = useTaskStore()
const router = useRouter()
const $q = useQuasar()

const columns = [
  { name: 'title', label: 'Title', field: 'title', sortable: true },
  { name: 'description', label: 'Description', field: 'description', sortable: true },
  { name: 'status', label: 'Status', field: 'status', sortable: true },
  { name: 'dueDate', label: 'Due Date', field: 'dueDate', sortable: true },
  { name: 'actions', label: 'Actions', field: 'actions', sortable: false },
]

// Edit dialog state
const editDialog = ref(false)
const editTaskData = ref({})

// Open edit dialog
const openEditDialog = (task) => {
  editTaskData.value = { ...task }
  if (editTaskData.value.dueDate) {
    editTaskData.value.dueDateTime = toDateTimeLocal(editTaskData.value.dueDate)
  }
  editDialog.value = true
}

// Save updated task
const saveTaskUpdate = async () => {
  // ✅ Prevent past date
  const selectedDate = new Date(editTaskData.value.dueDateTime)
  const now = new Date()
  if (selectedDate < now) {
    $q.notify({
      type: 'negative',
      message: 'Due date cannot be in the past',
    })
    return // stop here, keep dialog open with same data
  }

  const payload = {
    title: editTaskData.value.title,
    description: editTaskData.value.description,
    status: editTaskData.value.status,
    dueDate: formatDueDate(editTaskData.value.dueDateTime),
  }

  try {
    await taskStore.updateTask(editTaskData.value.id, payload)
    editDialog.value = false // ✅ close only on success
  } catch (err) {
    console.error('Error updating task:', err)
    // ❌ error notify handled in store
  }
}

// Add Task button
const addTask = () => {
  router.push('/dashboard/tasks/add')
}

onMounted(() => {
  taskStore.fetchTasks()
})
</script>
