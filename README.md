# shopping-list-vue-spring

[![CircleCI](https://circleci.com/gh/sgasior/shopping-list-vue-spring/tree/master.svg?style=shield)](https://circleci.com/gh/sgasior/shopping-list-vue-spring/tree/master)
[![versionjava](https://img.shields.io/badge/jdk-11-brightgreen.svg?logo=java)](https://openjdk.java.net)
[![versionspringboot](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/sgasior/shopping-list-vue-spring/master/pom.xml&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&label=springboot)](https://github.com/spring-projects/spring-boot)
[![versionvuejs](https://img.shields.io/badge/dynamic/json?color=brightgreen&url=https://raw.githubusercontent.com/sgasior/shopping-list-vue-spring/master/frontend/package.json&query=$.dependencies.vue&label=vue&logo=vue.js)](https://vuejs.org/)
[![versionnodejs](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/sgasior/shopping-list-vue-spring/master/frontend/pom.xml&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27build%27%5D%2F%2A%5Blocal-name%28%29%3D%27plugins%27%5D%2F%2A%5Blocal-name%28%29%3D%27plugin%27%5D%2F%2A%5Blocal-name%28%29%3D%27executions%27%5D%2F%2A%5Blocal-name%28%29%3D%27execution%27%5D%2F%2A%5Blocal-name%28%29%3D%27configuration%27%5D%2F%2A%5Blocal-name%28%29%3D%27nodeVersion%27%5D&label=nodejs&logo=node.js)](https://nodejs.org/en/)
[![versionaxios](https://img.shields.io/badge/dynamic/json?color=brightgreen&url=https://raw.githubusercontent.com/sgasior/shopping-list-vue-spring/master/frontend/package.json&query=$.dependencies.axios&label=axios)](https://github.com/axios/axios)

## First App run

Inside the root directory, do a: 

```
mvn clean install
```

Run our complete Spring Boot App:

```
mvn --projects backend spring-boot:run
```

Now go to http://localhost:8088/ and have a look at started app.


## App documentation

Documentation (created using Rest DOCS) can be found on paths:

* `OwnerController` => /doc/owner
* `TaskController` => /doc/task
* `ProductController` => /doc/product

## Webpack-dev-server

Webpack-dev-server is preconfigured in Vue.js out-of-the-box. Only thing needed to do is cd into `frontend`
and run:

```
npm run serve
```

