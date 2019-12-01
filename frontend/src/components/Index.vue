<template>
  <main>
    <div class="container">
      <Search />
      <div class="row">
        <div class="col s12 m4 l3">
          <div class="card">
            <div class="card-content">
              <span class="card-title center">Tommorow party</span>
              <p class="task-date">Creation date: 1.12.2019</p>
              <ul class="collection">
                <li class="collection-item">
                  <p class="center">
                    <label>
                      <input type="checkbox" class="filled-in" checked="checked" />
                      <span>Mleko dsaasss</span>
                    </label>
                  </p>
                </li>
                <li class="collection-item">
                  <p class="center">
                    <label>
                      <input type="checkbox" class="filled-in" checked="checked" />
                      <span>Mleko dsaasss</span>
                    </label>
                  </p>
                </li>
              </ul>
              <a class="btn-floating halfway-fab waves-effect waves-light grey darken-1">
                <i class="material-icons">edit</i>
              </a>
            </div>
          </div>
        </div>
      </div>
      <Pagination />
    </div>
  </main>
</template>

<script>
import Search from "@/components/Search";
import Pagination from "@/components/Pagination";
export default {
  name: "Index",
  data() {
    return {};
  },
  components: {
    Search,
    Pagination
  }
};
</script>

<style>
.card {
  margin: 6rem 0;
}

.collection {
  margin: 2rem 0;
}

.card .card-content {
  padding-left: 0;
  padding-right: 0;
}

.task-done {
  text-decoration: line-through;
}

.task-date {
  color: #757575;
  font-size: 0.8rem;
  text-align: center;
}
</style>

