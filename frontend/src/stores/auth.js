import { defineStore } from 'pinia'
import { authApi, setAuthToken, setProjectRootHeader } from '../api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('kitten_auth_token') || '',
    user: JSON.parse(localStorage.getItem('kitten_auth_user') || 'null'),
    loading: false,
    error: '',
    lastCodeResponse: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token && !!state.user
  },

  actions: {
    applyUserProject() {
      const projectPath = this.user?.default_project?.path || ''
      if (projectPath) setProjectRootHeader(projectPath)
    },

    async bootstrap() {
      if (!this.token) return false
      setAuthToken(this.token)
      try {
        const { data } = await authApi.me()
        this.user = data.user
        localStorage.setItem('kitten_auth_user', JSON.stringify(this.user))
        this.applyUserProject()
        return true
      } catch {
        this.logoutLocal()
        return false
      }
    },

    async sendCode(email) {
      this.loading = true
      this.error = ''
      this.lastCodeResponse = null
      try {
        const { data } = await authApi.sendEmailCode(email)
        this.lastCodeResponse = data
        return true
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || '验证码发送失败'
        return false
      } finally {
        this.loading = false
      }
    },

    async loginWithEmail(email, code, name = '') {
      this.loading = true
      this.error = ''
      try {
        const { data } = await authApi.emailLogin(email, code, name)
        this.token = data.token
        this.user = data.user
        setAuthToken(this.token)
        localStorage.setItem('kitten_auth_user', JSON.stringify(this.user))
        this.applyUserProject()
        return true
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || '登录失败'
        return false
      } finally {
        this.loading = false
      }
    },

    async registerWithEmail(email, code, password, name = '') {
      this.loading = true
      this.error = ''
      try {
        const { data } = await authApi.emailRegister(email, code, password, name)
        this.token = data.token
        this.user = data.user
        setAuthToken(this.token)
        localStorage.setItem('kitten_auth_user', JSON.stringify(this.user))
        this.applyUserProject()
        return true
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || '注册失败'
        return false
      } finally {
        this.loading = false
      }
    },

    async loginWithPassword(email, password) {
      this.loading = true
      this.error = ''
      try {
        const { data } = await authApi.passwordLogin(email, password)
        this.token = data.token
        this.user = data.user
        setAuthToken(this.token)
        localStorage.setItem('kitten_auth_user', JSON.stringify(this.user))
        this.applyUserProject()
        return true
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || '登录失败'
        return false
      } finally {
        this.loading = false
      }
    },

    async loginWithToken(token) {
      this.loading = true
      this.error = ''
      this.token = token
      setAuthToken(token)
      try {
        const { data } = await authApi.me()
        this.user = data.user
        localStorage.setItem('kitten_auth_user', JSON.stringify(this.user))
        this.applyUserProject()
        return true
      } catch (error) {
        this.logoutLocal()
        this.error = error?.response?.data?.detail || error.message || '第三方登录失败'
        return false
      } finally {
        this.loading = false
      }
    },

    async openLinuxDoLogin() {
      try {
        const { data } = await authApi.getLinuxDoUrl()
        window.location.href = data.url
      } catch (error) {
        this.error = error?.response?.data?.detail || 'Linux.do 登录尚未配置'
      }
    },

    logoutLocal() {
      this.token = ''
      this.user = null
      setAuthToken('')
      setProjectRootHeader('')
      localStorage.removeItem('kitten_auth_user')
      localStorage.removeItem('kitten_auth_token')
    },

    async logout() {
      try {
        if (this.token) await authApi.logout()
      } finally {
        this.logoutLocal()
      }
    }
  }
})
