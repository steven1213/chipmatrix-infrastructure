import { reactive } from 'vue'
import logo from '../assets/Chameleon-logo.svg'

export const useSystemConfig = () => {
  const systemConfig = reactive({
    // logo的位置在assets/Chameleon.svg
    logo:logo,
    title: '变色龙',
  })

  return {
    systemConfig
  }
}