import { test_userdata } from "../game/config/AxiosRequestConfig";

export default class AxiosManager {
    public static axios;
    public static instance;
    public static BaseUrl;
    /**
     * content-type 类型
     */
    public static CONENTTYPE = {
        FORMDATA: "multipart/form-data",
    }
    static userData: any;
    /**
     * 创建实例
     */
    public static init() {
        this.axios = window["Axios"]||window["axios"];
        const currentURL = window.location.href;
        if (currentURL.indexOf("localhost") != -1||currentURL.indexOf("192.168") != -1) {
            // this.BaseUrl = "http://13.214.33.171:8080";
            this.BaseUrl = "http://192.168.254.88:8080";
        } else {
            this.BaseUrl = "http://13.214.33.171:8080"; 
        }
        this.instance = null;
        if (this.axios) {
            this.instance = this.axios.create({
                'Content-Type': 'application/json;charset=UTF-8',
                json: true,
                baseURL: this.BaseUrl,
                timeout: 30000,
            });
            // 添加响应拦截器
            this.instance.interceptors.response.use(
                function (response) {//如果这里注册，那么这里先处理，然后再处理.then()
                    // 2xx 范围内的状态码都会触发该函数。
                    // console.log('aa', response);
                    const res = response.data;
                    // 对响应数据做点什么
                    console.warn("axios response use", res);
                    return res;
                },
                function (error) {
                    // 超出 2xx 范围的状态码都会触发该函数。
                    // 对响应错误做点什么
                    console.warn("axios response use error", error);
                    return Promise.reject(error);
                }
            );
        }
    }/**
     * post 请求
     */
    public static post(url, data: any, type = "", sucessfunc: Function = null, failfunc: Function = null) {

        if (!this.instance) {
            console.warn("没有创建axios 实例");
            return;
        }
        if (type == this.CONENTTYPE.FORMDATA) {
            let formdata;
            if (data instanceof FormData) {
                formdata = data;
            } else {
                //暂不可用
                let formdata = new FormData();
                for (const key in data) {
                    formdata.append(key, data[key]);
                }
            }
            let headers = {
                'Content-Type': 'multipart/form-data', json: true,
                baseURL: this.BaseUrl,
                timeout: 30000,
                Authorization:this.userData
            };
            this.instance.post(`${this.BaseUrl}${url}`, formdata, { headers: headers }).then(function (promise) {
                this.userData = promise;
                if (sucessfunc) {
                    sucessfunc(promise);
                }
                console.warn(promise);
            }.bind(this)).catch(function (err) {
                if (failfunc) failfunc(err);
                console.warn(err);
            })
        } else {
            let head = {
                'Content-Type': 'application/json;charset=UTF-8',
                json: true,
                baseURL: this.BaseUrl,
                timeout: 30000,
                Authorization:AxiosManager.userData?"Bearer " +AxiosManager.userData.access_token:"Bearer " +test_userdata.access_token
            }
            // let datas = window["Qs"].stringify(data);//
            this.instance.post(`${url}`, data,{headers:head}).then(function (promise) {
                this.userData = promise;
                if (sucessfunc) {
                    sucessfunc(promise);
                }
                console.warn(promise);
            }.bind(this)).catch(function (err) {
                if (failfunc) failfunc(err);
                console.warn(err);
            });
        }
    }
    /**
     * 
     * @param params 
     * @returns 
     */
    public static async get(url,params: object) {
        return await this.instance.get(`${this.BaseUrl}${url}`, {
            params: params
        });
    }
}