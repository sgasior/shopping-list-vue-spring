<template>
  <div v-if="this.dataLoaded">
    <li class="collection-item" v-for="(product,index) in task.productList" :key="index">
      <p>
        <label>
          <input
            type="checkbox"
            class="filled-in"
            checked="checked"
            v-model="product.isDone"
            @change="updateDoneStatus(task.taskNumber)"
          />
          <span>{{product.name}}</span>
        </label>
      </p>
    </li>
  </div>
</template>

<script>
import { APIService } from "@/api/APIService";
const apiService = new APIService();

export default {
  name: "Product",
  data() {
    return {
      ownerId: null,
      dataLoaded: false
    };
  },
  props: {
    task: Object
  },
  methods: {
    updateDoneStatus(taskNumber) {
      this.$emit("updateProductDoneStatus", taskNumber);
    }
  },
  created() {
    this.ownerId = this.$route.params.ownerId;
    if ((this.ownerId != null) & (this.task.productList == null)) {
      apiService
        .getProducts(this.ownerId, this.task.taskNumber)
        .then(productList => {
          this.task.productList = productList;
          this.dataLoaded = true;
        });
    } else {
      this.dataLoaded = true;
    }
  }
};
</script>

<style>
[type="checkbox"] + span:not(.lever) {
  padding-left: 42%;
}

.collection .collection-item {
  background-color: inherit;
}
</style>