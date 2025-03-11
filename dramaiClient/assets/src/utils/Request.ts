import crtptos from "../../core/network/http/cryptos";
import { observer } from "../game/App";

export default class Request {
  constructor(){

  }
  /**
   * 
   * @param url 请求链接
   * @param data 请求数据
   * @param type 是否是表单格式
   * @param isEencrypt 是否需要加密
   */
  public static post(url = '', data = null,type = {isForm:false},isEencrypt =  { isEencrypt: false }) {
    const xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.timeout = 5000;
    console.warn("111111")
    return new Promise((resolve, reject) => {
      xhr.open('post', url, true);
      if(type&&type.isForm == true){
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
      }else{
        xhr.setRequestHeader('Content-Type', 'application/json');
        console.warn("2222222")
        
      }
      // 需要加密
      if (isEencrypt&&isEencrypt.isEencrypt) {
        data = crtptos.encrypt(JSON.stringify(data));
    }
      xhr.onload = function onload() {
        const status: Number = xhr.status !== undefined ? xhr.status : 200;
        console.warn("3333333",status)

        if (status == 200 || status == 204 || status == 0) {
          if (xhr.responseText) {
            try {
              let data ;
              /** 解密处理 看head头部的返回 */
             let encode = xhr.getResponseHeader("encode");
              if (encode && encode.toLowerCase() === 'true') {
                //"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"
                data = JSON.parse(crtptos.decrypt(xhr.responseText));
            }else{
              data = JSON.parse(xhr.responseText);
            }
            console.warn("request load::",data);
              resolve(data);
            } catch (error) {
              console.warn("request load::",error);
              reject(error);
            }
          } else {
            reject();
          }
        } else {
          if(reject) reject();
          console.warn("netRequesterro", xhr);
          // observer.post("netRequesterro", xhr);
        }
      };
      xhr.onerror = (err)=>{
        console.warn("4444444",err);
        if(reject) reject(err);
         observer.post("netRequesterro", xhr);
       };

      if(type&&type.isForm == true){
        xhr.send(data);
      }else{
        if(!data) data = {};
        if (isEencrypt&&isEencrypt.isEencrypt) {
          xhr.send(data);
          console.warn("request send::",data);
        }else{
          console.warn("request stringify send::",data);
          xhr.send(JSON.stringify(data));
        }
       
      }
    });
  }
  public static get(url = '') {
    const xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.timeout = 5000;
    return new Promise((resolve, reject) => {
      xhr.open('get', url, true);
      xhr.onload = function onload() {
        const status: Number = xhr.status !== undefined ? xhr.status : 200;
        if (status == 200 || status == 204 || status == 0) {
          if (xhr.responseText) {
            try {
              const data = JSON.parse(xhr.responseText);
              resolve(data);
            } catch (error) {
              reject();
            }
          } else {
            reject();
          }
        } else {
          reject();
          observer.post("netRequesterro", xhr);
        }
      };
      xhr.onerror = (err)=>{
        console.log("request err:", xhr,"err::",err);
       observer.post("netRequesterro", xhr);
      };
      xhr.send();
    });
  }
}
