import type { MockHandler } from './index'

const userMocks: Record<string, MockHandler> = {
  '/api/user/login': ({ data }) => {
    return {
      code: 200,
      message: 'success',
      data: {
        token: '1234567890',
        user: data.username
      }
    }
  },
  
  '/api/user/logout': () => {
    return {
      code: 200,
      message: 'success',
      data: null
    }
  },

  '/api/user/info': () => {
    return {
      code: 200,
      message: 'success',
      data: {
        userId: '1',
        username: 'admin',
        roles: ['admin'],
        permissions: ['read', 'write']
      }
    }
  }
}

export default userMocks
