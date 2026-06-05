import { createRouter, createWebHistory } from 'vue-router'
import { setProjectRootHeader } from '../api'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

const routes = [
  {
    path: '/',
    name: 'landing',
    component: () => import('../views/LandingView.vue'),
    meta: { public: true }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/membership',
    name: 'membership',
    component: () => import('../views/MembershipView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/projects',
    name: 'projects',
    component: () => import('../views/HomeView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/workspace',
    component: () => import('../views/WorkspaceLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/workspace/dashboard' },
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/DashboardView.vue') },
      { path: 'project', name: 'project', component: () => import('../views/ProjectView.vue') },
      { path: 'pipeline', name: 'pipeline', component: () => import('../views/InspirationPipelineView.vue') },
      { path: 'prompts', name: 'prompts', component: () => import('../views/PromptConfigView.vue') },
      { path: 'realms', name: 'realms', component: () => import('../views/RealmConfigView.vue') },
      { path: 'system-ui', name: 'system-ui', component: () => import('../views/SystemUIConfigView.vue') },
      { path: 'outline', name: 'outline', component: () => import('../views/OutlineView.vue') },
      { path: 'timeline', name: 'timeline', component: () => import('../views/TimelineView.vue') },
      { path: 'write', name: 'write', component: () => import('../views/WriteView.vue') },
      { path: 'write/:chapter', name: 'write-chapter', component: () => import('../views/WriteView.vue') },
      { path: 'entities', name: 'entities', component: () => import('../views/EntityView.vue') },
      { path: 'rag', name: 'rag', component: () => import('../views/RagView.vue') },
      { path: 'characters', name: 'characters', component: () => import('../views/CharacterView.vue') },
      { path: 'relations', name: 'relations', component: () => import('../views/RelationGraphView.vue') },
      { path: 'admin', name: 'admin', component: () => import('../views/AdminView.vue'), meta: { adminOnly: true } }
    ]
  },
  { path: '/project', redirect: '/workspace/project' },
  { path: '/pipeline', redirect: '/workspace/pipeline' },
  { path: '/prompts', redirect: '/workspace/prompts' },
  { path: '/realms', redirect: '/workspace/realms' },
  { path: '/system-ui', redirect: '/workspace/system-ui' },
  { path: '/outline', redirect: '/workspace/outline' },
  { path: '/timeline', redirect: '/workspace/timeline' },
  { path: '/write', redirect: '/workspace/write' },
  { path: '/write/:chapter', redirect: to => `/workspace/write/${to.params.chapter}` },
  { path: '/entities', redirect: '/workspace/entities' },
  { path: '/rag', redirect: '/workspace/rag' },
  { path: '/characters', redirect: '/workspace/characters' },
  { path: '/relations', redirect: '/workspace/relations' },
  { path: '/admin', redirect: '/workspace/admin' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

async function fetchCurrentUser(token) {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/me`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!response.ok) return null
    const data = await response.json()
    if (data?.user) localStorage.setItem('kitten_auth_user', JSON.stringify(data.user))
    return data?.user || null
  } catch {
    return null
  }
}

router.beforeEach(async (to) => {
  const token = localStorage.getItem('kitten_auth_token')
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.name === 'login' && token) {
    return { path: '/projects' }
  }

  let user = null
  try {
    user = JSON.parse(localStorage.getItem('kitten_auth_user') || 'null')
  } catch {
    user = null
  }

  if (token && (!user || to.matched.some((record) => record.meta.adminOnly) || to.path.startsWith('/workspace'))) {
    user = await fetchCurrentUser(token) || user
  }

  if (to.matched.some((record) => record.meta.adminOnly) && user?.role !== 'admin') {
    return { path: '/workspace/dashboard' }
  }

  if (to.path.startsWith('/workspace')) {
    const hasProject = sessionStorage.getItem('webnovel_project_root')
    if (!hasProject) {
      const defaultProject = user?.default_project?.path
      if (defaultProject) {
        setProjectRootHeader(defaultProject)
        return true
      }
      return '/projects'
    }
  }
})

export default router
