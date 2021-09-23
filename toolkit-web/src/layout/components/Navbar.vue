<template>
  <div class="navbar">
    <hamburger
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />

    <breadcrumb class="breadcrumb-container" />

    <div class="right-menu">
      <el-badge
        v-show="permisionFlg"
        :value="consultCount"
        :max="99"
        style="margin-right: 20px;"
        class="item"
      >
        <el-button size="small" @click="ToConsult">{{ $t('common.labelInformation') }}</el-button>
      </el-badge>
      <el-dropdown class="avatar-container" trigger="click" :hide-on-click="false">
        <div class="avatar-wrapper">
          <div class="user-avatar">{{ userName }}</div>
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/">
            <el-dropdown-item>{{ $t('navbar.home') }}</el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">{{ $t('navbar.logout') }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
// import { changeLanguage } from '@/api/userManagement'
// import { getMenuItem } from '@/utils/auth'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      userName: this.$store.getters.name,
      consultCount: 0,
      permisionFlg: false
    }
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar'])
  },
  async mounted() {
    await this.init()
    // debugger
    // var userAgent = navigator.userAgent
    // var isOpera = userAgent.indexOf('Opera') > -1 // Opera
    // var isIE = userAgent.indexOf('compatible') > -1 && userAgent.indexOf('MSIE') > -1 && !isOpera // IE
    // var isIE11 = userAgent.indexOf('rv:11.0') > -1 // IE11
    // var isEdge = userAgent.indexOf('Edge') > -1 && !isIE // IEEdge
    // var beforeUnload_time = 0
    // var onunload_time = 0
    // if (!isIE11 && !isEdge && !isIE) {
    //   window.addEventListener('beforeunload', e => {
    //     beforeUnload_time = new Date().getTime()
    //   })
    //   window.addEventListener('unload', e => {
    //     onunload_time = new Date().getTime() - beforeUnload_time
    //     if (onunload_time <= 5) {
    //       this.set()
    //     }
    //   })
    // } else if (isIE11 || isIE) {
    //   var ieFlag = 0
    //   debugger
    //   // IE11和Edge浏览器在关闭时先触发beforeunload,然后再触发unload事件。而刷新时先触发beforeunload事件，再触发unload事件，再触发load事件。
    //   window.addEventListener('beforeunload', function(event) {
    //     ieFlag = 1
    //     console.log('beforeunload')
    //     unload(ieFlag).then(response => {
    //     }).finally(() => {
    //     })
    //     // event.preventDefault()
    //     // event.returnValue = ''
    //   })
    //   window.addEventListener('unload', e => {
    //     ieFlag = 0
    //     console.log('unload')
    //     load(ieFlag).then(response => {
    //     }).finally(() => {
    //     })
    //   })
    //   window.addEventListener('load', e => {
    //     ieFlag = 0
    //     console.log('load')
    //     load(ieFlag).then(response => {
    //     }).finally(() => {
    //     })
    //   })
    // } else if (isEdge) {
    //   window.addEventListener('beforeunload', e => {
    //     ieFlag = 1
    //     unload(ieFlag).then(response => {
    //     }).finally(() => {
    //     })
    //   })
    //   window.addEventListener('unload', e => {
    //     ieFlag = 0
    //     load(ieFlag).then(response => {
    //     }).finally(() => {
    //     })
    //   })
    //   window.addEventListener('load', e => {
    //     ieFlag = 0
    //     load(ieFlag).then(response => {
    //     }).finally(() => {
    //     })
    //   })
    // }
  },
  methods: {
    async init() {
      /** 站内信消息 */
      /**
      const menu = getMenuItem()
      if (menu.indexOf('/consult') === -1) {
        this.permisionFlg = false
      } else {
        this.permisionFlg = true
      }
      if (this.permisionFlg) {
        await getConsultCount(localStorage.getItem('userId')).then(
          (response) => {
            this.consultCount = response.count
          }
        )
      }*/
    },
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
    changeChinese() {
      this.$i18n.locale = 'zh-CN'
      localStorage.setItem('i18nLocale', 'zh-CN')
      this.$router.go(0)
    },
    changeEnglish() {
      this.$i18n.locale = 'en-US'
      localStorage.setItem('i18nLocale', 'en-US')
      this.$router.go(0)
    },
    changeJapanese() {
      this.$i18n.locale = 'ja-JP'
      localStorage.setItem('i18nLocale', 'ja-JP')
      this.$router.go(0)
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      if (await this.Notarize(this, this.$t('navbar.logoutMessage'))) {
        await this.$store.dispatch('user/logout')
        this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      }
    },
    ToConsult() {
      this.$router.push('/consult')
    },
    set() {
      this.$store.dispatch('user/logout')
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
