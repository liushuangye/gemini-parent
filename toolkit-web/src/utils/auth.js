import Cookies from 'js-cookie'

const TokenKey = 'Authorization'
const MenuItemKey = 'MemuItemDisable'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getMenuItem() {
  return Cookies.get(MenuItemKey)
}

export function setMenuItem(MenuItem) {
  return Cookies.set(MenuItemKey, MenuItem)
}

export function removeMenuItem() {
  return Cookies.remove(MenuItemKey)
}
