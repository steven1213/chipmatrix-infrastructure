<template>
    <div class="login-container">
      <div class="login-box">
        <!-- Logo和系统名称 -->
        <div class="header">
          <img :src="systemConfig.logo" class="logo" alt="logo">
          <h2 class="title">{{ systemConfig.title }}</h2>
        </div>
  
        <!-- 登录方式切换 -->
        <el-tabs v-model="activeTab" class="login-tabs">
          <el-tab-pane label="账号密码登录" name="account">
            <el-form ref="accountFormRef" :model="accountForm" :rules="accountRules">
              <el-form-item prop="username">
                <el-input v-model="accountForm.username" placeholder="请输入用户名">
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="accountForm.password" type="password" placeholder="请输入密码">
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>
  
          <el-tab-pane label="LDAP登录" name="ldap">
            <el-form ref="ldapFormRef" :model="ldapForm" :rules="ldapRules">
              <el-form-item prop="username">
                <el-input v-model="ldapForm.username" placeholder="请输入LDAP用户名">
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="ldapForm.password" type="password" placeholder="请输入LDAP密码">
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
  
        <!-- 登录按钮 -->
        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
          登录
        </el-button>
  
        <!-- 第三方登录 -->
        <div class="third-party">
          <div class="title">第三方登录</div>
          <div class="icons">
            <el-tooltip content="Github登录" placement="bottom">
              <el-icon class="icon" @click="thirdPartyLogin('github')"><svg-icon icon-name="github" /></el-icon>
            </el-tooltip>
            <el-tooltip content="企业微信登录" placement="bottom">
              <el-icon class="icon" @click="thirdPartyLogin('wework')"><svg-icon icon-name="wework" /></el-icon>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  import { User, Lock } from '@element-plus/icons-vue'
  import { useSystemConfig } from '../../hooks/useSystemConfig'
  import { accountLogin, ldapLogin } from '../../api/auth';
  
  const router = useRouter()
  const loading = ref(false)
  const activeTab = ref('account')
  const { systemConfig } = useSystemConfig()
  
  // 账号密码登录表单
  const accountForm = reactive({
    username: '',
    password: ''
  })
  
  // LDAP登录表单
  const ldapForm = reactive({
    username: '',
    password: ''
  })
  
  // 表单校验规则
  const accountRules = {
    username: [{ required: true, message: '请输用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
  }
  
  const ldapRules = {
    username: [{ required: true, message: '请输入LDAP用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入LDAP密码', trigger: 'blur' }]
  }
  
  // 登录处理
  const handleLogin = async () => {
    loading.value = true
    try {
      if (activeTab.value === 'account') {
        // 账号密码登录逻辑
        await accountLogin(accountForm)
      } else {
        // LDAP登录逻辑
        await ldapLogin(ldapForm)
      }
      router.push('/')
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  }
  
  // 第三方登录处理
  const thirdPartyLogin = (type: string) => {
    // 处理第三方登录
    console.log('第三方登录:', type)
  }
  </script>
  
  <style lang="scss">
  // 添加全局样式
  html, body {
    margin: 0;
    padding: 0;
    height: 100%;
    width: 100%;
    overflow: hidden;  // 防止滚动
  }

  #app {
    height: 100%;
    width: 100%;
  }
  </style>

  <style lang="scss" scoped>
  .login-container {
    min-height: 100vh;  // 改为 min-height
    width: 100vw;      // 添加宽度
    display: flex;
    justify-content: center;
    align-items: center;
    background: #0A192F;
    position: relative;
    overflow: hidden;    // 确保内容不会溢出
    box-sizing: border-box; // 确保padding不会导致溢出
  
    &::before {
      content: '';
      position: absolute;
      width: 200%;
      height: 200%;
      background-image: 
        linear-gradient(#00F5A0 1px, transparent 1px),
        linear-gradient(90deg, #00F5A0 1px, transparent 1px);
      background-size: 50px 50px;
      background-position: center center;
      transform: rotate(45deg);
      opacity: 0.03;
    }
  
    .login-box {
      width: 400px;
      padding: 40px;
      background: rgba(16, 32, 64, 0.8);
      border-radius: 16px;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(0, 245, 160, 0.1);
      box-shadow: 0 0 20px rgba(0, 217, 245, 0.2);
      position: relative;
      z-index: 1;
  
      &::before {
        content: '';
        position: absolute;
        top: -2px;
        left: -2px;
        right: -2px;
        bottom: -2px;
        background: linear-gradient(45deg, #00F5A0, #00D9F5, #37A3FF);
        border-radius: 17px;
        z-index: -1;
        opacity: 0.1;
        filter: blur(3px);
      }
  
      .header {
        text-align: center;
        margin-bottom: 30px;
  
        .logo {
          width: 160px;
          height: 160px;
          filter: drop-shadow(0 0 10px rgba(0, 245, 160, 0.5));
        }
  
        .title {
          margin-top: 15px;
          font-size: 24px;
          color: #fff;
          text-shadow: 0 0 10px rgba(0, 217, 245, 0.5);
          letter-spacing: 1px;
        }
      }
  
      .login-tabs {
        :deep(.el-tabs__item) {
          color: rgba(255, 255, 255, 0.6);
          &.is-active {
            color: #00F5A0;
          }
        }
        :deep(.el-tabs__active-bar) {
          background-color: #00F5A0;
        }
      }
  
      :deep(.el-input) {
        .el-input__wrapper {
          background: rgba(16, 32, 64, 0.6);
          border: 1px solid rgba(0, 245, 160, 0.2);
          box-shadow: none;
          
          &:hover, &.is-focus {
            border-color: #00F5A0;
            box-shadow: 0 0 5px rgba(0, 245, 160, 0.3);
          }
        }
        
        input {
          color: #fff;
          &::placeholder {
            color: rgba(255, 255, 255, 0.3);
          }
        }
  
        .el-input__prefix {
          color: rgba(0, 245, 160, 0.7);
        }
      }
  
      .login-button {
        width: 100%;
        margin-top: 20px;
        background: linear-gradient(45deg, #00F5A0, #00D9F5);
        border: none;
        height: 44px;
        font-size: 16px;
        font-weight: 500;
        letter-spacing: 1px;
        transition: all 0.3s;
  
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 5px 15px rgba(0, 245, 160, 0.3);
        }
      }
  
      .third-party {
        margin-top: 30px;
        text-align: center;
  
        .title {
          color: rgba(255, 255, 255, 0.5);
          font-size: 14px;
          margin-bottom: 15px;
        }
  
        .icons {
          display: flex;
          justify-content: center;
          gap: 20px;
  
          .icon {
            font-size: 24px;
            color: rgba(255, 255, 255, 0.6);
            cursor: pointer;
            transition: all 0.3s;
  
            &:hover {
              color: #00F5A0;
              transform: translateY(-2px);
              filter: drop-shadow(0 0 5px rgba(0, 245, 160, 0.5));
            }
          }
        }
      }
    }
  }
  </style>