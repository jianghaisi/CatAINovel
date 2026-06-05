import { defineStore } from 'pinia'
import { membershipApi } from '../api'

const STORAGE_KEY = 'kitten_membership'
const PENDING_ORDER_KEY = 'kitten_membership_pending_order'

function loadPendingOrder() {
  try {
    const order = JSON.parse(localStorage.getItem(PENDING_ORDER_KEY) || 'null')
    if (!order?.expires_at) return order
    const expiresAt = Date.parse(String(order.expires_at).replace(' ', 'T'))
    if (!Number.isNaN(expiresAt) && expiresAt <= Date.now()) {
      localStorage.removeItem(PENDING_ORDER_KEY)
      return null
    }
    return order
  } catch {
    localStorage.removeItem(PENDING_ORDER_KEY)
    return null
  }
}

export const useMembershipStore = defineStore('membership', {
  state: () => ({
    plans: [],
    status: JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null'),
    groupTeams: [],
    loading: false,
    error: '',
    pendingOrder: loadPendingOrder()
  }),

  getters: {
    isActive: (state) => !!state.status?.active,
    currentPlan: (state) => state.status?.plan || null,
    remainingTokens: (state) => state.status?.remaining_tokens || 0,
    quota: (state) => state.status?.quota || {},
    checkedInToday: (state) => !!state.status?.quota?.checked_in_today
  },

  actions: {
    saveStatus(status) {
      this.status = status
      localStorage.setItem(STORAGE_KEY, JSON.stringify(status))
    },

    savePendingOrder(order) {
      this.pendingOrder = order
      if (order) localStorage.setItem(PENDING_ORDER_KEY, JSON.stringify(order))
      else localStorage.removeItem(PENDING_ORDER_KEY)
    },

    pruneExpiredPendingOrder() {
      const order = loadPendingOrder()
      this.pendingOrder = order
      return order
    },

    clearLocal() {
      this.status = null
      this.savePendingOrder(null)
      localStorage.removeItem(STORAGE_KEY)
    },

    async loadPlans() {
      try {
        const { data } = await membershipApi.getPlans()
        this.plans = data.plans || []
      } catch {
        this.plans = []
      }
    },

    async loadGroupTeams() {
      this.pruneExpiredPendingOrder()
      try {
        const { data } = await membershipApi.getGroupTeams()
        this.groupTeams = data.teams || []
      } catch {
        this.groupTeams = []
      }
    },

    async refresh() {
      this.loading = true
      this.error = ''
      try {
        const { data } = await membershipApi.me()
        this.saveStatus(data)
        return data
      } catch (error) {
        this.clearLocal()
        this.error = error?.response?.data?.detail || error.message || 'Member status fetch failed'
        return null
      } finally {
        this.loading = false
      }
    },

    async checkin() {
      this.loading = true
      this.error = ''
      try {
        const { data } = await membershipApi.checkin()
        this.saveStatus(data)
        return data
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || 'Check-in failed'
        return null
      } finally {
        this.loading = false
      }
    },

    async createOrder(planId, teamId = '') {
      this.loading = true
      this.error = ''
      this.pruneExpiredPendingOrder()
      try {
        const { data } = await membershipApi.createOrder(planId, teamId)
        this.savePendingOrder(data.order)
        return data.order
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || 'Create payment order failed'
        return null
      } finally {
        this.loading = false
      }
    },

    async confirmOrder(orderId) {
      this.loading = true
      this.error = ''
      try {
        const { data } = await membershipApi.confirmOrder(orderId)
        this.saveStatus(data)
        this.savePendingOrder(null)
        return data
      } catch (error) {
        if (error?.response?.status === 410) this.savePendingOrder(null)
        this.error = error?.response?.data?.detail || error.message || 'Payment is not complete or confirmation failed'
        return null
      } finally {
        this.loading = false
      }
    },

    async cancelOrder(orderId) {
      this.loading = true
      this.error = ''
      try {
        const { data } = await membershipApi.cancelOrder(orderId)
        this.savePendingOrder(null)
        return data
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || 'Cancel order failed'
        return null
      } finally {
        this.loading = false
      }
    },

    async testPayOrder(orderId) {
      this.loading = true
      this.error = ''
      try {
        const { data } = await membershipApi.testPayOrder(orderId)
        this.saveStatus(data)
        this.savePendingOrder(null)
        return data
      } catch (error) {
        this.error = error?.response?.data?.detail || error.message || 'Test payment failed'
        return null
      } finally {
        this.loading = false
      }
    }
  }
})
