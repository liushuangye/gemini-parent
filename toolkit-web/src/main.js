import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import enLocale from 'element-ui/lib/locale/lang/en' // lang i18n
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
import jaLocale from 'element-ui/lib/locale/lang/ja'
import locale from 'element-ui/lib/locale'
import JsonViewer from 'vue-json-viewer'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'
import i18n from './utils/VueI18n'
import Meta from 'vue-meta'

import '@/icons' // icon
import '@/permission' // permission control

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

// set ElementUI lang to EN
Vue.use(ElementUI)
Vue.use(JsonViewer)
Vue.use(Meta)
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)
if (localStorage.getItem('i18nLocale') === 'zh-CN') {
  locale.use(zhLocale)
} else if (localStorage.getItem('i18nLocale') === 'en-US') {
  locale.use(enLocale)
} else if (localStorage.getItem('i18nLocale') === 'ja-JP') {
  locale.use(jaLocale)
} else {
  locale.use(zhLocale)
}
ElementUI.TableColumn.props.resizable = false
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
})
