<template>
  <div class="search-task">
    <form>
      <div class="input-field">
        <input id="search" type="search" required v-model="search" @keyup="clicked()" />
        <label class="label-icon" for="search">
          <i class="material-icons">search</i>
        </label>
        <i class="material-icons">close</i>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      search: ""
    };
  },
  methods: {
    clicked() {
      this.$emit("changeSearch", this.search);
    }
  }
};
</script>

<style>
.search-task {
  margin-top: 6rem;
  width: 40%;
  margin-left: auto;
  margin-right: auto;
}

.search-task .input-field i {
  position: absolute;
  right: 2rem;
}
</style>