<template>
  <nav class="nav-extended">
    <div class="nav-wrapper">
      <a href="#!" class="brand-logo center">Shopping list {{isAddingTaskActually}}</a>
      <ul class="right" v-if="!this.ownerId">
        <li>
          <a
            class="waves-effect waves-light btn-large btn-publish"
            @click="publishTask()"
          >Publish Task</a>
        </li>
      </ul>
    </div>
    <div class="nav-content">
      <router-link :to="{path:pathToAddTask}">
        <a class="btn-floating btn-large waves-effect waves-light add-btn">
          <i class="material-icons">add</i>
        </a>
      </router-link>
    </div>
  </nav>
</template>

<script>
import { EventBus } from "../event-bus.js";
export default {
  name: "Navbar",
  data() {
    return {
      ownerId: null
    };
  },
  methods: {
    publishTask() {
      EventBus.$emit("publish-task", "Piotr");
    }
  },
  computed: {
    pathToAddTask() {
      if (this.ownerId == null) {
        return "/add-task";
      } else {
        return `/owner/${this.ownerId}/add-task`;
      }
    }
  },
  created() {
    this.ownerId = this.$route.params.ownerId;
    this.isAddingTaskActually =
      this.$router.currentRoute.name === "AddTaskWithOwnerId";
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