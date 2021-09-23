export function buttonPermissionCheck(buttonName) {
  const menu = JSON.parse(sessionStorage.getItem('button'))
  if (menu == null) return false
  return menu.indexOf(buttonName) === -1
}
