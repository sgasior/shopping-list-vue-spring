import axios from 'axios'
export class APIService {

    constructor() {

    }

    getTasks(ownerId) {
        const url = `/api/v1/owner/${ownerId}/task`;
        return axios
            .get(url)
            .then(response => response.data._embedded.taskDtoList);
    }

    getProducts(ownerId, taskNumber) {
        const url = `/api/v1/owner/${ownerId}/task/${taskNumber}/product`;
        return axios
            .get(url)
            .then(response => response.data._embedded.productDtoList);
    }

    deleteTask(ownerId, taskNumber) {
        const url = `/api/v1/owner/${ownerId}/task/${taskNumber}`;
        return axios.delete(url);
    }

    async createOwner(name) {
        const url = `/api/v1/owner`;
        return axios.post(url, {
            name: name
        });
    }

    async saveTaskInOwner(ownerId, taskToBeSaved) {
        const url = `/api/v1/owner/${ownerId}/task`;
        return axios.post(url, {
            taskTitle: taskToBeSaved.taskTitle,
            isDone: taskToBeSaved.isDone,
            hexColor: taskToBeSaved.hexColor,
        });
    }

    async saveProductInTask(ownerId, taskNumber, productToBeSaved) {
        const url = `/api/v1/owner/${ownerId}/task/${taskNumber}/product`;
        return axios.post(url, {
            name: productToBeSaved.name,
            isDone: productToBeSaved.isDone
        });
    }

    async updateTask(ownerId, taskNumber, updatedTask) {
        console.log(ownerId);
        console.log(taskNumber);
        console.log(updatedTask);
        const url = `/api/v1/owner/${ownerId}/task/${taskNumber}`;
        return axios.put(url, {
            taskTitle: updatedTask.title,
            hexColor: updatedTask.color,
            isDone: updatedTask.isDone
        });
    }

    async updateProduct(ownerId, taskNumber, updatedProduct, productNumber) {
        const url = `/api/v1/owner/${ownerId}/task/${taskNumber}/product/${productNumber}`;
        return axios.put(url, {
            name: updatedProduct.name,
            isDone: updatedProduct.isDone
        });
    }

    deleteProduct(ownerId, taskNumber, productNumber) {
        const url = `/api/v1/owner/${ownerId}/task/${taskNumber}/product/${productNumber}`;
        return axios.delete(url);
    }

}