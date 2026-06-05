// Copyright (c) 2026 左岚. All rights reserved.
// API 封装模块

import axios from 'axios'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api'

const api = axios.create({
    baseURL: apiBaseUrl,
    timeout: 600000, // 10分钟超时，支持 AI 长时间请求
    headers: {
        'Content-Type': 'application/json'
    }
})

const savedToken = localStorage.getItem('kitten_auth_token')
if (savedToken) {
    api.defaults.headers.common.Authorization = `Bearer ${savedToken}`
}

export function setAuthToken(token) {
    if (token) {
        localStorage.setItem('kitten_auth_token', token)
        api.defaults.headers.common.Authorization = `Bearer ${token}`
    } else {
        localStorage.removeItem('kitten_auth_token')
        delete api.defaults.headers.common.Authorization
    }
}

export function setProjectRootHeader(path) {
    if (path) {
        sessionStorage.setItem('webnovel_project_root', path)
        api.defaults.headers.common['X-Project-Root'] = encodeURIComponent(path)
    } else {
        sessionStorage.removeItem('webnovel_project_root')
        delete api.defaults.headers.common['X-Project-Root']
    }
}

export const authApi = {
    sendEmailCode: (email, purpose = 'login') => api.post('/auth/email/code', { email, purpose }),
    emailLogin: (email, code, name = '') => api.post('/auth/email/login', { email, code, name }),
    emailRegister: (email, code, password, name = '') => api.post('/auth/email/register', { email, code, password, name }),
    passwordLogin: (email, password) => api.post('/auth/password/login', { email, password }),
    me: () => api.get('/auth/me'),
    logout: () => api.post('/auth/logout'),
    getLinuxDoUrl: () => api.get('/auth/linux-do/url')
}

export const membershipApi = {
    getPlans: () => api.get('/membership/plans'),
    getGroupTeams: () => api.get('/membership/group-teams'),
    me: () => api.get('/membership/me'),
    checkin: () => api.post('/membership/checkin'),
    createOrder: (planId, teamId = '') => api.post('/membership/orders', { plan_id: planId, team_id: teamId }),
    confirmOrder: (orderId) => api.post('/membership/orders/confirm', { order_id: orderId }),
    cancelOrder: (orderId) => api.post('/membership/orders/cancel', { order_id: orderId }),
    testPayOrder: (orderId) => api.post('/membership/orders/test-pay', { order_id: orderId })
}

export const adminApi = {
    overview: () => api.get('/admin/overview'),
    users: () => api.get('/admin/users'),
    userFiles: (userId) => api.get(`/admin/users/${userId}/files`),
    getSettings: () => api.get('/admin/settings'),
    updateSettings: (settings) => api.put('/admin/settings', settings),
    getMembershipPlans: () => api.get('/admin/membership/plans'),
    updateMembershipPlans: (plans) => api.put('/admin/membership/plans', { plans })
}

export const preferencesApi = {
    get: () => api.get('/preferences'),
    update: (preferences) => api.put('/preferences', preferences),
    getPromptContext: () => api.get('/preferences/prompt-context')
}

export const timelineApi = {
    get: () => api.get('/timeline'),
    update: (events) => api.put('/timeline', { events }),
    create: (event) => api.post('/timeline', event),
    delete: (id) => api.delete(`/timeline/${id}`),
    generateFromOutline: (payload = {}) => api.post('/timeline/generate-from-outline', payload),
    expand: (payload = {}) => api.post('/timeline/expand', payload)
}

// 项目管理 API
export const pipelineApi = {
    createFromInspiration: (payload) => api.post('/pipeline/from-inspiration', payload)
}

export const projectsApi = {
    getStatus: () => api.get('/projects/status'),
    getConfig: () => api.get('/projects/config'),
    getSettings: () => api.get('/projects/settings'),
    getPromptConfig: () => api.get('/projects/prompt-config'),
    updateConfig: (config) => api.put('/projects/config', config),
    updatePromptConfig: (payload) => api.put('/projects/prompt-config', payload),
    updateInfo: (data) => api.put('/projects/info', data),
    resetPromptConfig: (payload = {}) => api.post('/projects/prompt-config/reset', payload),
    init: (data) => api.post('/projects/init', data),
    getGenres: () => api.get('/projects/genres'),
    reset: () => api.delete('/projects/reset'),
    getActivities: () => api.get('/projects/activities'),
    // 多项目管理
    list: () => api.get('/projects/list'),
    create: (data) => api.post('/projects/create', data),
    switch: (path) => api.post('/projects/switch', { path }),
    import: (path) => api.post('/projects/import', { path }),
    delete: (projectId, deleteFiles = false) => api.delete(`/projects/${projectId}?delete_files=${deleteFiles}`)
}

// 大纲管理 API
export const outlinesApi = {
    getAll: () => api.get('/outlines'),
    getVolume: (volume) => api.get(`/outlines/${volume}`),
    updateVolume: (volume, content) => api.put(`/outlines/${volume}`, { content }),
    updateTotal: (content) => api.put('/outlines/total', { content }),
    deleteVolume: (volume, deleteRelatedCharacters = false) => api.delete(`/outlines/${volume}`, {
        params: { delete_related_characters: deleteRelatedCharacters }
    }),
    getTree: () => api.get('/outlines/tree')
}

// 章节管理 API
export const chaptersApi = {
    getAll: () => api.get('/chapters'),
    get: (id) => api.get(`/chapters/${id}`),
    update: (id, data, opts) => api.put(`/chapters/${id}`, data, opts && opts.projectRoot ? { params: { project_root: opts.projectRoot } } : undefined),
    delete: (id) => api.delete(`/chapters/${id}`),
    getSyncStatus: () => api.get('/chapters/sync/status'),
    syncMissing: () => api.post('/chapters/sync/missing'),
    write: (data) => api.post('/chapters/write', data),
    review: (chapters) => api.post('/chapters/review', { chapters }),
    getStats: () => api.get('/chapters/stats'),
    getTaskStatus: (taskId) => api.get(`/chapters/tasks/${taskId}`),
    ackTask: (taskId) => api.delete(`/chapters/tasks/${taskId}`),
    forceExtract: (id) => api.post(`/chapters/${id}/extract`),
    extractPreview: (id, content) => api.post(`/chapters/${id}/extract-preview`, { content }),
    extractApply: (id, extraction, content) => api.post(`/chapters/${id}/extract-apply`, { extraction, content }),
}

// 实体管理 API
export const entitiesApi = {
    getAll: (params) => api.get('/entities', { params }),
    get: (id) => api.get(`/entities/${id}`),
    getByType: (type) => api.get(`/entities/type/${type}`),
    search: (q) => api.get('/entities/search', { params: { q } }),
    getTypes: () => api.get('/entities/types'),
    getTiers: () => api.get('/entities/tiers'),
    getProtagonist: () => api.get('/entities/protagonist'),
    getCharacters: () => api.get('/entities/characters'),
    getForeshadowing: (status) => api.get('/entities/foreshadowing', { params: { status } })
}

// RAG 检索 API
export const ragApi = {
    search: (query, mode = 'hybrid', topK = 10) => api.post('/rag/search', { query, mode, top_k: topK }),
    getStats: () => api.get('/rag/stats'),
    test: (query) => api.get('/rag/test', { params: { query } }),
    rebuildIndex: () => api.post('/rag/index/all')
}

// Helper for stream requests
function getAuthHeaders() {
    const headers = { 'Content-Type': 'application/json' }
    if (api.defaults.headers.common.Authorization) {
        headers.Authorization = api.defaults.headers.common.Authorization
    }
    if (api.defaults.headers.common['X-Project-Root']) {
        headers['X-Project-Root'] = api.defaults.headers.common['X-Project-Root']
    }
    return headers
}

// AI 写作 API
export const aiApi = {
    getConfig: () => api.get('/ai/config'),
    updateConfig: (config) => api.put('/ai/config', config),
    chat: (messages, useProjectContext = true) => api.post('/ai/chat', { messages, use_project_context: useProjectContext }),
    testConnection: () => api.get('/ai/test'),
    getModels: () => api.get('/ai/models'),
    getGenres: () => api.get('/ai/genres'),
    initProject: (data) => api.post('/ai/init', data),
    initProjectStream: (data) => fetch(`${api.defaults.baseURL}/ai/init-stream`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    }),
    buildContext: (chapter) => api.post('/ai/context', null, { params: { chapter } }),
    writeChapter: (chapter, wordCount = 3500) => api.post('/ai/write', { chapter, word_count: wordCount }),
    writeChapterStream: (chapter, wordCount = 3500) => fetch(`${api.defaults.baseURL}/ai/write-stream`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ chapter, word_count: wordCount })
    }),
    reviewChapter: (chapter, content = '') => api.post('/ai/review', content ? { content } : null, { params: { chapter } }),
    planVolume: (volume, chaptersCount = 30, guidance = "") => api.post('/ai/plan', { volume, chapters_count: chaptersCount, guidance }),
    planVolumeStream: (volume, chaptersCount = 30, guidance = "") => fetch(`${api.defaults.baseURL}/ai/plan-stream`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ volume, chapters_count: chaptersCount, guidance })
    }),
    polishOutlineStream: (volume, content, requirements) => fetch(`${api.defaults.baseURL}/ai/polish-outline-stream`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ volume, content, requirements })
    }),

    polishChapterStream: (chapterId, content, suggestions, mode = 'rewrite') => fetch(`${api.defaults.baseURL}/ai/polish-stream`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ chapter_id: chapterId, content, suggestions, mode })
    }),
    polishChapter: (chapterId, content, suggestions, mode = 'rewrite') => api.post('/ai/polish', { chapter_id: chapterId, content, suggestions, mode }),
    generateSynopsis: () => api.post('/ai/generate-synopsis'),
    generateTitles: () => api.post('/ai/generate-titles'),
    updateProjectInfo: (data) => api.put('/projects/info', data),
    generateEndingPlan: (remainingChapters) => api.post('/ai/ending-plan', { remaining_chapters: remainingChapters })
}

// 角色管理 API
export const charactersApi = {
    list: () => api.get('/characters'),
    getFile: (path) => api.get('/characters/file', { params: { path } }),
    updateFile: (path, content) => api.put('/characters/file', { content }, { params: { path } }),
    create: (name, category) => api.post('/characters/create', null, { params: { name, category } }),
    delete: (path) => api.delete('/characters/file', { params: { path } }),
    getRelationships: () => api.get('/characters/relationships'),
    getProfile: (name) => api.get('/characters/profile', { params: { name } })
}

// 番茄自动上传 API
export const fanqieApi = {
    getStatus: () => api.get('/fanqie/status'),
    startLogin: (accountName) => api.post('/fanqie/login', { account_name: accountName || '默认账号' }),
    pollLogin: () => api.get('/fanqie/login/poll'),
    closeLoginBrowser: () => api.post('/fanqie/login/close'),
    logout: (accountName) => api.post('/fanqie/logout', { account_name: accountName }),
    verifyAccounts: (accountName) => api.get('/fanqie/verify', { params: accountName ? { account_name: accountName } : {} }),
    getBrowserSessions: () => api.get('/fanqie/browsers'),
    closeAllBrowsers: () => api.post('/fanqie/browsers/close-all'),
    getBooks: (accountName) => api.get('/fanqie/books', { params: { account_name: accountName || '默认账号' } }),
    updateConfig: (config) => api.put('/fanqie/config', config),
    getChapters: () => api.get('/fanqie/chapters'),
    syncChapters: () => api.post('/fanqie/chapters/sync'),
    startPublish: (chapterIds) => api.post('/fanqie/publish', { chapter_ids: chapterIds }),
    pollPublish: () => api.get('/fanqie/publish/poll'),
    stopPublish: () => api.post('/fanqie/publish/stop'),
}

export default api
