import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/login/check',
    method: 'post',
    data
  })
}

// export function getInfo(token) {
//   return request({
//     url: '/user/info',
//     method: 'get',
//     params: { token }
//   })
// }

export function logout() {
  return request({
    url: '/login/logout',
    method: 'post'
  })
}

export function forgetPassword(data) {
  return request({
    url: 'login/forgetPassword',
    method: 'post',
    data
  })
}
