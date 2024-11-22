import { ElMessage } from 'element-plus'

export const showError = (message: string) => {
  ElMessage({
    message,
    type: 'error',
    duration: 3000,  // 显示3秒
    showClose: true
  })
}

export const showSuccess = (message: string) => {
  ElMessage({
    message,
    type: 'success',
    duration: 3000,
    showClose: true
  })
}