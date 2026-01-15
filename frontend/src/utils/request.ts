import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/store/user'
import router from '@/router'
import type { Result } from '@/types'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const res = response.data
    
    // 业务成功
    if (res.code === 200) {
      return res as any
    }
    
    // 业务错误处理
    const message = window.$message
    if (message) {
      message.error(res.msg || '请求失败')
    }
    
    // 未登录或Token过期
    if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    }
    
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    console.error('响应错误:', error)
    const message = window.$message
    
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          if (message) message.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          if (message) message.error('没有权限访问')
          break
        case 404:
          if (message) message.error('请求的资源不存在')
          break
        case 500:
          if (message) message.error('服务器内部错误')
          break
        default:
          if (message) message.error(error.response.data?.msg || '请求失败')
      }
    } else if (error.request) {
      if (message) message.error('网络错误，请检查网络连接')
    } else {
      if (message) message.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
export const request = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<Result<T>> {
    return service.get(url, config)
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<Result<T>> {
    return service.post(url, data, config)
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<Result<T>> {
    return service.put(url, data, config)
  },
  
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<Result<T>> {
    return service.delete(url, config)
  }
}

export default service

// 全局消息实例声明
declare global {
  interface Window {
    $message: ReturnType<typeof useMessage> | null
  }
}
