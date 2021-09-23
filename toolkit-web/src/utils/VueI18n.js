import Vue from 'vue'
import VueI18n from 'vue-i18n'

Vue.use(VueI18n)

const i18n = new VueI18n({
  // locale:"zh-CN",
  // 读取localstage中的语言值，如果没有设置，默认使用中文
  locale: localStorage.getItem('i18nLocale') || 'zh-CN',
  messages: {
    'zh-CN': require('./i18n/zh'),
    'en-US': require('./i18n/en')
  }
})

export default i18n
