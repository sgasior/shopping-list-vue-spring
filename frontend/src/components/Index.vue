<template>
  <main>
    <div class="container">
      <Search v-on:changeSearch="updateSearch($event)" />
      <div class="row">
        <div class="col s12 m4 l3" v-for="(task,index) in paginatedData" :key="index">
          <div class="card">
            <i class="material-icons delete" @click="deleteTask(task.taskNumber)">delete</i>
            <div class="card-content" :style="{backgroundColor: task.hexColor}">
              <span class="card-title center">{{task.taskTitle}}</span>
              <p class="task-date">Creation date: {{task.createdDate}}</p>
              <ul class="collection">
                <Product :task="task" />
              </ul>
              <a class="btn-floating halfway-fab waves-effect waves-light edit-task">
                <i class="material-icons">edit</i>
              </a>
            </div>
          </div>
        </div>
      </div>
      <Pagination
        v-on:goToPage="goToPageNumber($event)"
        :pageCount="pageCount"
        :pageNumber="pageNumber"
      />
      <button @click="test()">New item</button>
    </div>
  </main>
</template>

<script>
import { APIService } from "@/api/APIService";
import Search from "@/components/Search";
import Pagination from "@/components/Pagination";
import Product from "@/components/card/Product";
const apiService = new APIService();
import { EventBus } from "../event-bus.js";

export default {
  name: "Index",
  data() {
    return {
      search: "",
      taskList: [
        // {
        //   createdDate: "2019-12-03 16.11",
        //   isDone: false,
        //   taskNumber: 3,
        //   taskTitle: "Zakupy testowe",
        //   productList: [
        //     {
        //       name: "Kukurydza",
        //       isDone: true
        //     },
        //     {
        //       name: "Pepsi",
        //       isDone: false
        //     }
        //   ]
        // }
      ],
      ownerId: null,
      pageNumber: 1,
      maxTasksPerPage: 8
    };
  },
  components: {
    Search,
    Pagination,
    Product
  },
  methods: {
    deleteTask(taskNumber) {
      if (this.ownerId == null) {
        this.deleteTaskLocaly(taskNumber);
      } else {
        apiService.deleteTask(this.ownerId, taskNumber).then(() => {
          this.deleteTaskLocaly(taskNumber);
        });
      }
    },
    deleteTaskLocaly(taskNumber) {
      this.taskList = this.taskList.filter(task => {
        return task.taskNumber != taskNumber;
      });
      this.taskList.forEach(task => {
        if (task.taskNumber > taskNumber) {
          task.taskNumber--;
        }
      });
      if (this.paginatedData.length == 0 && this.pageNumber > 1) {
        this.pageNumber--;
      }
    },
    updateSearch(search) {
      this.pageNumber = 1;
      this.search = search;
    },
    async publishAllTasks(name) {
      const ownerId = await apiService
        .createOwner(name)
        .then(response => response.data.id);
      for (const task of this.taskList) {
        const savedTaskNumber = await apiService
          .saveTaskInOwner(ownerId, task)
          .then(response => response.data.taskNumber);
        task.productList.forEach(product => {
          apiService.saveProductInTask(ownerId, savedTaskNumber, product);
        });
      }
      this.$router.push({
        name: "IndexWithOwnerId",
        params: { ownerId: ownerId }
      });
    },
    goToPageNumber(pageNumber) {
      this.pageNumber = pageNumber;
    },
    test() {
      this.taskList.push({
        createdDate: "2019-12-03 16.11",
        isDone: false,
        taskNumber: 3,
        taskTitle: "Zakupy testowe",
        productList: [
          {
            name: "Kukurydza",
            isDone: true
          },
          {
            name: "Pepsi",
            isDone: false
          }
        ]
      });
    }
  },
  created() {
    EventBus.$on("publish-task", name => {
      this.publishAllTasks(name);
    });
    this.ownerId = this.$route.params.ownerId;
    if (this.ownerId != null) {
      apiService.getTasks(this.ownerId).then(taskList => {
        taskList.forEach(task => this.taskList.push(task));
      });
    }
  },
  computed: {
    filteredTaskList() {
      return this.taskList.filter(task =>
        task.taskTitle.toLowerCase().includes(this.search.toLowerCase())
      );
    },
    pageCount() {
      let l = this.filteredTaskList.length,
        s = this.maxTasksPerPage;
      return Math.ceil(l / s);
    },
    paginatedData() {
      const start = (this.pageNumber - 1) * this.maxTasksPerPage,
        end = start + this.maxTasksPerPage;
      return this.filteredTaskList.slice(start, end);
    }
  }
};
</script>

<style>
.row {
  margin-top: 6rem;
}
.collection {
  margin: 2rem 0;
}

.card .card-content {
  margin-bottom: 3rem;
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
  cursor: pointer;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-image: -moz-linear-gradient(top, #801336 0%, #e6a400 100%);
  background-image: -webkit-linear-gradient(top, #801336 0%, #e6a400 100%);
  background-image: -o-linear-gradient(top, #801336 0%, #e6a400 100%);
  background-image: -ms-linear-gradient(top, #801336 0%, #e6a400 100%);
  background-image: linear-gradient(
    to bottom,
    #801336 0%,
    #c72c41 70%,
    #e6a400 100%
  );
  transition: all 0.2s;
}

.delete:hover {
  transform: scale(1.1);
  opacity: 0.9;
}

.edit-task {
  background-color: #3fc5f0;
}

.edit-task:hover {
  background-color: #42dee1;
}

.card-content {
  /* colors: */
  /* fffde1 */
  /* f6fea1 */
  /* ffb270 */
  /* e6f7f7 */
  /* f7beff */
}
</style>

