<template>
    <ul class="pagination center">
      <li class="disabled">
        <a href="#!">
          <i class="material-icons">chevron_left</i>
        </a>
      </li>
      <li class="active">
        <a href="#!">1</a>
      </li>
      <li class="waves-effect">
        <a href="#!">2</a>
      </li>
      <li class="waves-effect">
        <a href="#!">3</a>
      </li>
      <li class="waves-effect">
        <a href="#!">4</a>
      </li>
      <li class="waves-effect">
        <a href="#!">5</a>
      </li>
      <li class="waves-effect">
        <a href="#!">
          <i class="material-icons">chevron_right</i>
        </a>
      </li>
    </ul>
</template>

<script>
export default {
    
}
</script>


<style>
    .pagination{
        margin-top: 6rem;
    }

    .pagination li.active{
        background-color: #e6a400;
    }
</style>