// import { delMsg } from "../../core/message/MessageManager";
// import { post_bozhong, post_sellplant, post_sellplplant, post_sellplseed, post_sellseed, post_shifei, post_shopbuy, post_shouhuo } from "./AxiosRequestConfig";

// export class MessageConfig {
//     static _instance: MessageConfig;
//     static get instance () {
//         if (this._instance) {
//             return this._instance;
//         }

//         this._instance = new MessageConfig();
//         this._instance.start();
//         return this._instance;
//     }
//     start () {
//         this.configArr["const"] = {
//             key_1:"等级不足，不能购买未解锁的商品",
//             key_2:"已达到限购数量，不能购买",
//             notfound:"请求异常,稍后再试~",
//         };
//         this.configArr[post_shopbuy] = {
//             "lack":"金币余额不足",
//             "illegal":"等级不足，不能购买未解锁的商品",
//             "limited":"已达到限购数量，不能购买",
//         }
//         this.configArr[post_bozhong] = {
//             "locked":"地块被锁，不允许播种",
//             "occupy":"地块已被种植，不允许再次播种",
//             "lack":"没有该种子，不允许播种",
//         }
//         this.configArr[post_shouhuo] = {
//             "forbidden":"地块未成熟",
//         }
//         this.configArr[post_shifei] = {
//             "occupy":"地块已被施肥，不允许再次施肥",
//             "lack":"缺乏对应道具",
//         }
//         this.configArr[post_sellplant] = {
//             "lack":"用户可售植物不足",
//             "illegal":"用户没有该植物可卖",
//         }
//         this.configArr[post_sellplplant] = {
//             "lack":"用户可售植物不足",
//             "illegal":"用户没有该植物可卖",
//         }
//         this.configArr[post_sellseed] = {
//             "lack":"用户可售种子不足",
//             "illegal":"用户没有该种子可卖",
//         }
//         this.configArr[post_sellplseed] = {
//             "lack":"用户可售种子不足",
//             "illegal":"用户没有该种子可卖",
//         }
//     }
//     private configArr:any[] = [];

//     public getConfig(url:string,msg:string){
//         let str:string = "";
//         str =this.configArr[url][msg];
//         if(!str){
//             str = msg;
//         }
//         return str;
//     }

//     /**
//      * 
//      * @param key  
//      * @returns 
//      */
//     public getConstConfig(key:string){
//         let str:string = "";
//         str =this.configArr["const"][key];
//         if(!str){
//             str = "待配置文本"+key;
//         }
//         return str;
//     }

// }