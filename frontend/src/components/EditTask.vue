<template>
  <main>
    <div class="container task-form">
      <h5 class="center-align header">Edit task</h5>
      <form class="col s12">
        <div class="input-field col s12 add-input">
          <input
            id="task_title"
            type="text"
            v-bind:class="getValidationClassForTask(task)"
            v-model="task.title"
            v-focus
          />
          <label for="task_title">Task title</label>
          <div class="counter">{{task.title.length}}/{{task.maxLen}}</div>
        </div>
        <div class="input-field col s12">
          <input disabled id="task_creation_date" type="text" :value="task.createdDate" />
          <div class="product-name">Creation date</div>
        </div>
        <div class="input-field col s12" :style="cssVars">
          <select v-model="task.color">
            <option value="#fffde1">
              <span class="box box1"></span>
            </option>
            <option value="#f6fea1">
              <span class="box box2"></span>
            </option>
            <option value="#f4f4f4">
              <span class="box box3"></span>
            </option>
            <option value="#e6f7f7">
              <span class="box box4"></span>
            </option>
            <option value="#fce2ff">
              <span class="box box5"></span>
            </option>
          </select>
        </div>
        <label>Choose the color of your task</label>
        <div v-for="(product,index) in products" :key="index" class="input-field col s12">
          <input
            id="product"
            type="text"
            v-bind:class="getValidationClassForProduct(products[index])"
            v-model="products[index].name"
          />
          <div class="product-name">Product</div>
          <div class="counter">{{products[index].name.length}}/{{task.maxLen}}</div>
          <i class="material-icons delete" @click="deleteProduct(index)">delete</i>
        </div>
        <div class="input-field col s12">
          <input
            id="add_product"
            type="text"
            v-bind:class="getValidationClassForProduct(product)"
            @keydown.tab.prevent="addProduct"
            v-model="product.name"
          />
          <div class="counter">{{product.name.length}}/{{product.maxLen}}</div>
          <label for="add_product">Add an product (press TAB or proper icon):</label>
          <i class="material-icons add" @click="addProduct()">add_circle</i>
          <div :style="{visibility: error ? 'visible':'hidden'}">
            <div class="error">{{error}}</div>
          </div>
        </div>
        <a
          class="waves-effect waves-light btn-large btn-publish btn-add-task"
          v-bind:class="inputsAreValid ? '' :'disabled'"
          @click="updateTask()"
        >Update Task</a>
      </form>
    </div>
  </main>
</template>

<script>
import { APIService } from "@/api/APIService";
import { EventBus } from "../event-bus.js";

const apiService = new APIService();

export default {
  data() {
    return {
      products: [],
      productsNumberToDelete: [],
      product: {
        name: "",
        minLen: 3,
        maxLen: 50,
        isDone: false
      },
      task: {
        title: "",
        color: "#fffde1",
        minLen: 3,
        maxLen: 50
      },
      error: null,
      ownerId: null
    };
  },
  methods: {
    async updateTask() {
      if (this.ownerId == null) {
        this.updateTaskLocally();
        this.$router.push({ name: "Index" });
      } else {
        await apiService
          .updateTask(this.ownerId, this.task.number, this.task)
          .then(response => response.data.taskNumber);

        let productsToAdd = this.products.filter(prod => {
          return prod.productNumber === undefined;
        });

        for (let i = 0; i < this.products.length; i++) {
          if (
            (this.products[i].productNumber !== undefined) &
            !this.productsNumberToDelete.includes(
              this.products[i].productNumber
            )
          ) {
            await apiService.updateProduct(
              this.ownerId,
              this.task.number,
              this.products[i],
              this.products[i].productNumber
            );
          }
        }
        for (let i = 0; i < productsToAdd.length; i++) {
          await apiService.saveProductInTask(
            this.ownerId,
            this.task.number,
            productsToAdd[i]
          );
        }
        this.productsNumberToDelete.forEach(prodNum => {
          apiService.deleteProduct(this.ownerId, this.task.number, prodNum);
        });
        this.$router.push({ name: "IndexWithOwnerId" });
      }
    },
    updateTaskLocally() {
      let task = {
        taskTitle: this.task.title,
        hexColor: this.task.color,
        productList: this.products,
        taskNumber: this.task.number,
        productList: this.products
      };
      EventBus.$emit("update-task", task);
    },
    addProduct() {
      if (this.product.name == "") {
        this.error = "You must enter a product name before add";
      } else {
        let prod = {};
        prod.name = this.product.name;
        prod.isDone = this.product.isDone;
        this.products.push(prod);
        this.product.name = "";
        this.error = null;
      }
    },
    deleteProduct(index) {
      this.productsNumberToDelete.push(this.products[index].productNumber);
      this.products = this.products.filter(product => {
        return product !== this.products[index];
      });
    },
    getValidationClassForTask(taskToCheck) {
      if (taskToCheck.title.length == 0) {
        return "";
      }
      if (
        taskToCheck.title.trim().length < this.task.minLen ||
        taskToCheck.title.length > this.task.maxLen
      ) {
        return "invalid";
      } else {
        return "valid";
      }
    },
    getValidationClassForProduct(prodToCheck) {
      if (prodToCheck.name.length == 0) {
        if (this.products.includes(prodToCheck)) {
          return "invalid";
        }
        return "";
      }
      if (
        prodToCheck.name.trim().length < this.product.minLen ||
        prodToCheck.name.length > this.product.maxLen
      ) {
        return "invalid";
      } else {
        return "valid";
      }
    }
  },
  created() {
    this.ownerId = this.$route.params.ownerId;
    EventBus.$on("emit-task", task => {
      this.task.title = task.taskTitle;
      this.task.color = task.hexColor;
      this.task.number = task.taskNumber;
      this.task.createdDate = task.createdDate;
      this.products = task.productList;
      this.productsBeforeEditing = task.productList;
    });
  },
  mounted() {
    M.AutoInit();
  },
  computed: {
    cssVars() {
      return {
        "--task-bg-color": this.task.color
      };
    },
    inputsAreValid() {
      if (
        this.task.title.trim().length < this.task.minLen ||
        this.task.title.length > this.task.maxLen ||
        this.task.title == null ||
        this.products.length == 0
      ) {
        return false;
      }
      for (let i = 0; i < this.products.length; i++) {
        if (
          this.products[i].name.trim().length < this.product.minLen ||
          this.products[i].name.length > this.product.maxLen ||
          this.products[i].name == null
        ) {
          return false;
        }
      }
      return true;
    }
  },
  directives: {
    focus: {
      inserted: function(el) {
        el.focus();
      }
    }
  }
};
</script>

<style>
.select-wrapper input.select-dropdown {
  background-color: var(--task-bg-color);
}

.select-wrapper .caret {
  z-index: 1;
  fill: #827717;
}

.task-form {
  max-width: 600px;
}
.task-form .header {
  font-size: 1.8rem;
  margin-top: 8rem;
  margin-bottom: 4rem;
  background: -webkit-linear-gradient(#9ccc65 0%, #827717 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.task-form .input-field {
  margin-top: 3rem;
}

.defaultOption {
  color: #9e9e9e;
}

/* label color */
/* .input-field label {
  color: transparent;
} */

/* label underline focus color */
.task-form .input-field input[type="text"]:focus {
  border-bottom: 1px solid #9e9e9e;
  box-shadow: 0 1px 0 0 #9e9e9e;
}

/* label focus color */
.task-form .input-field input[type="text"]:focus + label {
  color: #9e9e9e;
}

/* valid color */
.task-form .input-field input[type="text"].valid {
  border-bottom: 1px solid #9ccc65;
  box-shadow: 0 1px 0 0 #9ccc65;
}

/* invalid color */
.task-form .input-field input[type="text"].invalid {
  border-bottom: 1px solid orangered;
  box-shadow: 0 1px 0 0 orangered;
}

.task-form .field {
  position: relative;
}
.task-form .delete {
  position: absolute;
  right: 0;
  bottom: 35px;
}

.task-form .add {
  position: absolute;
  right: 0;
  bottom: 35px;
  cursor: pointer;
  color: #e6a400;
  transition: all 0.3s;
}

.task-form .add:hover {
  color: #e6b31e;
  transform: scale(1.1);
}

.counter,
.product-name,
.error {
  font-size: 0.8rem;
  text-align: right;
  display: inline-block;
}

.error {
  color: orangered;
}

.product-name {
  color: #9e9e9e;
}

.counter {
  color: #9e9e9e;
  position: absolute;
  right: 0;
  bottom: 0;
}

.add-input .counter {
  bottom: -23px;
}

.btn-add-task {
  margin: 3rem 0;
  width: 100%;
}

.box {
  width: 20px;
  height: 20px;
  position: relative;
  left: 1rem;
  display: block !important;
  border-radius: 3px;
  border: 1px solid black;
}

.box1 {
  background-color: #fffde1;
}

.box2 {
  background-color: #f6fea1;
}

.box3 {
  background-color: #f4f4f4;
}

.box4 {
  background-color: #e6f7f7;
}

.box5 {
  background-color: #fce2ff;
}
</style>