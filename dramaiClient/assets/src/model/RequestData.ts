import { NPCServerD, itemServerD, taskcell } from "../game/config/DataStruct";

export module network {



    export interface InetwarkResponseData {
        command: number;
        requestId: number;//发送接收去重--发送时间戳;
        type: number;
        message: string;
        code: number;
    }
    //=========//GM add item
    {

    }
    export class CmdRequest {
        constructor() {
            this.data = {
                "cmd": "additem 10101000 100"
            }
        }
        "requestId" = 1111110;
        "type" = 1;
        "command" = 10023;
        "data": {
            "cmd": string
        }
    }

    //===========================扒拉猫
    export class TouchCatRequest {
        constructor() {
            this.data = {
                "catId": 1
            }
        }
        "requestId" = 1111110;
        "type" = 1;
        "command" = 10026;
        "data": {
            "catId": number;
        }
    }

    //===========================移动信息
    export class NPCMoveRequest {
        constructor() {
            this.data = {
                "x": 0,
                "y": 0,
                "npcId": 0
            }
        }
        "requestId" = 1111110;
        "type" = 1;
        "command" = 10006;
        "data": {
            "x": number,
            "y": number,
            "npcId": number
        }
    }

    //===========================使用玩具逗猫
    export class GetAllNPCRequest {
        // constructor() {
        //     this.data = {
        //         "myNpc": {
        //             "id": 0,
        //             "name": "",
        //             "type": 0,
        //             "model": 0,
        //             "career": "",
        //             "keyword": "",
        //             "hair": 0,
        //             "top": 0,
        //             "bottoms": 0,
        //             "speed": 0,
        //             "x": 0,
        //             "y": 0
        //         },
        //         "otherNpc": [
        //             {
        //                 "id": 0,
        //                 "name": "",
        //                 "type": 0,
        //                 "model": 0,
        //                 "career": "",
        //                 "keyword": "",
        //                 "hair": 0,
        //                 "top": 0,
        //                 "bottoms": 0,
        //                 "speed": 0,
        //                 "x": 0,
        //                 "y": 0
        //             }
        //         ]
        //     }
        // }
        "requestId" = 1719819694942;
        "type" = 1;
        "command" = 10002;
        // "data":{
        //     "myNpc": {
        //         "id": number,
        //         "name": string,
        //         "type": number,
        //         "model": number,
        //         "career": string,
        //         "keyword": string,
        //         "hair": number,
        //         "top": number,
        //         "bottoms": number,
        //         "speed": number,
        //         "x": number,
        //         "y": number
        //     },
        //     "otherNpc": 
        //         {
        //             "id": number,
        //             "name": string,
        //             "type": number,
        //             "model": number,
        //             "career": string,
        //             "keyword": string,
        //             "hair": number,
        //             "top": number,
        //             "bottoms": number,
        //             "speed": number,
        //             "x": number,
        //             "y": number
        //         }[]
        // }
    }

    //===========================创建角色
    export class CreateRoleRequest {
        constructor() {
            this.data = {
                "model": 0,
                "name": "alice",
                "career": "programmer",
                "keyword": "computer",
                "hair": 1,
                "top": 2,
                "bottoms": 3
            }
        }
        "requestId": 1719819694941;
        "type": 1;
        "command": 10001;
        "data": {
            "model": number,
            "name": string,
            "career": string,
            "keyword": string,
            "hair": number,
            "top": number,
            "bottoms": number
        }
    }

    //=================================1.登录接口
    export class LoginRequest {
        "requestId": 0;
        "type": 1;
        "command": 10000;
        constructor() {
            this.data = {
                "loginType": 1,
                "name": "loginName",
                "password": "",
                "nickName": "nickName",
                "avatar": "",
                "sex": 0,
                "timeZone": 0,
                "clientOs": "",
                userId: "aaa",
                inviteCode: "",
                invite: ""
            }
        }
        public data: {
            "loginType": number,
            "name": string,
            "password": string,
            "nickName": string,
            "avatar": string,
            "sex": number,
            "timeZone": number,
            "clientOs": string,
            userId: string,
            inviteCode: string,
            invite: string
        };

    }
    export class LoginResponse implements InetwarkResponseData//返回报文
    {
        constructor() {

        }

        "requestId": number;
        "playerId": number;
        "type": number;
        "command": number;
        "code": number;
        "message": string;
        public "data": {
            "token": string,
            "timestamp": number,
            "player": {
                // isOpen: number,
                // sign: number,//是否能签到:0:否，1：是，连续三天签到后就不能再签到
                // catId: number,
                // isGuide: number,
                // guide: number[],
                // chatNum: number,
                // dailyChat?: number,
                // dailyWork?: number,
                "playerId": "2416BE07V201Y2",
                "charater": 0,
                "loginType":0
            },

        }
    }
    export class SelectCatResponse implements InetwarkResponseData {
        constructor() {

        }
        "requestId" = 0;
        "playerId" = 0;
        "type" = 1;
        "command" = 10007;
        "code" = 0;
        "message" = "";
        "data":
            {
                cat: NPCServerD

            }
    }

    //===================查询猫咪================================
    //是否已拥有
    export class CheckCatsRequest {
        constructor() {
            this.data = {
                "playerId": 0,
                "catIds": [

                ]
            }
        }
        "requestId" = 0;
        "type" = 1;
        "command" = 10005;
        "data": {
            "playerId": 0,
            "catIds": [

            ]
        }
    }
    export class CheckCatsResponse implements InetwarkResponseData {
        "requestId": number;
        "playerId": number;
        "type": number;
        "command": number;
        "code": number;
        "message": string;
        "data": NPCServerD[]
    }

    //===================创建角色=========================
    export class ChangeCatsRequest {
        "requestId" = 0;
        "playerId" = 0;
        "type" = 0;
        "command" = 10019;
        "data": {
            "catId": number;
        }
        constructor() {
            this.data = { "catId": 0 };
        }

    }
    export class ChangeCatsResponse implements InetwarkResponseData {
        "requestId" = 0;
        "playerId" = 0;
        "type" = 1;
        "command" = 10019;
        "code" = 0;
        "message" = "";
        "data":
            {
                catId: number;
            }

    }

    //======================完成动作回复客户端=============================
    export class NpcActionDone {
        "requestId" = 0;
        "type" = 1;
        "command" = 10008;
        constructor() {
            this.data = {
                "bid": 1,
                "npcId": 10003,
                "actionId": 100,
                "isFinish": 0,
                "x": 100,
                "y": 100,
                "objId": "",
                "state": 1,
                "params": {

                }
            };
        }
        "data": {
            "bid": number,
            "npcId": number,
            "actionId": number,
            "isFinish": number,
            "x": number,
            "y": number,
            "objId": string,
            "state": number,
            "params": {

            }
        }
    }

    //======================玩家道具列表获取=============================
    export class checkPlayerItemsRequest {
        "requestId" = 0;
        "type" = 1;
        "command" = 10008;
        constructor() {
            this.data = {
                "playerId": 0,
                "ids": []
            };
        }
        "data": {
            "playerId": number,
            "ids": []
        }
    }
    export class checkPlayerItemsResponse implements InetwarkResponseData {
        "requestId": number;
        "playerId": number;
        "type": number;
        "command" = 10008;
        "code" = 0;
        "message": "";
        "data": itemServerD[

        ]
    }
    export class chatListResponse implements InetwarkResponseData {
        "requestId": number;
        "type": number;
        "command": number = 10017;
        "code": number;
        message: string;
        "data": {
            "totalPage": number,
            "chats": [
                {
                    "from": number,
                    "to": number,
                    "type": number,
                    "content": string,
                    "time": number
                }
            ]
        }
    }
    //==================task===================
    export class taskListRequest {
        constructor() {
            this.data = {
                "playerId": 0,
                "type": -1//-1获取所有数据，对应表格postion
            }
        }
        "requestId" = 0;
        "type" = 1;
        "command" = 10012;
        "data": {
            "playerId": number,
            "type": number
        }
    }
    export class taskListResponse implements InetwarkResponseData {
        "requestId": 0;
        "playerId": 0;
        "type": 1;
        "command": 10012;
        "code": 0;
        "message": "";
        "data": taskcell[
        ]
    }
    export class taskChangeResponse implements InetwarkResponseData {
        "requestId": number;
        "type" = 1;
        "command" = 10010;
        "code": number;
        "message": string;
        "data": [
            {
                "playerId": 0,
                "taskId": 0,
                "status": number,
                "value1": 0,
                "unlock": 0
            }
        ]
    }
    export class taskgetRequest {
        "requestId" = 0;
        "type" = 1;
        "command" = 10013;
        "data": {
            "taskId": number
        } = {
                "taskId": 0
            }
    }

    export class taskgetResponse implements InetwarkResponseData {
        "requestId": number;
        "playerId": number;
        "type": number;
        "command": number;
        "code": number;
        "message": string;
        "data": {
            "goodsDataList": itemServerD[
            ],
            "taskIdList": [
                0
            ]
        }
    }
    //======================签到=========================
    export class qiandaoRequest {
        "requestId" = 0;
        "type" = 1;
        "command" = 10014;
    }
    export class qiandaoResponse implements InetwarkResponseData {
        "requestId": number;
        "playerId": number;
        "type": number;
        "command": number;
        "code": number;
        "message": string;
        "data": {
            "sign": number,//是否能签到:0:否，1：是，连续三天签到后就不能再签到
            "itemType": number,//签到3次获得的道具类型：0: 无, 1:猫，2:积分
            "itemValue": number//如果itemType是猫，value=猫ID，itemType积分，则value=积分
        }
    }
    //======================分享=========================
    export class shareRequest {
        "requestId" = 111110;
        "type" = 1;
        "command" = 10015;
    }
    export class shareResponse implements InetwarkResponseData {
        message: string;
        "requestId": number;
        "type": number;
        "command": number;
        "code": number;
        "data": {
            "url": string;
        }

    }
    //=======================道具，背包============================
    export class itemUpdateResponse implements InetwarkResponseData {
        "requestId": number;
        "type": number;
        "command": 10009;
        "code": number;
        "message": string;
        "data": [
            itemServerD
        ]
    }
    //=========================修改猫咪信息=========================
    export class changeCatInfoRequest {
        constructor() {
            this.data = {
                "catId": 10001,
                "name": "猫的名字",
                "userName": "代理猫院长名字",
                "career": "teacher",
            }
        }
        "requestId" = 0;
        "type" = 1;
        "command" = 10006;
        "data": {
            "catId": number,
            "name": string,
            "userName": string,
            "career"?: string
        }
    }
    export class changeCatInfoResponse implements InetwarkResponseData {
        "requestId": number;
        "playerId": number;
        "type": number;
        "command" = 10006;
        "code": number;
        "message": string;
        "data": {
            cat: NPCServerD
        }
    }
    //========================引导==========================
    export class yindaoRequest {
        constructor() {
            this.data = {
                guide: 0
            }
        }
        requestId = 1111110;
        type = 1;
        command = 10021;
        data: {
            guide: number;
        }
    }
    // export class yindaoResponse implements InetwarkResponseData{

    // }
    //=====================打招呼=========================
    export class zhaohuRequest {
        "requestId" = 1111110;
        "type" = 1;
        "command" = 10022;
    }
    export class zhaohuResponse implements InetwarkResponseData {
        type: number;
        "requestId": number;
        "command": number;
        "code": number;
        "message": string;
        "data": {
            "msg": string;
        }
    }

    //===============查询商店列表===================
    export class shopListRequest {
        "requestId" = 1111110;
        "type" = 1;
        "command" = 10024;
    }

    //===========================发送评论
    export class RequestSendChatData {
        "requestId" = 111110;
        "type" = 2;
        "command" = 10016;
        "data" = {
            "context": "写一段七言绝句诗，题目是：火锅！",
            "npcId": 10006,
            "sender": "sasadad",
        }
    }
    

    //===========================获取聊天记录
    export class getChatDataInfo {
        "requestId" = 1111110;
        "type" = 2;
        "command" = 10017;
        "data" = {
            "npcId":10006
        }
    }

    //==========================获取物品信息
    export class getPlayerItemInfo {
        "requestId" = 1111110;
        "type" = 2;
        "command" = 10081;
    }

    //通知玩家场景动作
    export class NtfPlayerAction{
        
    }
}

