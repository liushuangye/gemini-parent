import axios from 'axios'
import request from '@/utils/request'

//模型列表-data中是page信息
export function listModel(data) {
  return request({
    url: '/workflow/listModel',
    method: 'post',
    data
  })
}
//发布列表-data中是page信息
export function listRelease(data) {
  return request({
    url: '/workflow/listRelease',
    method: 'post',
    data
  })
}
//设计流程模型
export function design(modelId) {
  return request({
    url: '/workflow/design?modelId=' + modelId,
    method: 'get'
  })
}
//发布流程模型
export function deploy(data) {
  return request({
    url: '/workflow/deploy',
    method: 'post',
    data
  })
}
//创建流程模型
export function create() {
  return request({
    url: '/workflow/create',
    method: 'get'
  })
}
//导入流程模型
export function importBpmnModel(data) {
  return request({
    url: '/workflow/importBpmnModel',
    method: 'post',
    data
  })
}
//导出流程模型
export function exportSelect(data) {
  const modelId = data[0].id
  return request({
    url: '/workflow/exportByModelId?modelId=' + modelId,
    method: 'get'
  })
}
//删除模型
export function deleteModel(modelId) {
  return request({
    url: '/workflow/deleteModel/' + modelId,
    method: 'delete'
  })
}
