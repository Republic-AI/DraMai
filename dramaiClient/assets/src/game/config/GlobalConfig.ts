import { director, log, Node } from "cc";
import { network } from "../../model/RequestData";
import { modelMgr, observer, socket } from "../App";
import { catCfgD } from "./DataStruct";
import { EventType } from "../../EventType";
import { NpcRoomIndex } from "../../StaticUtils/NPCConfig";
import WebUtils from "../../utils/WebUtils";

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
    chatRecord = null;
    roomDataList = null;
    bulletMsgCDTime = 0;
    bulletMsgCreateTime = 0;
    address = "";
    goldNum = 0;
    isWebFrame = false;
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
        console.log("webEventManager start");
        window.addEventListener('message', (event) => {
            // 安全检查：确认消息来源
            if (event.origin !== window.location.origin) {
              console.warn(' sam Test Received message from unknown origin:', event.origin);
              return;
            }
            
            // 处理消息
            const message = event.data;
            console.log('sam Test Received message from parent:' + JSON.stringify(message));
            if(message.data && message.data.action && message.data.action == "navigate"){
                console.log("sam Test navigate11111=====");
                if(this.isWebFrame){
                    console.log("sam Test navigate22222=====");
                    if(message.data.target){
                        console.log("sam Test navigate 33333333=====" + message.data.target);
                        let npcId = Number(message.data.target) > 10000 ? Number(message.data.target) : 0;
                        if(npcId){
                            console.log("sam Test navigate44444=====" +npcId);
                            let npcSceneId = NpcRoomIndex[npcId];
                            if(npcSceneId == GlobalConfig.instance.chooseScene){
                                console.log("sam Test webframe followNpc=====" + npcId);
                                observer.post(EventType.FOLLOWNPC,npcId);
                            }
                            else{
                                console.log("sam Test navigate555555555=====" + npcSceneId);
                                console.log("sam Test navigate666666666=====" + npcId);
                                WebUtils.showMaskNode();
                                let json = new network.GetAllNPCRequest();
                                json.command = 10012;
                                json.type = 1;
                                json["data"] = {};
                                json["data"]["roomId"] = npcSceneId;
                                GlobalConfig.instance.chooseNpc = npcId
                                GlobalConfig.instance.chooseScene = npcSceneId;
                                socket.sendWebSocketBinary(json);
                            }
                            return;
                        }

                        let sceneId = Number(message.data.target) < 10 ? Number(message.data.target) : 0;
                        if(sceneId){
                            console.log("sam Test navigate777777777=====" + sceneId);
                            if(sceneId == GlobalConfig.instance.chooseScene){
                                
                            }
                            else{
                                console.log("sam Test navigate888888888=====" + sceneId);
                                WebUtils.showMaskNode();
                                let json = new network.GetAllNPCRequest();
                                json.command = 10012;
                                json.type = 1;
                                json["data"] = {};
                                json["data"]["roomId"] = sceneId;
                                GlobalConfig.instance.chooseNpc = null
                                GlobalConfig.instance.chooseScene = sceneId;
                                socket.sendWebSocketBinary(json);
                            }
                            return;
                        }
                    }
                }
            }
            // switch (message.type) {
            //   case 'INIT_SCENE':
            //     // 初始化场景
            //     const sceneId = message.data.sceneId;
            //     initGameScene(sceneId);
            //     break;
                
            //   case 'TEST_ACTION':
            //     // 执行测试动作
            //     performAction(message.data.action);
            //     break;
                
            //   // 更多消息类型处理...
            // }
          });
    }
    getGlobalData(key: string) {
        return this.jsonData[key];
    }
    setGlobalData(key: string, value: any) {
        this.jsonData[key] = value;
    }

    setBulletMsgCDTime(time:number){
        this.bulletMsgCDTime = time;
        if(time > 0){
            this.bulletMsgCreateTime = new Date().getTime();
        }
    }

    getBulletMsgCDTime(){
        let nowTime = new Date().getTime();
        if(nowTime - this.bulletMsgCreateTime >= this.bulletMsgCDTime){
            return 0;
        }
        else{
            let time = this.bulletMsgCDTime - (nowTime - this.bulletMsgCreateTime);
            return time;
        }
    }

    getBulletMsgCreateTime(){
        return this.bulletMsgCreateTime;
    }
    
}