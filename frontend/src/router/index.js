import Vue from 'vue'
import VueRouter from 'vue-router'
import Index from '@/components/Index'
import AddTask from '@/components/AddTask'
Vue.use(VueRouter)

const routes = [{
    path: '/',
    name: 'Index',
    component: Index
  },
  {
    path: '/owner/:ownerId',
    name: 'IndexWithOwnerId',
    component: Index
  },
  {
    path: '/add-task',
    name: 'AddTask',
    component: AddTask
  },
  {
    path: '/owner/:ownerId/add-task',
    name: 'AddTaskWithOwnerId',
    component: AddTask
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router