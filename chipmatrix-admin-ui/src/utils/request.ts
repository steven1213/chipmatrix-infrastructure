import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { mockRequest, globalMockConfig } from '../mock/modules'
import { ElMessage } from 'element-plus'

// 扩展 AxiosRequestConfig 类型，添加 mock 属性
declare module 'axios' {
  interface AxiosRequestConfig {
    mock?: boolean
  }
}

// 响应数据接口
interface ResponseData<T = any> {
  code: number
  data: T
  message: string
}

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  async (config) => {
    // 添加 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // mock 处理：优先使用请求配置的 mock 值，否则使用全局配置
    const shouldMock = config.mock ?? globalMockConfig.enabled
    
    if (shouldMock) {
      try {
        const res = await mockRequest(config)
        return Promise.reject({
          config,
          response: { data: res }
        })
      } catch (error) {
        return Promise.reject(error)
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ResponseData>) => {
    const { code, message, data } = response.data

    // 根据自定义错误码判断请求是否成功
    if (code === 200) {
      return data
    }
    
    // 处理业务错误
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    // 处理 mock 数据
    if (error.response?.data?.code === 200) {
      return error.response.data.data
    }

    // 处理 HTTP 错误
    let message = '网络错误'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          // 可以在这里处理登录过期
          // router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求错误，未找到该资源'
          break
        case 500:
          message = '服务器错误'
          break
        default:
          message = `连接错误${error.response.status}`
      }
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 封装请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.get(url, config)
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return request.post(url, data, config)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return request.put(url, data, config)
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.delete(url, config)
  }
}

export default request