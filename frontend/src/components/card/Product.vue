<template>
  <div>
    <li class="collection-item" v-for="(product,index) in productList" :key="index">
      <p class="center">
        <label>
          <input type="checkbox" class="filled-in" checked="checked" />
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
      productList: []
    };
  },
  props: {
    taskNumber: Number
  },
  created() {
    apiService.getProducts(this.taskNumber).then(productList => {
      productList.forEach(product => {
        this.productList.push(product);
      });
    });
  }
};
</script>

<style>
</style>