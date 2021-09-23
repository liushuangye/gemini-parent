import { login, logout, forgetPassword } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { getMenuItem, setMenuItem, removeMenuItem } from '@/utils/auth'
import { resetRouter } from '@/router'
import i18n from '@/utils/VueI18n'
import { changeUser } from '@/api/userManagement'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    user: [],
    roles: [],
    button: [],
    menuList: [],
    // menubar 中有权限，可以显示的path，存入下记的数组
    menuItemDisable: getMenuItem(),
    avatar: ''
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_USER: (state, user) => {
    state.user = user
  },
  SET_ROLE: (state, roles) => {
    state.roles = roles
  },
  SET_ROLE_TREE: (state, rolesTree) => {
    state.rolesTree = rolesTree
  },
  SET_BUTTON: (state, button) => {
    state.button = button
  },
  SET_MENU_LIST: (state, menuList) => {
    state.menuList = menuList
  },
  // TODO
  SET_MENU_ITEM_DISABLE: (state, menuItem) => {
    // console.log(menuItem)
    // 根据情况 从DB读取设置以下变量
    state.menuItemDisable = menuItem
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password }).then(response => {
        // 获取语言信息
        if (response.data.language === 'zh-CN' || response.data.language === 'en-US' || response.data.language === 'ja-JP') {
          localStorage.setItem('i18nLocale', response.data.language)
          i18n.locale = response.data.language
        }

        // 储存设置token
        commit('SET_TOKEN', response.data.token)
        setToken(response.data.token)
        localStorage.setItem('Authorization', response.data.token)

        // 储存用户信息及货币类型
        localStorage.setItem('userName', response.data.staffName)
        localStorage.setItem('userId', response.data.userName)
        commit('SET_USER', response.data)
        commit('SET_NAME', response.data.staffName)
        // 重置路由
        resetRouter()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // // get user info
  // getInfo({ commit }) {
  //   return new Promise((resolve, reject) => {
  //     commit('SET_NAME', state.user.userId)
  //     resolve()
  //   })
  // },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        removeToken() // must remove  token  first
        // 删除menubar 的权限记录
        removeMenuItem()
        resetRouter()
        localStorage.clear()
        sessionStorage.clear()
        commit('RESET_STATE')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      // 删除menubar 的权限记录
      removeMenuItem()
      commit('RESET_STATE')
      resolve()
    })
  },

  changeUser({ commit }, userForm) {
    return new Promise((resolve, reject) => {
      changeUser(userForm).then(res => {
        commit('SET_NAME', userForm.name)
        resolve(res)
      }).catch((err1) => {
        console.log(err1)
        reject(err1)
      })
    })
  },

  forgetPassword({ commit }, resetFrom) {
    return new Promise((resolve, reject) => {
      forgetPassword(resetFrom).then(response => {
        resolve(response)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

/**
 * 通过部门id数组取得权限数
 * @param {*} deptTree
 * @param {*} deptId
 * @param {*} deptArray
 */
function getArrTreeCode(deptTree, deptId, deptArray) {
  for (let i = 0; i < deptTree.length; i++) {
    deptArray = getTreeCode(deptTree[i], deptId, deptArray)
  }
  return deptArray
}

function getTreeCode(deptTree, deptId, deptArray) {
  if (deptId.indexOf(deptTree.id + '') !== -1) {
    deptArray.push(deptTree)
    return deptArray
  }
  if (deptTree.children !== undefined && deptTree.children !== null) {
    for (let i = 0; i < deptTree.children.length; i++) {
      deptArray = getTreeCode(deptTree.children[i], deptId, deptArray)
    }
  }
  return deptArray
}

/**
 * 权限树遍历，获取权限数组
 * @param {*} deptArray
 * @param {*} roleArray
 */
function getRole(deptArray, roleArray) {
  for (let i = 0; i < deptArray.length; i++) {
    roleArray = getTreeRole(deptArray[i], roleArray)
  }
  return roleArray
}

function getTreeRole(deptArray, roleArray) {
  if (deptArray.id !== undefined && deptArray.id !== null && roleArray !== null && roleArray.indexOf(deptArray.id) === -1) {
    roleArray.push(deptArray.id)
  }
  if (deptArray.children !== undefined && deptArray.children !== null) {
    for (let i = 0; i < deptArray.children.length; i++) {
      roleArray = getTreeRole(deptArray.children[i], roleArray)
    }
  }
  return roleArray
}

/**
 * 取menuPath数组
 * @param {*} menu
 * @param {*} menuPath
 */
function getMenuList(menu, menuPath) {
  for (let i = 0; i < menu.length; i++) {
    menuPath = getMenuPathList(menu[i], menuPath)
  }
  return menuPath
}

function getMenuPathList(menu, menuPath) {
  if (menu.menuUrl !== undefined && menu.menuUrl !== null && menuPath !== null && menuPath.indexOf(menu.menuUrl) === -1) {
    menuPath.push(menu.menuUrl)
  }
  if (menu.children !== undefined && menu.children !== null) {
    for (let i = 0; i < menu.children.length; i++) {
      menuPath = getMenuPathList(menu.children[i], menuPath)
    }
  }
  return menuPath
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

