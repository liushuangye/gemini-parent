import request from '@/utils/request'

export function getByPage(data) {
  return request({
    url: '/devops/getByPage',
    method: 'post',
    data
  })
}

export function getById(data) {
  return request({
    url: '/devops/getById?id=' + data,
    method: 'get'
  })
}

export function importTemplate(data) {
  return request({
    url: '/devops/importTemplate',
    method: 'post',
    data
  })
}

export function updateTemplate(data) {
  return request({
    url: '/devops/updateTemplate',
    method: 'post',
    data
  })
}

export function execStep(data) {
  return request({
    url: '/devops/task/execStep',
    method: 'post',
    data
  })
}

export function deleteTool(data) {
  return request({
    url: '/devops/deleteToolkitById?id=' + data,
    method: 'delete'
  })
}

export function getAllDevops() {
  return request({
    url: '/devops/getAllDevops',
    method: 'get'
  })
}

export function getByPageTask(data) {
  return request({
    url: '/devops/task/getByPage',
    method: 'post',
    data
  })
}

export function createTask(data) {
  return request({
    url: '/devops/task/createTask',
    method: 'post',
    data
  })
}

export function execRollback(data) {
  return request({
    url: '/devops/task/execRollback',
    method: 'post',
    data
  })
}

export function getTaskById(data) {
  return request({
    url: '/devops/task/getTaskById?id=' + data,
    method: 'get'
  })
}

export function getDetails(data) {
  return request({
    url: '/devops/log/details?id=' + data,
    method: 'get'
  })
}

export function deleteTask(data) {
  return request({
    url: '/devops/task/deleteTask?id=' + data,
    method: 'delete'
  })
}
