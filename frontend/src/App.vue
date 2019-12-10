<template>
  <div id="app">
    <Navbar />
    <Index />
    <Footer />
  </div>
</template>

<script>
import Navbar from "@/components/Navbar";
import Index from "@/components/Index";
import Footer from "@/components/Footer";
export default {
  name: "App",
  components: {
    Navbar,
    Index,
    Footer
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
