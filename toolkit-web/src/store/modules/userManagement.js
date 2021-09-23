import { getUserInfo, changePassword } from '@/api/userManagement'

const actions = {
  getUserInfo({ commit }, userId) {
    return new Promise((resolve, reject) => {
      getUserInfo({ userId: userId }).then(res => {
        resolve(res)
      })
    })
  },

  changePassword({ commit }, psForm) {
    return new Promise((resolve, reject) => {
      changePassword(psForm).then(res => {
        resolve(res)
      }).catch((err1) => {
        console.log(err1)
        reject(err1)
      })
    })
  }

}

export default {
  namespaced: true,

  actions
}

