import axios from 'axios'
export class APIService {

    constructor() {

    }

    getTasks() {
        const url = `/api/v1/owner/e190095d-2507-4e28-9646-2ae7f618ca13/task`;
        return axios
            .get(url)
            .then(response => response.data._embedded.taskDtoList);
    }

    getProducts(taskNumber) {
        const url = `/api/v1/owner/e190095d-2507-4e28-9646-2ae7f618ca13/task/${taskNumber}/product`;
        return axios
            .get(url)
            .then(response => response.data._embedded.productDtoList);
    }

    deleteTask(taskNumber){
        const url = `/api/v1/owner/e190095d-2507-4e28-9646-2ae7f618ca13/task/${taskNumber}`;
        return axios.delete(url);
    }


}