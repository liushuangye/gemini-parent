import request from '@/utils/request'

export function getTBasedataImportHis(data) {
  return request({
    url: '/basedata/getTBasedataImportHis',
    method: 'post',
    data
  })
}

export function getTemplateType() {
  return request({
    url: '/basedata/getTemplateType',
    method: 'get'
  })
}

// 文件导出
export function outPutXLS(data) {
  return request({
    url: '/basedata/outPutXLS',
    method: 'post',
    responseType: 'blob',
    data
  })
}

// 上传文件
export function importXLS(data) {
  return request({
    url: '/basedata/importXLS',
    method: 'post',
    data
  })
}

// 文件导出
export function outPutFile(data) {
  return request({
    url: '/basedata/outPutFile?id=' + data,
    method: 'get',
    responseType: 'blob'
  })
}
