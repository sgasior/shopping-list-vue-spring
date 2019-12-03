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


}