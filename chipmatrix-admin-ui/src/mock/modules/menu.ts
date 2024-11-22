import type { MockHandler } from './index'

const menuMocks: Record<string, MockHandler> = {
  '/api/menus': () => {
    return {
      code: 200,
      message: 'success',
      data: [
        {
          id: '1',
          name: '首页',
          path: '/dashboard'
        },
        {
          id: '2',
          name: '系统管理',
          path: '/system',
          children: [
            {
              id: '2-1',
              name: '用户管理',
              path: '/system/user'
            }
          ]
        }
      ]
    }
  }
}

export default menuMocks
