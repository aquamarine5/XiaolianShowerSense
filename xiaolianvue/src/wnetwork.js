import axios from 'axios';
import { ElNotification } from 'element-plus';

const service = axios.create({
    baseURL: import.meta.env.VITE_APIHOST,
    timeout: 5000
});

service.interceptors.response.use(
    response => {
        if (response.data.status === 'err') {
            ElNotification({
                title: '错误',
                message: response.data.message || '发生未知错误',
                type: 'error',
            });
            return Promise.reject(new Error(response.data.message || '发生未知错误'));
        }
        return response;
    },
    error => {
        console.log(error);
        ElNotification({
            title: error.message,
            message: error.response.data.message || '发生未知错误',
            type: 'error',
        });
        return Promise.reject(error);
    }
);

export default class wnetwork {
    static get(url, params) {
        return service.get(url, { params });
    }

    static post(url, data) {
        return service.post(url, data);
    }
}