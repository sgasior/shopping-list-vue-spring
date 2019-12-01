<template>
    <nav class="nav-extended grey darken-3">
      <div class="nav-wrapper">
        <a href="#!" class="brand-logo center">Shopping list</a>
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
    return {};
  }
};
</script>

<style>
nav {
  padding: 2rem;
  text-align: center;
}

.nav-content{
    position: relative;
}

.add-btn{
    position: absolute;
    left: 50%;
    transform: translateX(-50%)
}
</style>