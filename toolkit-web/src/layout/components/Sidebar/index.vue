<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapGetters(['sidebar']),
    routes() {
      // 有权限可以显示的menuitem
      /**
      const canDisableMenuItem = this.$store.getters.menuItem
      if (!canDisableMenuItem) {
        return
      }
       */
      // 循环全部的router，过滤掉没有权限的router，保留有权限的router
      const menuItems = this.$router.options.routes.filter((item) => {
        if (item.children && item.children.length > 1) {
          item.children = item.children.filter((child) => {
            if (child.path === '') return true
            //return canDisableMenuItem.indexOf(child.path) >= 0
            return true
          })
          if (
            item.children.length === 0 ||
            (item.children.length === 1 &&
              item.children[0].path === '' &&
              !item.children[0].path.children)
          ) {
            return false
          } else {
            //return canDisableMenuItem.indexOf(item.path) >= 0
            return true
          }
        } else if (item.children) {
          //return canDisableMenuItem.indexOf(item.path) >= 0
          return true
        } else {
          return true
        }
      })
      return menuItems
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  }
}
</script>
