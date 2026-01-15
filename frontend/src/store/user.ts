import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') || 'null'))
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 1)
  const userId = computed(() => user.value?.id)
  const username = computed(() => user.value?.username)
  
  // 方法
  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  function setUser(newUser: User) {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }
  
  function login(loginToken: string, loginUser: User) {
    setToken(loginToken)
    setUser(loginUser)
  }
  
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  function updateUser(updates: Partial<User>) {
    if (user.value) {
      user.value = { ...user.value, ...updates }
      localStorage.setItem('user', JSON.stringify(user.value))
    }
  }
  
  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    userId,
    username,
    setToken,
    setUser,
    login,
    logout,
    updateUser
  }
})
