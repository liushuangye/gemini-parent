// 格式化日期
export function formatDate(params) {
  const defalutParams = {
    date: new Date(),
    formatStr: 'yyyy-MM-dd HH:mm:ss'
  }
  params = { ...defalutParams, ...params }
  let date = params.date
  let formatStr = params.formatStr
  // 传入日期字符串 - 转成时间戳 - 转成标准时间
  const timeStamp = new Date(date).getTime()
  date = new Date(timeStamp)
  formatStr = formatStr.replace(new RegExp('yyyy'), `${date.getFullYear()}`)
  const month = date.getMonth() + 1
  formatStr = formatStr.replace(new RegExp('MM'), `${month > 9 ? month : '0' + month}`)
  const day = date.getDate()
  formatStr = formatStr.replace(new RegExp('dd'), `${day > 9 ? day : '0' + day}`)
  const hour = date.getHours()
  formatStr = formatStr.replace(new RegExp('HH'), `${hour > 9 ? hour : '0' + hour}`)
  const min = date.getMinutes()
  formatStr = formatStr.replace(new RegExp('mm'), `${min > 9 ? min : '0' + min}`)
  const sec = date.getSeconds()
  formatStr = formatStr.replace(new RegExp('ss'), `${sec > 9 ? sec : '0' + sec}`)
  return formatStr
}

// 手机号check
export function checkMobile(param) {
  if (/(^0?[1][0-9]{10}$)/.test(param)) {
    return true
  } else {
    return false
  }
}

// 固定电话check
export function checkTel(param) {
  if (/^(((0[1-9]{3})?(0[12][0-9])?[-])?\d{6,8})?(0?[1][35678][0-9]{9})?(-\d{1,6})?$/.test(param)) {
    return true
  } else {
    return false
  }
}

// 邮箱check
export function checkMail(param) {
  if (/\w{1,}[@][\w\-]{1,}([.]([\w\-]{1,})){1,3}$/.test(param)) {
    return true
  } else {
    return false
  }
}

// 日期check
export function checkDate(param) {
  if (/\d{4}\/\d{1,2}\/\d{1,2}/.test(param)) {
    return true
  } else {
    return false
  }
}

// 统一社会信用代码check
export function checkSocialCreditCode(param) {
  if (/[1-9A-GY]{1}[1239]{1}[1-5]{1}[0-9]{5}[0-9A-Z]{10}/.test(param)) {
    return true
  } else {
    return false
  }
}

// 传真check
export function checkFax(param) {
  if (/^[a-zA-Z0-9\(\)\-\.|,+_&\/ ]+$/.test(param)) {
    return true
  } else {
    return false
  }
}

// 邮编check
export function checkZipCode(param) {
  if (/^[a-zA-Z0-9\(\)\-\.|_&\/ ]+$/.test(param)) {
    return true
  } else {
    return false
  }
}
