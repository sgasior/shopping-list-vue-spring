<template>
  <main>
    <div class="container add-task">
      <h5 class="center-align header">Add New Task</h5>
      <form class="col s12">
        <div class="input-field col s12">
          <input id="task_title" type="text" class="validate" v-model="taskTitle" />
          <label for="task_title">Task title</label>
        </div>
        <div class="input-field col s12" :style="cssVars">
          <select v-model="taskColor">
            <option value="#fffde1">
              <span class="box box1"></span>
            </option>
            <option value="#f6fea1">
              <span class="box box2"></span>
            </option>
            <option value="#ffb270">
              <span class="box box3"></span>
            </option>
            <option value="#e6f7f7">
              <span class="box box4"></span>
            </option>
            <option value="#f7beff">
              <span class="box box5"></span>
            </option>
          </select>
        </div>
        <label>Choose the color of your task</label>
        <div v-for="(product,index) in products" :key="index" class="field col s12">
          <input id="product" type="text" v-model="products[index]" />
          <label for="product">Product</label>
          <i class="material-icons delete" @click="deleteProduct(index)">delete</i>
        </div>
        <div class="input-field col s12">
          <input
            id="add_product"
            type="text"
            class="validate"
            @keydown.tab.prevent="addProduct"
            v-model="productName"
          />
          <label for="add_produc">Add an product (press TAB or proper icon):</label>
        </div>
        <a
          class="waves-effect waves-light btn-large btn-publish btn-add-task"
          v-bind:class="allFieldsAreValid ? '' :'disabled'"
          @click="publishTask()"
        >Publish Task</a>
      </form>
    </div>
  </main>
</template>

<script>
export default {
  data() {
    return {
      taskTitle: "",
      taskColor: "#fffde1",
      products: [],
      productName: null,
      allFieldsAreValid: false
    };
  },
  methods: {
    addProduct() {
      if (this.productName == null) {
        console.log("null jest");
      } else {
        this.products.push(this.productName);
        this.productName = null;
      }
    },
    deleteProduct(index) {
      this.products = this.products.filter(product => {
        return product !== this.products[index];
      });
    }
  },
  mounted() {
    M.AutoInit();
  },
  computed: {
    cssVars() {
      return {
        "--task-bg-color": this.taskColor
      };
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

.add-task {
  max-width: 600px;
}
.add-task .header {
  font-size: 1.8rem;
  margin-top: 8rem;
  margin-bottom: 4rem;
  background: -webkit-linear-gradient(#9ccc65 0%, #827717 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.add-task .input-field {
  margin-top: 3rem;
}

.defaultOption {
  color: #9e9e9e;
}

.add-task .field {
  position: relative;
}
.add-task .delete {
  position: absolute;
  right: 0;
  bottom: 35px;
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
  background-color: #ffb270;
}

.box4 {
  background-color: #e6f7f7;
}

.box5 {
  background-color: #f7beff;
}

/* colors: */
/* fffde1 */
/* f6fea1 */
/* ffb270 */
/* e6f7f7 */
/* f7beff */
</style>