<template>
  <main>
    <div class="container">
      <Search />
      <div class="row">
        <div class="col s12 m4 l3" v-for="(task,index) in taskList" :key="index">
          <div class="card">
            <i class="material-icons delete" @click="deleteTask(task.taskNumber)">delete</i>
            <div class="card-content">
              <span class="card-title center">{{task.taskTitle}}</span>
              <p class="task-date">Creation date: {{task.createdDate}}</p>
              <ul class="collection">
                <Product :taskNumber="task.taskNumber" />
              </ul>
              <a class="btn-floating halfway-fab waves-effect waves-light grey darken-1">
                <i class="material-icons">edit</i>
              </a>
            </div>
          </div>
        </div>
      </div>
      <Pagination />
    </div>
  </main>
</template>

<script>
import { APIService } from "@/api/APIService";
import Search from "@/components/Search";
import Pagination from "@/components/Pagination";
import Product from "@/components/card/Product";
const apiService = new APIService();

export default {
  name: "Index",
  data() {
    return {
      taskList: []
    };
  },
  components: {
    Search,
    Pagination,
    Product
  },
  methods: {
    deleteTask(taskNumber) {
      apiService.deleteTask(taskNumber).then(() => {
        this.taskList = this.taskList.filter(task => {
          return task.taskNumber != taskNumber;
        });
      });
    }
  },
  created() {
    apiService.getTasks().then(taskList => {
      taskList.forEach(task => this.taskList.push(task));
    });
  }
};
</script>

<style>
.card {
  margin: 6rem 0;
}

.collection {
  margin: 2rem 0;
}

.card .card-content {
  padding-left: 0;
  padding-right: 0;
}

div.card-title {
  padding: 1rem;
}

.task-done {
  text-decoration: line-through;
}

.task-date {
  color: #757575;
  font-size: 0.8rem;
  text-align: center;
}

.delete {
  position: absolute;
  right: 6px;
  color: #757575;
}
</style>

