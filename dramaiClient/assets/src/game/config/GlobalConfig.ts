import { log, Node } from "cc";
import { network } from "../../model/RequestData";
import { modelMgr } from "../App";
import { catCfgD } from "./DataStruct";

export class GlobalConfig {
    static _instance: GlobalConfig;
    /**
     * 保存项目运行时状态
     */

    isProduction: boolean = false

    jsonData: {};
    public hasLogin: boolean = false;
    hasInitGame: boolean = false;
    LoginData: network.LoginResponse;
    hasJsonsLoad: boolean = false;
    haSelect: boolean;
    currentChatTimes: number = 0
    // curSelCat: network.SelectCatResponse;//首次登录使用
    public nickName = "";
    public playername = "";
    public userId = "";
    public curCatData:catCfgD;
    public loginType = 0;
    catInRange: boolean = true;
    catRootNode: Node = new Node();
    testTools: boolean = false;
    currentNpcIndex: any = 0;
    isDebug:boolean = false;
    isCreatePlayer:boolean = false;
    playerCreateTime = 0;
    playerEndTime = 0;
    CreateIDArr = {};
    isStoryModel = false;
    nowSceneData = null;
    chooseNpc = null;
    chooseScene = null;
    twitterData = [];
    static get instance() {
        if (this._instance) {
            return this._instance;
        }

    this._instance = new GlobalConfig();
        this._instance.start();
       this._instance.nickName = this._instance.playername = this._instance.userId = "jack13";
        return this._instance;
    }
    start() {
        this.jsonData = {
            "userId": ""
        };
    }
    getGlobalData(key: string) {
        return this.jsonData[key];
    }
    setGlobalData(key: string, value: any) {
        this.jsonData[key] = value;
    }
    
}