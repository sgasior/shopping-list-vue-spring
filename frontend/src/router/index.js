import Vue from 'vue'
import VueRouter from 'vue-router'
import Index from '@/components/Index'
import AddTask from '@/components/AddTask'
import EditTask from '@/components/EditTask'
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
  },
  {
    path: '/edit-task/:taskNumber',
    name: 'EditTask',
    component: EditTask
  },
  {
    path: '/owner/:ownerId/edit-task/:taskNumber',
    name: 'EditTaskWithOwnerId',
    component: EditTask
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router