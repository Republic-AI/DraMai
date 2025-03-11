import { resources, assetManager, JsonAsset, error, log } from "cc";
import * as _ from "lodash-es";
import BaseModel from "../../core/base/BaseModel";
import ItemCData from "./ItemCData";
import { GlobalConfig } from "../../game/config/GlobalConfig";
import { catCfgD } from "../../game/config/DataStruct";
import Log from '../../../../assets/src/utils/LogUtils'

const TAG = 'StaticConfigModel'
export default class StaticConfigModel extends BaseModel {
    // private _commonconfig = "http://192.168.95.67:8000/config.json";
    // private _commonconfig = "https://web-static.baochuangames.com/nos/image/plantflowergames/config.json";
    private _commonconfig = "https://d1.music.126.net/dmusic/obj/w5zCg8OAw6HDjzjDgMK_/24055495632/2cdc/6e09/89e9/e9f5817449174b3672862dc3bd1dfb7d.json?download=config.json";

    private _commonConfigArr: any = {
        Cat: [],
        TaskCfg: [],
        DropsCfg: [],
        ItemCfg: [],
        SysParameter: [],
        ConstCfg:[]
    };

    /**
     * id:key
     */
    private _commonConfigJson:any = {
        Cat: Object,
        TaskCfg: Object,
        DropsCfg: Object,
        ItemCfg: Object,
        SysParameter: Object,
        ConstCfg:Object
    };
    
    public  static readonly  Cat = "Cat";
    public  static readonly  TaskCfg = "TaskCfg";
    public  static readonly  DropsCfg = "DropsCfg";
    public  static readonly  ItemCfg = "ItemCfg";
    public  static readonly  ConstCfg = "ConstCfg";
    public  static readonly  SysParameter = "SysParameter";
    public get commonConfigJson() {
        return this._commonConfigArr;
    }
    public getCatDataById(catid:number):catCfgD{
        let arr: any[] = this.commonConfigJson.Cat;
        for (let index = 0; index < arr.length; index++) {
            const element = arr[index];
            if(element.id==catid){
                return element;
            }
        }
        return null;
    }

    public getDropConfigById(dropid:number){
        let arr: any[] = this.commonConfigJson.DropsCfg;
        for (let index = 0; index < arr.length; index++) {
            const element = arr[index];
            if(element.id==dropid){
                return element;
            }
        }
        return null;
    }

    public getConfigById(configname:string ,id:number){
        let arr: any[] = this.commonConfigJson[configname];
        for (let index = 0; index < arr.length; index++) {
            const element = arr[index];
            if(element.id==id){
                return element;
            }
        }
        return null;
    }
    /**
     * 远程加载
     * @param name 
     * @param callback 
     */
    public loadconfig(name: string = null, callback: Function) {
        if (!name) {
            name = this._commonconfig
        }
        assetManager.loadRemote<JsonAsset>(name, (err: any, res: JsonAsset) => {
            if (err) {
                error(err.message || err);
                return;
            }
            // 获取到 Json 数据
            this._commonConfigArr = res.json!;
            if (callback) {
                callback();
            }
        })
        // resources.load('gameGiftJson', (err: any, res: JsonAsset) => {
        //     if (err) {
        //         error(err.message || err);
        //         return;
        //     }
        //     // 获取到 Json 数据
        //     const jsonData: object = res.json!;

        // })
    }
    /**
     *  用于管理所有在 assets/resources 下的资源。
     * @param name 
     * @param callback 
     */
    public loadconfiglocal(name: string = null, callback: Function) {
        if (!name) {
            name = this._commonconfig
        }
        resources.load<JsonAsset>(name, (err: any, res: JsonAsset) => {
            if (err) {
                error(err.message || err);
                return;
            }
            // 获取到 Json 数据
            this._commonConfigArr = res.json!;
            if (callback) {
                callback();
            }
        })
        // resources.load('gameGiftJson', (err: any, res: JsonAsset) => {
        //     if (err) {
        //         error(err.message || err);
        //         return;
        //     }
        //     // 获取到 Json 数据
        //     const jsonData: object = res.json!;

        // })
    }
    /**
     * 加载指定目录里的json配置
     */
    public loadResDir() {
        resources.loadDir('jsons', JsonAsset, (err, assets) => {
            if (err) {
                error(err.message || err);
                return;
            }

            for (let i = 0; i < assets.length; ++i) {
                const asset = assets[i];
                this._commonConfigArr[asset.name] = asset.json;
                this._commonConfigJson[asset.name] = _.keyBy(asset.json,"id");
                log(asset.name + ' loaded', asset);
                // 处理加载的 JSON 文件
            }
            GlobalConfig.instance.hasJsonsLoad = true;
        });
    }

    public getItemByid(id: number): ItemCData {
        let item = this._commonConfigArr.items[id + ""];
        if (!item) {
            Log.log("没有找到道具itemid:" + id);
        }
        return item;
    }

    /**
     * 
     * @param obj 
     * @returns 
     */
    public objtoArr(obj: object): any[] {
        let arr = _.values(obj);
        return arr;
    }

    /**
     * 获取系列
     * @param id 
     * @returns 
     */
    public getseriesConfigById(id: number) {
        let item = this._commonConfigArr.series[id + ""];
        if (!item) {
            Log.log("没有找到系列:" + id);
            item = {};
        }
        return item;
    }

    public getGrpsByName(name: string) {
        let grps = this._commonConfigArr[name];
        if (!grps) {
            Log.log("没有找到组:" + name);
            grps = [];
        }
        return grps;
    }

    /**
     * 
     * @param name 
     * @param id 
     * @returns 
     */
    public getconfigByGrpAId(name: string, id: number) {//id==key
        let grps = this._commonConfigJson[name];
        if(!grps){
            Log.log("没有找到组",name);
            return;
        }
        let item = this._commonConfigJson[name][id]
        if (!item) {
            Log.log("没有找到系列:" + id);
            item = {};
        }
        return item;
    }
    public getStrById(id:number):string{
      let strobj =  this._commonConfigJson[StaticConfigModel.ConstCfg][id] ;
      if(strobj&&strobj.en_str){
          return strobj.en_str;
      }else{
        return "未获取到配置："+id;
      }
    }
}


//item
// {
//     "id": 110011,
//     "season": 1,
//     "type": 1,
//     "name": "普通种子",
//     "icon": "https://web-static.baochuangames.com/nos/image/plantflowergames/zzbg.png",
//     "description": "可以种出普通级的花朵"
// }