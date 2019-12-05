<template>
  <nav class="nav-extended">
    <div class="nav-wrapper">
      <a href="#!" class="brand-logo center">Shopping list</a>
      <ul class="right" v-if="!this.ownerId">
        <li>
          <a class="waves-effect waves-light btn-large btn-publish">Publish Task</a>
        </li>
      </ul>
    </div>
    <div class="nav-content">
      <a class="btn-floating btn-large waves-effect waves-light add-btn">
        <i class="material-icons">add</i>
      </a>
    </div>
  </nav>
</template>

<script>
export default {
  name: "Navbar",
  data() {
    return {
      ownerId: null
    };
  },
  created() {
    this.ownerId = this.$route.params.ownerId;
  }
};
</script>

<style>
nav {
  padding: 2rem;
  text-align: center;
  background: linear-gradient(
    to top right,
    #a5d6a7 0%,
    #9ccc65 30%,
    #827717 100%
  );
}

.nav-content {
  position: relative;
}

.add-btn {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  background-color: #e6a400;
}

.add-btn:hover {
  background-color: #e6b31e;
}

.btn-publish {
  background-color: #e6a400;
}

.btn-publish:hover {
  background-color: #e6b31e;
}
</style>