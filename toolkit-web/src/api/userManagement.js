import request from '@/utils/request'

export function changeUser(data) {
  return request({
    url: 'setting/changeUser',
    method: 'post',
    data
  })
}

export function getUserInfo(data) {
  return request({
    url: 'setting/getUserInfo',
    method: 'post',
    data
  })
}

export function changePassword(data) {
  return request({
    url: 'setting/changePassword',
    method: 'post',
    data
  })
}

export function changeLanguage(data) {
  return request({
    url: 'setting/changeLanguage',
    method: 'post',
    data
  })
}

export function changeCurrency(data) {
  return request({
    url: 'setting/changeCurrency',
    method: 'post',
    data
  })
}
