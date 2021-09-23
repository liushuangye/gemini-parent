const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  user: state => state.user.user,
  roles: state => state.user.roles, // 部门idList
  rolesTree: state => state.user.rolesTree, // 部门数据森林
  button: state => state.user.button, // 按钮List
  addRouters: state => state.permission.addRouters,
  menuList: state => state.user.menuList, // 菜单树 不包含按钮
  // menuBar权限表示用
  menuItem: state => state.user.menuItemDisable
}
export default getters
