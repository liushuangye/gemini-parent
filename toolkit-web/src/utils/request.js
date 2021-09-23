import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router from '@/router'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 500000, // request timeout
  withCredentials: true
})

// 解决IE 读取缓存问题
function filterUrl(url) {
  return url.indexOf('?') !== -1 ? `${url}&time=${new Date().getTime()}` : `${url}?time=${new Date().getTime()}`
}

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    config.url = filterUrl(config.url)
    if (store.getters.token) {
      // let each request carry token
      // please modify it according to the actual situation
      config.headers['Authorization'] = getToken()
      config.headers['Accept-Language'] = localStorage.getItem('i18nLocale')
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    // 取得最新生成的token信息，并保存到cookies中
    if (response.headers.authorization) {
      setToken(response.headers.authorization)
      localStorage.setItem('Authorization', response.headers.authorization)
    }
    // 取得下载文件名，并保存到cookies中
    if (response.headers['content-disposition']) {
      localStorage.setItem('content-disposition', response.headers['content-disposition'])
    }
    // if the custom code is not 20000, it is judged as an error.
    if (res.code && res.code !== 0 && res.code != 200) {
      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 400 || res.code === 500) {
        Message({
          message: res.msg || 'Error',
          type: 'error',
          duration: 1 * 1000
        })
      } else if (res.code === 401) {
        removeToken()
        router.push({ path: '/login' })
      } else if (res.code === 404) {
        router.push({ path: '/404' })
      } else if (res.code === 403) {
        router.push({ path: '/403' })
      } else {
        Message({
          message: res.msg || 'Error',
          type: 'error',
          duration: 1 * 1000
        })
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    } else if (res instanceof Blob && res.type === 'application/json') {
      /** file download error */
      const reader = new FileReader()
      reader.readAsText(res, 'utf-8')
      reader.onload = () => {
        // 处理报错信息
        const jsonData = JSON.parse(reader.result)
        if (jsonData.code === 400 || jsonData.code === 500) {
          Message({
            message: jsonData.msg || 'Error',
            type: 'error',
            duration: 1 * 1000
          })
        } else if (jsonData.code === 401) {
          removeToken()
          router.push({ path: '/login' })
        } else if (jsonData.code === 404) {
          router.push({ path: '/404' })
        } else if (jsonData.code === 403) {
          router.push({ path: '/403' })
        } else {
          Message({
            message: jsonData.msg || 'Error',
            type: 'error',
            duration: 1 * 1000
          })
        }
      }
    } else {
      //如果是个字符串，则认为其实一段url，如/workflow/xxx.html
      if(typeof(res) == "string" && res.startsWith("static") ){
        window.open(process.env.VUE_APP_BASE_API + res,'_blank')
      }
      //成功
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 1 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
