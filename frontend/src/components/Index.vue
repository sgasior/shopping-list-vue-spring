<template>
  <main>
    <div class="container">
      <Search v-on:changeSearch="updateSearch($event)" />
      <div class="row">
        <div class="col s12 m4 l3" v-for="(task,index) in filteredTaskList" :key="index">
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
      search: "",
      taskList: [
        // {
        //   createdDate: "2019-12-03 16.11",
        //   isDone: false,
        //   taskNumber: 0,
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
      ownerId: null
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
          console.log(task.taskNumber);
        }
      });
    },
    updateSearch(search) {
      this.search = search;
    }
  },
  created() {
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
  transform: scale(1.02);
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

