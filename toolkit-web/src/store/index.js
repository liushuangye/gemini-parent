import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import user from './modules/user'
import userManagement from './modules/userManagement'
import preventReClick from './modules/preventReClick'
Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    settings,
    user,
    userManagement,
    preventReClick
  },
  getters
})

export default store
