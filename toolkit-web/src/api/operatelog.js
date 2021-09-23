import request from '@/utils/request'
// 操作日志初始化
export function getOpLogInfo(par) {
  return request({
    url: `/operatelog/getOpLogInfo?userName=${par.userName}&current=${par.current}&size=${par.size}&moduleName=${par.moduleName}&requestTimeFrom=${par.requestTimeFrom}&requestTimeTo=${par.requestTimeTo}`,
    method: 'get'
  })
}
