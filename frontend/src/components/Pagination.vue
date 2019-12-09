<template>
  <ul class="pagination center">
    <li class="waves-effect" :class="{'disabled': pageNumber==1}">
      <a href="#!" @click="prevPage()">
        <i class="material-icons">chevron_left</i>
      </a>
    </li>
    <span v-for="(count,index) in pageCountToShow" :key="index">
      <li
        class="waves-effect"
        v-bind:class="{'active': pageNumber==count}"
        @click="goToPage(count)"
      >
        <a href="#!">{{count}}</a>
      </li>
    </span>
    <li class="waves-effect" :class="{'disabled':(pageNumber==pageCount) || (pageCount<1)}">
      <a href="#!" @click="nextPage()">
        <i class="material-icons">chevron_right</i>
      </a>
    </li>
  </ul>
</template>

<script>
export default {
  data() {
    return {};
  },
  methods: {
    nextPage() {
      if (this.pageCount == this.pageNumber || this.pageCount < 1) {
        return;
      }
      this.$emit("goToPage", this.pageNumber + 1);
    },
    prevPage() {
      if (this.pageNumber == 1) {
        return;
      }
      this.$emit("goToPage", this.pageNumber - 1);
    },
    goToPage(pageNumber) {
      if (this.pageCount < 2) {
        return;
      }
      this.$emit("goToPage", pageNumber);
    }
  },
  props: {
    pageCount: Number,
    pageNumber: Number
  },
  computed: {
    pageCountToShow() {
      let result = [];
      if (this.pageNumber - 1 >= 1) {
        result.push(this.pageNumber - 1);
      }
      result.push(this.pageNumber);
      if (this.pageNumber < this.pageCount || this.pageNumber == 1) {
        result.push(this.pageNumber + 1);
      }
      return result;
    }
  }
};
</script>


<style>
.pagination {
  margin-top: 6rem;
}

.pagination li.active {
  background-color: #e6a400;
}
</style>