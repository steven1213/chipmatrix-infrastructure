import request from '../utils/request'

interface LoginForm {
  username: string
  password: string
}

export function accountLogin(data: LoginForm) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function ldapLogin(data: LoginForm) {
  return request({
    url: '/auth/ldap/login',
    method: 'post',
    data
  })
}