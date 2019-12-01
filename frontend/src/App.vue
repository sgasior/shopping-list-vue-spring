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

</style>
