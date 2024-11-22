import { AxiosRequestConfig } from 'axios'

// 导入所有模块
import userMocks from './user'
import menuMocks from './menu'

// Mock 响应接口
export interface MockResponse<T = any> {
  code: number
  data: T   
  message: string
}

// Mock 处理函数接口
export interface MockHandler {
  (config: {
    method: string
    data: any
    params: any
    url: string
  }): MockResponse | Promise<MockResponse> | any
}

// Mock 模块接口
export interface MockModule {
  [key: string]: MockHandler | any
}

// 全局 Mock 配置接口
export interface MockConfig {
  enabled: boolean
  delay: number
}

// 全局 Mock 配置
export const globalMockConfig: MockConfig = {
  enabled: import.meta.env.MODE === 'development', // 改用 Vite 的环境变量
  delay: 1000
}

// 合并所有模块
const mockModules: Record<string, MockHandler> = {
  ...userMocks,
  ...menuMocks
}

/**
 * 统一处理 mock 响应格式
 */
const formatResponse = (data: any): MockResponse => {
  if (data && data.code !== undefined) {
    return data
  }
  return {
    code: 200,
    data,
    message: 'success'
  }
}

/**
 * Mock 请求处理函数
 */
export function mockRequest(config: AxiosRequestConfig): Promise<MockResponse> {
  const { url, method = 'get', data, params } = config

  // 检查是否启用 mock
  const shouldMock = config.mock ?? globalMockConfig.enabled
  if (!shouldMock) {
    return Promise.reject(new Error('Mock is disabled'))
  }

  return new Promise((resolve, reject) => {
    // 检查是否存在对应的 mock 处理器
    const handler = mockModules[url as keyof typeof mockModules]
    if (!handler) {
      reject(new Error(`未找到 mock 接口：${url}`))
      return
    }

    // 延迟响应
    setTimeout(async () => {
      try {
        // 执行 mock 处理器，确保 method 是字符串类型
        const response = typeof handler === 'function'
          ? await handler({ method: String(method || 'get').toLowerCase(), data, params, url })
          : handler

        // 格式化响应数据
        resolve(formatResponse(response))
      } catch (error) {
        // 处理 mock 处理器抛出的错误
        if (error instanceof Error) {
          reject({
            code: 500,
            data: null,
            message: error.message
          })
        } else {
          reject({
            code: 500,
            data: null,
            message: '未知错误'
          })
        }
      }
    }, globalMockConfig.delay)
  })
}

export const setMockConfig = (config: Partial<MockConfig>) => {
  Object.assign(globalMockConfig, config)
}

export const toggleMock = (enabled: boolean) => {
  globalMockConfig.enabled = enabled
}

export default mockModules