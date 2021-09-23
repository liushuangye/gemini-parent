<template>
  <span>
    <div class="login-container">
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        auto-complete="on"
        label-position="left"
      >
        <div class="title-container">
          <h3 class="title">{{ title }}</h3>
        </div>

        <el-form-item prop="username">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="username"
            v-model="loginForm.username"
            :placeholder="$t('login.userId')"
            name="username"
            type="text"
            tabindex="1"
            maxlength="20"
            auto-complete="on"
          />
        </el-form-item>

        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            :placeholder="$t('login.password')"
            name="password"
            tabindex="2"
            maxlength="20"
            auto-complete="on"
            @keyup.enter.native="handleLogin"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
          </span>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          style="width:100%;margin-bottom:30px;"
          @click.native.prevent="handleLogin"
        >{{ $t('login.login') }}</el-button>
      </el-form>
    </div>
    <span>
      <el-dialog
        :title="$t('login.forget')"
        :visible.sync="dialogFormVisible"
        :modal-append-to-body="false"
        :close-on-click-modal="false"
      >
        <el-form :model="resetForm" style="width:80%;">
          <el-form-item :label="$t('login.userId')" :label-width="formLabelWidth" :required="true">
            <el-input v-model="resetForm.userId" maxlength="20" />
          </el-form-item>
          <el-form-item :label="$t('login.mail')" :label-width="formLabelWidth" :required="true">
            <el-input v-model="resetForm.mail" maxlength="50" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button
            :loading="loading"
            type="primary"
            @click="forgetPassword()"
          >{{ $t('login.confirm') }}</el-button>
          <el-button @click="dialogFormVisible=false">{{ $t('login.cancel') }}</el-button>
        </div>
      </el-dialog>
    </span>
  </span>
</template>

<script>
import { validUsername } from '@/utils/validate'
// const axios = require('axios')
export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error(this.$t('login.userIdNull')))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      callback()
    }
    return {
      title: this.$t('login.title'),
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, trigger: 'blur', validator: validateUsername }
        ],
        password: [
          { required: true, trigger: 'blur', validator: validatePassword }
        ]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined,
      dialogFormVisible: false,
      resetForm: {
        userId: '',
        mail: ''
      },
      formLabelWidth: '120px'
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    Notarize(_this, message) {
      return new Promise((resolve, rejects) => {
        _this
          .$confirm(message, this.$t('common.prompt'), {
            cancelButtonClass: 'bhh-btn-custom-cancel'
          })
          .then(() => {
            resolve(true) // 确认返回true
          })
          .catch(() => {
            resolve(false) // 取消返回false
          })
      })
    },
    openDia() {
      this.dialogFormVisible = true
      this.resetForm = {
        userId: '',
        mail: ''
      }
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          this.$store
            .dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || '/' })
              this.$router.go(0)
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    async forgetPassword() {
      var mailReg = /^[A-Za-zd0-9]+([-_.][A-Za-zd]+)*@([A-Za-zd0-9]+[-.])+[A-Za-zd]{2,5}$/
      var mail = this.resetForm.mail
      if (this.resetForm.userId.trim() === '') {
        this.$message.error(this.$t('login.userIdNull'))
        return
      }
      if (this.resetForm.mail.trim() === '') {
        this.$message.error(this.$t('login.mailNull'))
        return
      } else if (!mailReg.test(mail)) {
        this.$message({
          type: 'error',
          // 邮箱格式错误！
          message: this.$t('accountManagement.mailWanring'),
          duration: 1000
        })
        return
      }
      var msg = this.$t('login.confirmRePassword')
      if (await this.Notarize(this, msg)) {
        this.loading = true
        this.$store
          .dispatch('user/forgetPassword', this.resetForm)
          .then((res) => {
            this.dialogFormVisible = false
            this.$message({
              type: 'success',
              message: this.$t('login.RePasswordSuccess'),
              duration: 1000
            })
          })
          .finally(() => {
            this.loading = false
          })
      }
    }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
$bg: #283443;
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
