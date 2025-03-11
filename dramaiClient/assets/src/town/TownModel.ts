import * as _ from "lodash-es";
import { EventType } from "../EventType";
import BaseModel from "../core/base/BaseModel";
import { showMsg2 } from "../core/message/MessageManager";
import { mduManger, modelMgr, observer, socket } from "../game/App";
import { NPCServerD, itemServerD, taskcell } from "../game/config/DataStruct";
import { GlobalConfig } from "../game/config/GlobalConfig";
import RedManager from "../manager/RedManager";
import { network } from "../../src/model/RequestData";
import StaticConfigModel from "../../src/model/StaticConfig/StaticConfigModel";
import { ModuleID } from "../game/config/ModuleID";
import { director } from "cc";
import { GuideIndex } from "../../src/model/StaticTextConfig";
import Log from '../../../assets/src/utils/LogUtils'

const TAG = 'TownModel'
export default class TownModel extends BaseModel {

    playAniComplete: boolean = true
    taskmaodan: number;
    chatIsediting: boolean = false
    hasToySpine: boolean = false;
    firstIn: boolean = false ;
    constructor() {
        super();
    }
    init() {
        observer.on(EventType.SOCKET_ONMESSAGE, this.onMessage, this);
    }
    /**
     * 获取cats server data
     * @param id 
     * @returns 
     */
    getServerCatsData(id: number): NPCServerD {
        if (this._NPCsSeverData?.data) {
            let da: any[] = this._NPCsSeverData.data;
            for (let index = 0; index < da.length; index++) {
                const element = da[index];
                if (element.id == id) {
                    return element;
                }
            }
        }
        return null;
    }
    /**
     * 添加新猫咪
     */
    chgServerCatsData(cat: NPCServerD) {

        if (this._NPCsSeverData?.data) {
            let curCats = this.getServerCatsData(cat.id);
            if (curCats) {
                for (const key in curCats) {
                    if (Object.prototype.hasOwnProperty.call(curCats, key)) {
                        curCats[key] = cat[key];
                    }
                }
            } else {
                this._NPCsSeverData?.data.push(cat);
            }
        }
    }
    /**
     * tab 当前页提示
     */
    currentTapIndex: any = {
        "mainbottom": 2,
        "gedantab": 0,
        "jishi": 0,
        "jishi_goumai": 0,
        "jishi_chushou": 0,
        "renwuab": 0,
        "pre_renwutab": 0,
        "pre_tujiantab": 0,
        "pre_tujianrwdtab": 0,
        "qingdantab": 0,
        "tujianrdtab": 0,
        "pre_Tujianrand": 0,
        "pre_emailtab": 0,
        "pre_dongtaitab": 0,
        "tujian_pets": 0,
        "shop": 0,

    };
    goumaitishi: number;
    /**
     * frameId:头像框
     * uid:用户uid,
     * done:登录奖励是否领取
     * level:等级
     * redVipLevel: 会员等级
     * done: 是否已领取登录奖励
     * gold: 云贝数量
     * avatarUrl 用户头像 
     * redVipExpire:用户会员到期时间
     */
    userinfo: {
        frameId: string,
        uid: number,
        done: boolean,
        level: number,
        redVipLevel: number,
        gold: number,
        avatarUrl: string,
        redVipExpire: number,
    } = {
            frameId: "dff",
            uid: 121,
            done: false,
            level: 11,
            redVipLevel: 1,
            gold: 10,
            avatarUrl: "",
            redVipExpire: 11,
        };

    /**
     * 根据道具id获取道具数量
     * @param id 
     * @returns 
     */
    public getserItemCountById(id: number) {
        let da = this.playerItems?.data;
        if (da && da.length > 0) {
            for (let index = 0; index < da.length; index++) {
                const element = da[index];
                if (element.goodsId == id) {
                    return element.count;
                }
            }
        } else {
            return 0;
        }
        return 0;
    }
    /**
        * 根据道具id获取道具
        * @param id 
        * @returns 
        */
    public getserItemById(id: number): itemServerD {
        let da = this.playerItems?.data;
        if (da && da.length > 0) {
            for (let index = 0; index < da.length; index++) {
                const element = da[index];
                if (element.goodsId == id) {
                    return element;
                }
            }
        } else {
            return null;
        }
        return null;
    }
    private updateTasklist(da: taskcell) {
        if (this?.tasklist?.data) {
            for (let index = 0; index < this?.tasklist?.data.length; index++) {
                let element = this?.tasklist?.data[index];
                if (element.taskId == da.taskId) {
                    element.status = da.status;
                }
            }
            RedManager.instance.setkeyState("renwutabred", this.tasklist);
        }
    }

    public getTaskserById(taskid: number): taskcell {
        if (this?.tasklist?.data) {
            for (let index = 0; index < this?.tasklist?.data.length; index++) {
                let element = this?.tasklist?.data[index];
                if (element.taskId == taskid) {
                    return element;
                }
            }
        }
        return null;
    }

    //======================================
    public checkcatReq() {
        let json = new network.CheckCatsRequest();
        socket.sendWebSocketBinary(json);
    }

    refreshItemDate(){
        let json = new network.itemUpdateResponse();
        json.command = 10009
        json.code = 1
        json.requestId = 1719819694942
        socket.sendWebSocketBinary(json);
    }

    public changecatInfoReq(catId: number, name: string, userName: string, career) {
        let json = new network.changeCatInfoRequest();
        json.data.catId = catId;
        json.data.name = name;
        json.data.userName = userName;
        json.data.career = career
        socket.sendWebSocketBinary(json);
    }
    public changecatReq(catid: number) {
        let json = new network.ChangeCatsRequest();
        json.data.catId = catid;
        socket.sendWebSocketBinary(json);
    }
    public checkplayerItems() {
        let json = new network.checkPlayerItemsRequest();
        socket.sendWebSocketBinary(json);
    }
    public chatReq(catid: number, str: string) {
        // let json = new network.chatRequest();
        // json.data.catId = catid;
        // json.data.context = str;
        // socket.sendWebSocketBinary(json);
    }
    public chatlistReq(catid: number, pageNum: number) {
        // let json = new network.chatListRequest();
        // json.data.catId = catid;
        // json.data.pageNum = pageNum;
        // socket.sendWebSocketBinary(json);
    }
    public tasklistReq() {
        let json = new network.taskListRequest();
        socket.sendWebSocketBinary(json);
    }

    public taskgetReq(taskid: number) {
        let json = new network.taskgetRequest();
        json.data.taskId = taskid;
        socket.sendWebSocketBinary(json);
    }
    public shareReq() {
        let json = new network.shareRequest();
        socket.sendWebSocketBinary(json);
    }
    public qiandaoReq() {
        let json = new network.qiandaoRequest();
        socket.sendWebSocketBinary(json);
    }

    public yindaoReq(guideid: number) {
        let json = new network.yindaoRequest();
        json.data.guide = guideid;
        socket.sendWebSocketBinary(json);


    }
    public zhaohuReq() {
        let json = new network.zhaohuRequest();
        socket.sendWebSocketBinary(json);
    }

    public getShopList(){
        let shopListParameter = new network.shopListRequest
        socket.sendWebSocketBinary(shopListParameter)
    }

    public playWithCat(catId: number, goodsId: number, count: number){
        // let playWithCat = new network.PlayWithCatRequest
        // playWithCat.data.catId = catId
        // playWithCat.data.count = count
        // playWithCat.data.goodsId = goodsId
        // socket.sendWebSocketBinary(playWithCat)
    }

    public touchCat(){
        // let touchWithCat = new network.TouchCatRequest
        // touchWithCat.data.catId = GlobalConfig.instance.getCurCatId()
        // socket.sendWebSocketBinary(touchWithCat)
    }

    private _NPCsSeverData: network.CheckCatsResponse;
    public playerItems: network.checkPlayerItemsResponse;
    public tasklist: network.taskListResponse;
    public qiandaoResp: network.qiandaoResponse;
    public onMessage(da: any) {
        let repData: network.InetwarkResponseData = da.data;
        if (repData.command == 99998) {
            mduManger.closeModu(ModuleID.TOWN);
            let load = director.loadScene("login", (error: Error, scene: any) => {
                Log.log(TAG,error, scene);
            });
            if (load) {
                //showMsg2(modelMgr.configModel.getStrById(99998));
            }
            return;
        }
        if (repData.command == 10002) {//查询NPC信息
            this._NPCsSeverData = da.data;
            console.log("npcData=====" + JSON.stringify(this._NPCsSeverData));
            observer.post(EventType.SOCKET_GETALL_NPCS, this._NPCsSeverData);
        } else if (repData.command == 10006) {//修改npc信息
            // let catinfo = this.getServerCatsData(da.data.data.cat.id);
            // for (const key in catinfo) {
            //     if (Object.prototype.hasOwnProperty.call(catinfo, key)) {
            //         catinfo[key] = da.data.data.cat[key];
            //         catinfo.career = da.data.data.cat?.career
            //     }
            // }
            observer.post(EventType.SOCKET_NPC_MOVE, da.data.data);
        }
        else if (repData.command == 10007) {//npc action
           // observer.post(EventType.SOCKET_NPC_ACTION, da.data);
        } 
        else if (repData.command == 10004) {//上线信息推送
            //const cati = da.data.data[0];
            //this.chgServerCatsData(cati);
            observer.post(EventType.SOCKET_ONLINE_NPCS, da.data.data);
        } else if (repData.command == 10005) {//下线选角
            // GlobalConfig.instance.curSelCat = da.data;
            //GlobalConfig.instance.setCurCatId(da.data.data.cat.id);
            observer.post(EventType.SOCKET_OFFLINE_NPCS, da.data.data);
        } else if (repData.command == 10011) {//npc farm行为
            this.playerItems = da.data;
            observer.post(EventType.SOCKET_ITEM_STATE_CHANGE, da.data.data);

        } else if (repData.command == 10019) {//切换猫咪
            //GlobalConfig.instance.setCurCatId(Number(da.data.data.catId));
            observer.post(EventType.SOCKET_CHANGE_CATS);
        } else if (repData.command == 10016) {
        } else if (repData.command == 10017) {
            observer.post(EventType.SOCKET_CHATLIST, da.data);
            //GlobalConfig.instance.currentChatTimes = da.data.data.chats.filter(obj => obj.to === GlobalConfig.instance.getCurCatId()).length
        } else if (repData.command == 10012) {
            this.tasklist = da.data;
            RedManager.instance.setkeyState("renwutabred", this.tasklist);
            observer.post(EventType.SOCKET_TASKLIST, da.data);
        } else if (repData.command == 10010) {//任务状态改变
            let lasttask: taskcell = _.clone(this.getTaskserById(da.data.data[0].taskId));
            this.updateTasklist(da.data.data[0]);
            let obserdata = { last: lasttask, cur: da.data.data[0] };
            observer.post(EventType.SOCKET_TASK_CHANGE, obserdata);
        } else if (repData.command == 10013) {//任务领取
            let task = this.getTaskserById(da.data.data.taskIdList[0]);
            task.status = 3;
            // this.updateTasklist();
            RedManager.instance.setkeyState("renwutabred", this.tasklist);
            let taskconf = modelMgr.configModel.getConfigById(StaticConfigModel.TaskCfg, task.taskId);
            let dropconfig = modelMgr.configModel.getConfigById(StaticConfigModel.DropsCfg, taskconf.taskReward);
            if (task.taskId == 10003 || task.taskId == 10005 || task.taskId == 30001) {
                let catids = da.data.data.catIds;
                if (catids && catids[0]) {
                    this.taskmaodan = catids[0];
                    observer.post("showcategg", catids[0]);
                } else {//猫蛋转金币是10个金币；
                    showMsg2(`Congratulations on receiving 10 Cat Coins.`)
                }
            } else {
                showMsg2(dropconfig.showmsg);
            }
            // if (da.data.goodsDataList && da.data.goodsDataList.length) {
            //     for (let index = 0; index < da.data.goodsDataList.length; index++) {
            //         const element: itemServerD = da.data.goodsDataList[index];
            //         let goods = this.getserItemById(element.goodsId);
            //         if (goods) {
            //             goods = { ...element };
            //         }
            //     }
            // }
            //猫蛋特殊处理

            observer.post(EventType.SOCKET_TASK_GET, da.data);
        } else if (repData.command == 10015) {//分享
            observer.post(EventType.SOCKET_SHARE, da.data);
        } else if (repData.command == 10014) {//签到
            this.qiandaoResp = da.data;
            observer.post(EventType.SOCKET_QIANDAO, da.data);
        } else if (repData.command == 10009) {//道具更新
            observer.post(EventType.SOCKET_ITEMUPDATE, da.data.data);
        } else if (repData.command == 10022) {//打招呼
            // const guideInfo = GlobalConfig.instance.LoginData.data.player.guide
            // if(GlobalConfig.instance.LoginData.data.player.chatNum >= 3 && !guideInfo?.find?.(element => element === GuideIndex.START_GUIDE_STATE5.end)){
            //     //触发第五阶段引导
            //     observer.post(EventType.UPDATE_GUIDE_INFO_MAIN_VIEW, GuideIndex.START_GUIDE_STATE5)
            //     observer.post(EventType.SHOW_CHAT_LOG_BUTTON)
            // }
            observer.post(EventType.SOCKET_ZHAOHU, da.data);
        }
        else if(repData.command == 10024){ //商店列表
            //observer.post(EventType.SOCKET_SHOP_ITEM_LIST, da.data)
        }
        else if(repData.command == 10008){ //商店列表
            observer.post(EventType.SOCKET_MOVE, da.data)
        }
        else if(repData.command == 10025){ //商店列表
            // observer.post(EventType.SEND_BUY_SHOP_ITEM_SUCCESS_INFO, da.data)
            // this.checkplayerItems()
        }

        else if(repData.command == 10027){ //使用逗猫玩具
            //observer.post(EventType.SEND_BUY_SHOP_ITEM_SUCCESS_INFO, da.data)
            //this.checkplayerItems()
            Log.log(TAG, da.data)
        }

        else if(repData.command == 10026){ //抚摸猫
            //observer.post(EventType.SEND_BUY_SHOP_ITEM_SUCCESS_INFO, da.data)
            // //this.checkplayerItems()
            // Log.log(TAG,'play with cat', da.data)
            // observer.post(EventType.UPDATE_CAT_CV, da.data)
        }

        else if(repData.command == 10028){ //创建打工活动
            // Log.log(TAG,'create work active', da.data)
            // if(da.data.code !== 0){
            //     observer.post(EventType.WORK_DEFAULT)
            // }
            //WorkCatModel.currentWorkId = da?.data?.data?.workId
        }

        else if(repData.command == 10029){ //领取打工奖励
            //Log.log(TAG,'work reward', da.data)
        }

        else if(repData.command == 10030){ //打工数据推送
            // Log.log(TAG,'work result', da.data)
            // observer.post(EventType.WORK_STATE_INFO, da.data)
        }

    }

}