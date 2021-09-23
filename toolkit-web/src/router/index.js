import Vue from 'vue'
import Router from 'vue-router'
import i18n from '@/utils/VueI18n'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/error-page/404/index'),
    hidden: true
  },

  {
    path: '/403',
    component: () => import('@/views/error-page/403/index'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/basedata',
    children: [{
      path: 'basedata',
      name: 'basedata',
      component: () => import('@/views/basedata/index'),
      meta: { title: i18n.t('language.basedata'), icon: 'dashboard' }
    }]
  },

  {
    path: '/sysparam',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'sysparam',
        component: () => import('@/views/sysparam/index'),
        meta: { title: i18n.t('language.sysparam'), icon: 'form' }
      }
    ]
  },
  {
    path: '/devopstools',
    component: Layout,
    meta: { title: i18n.t('language.devopstools'), icon: 'form' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/devopstools/index'),
        meta: { title: i18n.t('language.toolsList'), icon: 'form' }
      },
      {
        path: 'view',
        name: 'view',
        component: () => import('@/views/devopstools/view'),
        hidden: true,
        meta: { title: i18n.t('language.toolsView') }
      },
      {
        path: 'edit',
        name: 'edit',
        component: () => import('@/views/devopstools/edit'),
        hidden: true,
        meta: { title: i18n.t('language.toolsEdit') }
      },
      {
        path: 'task',
        name: 'task',
        component: () => import('@/views/devopstools/task'),
        meta: { title: i18n.t('language.devopsTask'), icon: 'form' }
      },
      {
        path: 'detail',
        name: 'detail',
        component: () => import('@/views/devopstools/detail'),
        hidden: true,
        meta: { title: i18n.t('language.taskDetail'), icon: 'form' }
      },
      {
        path: 'log',
        name: 'log',
        component: () => import('@/views/devopstools/log'),
        meta: { title: i18n.t('language.execLog'), icon: 'form' }
      },
      {
        path: 'history',
        name: 'history',
        component: () => import('@/views/devopstools/history'),
        hidden: true,
        meta: { title: i18n.t('language.taskHistory'), icon: 'form' }
      }
    ]
  },
  {
    //lsy-自定义流程
    path: '/workflow',
    component: Layout,
    meta: { title: i18n.t('language.workflow'), icon: 'form' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/workflow/index'),
        meta: { title: i18n.t('language.workfloModdelList'), icon: 'form' }
      },
      {
        path: 'release',
        name: 'release',
        component: () => import('@/views/workflow/release'),
        meta: { title: i18n.t('language.workfloReleaseList'), icon: 'form' }
      }
    ]
  },
  {
    path: '/operatelog',
    component: Layout,
    children: [
      {
        path: 'index',
        name: 'operatelog',
        component: () => import('@/views/operatelog/index'),
        meta: { title: i18n.t('language.operatelog'), icon: 'form' }
      }
    ]
  },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
