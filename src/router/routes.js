const routes = [
  {
    path: '/',
    component: () => import('layouts/AuthLayout.vue'),
    children: [
      { path: 'login', component: () => import('pages/LoginPage.vue') },
      { path: 'register', component: () => import('pages/RegisterPage.vue') },
      { path: '', redirect: '/login' }, // 👈 redirect root to login
    ],
  },
  {
    path: '/dashboard',
    component: () => import('src/layouts/TaskManagerLayout.vue'),
    children: [
      { path: '', component: () => import('pages/DashboardPage.vue') },
      { path: 'tasks', component: () => import('pages/TasksPage.vue') },
      { path: 'tasks/add', component: () => import('pages/AddTaskPage.vue') },
      { path: 'profile', component: () => import('pages/ProfilePage.vue') },
    ],
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
