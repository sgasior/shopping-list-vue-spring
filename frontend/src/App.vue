<template>
  <div id="app">
    <Navbar />
    <template v-if="ownerId==null">
      <keep-alive include="Index">
        <router-view />
      </keep-alive>
    </template>
    <template v-else>
      <router-view />
    </template>
    <Footer />
  </div>
</template>

<script>
import Navbar from "@/components/Navbar";
import Index from "@/components/Index";
import Footer from "@/components/Footer";
export default {
  name: "App",
  data() {
    return {
      ownerId: null
    };
  },
  components: {
    Navbar,
    Index,
    Footer
  },
  created() {
    this.ownerId = this.$route.params.ownerId;
  },
  watch: {
    $route(to, from) {
      this.ownerId = this.$route.params.ownerId;
    }
  }
};
</script>
<style>
#app {
  display: flex;
  min-height: 100vh;
  flex-direction: column;
}

main {
  flex: 1 0 auto;
}
</style>
