import request from '@/utils/request'

export function getTree(data) {
  return request({
    url: '/sysparam/getTree',
    method: 'post',
    data
  })
}

export function getTCode(data) {
  return request({
    url: '/sysparam/getTCode',
    method: 'post',
    data
  })
}

export function save(data) {
  return request({
    url: '/sysparam/save',
    method: 'post',
    data
  })
}

export function deleteCode(data) {
  return request({
    url: '/sysparam/delete',
    method: 'post',
    data
  })
}

// 文件导出
export function outPutXLS(data) {
  return request({
    url: '/sysparam/outPutXLS',
    method: 'post',
    responseType: 'blob',
    data
  })
}

// 上传文件
export function importXLS(data) {
  return request({
    url: '/sysparam/importXLS',
    method: 'post',
    data
  })
}
