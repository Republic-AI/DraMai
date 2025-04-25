import {
    _decorator,
    AnimationManager,
    assetManager,
    Camera,
    Component,
    director,
    dragonBones,
    EventTouch,
    find,
    instantiate,
    KeyCode,
    Label,
    Node,
    Prefab,
    size,
    Sprite,
    TiledLayer,
    TiledMap,
    TiledObjectGroup,
    Tween,
    tween,
    UITransform,
    v2,
    v3,
    Vec2,
    Vec3,
    view,
} from "cc";

//import _ from "lodash";

import * as _ from "lodash-es";





import { AudioManager } from "../../../../../src/manager/AudioManager";
import { observer, socket } from "../../../../../src/game/App";
import { GameScene } from "../../../../../src/game/scene/GameScene";
import { GlobalConfig } from "../../../../../src/game/config/GlobalConfig";
import { NpcManager } from "../../../../../src/NPC/NpcManager";
import { BubbleImgUrl, NpcEventType, NpcIndex } from "../../../../../src/StaticUtils/NPCConfig";
import { EventType } from "../../../../../src/EventType";
import { network } from "../../../../../src/model/RequestData";
import { NPCServerD } from "../../../../../src/game/config/DataStruct";
import WebUtils from "../../../../../src/utils/WebUtils";
import { furnitureNode } from "../../../../../src/game/gameUI/furnitureNode";
import { UILayer } from "../../../../../src/game/gameUI/UILayer";
import { changeSkinPrefab } from "../../../../../src/game/gameUI/changeSkinPrefab";
export const sleepFramePosX = [40, -61];
export const sleepFrameTime = 0.45;
export const bubbleTime = 0.7;



//let hight = 6720
// 434, 820
const PosAdapt = 1;
declare global {
    interface Window {
        farmGoSleep: any;
        farm: any;
        farmcook: any;
        farmeat: any;
        fieldReady: any;

        herdSleep: any;
        herdCook: any;
        herdDinning: any;

        bakerSleep: any;
        bakerCook: any;
        bakerDinning: any;

        GrocerSleep: any;
        GrocerCook: any;
        GrocerDinning: any;
        farmHarvest: any;
        farmStopSleep: any
    }
}
const { ccclass, property } = _decorator;

@ccclass('gameLayer_map1')
export class gameLayer_map1 extends Component {
    @property({ type: [Node] })
    public otherNPCarr: Node[] = [];

    @property(Node)
    public playerLayer: Node = null;

    @property(Node)
    pepeFoodNode: Node = null;

    @property(Node)
    catFoodNode: Node = null;

    @property(Node)
    pippinFoodNode: Node = null;

    @property(Node)
    pepeBoxNode: Node = null;

    @property(Node)
    muskCoffeeNode: Node = null;

    @property(Node)
    pepeCoffeeNode: Node = null;

    @property(Node)
    satoshiCoffeeNode: Node = null;

    @property(Node)
    popcatCoffeeNode: Node = null;

    @property(Node)
    trumpCoffeeNode: Node = null;

    @property(Node)
    avaCoffeeNode: Node = null;

    @property(Node)
    speakLayer:Node = null;

    @property(Prefab)
    replyNode:Prefab = null;

    @property(Prefab)
    speakNode:Prefab = null;

    @property(Prefab)
    speakNode_Ex:Prefab = null;

    private _layerFloor: TiledLayer = null!;

    _npcSpeakObj = {};
    _npcTileArray: Vec2[] = [];
    _height = null;
    _typeComputerInfo = {};
    _initActionData =  {};
    _myPlayerNode = null;
    _playerArr = [];
    _createTimeInfo = {};
    _coffeeNodeStatus = {};
    private _isValid: boolean = true;
    protected onLoad(): void {
        this.startScrollNum();;
        AudioManager.instance.init()
        AudioManager.instance.loadAndPlayAudio("sound/bgm");
        this._initData()
        observer.on(EventType.SOCKET_GETALL_NPCS, this.setNPCPos, this);
        observer.on(EventType.SOCKET_NPC_ACTION, this.playNPCAction, this);
        observer.on(EventType.SCENE_ACTION, this.playSceneAction, this);
        observer.on(EventType.RELOGIN, this.reconect, this);
        observer.on(EventType.RECONECTSCENE,this.reEnterRoom,this);
        // observer.on(EventType.CREATEPLAYER,this.createPlayer,this);
        // observer.on(EventType.DESTROYPLAYER,this.destroyPlayer,this);
        // observer.on(EventType.GETALLNFTSTATUS,this.initAllNftStatus,this);
        // observer.on(EventType.CHANGENFTSTATUS,this.changeNftStatus,this);

        this.node.on(Node.EventType.TOUCH_START,this.onPlayerTouch,this);
    }

    protected onDestroy(): void {
        this._isValid = false;
        // 停止当前播放的音频
        AudioManager.instance.stop();
        observer.off(EventType.SOCKET_GETALL_NPCS, this.setNPCPos, this);
        observer.off(EventType.SOCKET_NPC_ACTION, this.playNPCAction, this);
        observer.off(EventType.SCENE_ACTION, this.playSceneAction, this);
        observer.off(EventType.RELOGIN, this.reconect, this);
        observer.off(EventType.RECONECTSCENE, this.reEnterRoom, this);
    }

    start() {

    }

    update(deltaTime: number) {
        //this.checkTypeComputer()
        // let npcNode_10008 = _.find(this.otherNPCarr, (item) => {
        //     return (
        //         item.getComponent(NpcManager).NpcID === 10008
        //     );
        // });
        // if(npcNode_10008){
        //     npcNode_10008.setSiblingIndex(0);
        // }
        let allModelArr = [];
        this.playerLayer.children.forEach(node=>{
            allModelArr.push(node);
        })
        allModelArr.sort((a,b)=>{
            return b.position.y - a.position.y;
        })
        allModelArr.forEach((node,index)=>{
            node.setSiblingIndex(index);
        })
    }

    _initData() {
        //初始化一定要将地图脚本传到Scene里
        director.getScene().getComponentInChildren(GameScene).setMapScript(this);
        
        this._layerFloor = this.node.getComponent(TiledMap).getLayer("building")!;
        this._height = this.node.getComponent(UITransform).contentSize.height;
        if(!GlobalConfig.instance.isStoryModel){
            // let json = new network.GetAllNPCRequest();
            // json.command = 10002;
            // json.type = 1;
            // socket.sendWebSocketBinary(json);

            // let json2 = new network.GetAllNPCRequest();
            // json2.command = 10028;
            // json2.type = 1;
            // socket.sendWebSocketBinary(json2);
            //modelMgr.townModel.refreshItemDate();
        }
        else{
            //this.initStoryNpcs();
        }
        // const currentUrl = window.location.href;
        // const url = new URL(currentUrl);
        // 获取查询参数
        // this.typeComputerNode.active = true;
        // this.satoshiComputerAction();
        tween(this.node).delay(0.1).call(()=>{
            this.initOtherNPCs(GlobalConfig.instance.nowSceneData.otherNpc);
        }).start()
        if(GlobalConfig.instance.chooseNpc){
            tween(this.node).repeatForever(tween(this.node).delay(0.1).call(()=>{
                this.node.getComponentsInChildren(NpcManager).forEach(npcScript=>{
                    if(npcScript.NpcID == GlobalConfig.instance.chooseNpc){
                        observer.post(EventType.FOLLOWNPC,GlobalConfig.instance.chooseNpc);
                        Tween.stopAllByTarget(this.node);
                    }
                })
            }).start()).start()
        }
        else{
            //如果是由Banner进入的场景，则打开一次新闻
            tween(this.node).delay(0.1).call(()=>{
                let UILayerSrc = director.getScene().getComponentInChildren(UILayer);
                UILayerSrc.onBtnNews();
            }).start()
        }
        if(GlobalConfig.instance.nowSceneData.furnitureMsgDataMap){
            for(let i in GlobalConfig.instance.nowSceneData.furnitureMsgDataMap){
                let id = GlobalConfig.instance.nowSceneData.furnitureMsgDataMap[i].id;
                let endTime = GlobalConfig.instance.nowSceneData.furnitureMsgDataMap[i].endTime || 0;
                this.node.getComponentsInChildren(furnitureNode).forEach(itemScript=>{
                    if(itemScript.type == i){
                        itemScript.setItemFrame(id,endTime);
                    }
                })
            }
        }
    }

    setNPCPos(NPCs: any) {
        if (!NPCs) {
            return;
        }
        NPCs?.data?.data?.otherNpc && this.initOtherNPCs(NPCs.data.data.otherNpc);
        //NPCs?.data?.data?.myNpc && this.createPlayer(NPCs);
        // NPCs?.data?.data?.otherPlayerNpc && this.createPlayerArr(NPCs?.data?.data?.otherPlayerNpc);
        // NPCs?.data?.data?.joinIdMap && this.setCreateInfo(NPCs?.data?.data?.joinIdMap);
    }    
    setCreateInfo(data){
        GlobalConfig.instance.CreateIDArr = data;
        Tween.stopAllByTarget(this.playerLayer);
        for(let i in GlobalConfig.instance.CreateIDArr){
            if(GlobalConfig.instance.CreateIDArr[i]){
                this._createTimeInfo[i] = new Date().getTime();
            }
        }
        tween(this.playerLayer).repeatForever(tween(this.playerLayer).delay(1).call(()=>{
            for(let i in GlobalConfig.instance.CreateIDArr){
                if(GlobalConfig.instance.CreateIDArr[i]){
                    let nowTime = new Date().getTime() - this._createTimeInfo[i];
                    if(nowTime > GlobalConfig.instance.CreateIDArr[i]){
                        GlobalConfig.instance.CreateIDArr[i] = null;
                    }
                }
            }
        }).start()).start()
        
    }
    initOtherNPCs(myNPC: NPCServerD[]) {
        myNPC.forEach((NPC: NPCServerD) => {
            this.initOtherNPC(NPC);
        });
    }
    // createPlayerArr(otherPlayer){
    //     console.log("init createPlayerArr")
    //     otherPlayer.forEach(playerData => {
    //         this.createPlayer({data:{data:{myNpc:playerData}}});
    //     });
    // }
    NpcMoveAction(movePath) {

    }
    //?接收行为数据
    playNPCAction(actions: any) {
        //!先移动到位置
        const actionData  = actions.data.data
        console.log("actionData======" + JSON.stringify(actionData));
        // if(actionData.npcId > 99999){
        //     this.playPlayerAction(actionData);
        //     return;
        // }
        const npc = _.find(this.otherNPCarr, (item) => {
            return (
                item.getComponent(NpcManager).NpcID === actionData.npcId
            );
        });
        if(!npc){
            console.log(actionData.npcId + " npc 初始化未完成")
            if(this._initActionData[actionData.npcId]){
                this._initActionData[actionData.npcId].push(actionData)
            }
            else{
                this._initActionData[actionData.npcId] = [];
                this._initActionData[actionData.npcId].push(actionData)
            }
            return;
        }
        else{

        }
        let eventPox
        let finishTile
        let npcControl = npc.getComponent(NpcManager)
        npcControl.setActionID(actionData.actionId);
        try {
            //前端执行NPC动作逻辑
            npcControl.sleepFinish();
            if (actionData.actionId === NpcEventType.move) {
                npcControl.hideBubble();
                if(this._coffeeNodeStatus && this._coffeeNodeStatus[actionData.npcId]){
                    this._coffeeNodeStatus[actionData.npcId].active = false;
                    this._coffeeNodeStatus[actionData.npcId] = null;
                }
                if (actionData.params.path.length > 0) {
                    console.log( actionData.npcId + "startMove");
                    npcControl.serverPathMove(actionData.params.path);
                }
                if(actionData.npcId == 10008){
                    this.stopPepeBoxAction();
                    this.pepeFoodNode.active = false;
                }
                else if(actionData.npcId == 10007){
                    this.catFoodNode.active = false;
                    npcControl.fishFinish();
                }
                else if(actionData.npcId == 10009){
                    // this.muskFoodNode.active = false;
                    // this.stopMuskMeeting();
                }
                else if(actionData.npcId == 10006){
                    // this.fixNode.getComponent(fixPrefab).setIdleStatus();
                    // Tween.stopAllByTarget(this.typeComputerNode);
                    // this.typeComputerNode.active = false;
                }
                else if(actionData.npcId == 10011){

                }
                else if(actionData.npcId == 10012){
                    // this.trumpFoodNode.active = false;
                    // npcControl.stopSpeech();
                }
                else if(actionData.npcId == 10013){

                }
                else if(actionData.npcId == 10014){
                    // this.avaFoodNode.active = false;
                    // this.avaComputerEffect.active = false;
                }
                else if(actionData.npcId == 10015){
                    //this.lunaFoodNode.active = false;
                }
            }
            else if (actionData.actionId === NpcEventType.type) {
                // npcControl.showBubble(BubbleImgUrl.type);
                // npcControl.setIdleStatus(KeyCode.KEY_W);
                // this.satoshiComputerAction();
            }
            else if (actionData.actionId === NpcEventType.think) {
                npcControl.showBubble(BubbleImgUrl.think);
                if(actionData.npcId == 10006){
                    npcControl.setIdleStatus(KeyCode.KEY_S);
                }
                else if(actionData.npcId == 10007){
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                }
                else if(actionData.npcId == 10008){
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10009){
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10010){
                    npcControl.setIdleStatus(KeyCode.KEY_S);
                }
                else if(actionData.npcId == 10012){
                    npcControl.setIdleStatus(KeyCode.KEY_S);
                }
                else if(actionData.npcId == 10014){
                    npcControl.setIdleStatus(KeyCode.KEY_S);
                }
                else if(actionData.npcId == 10015){
                    npcControl.setIdleStatus(KeyCode.KEY_W);
                }
            }
            else if (actionData.actionId === NpcEventType.read) {
                npcControl.showBubble(BubbleImgUrl.read);
                if(actionData.npcId == 10006){
                    npcControl.setIdleStatus(KeyCode.KEY_W);
                }
                else if(actionData.npcId == 10007){
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                }
                else if(actionData.npcId == 10008){
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10009){
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10010){
                    npcControl.setIdleStatus(KeyCode.KEY_S);
                }
                else if(actionData.npcId == 10012){
                    npcControl.setIdleStatus(KeyCode.KEY_S);
                }
                else if(actionData.npcId == 10014){
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10015){
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                }
            }
            else if (actionData.actionId === NpcEventType.fix) {
                // npcControl.showBubble(BubbleImgUrl.fix);
                // npcControl.setIdleStatus(KeyCode.KEY_A);
                //this.fixNode.getComponent(fixPrefab).setFixOnStatus();
            }
            else if (actionData.actionId === NpcEventType.speak) {
                npcControl.speak(actionData.params.content);
                if(actionData.params.npcId && actionData.params.npcId != actionData.npcId){
                    const talkNpc = _.find(this.otherNPCarr, (item) => {
                        console.log("NpcID====" + item.getComponent(NpcManager).NpcID)
                        console.log("datID=====" + actionData.npcId)
                        return (
                            item.getComponent(NpcManager).NpcID === actionData.params.npcId
                        );
                    });
                    if(!talkNpc){
                        return;
                    }
                    let npcTile = npcControl.getNpcTile();
                    let talkNpcTile = talkNpc.getComponent(NpcManager).getNpcTile();
                    if(npcTile.y > talkNpcTile.y){
                        npcControl.setIdleStatus(KeyCode.KEY_W)
                        npcControl.speakEx(actionData.params.content);
                    }
                    else if(npcTile.y < talkNpcTile.y){
                        npcControl.setIdleStatus(KeyCode.KEY_S);
                        npcControl.speak(actionData.params.content);
                    }
                    else if(npcTile.x < talkNpcTile.x){
                        npcControl.setIdleStatus(KeyCode.KEY_D);
                        npcControl.speakEx(actionData.params.content);
                    }
                    else if(npcTile.x > talkNpcTile.x){
                        npcControl.setIdleStatus(KeyCode.KEY_A);
                        npcControl.speak(actionData.params.content);
                    }
                }
                else{
                    npcControl.speak(actionData.params.content);
                }
            }
            else if (actionData.actionId === NpcEventType.reply) {
                console.log("replyData====" + JSON.stringify(actionData))
                let replyName = actionData.params.chatData.rName;
                let content = actionData.params.chatData.context;
                let userNo = actionData.params.chatData.receiver;
                // let UILayerSrc = director.getScene().getComponentInChildren(UILayer);
                // UILayerSrc.showBulletMsg(actionData.params.chatData);
                //npcControl.replyMsg(replyName,content,userNo)
                // let UILayerSrc = director.getScene().getComponentInChildren(changeSkinPrefab);
                // UILayerSrc.showNpcReplyPlayer(actionData.npcId,actionData.params.chatData.context)
                // ;
                if(actionData.params.chatData.privateMsg){
                    let UILayerSrc = director.getScene().getComponentInChildren(changeSkinPrefab);
                    UILayerSrc.showNpcReplyPlayer(actionData.npcId,content);
                }
                else{
                   npcControl.replyMsg(replyName,content,userNo) 
                }
            }
            else if(actionData.actionId === NpcEventType.sleep){
                console.log(actionData.npcId + "start sleep");
                npcControl.sleepStart();
            }
            else if(actionData.actionId === NpcEventType.cook){
                console.log(actionData.npcId + "start cook");
                npcControl.showBubble(BubbleImgUrl.cook)
                if(actionData.npcId == 10014 || actionData.npcId == 10015){
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else{
                    npcControl.setIdleStatus(KeyCode.KEY_W);
                }
            }
            else if(actionData.actionId === NpcEventType.stock){
                console.log(actionData.npcId + "start stock");
                npcControl.showBubble(BubbleImgUrl.getItem)
                npcControl.setIdleStatus(KeyCode.KEY_A);
            }
            else if(actionData.actionId === NpcEventType.tidyup){
                // console.log(actionData.npcId + "start tidyup");
                // npcControl.showBubble(BubbleImgUrl.cleanItem);
                // npcControl.setIdleStatus(KeyCode.KEY_W);
                // this.startPepeBoxAction();
            }
            else if(actionData.actionId === NpcEventType.dinning){
                console.log(actionData.npcId + "start eat");
                npcControl.showBubble(BubbleImgUrl.dinning);
                if(actionData.npcId == 10007){
                    this.catFoodNode.active = true;
                    this.catFoodNode.children[0].active = true;
                    this.catFoodNode.children[1].active = false;
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                }
                else if(actionData.npcId == 10008){
                    this.pepeFoodNode.active = true;
                    this.pepeFoodNode.children[0].active = true;
                    this.pepeFoodNode.children[1].active = false;
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10009){
                    // this.muskFoodNode.active = true;
                    // this.muskFoodNode.children[0].active = true;
                    // this.muskFoodNode.children[1].active = false;
                    // npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10010){
                    this.pippinFoodNode.active = true;
                    this.pippinFoodNode.children[0].active = true;
                    this.pippinFoodNode.children[1].active = false;
                    npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10012){
                    // this.trumpFoodNode.active = true;
                    // this.trumpFoodNode.children[0].active = true;
                    // this.trumpFoodNode.children[1].active = false;
                    // npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10014){
                    // this.avaFoodNode.active = true;
                    // this.avaFoodNode.children[0].active = true;
                    // this.avaFoodNode.children[1].active = false;
                    // npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.npcId == 10015){
                    // this.lunaFoodNode.active = true;
                    // this.lunaFoodNode.children[0].active = true;
                    // this.lunaFoodNode.children[1].active = false;
                    // npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                tween(npc).delay(5).call(()=>{
                    npcControl.showBubble(BubbleImgUrl.afterDinner);
                }).start();
                tween(this.node).delay(5).call(()=>{
                    if(actionData.npcId == 10007){
                        this.catFoodNode.active = true;
                        this.catFoodNode.children[0].active = false;
                        this.catFoodNode.children[1].active = true;
                    }
                    else if(actionData.npcId == 10008){
                        this.pepeFoodNode.active = true;
                        this.pepeFoodNode.children[0].active = false;
                        this.pepeFoodNode.children[1].active = true;
                    }
                    else if(actionData.npcId == 10009){
                        // this.muskFoodNode.active = true;
                        // this.muskFoodNode.children[0].active = false;
                        // this.muskFoodNode.children[1].active = true;
                    }
                    else if(actionData.npcId == 10010){
                        this.pippinFoodNode.active = true;
                        this.pippinFoodNode.children[0].active = false;
                        this.pippinFoodNode.children[1].active = true;
                    }
                    else if(actionData.npcId == 10012){
                        // this.trumpFoodNode.active = true;
                        // this.trumpFoodNode.children[0].active = false;
                        // this.trumpFoodNode.children[1].active = true;
                    }
                    else if(actionData.npcId == 10014){
                        // this.avaFoodNode.active = true;
                        // this.avaFoodNode.children[0].active = false;
                        // this.avaFoodNode.children[1].active = true;
                    }
                    else if(actionData.npcId == 10015){
                        // this.lunaFoodNode.active = true;
                        // this.lunaFoodNode.children[0].active = false;
                        // this.lunaFoodNode.children[1].active = true;
                    }
                }).start();

            }
            else if(actionData.actionId === NpcEventType.fishing){
                console.log(actionData.npcId + "start fish");
                let dir = "";
                if(actionData.params.oid.includes("up")){
                    dir = "up";
                    npcControl.setIdleStatus(KeyCode.KEY_S)
                }
                else if(actionData.params.oid.includes("down")){
                    dir = "down";
                    npcControl.setIdleStatus(KeyCode.KEY_W)
                }
                else if(actionData.params.oid.includes("left")){
                    dir = "left";
                    npcControl.setIdleStatus(KeyCode.KEY_D)
                }
                else if(actionData.params.oid.includes("right")){
                    dir = "right";
                    npcControl.setIdleStatus(KeyCode.KEY_A)
                }
                if(!dir){
                    console.log("fish dir error=====" + actionData.params.oid);
                    return;
                }
                npcControl.showBubble(BubbleImgUrl.fishstart)
                npcControl.fishStart(dir);
            }
            else if(actionData.actionId === NpcEventType.sale){
                console.log(actionData.npcId + "start sale");
                if(actionData.npcId == 10007){
                    npcControl.setIdleStatus(KeyCode.KEY_D)
                    npcControl.showBubble(BubbleImgUrl.buy);
                }
                else if(actionData.npcId == 10008){
                    npcControl.setIdleStatus(KeyCode.KEY_S)
                    npcControl.showBubble(BubbleImgUrl.buy);
                }
                else if(actionData.npcId == 10010){
                    npcControl.setIdleStatus(KeyCode.KEY_S)
                    npcControl.showBubble(BubbleImgUrl.buy);
                }
            }
            else if(actionData.actionId === NpcEventType.buy){
                console.log(actionData.npcId + "start buy");
                if(actionData.params.oid.includes("pippin")){
                    npcControl.setIdleStatus(KeyCode.KEY_W);
                    npcControl.showBubble(BubbleImgUrl.getCoffee);
                    return;
                }
                if(actionData.params.oid.includes("pepe")){
                    npcControl.setIdleStatus(KeyCode.KEY_W);
                    npcControl.showBubble(BubbleImgUrl.getItem);
                    return;
                }
                if(actionData.params.oid.includes("popcat")){
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                    npcControl.showBubble(BubbleImgUrl.fishfinish);
                    return;
                }
            }  
            else if(actionData.actionId === NpcEventType.data){
                if(actionData.params.oid.includes("up")){
                    npcControl.setIdleStatus(KeyCode.KEY_W)
                }
                else if(actionData.params.oid.includes("left")){
                    npcControl.setIdleStatus(KeyCode.KEY_A)
                }
                else if(actionData.params.oid.includes("right")){
                    npcControl.setIdleStatus(KeyCode.KEY_D)
                }
                else if(actionData.npcId == 10014){
                    // npcControl.setIdleStatus(KeyCode.KEY_W);
                    // this.avaComputerEffect.active = true;
                    // this.avaComputerEffect.getComponent(dragonBones.ArmatureDisplay).armatureName = "play";
                    // this.avaComputerEffect.getComponent(dragonBones.ArmatureDisplay).playAnimation("play",0);
                }
                npcControl.showBubble(BubbleImgUrl.data)
            }
            else if(actionData.actionId === NpcEventType.meeting){
                // npcControl.setIdleStatus(KeyCode.KEY_W)
                // npcControl.showBubble(BubbleImgUrl.meeting);
                // this.startMuskMeeting();
            }
            else if(actionData.actionId === NpcEventType.makecoffee){
                npcControl.setIdleStatus(KeyCode.KEY_W)
                npcControl.showBubble(BubbleImgUrl.makeCoffee);
            }
            else if(actionData.actionId === NpcEventType.drinkcoffee){
                if(actionData.params.oid.includes("musk")){
                    npcControl.setIdleStatus(KeyCode.KEY_A)
                    this.muskCoffeeNode.active = true;
                    this._coffeeNodeStatus[actionData.npcId] = this.muskCoffeeNode;
                }
                else if(actionData.params.oid.includes("pepe")){
                    npcControl.setIdleStatus(KeyCode.KEY_A)
                    this.pepeCoffeeNode.active = true;
                    this._coffeeNodeStatus[actionData.npcId] = this.pepeCoffeeNode;
                }
                else if(actionData.params.oid.includes("satoshi")){
                    npcControl.setIdleStatus(KeyCode.KEY_D)
                    this.satoshiCoffeeNode.active = true;
                    this._coffeeNodeStatus[actionData.npcId] = this.satoshiCoffeeNode;
                }
                else if(actionData.params.oid.includes("popcat")){
                    npcControl.setIdleStatus(KeyCode.KEY_D)
                    this.popcatCoffeeNode.active = true;
                    this._coffeeNodeStatus[actionData.npcId] = this.popcatCoffeeNode;
                }
                else if(actionData.params.oid.includes("eliza")){
                    // this.elizaCoffeeNode.active = true;
                    // this._coffeeNodeStatus[actionData.npcId] = this.elizaCoffeeNode;
                    // npcControl.setIdleStatus(KeyCode.KEY_A);
                }
                else if(actionData.params.oid.includes("trump")){
                    this.trumpCoffeeNode.active = true;
                    this._coffeeNodeStatus[actionData.npcId] = this.trumpCoffeeNode;
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                }
                else if(actionData.params.oid.includes("morpheus")){
                    // this.morpheusCoffeeNode.active = true;
                    // this._coffeeNodeStatus[actionData.npcId] = this.morpheusCoffeeNode;
                    // npcControl.setIdleStatus(KeyCode.KEY_D);
                }
                else if(actionData.params.oid.includes("ava")){
                    this.avaCoffeeNode.active = true;
                    this._coffeeNodeStatus[actionData.npcId] = this.avaCoffeeNode;
                    npcControl.setIdleStatus(KeyCode.KEY_A);
                }
                npcControl.showBubble(BubbleImgUrl.drinkCoffee);
            } 
            else if(actionData.actionId === NpcEventType.christmastree){
                //npcControl.setIdleStatus(KeyCode.KEY_W);
            } 
            else if(actionData.actionId === NpcEventType.speech){
                // npcControl.setIdleStatus(KeyCode.KEY_S);
                // npcControl.showBubble(BubbleImgUrl.speech);
                // npcControl.startSpeech();
            }
            else if(actionData.actionId === NpcEventType.draw){
                // npcControl.setIdleStatus(KeyCode.KEY_D);
                npcControl.showBubble(BubbleImgUrl.draw);
                if(actionData.params.oid.includes("up")){
                    npcControl.setIdleStatus(KeyCode.KEY_W)
                }
                if(actionData.params.oid.includes("right")){
                    npcControl.setIdleStatus(KeyCode.KEY_D)
                }
            }
            else if(actionData.actionId === 127){
                // if(actionData.npcId == 10012){
                //     console.log("123123123");
                //     npcControl.setIdleStatus(KeyCode.KEY_S);
                // }
            }  
            else if(actionData.actionId === NpcEventType.game){
                // if(actionData.npcId == 10012){
                //     console.log("123123123");
                //     npcControl.setIdleStatus(KeyCode.KEY_W);
                //     npcControl.showBubble(BubbleImgUrl.game);
                // }
            }
            else if(actionData.actionId === NpcEventType.sing){
                // npcControl.setIdleStatus(KeyCode.KEY_S);
                // npcControl.showBubble(BubbleImgUrl.sing);
            } 
            else if(actionData.actionId === NpcEventType.sendItem){
                if(actionData.params.npcId && actionData.params.npcId != actionData.npcId){
                        const talkNpc = _.find(this.otherNPCarr, (item) => {
                        console.log("NpcID====" + item.getComponent(NpcManager).NpcID)
                        console.log("datID=====" + actionData.npcId)
                        return (
                            item.getComponent(NpcManager).NpcID === actionData.params.npcId
                        );
                    });
                    if(!talkNpc){
                        return;
                    }
                    let npcTile = npcControl.getNpcTile();
                    let talkNpcTile = talkNpc.getComponent(NpcManager).getNpcTile();
                    let scaleX = npcTile.x < talkNpcTile.x ? 1 : -1;
                    //npcControl.playSendItemAction(scaleX);
                }
                else{
                    console.log("no npcId======" + JSON.stringify(actionData.params));
                }
            }     
            else {
                console.log("error actionID======" + actionData.actionId);
                if (actionData.params.oid) {
                    // let startTile = this._getTilePos(
                    //         new Vec2(npc.getPosition().x, npc.getPosition().y)
                    // );
                    // eventPox = this.tileObject.getObject(actionData.params.oid);
                    // finishTile =  this._getTilePos(new Vec2(eventPox.x, eventPox.y));

                    //         console.log("samTest1====" + JSON.stringify(finishTile))
                    //         this.stopActionNormal(npc, actionData);
                    //         npc
                    //             .getChildByName("NPCinstantiate")
                    //             .getComponent(NPCControl)
                    //             .aStartMove(
                    //             new Vec2(startTile.x, startTile.y),
                    //             new Vec2(finishTile.x, finishTile.y),
                    //             );
                }
            }
            let mood = actionData.mood || ""
            npcControl.setFeelingStatus(mood);

            ////前端执行NPC动作引发的场景逻辑

        } catch (error) {

        }

        // if(startTile.x === finishTile.x && startTile.y === finishTile.y){
        //     return
        // }
        // console.log("samTest1====" + JSON.stringify(finishTile))
        //   this.stopActionNormal(npc, actionData);
        //   npc
        //     .getChildByName("NPCinstantiate")
        //     .getComponent(NPCControl)
        //     .aStartMove(
        //       new Vec2(startTile.x, startTile.y),
        //       new Vec2(finishTile.x, finishTile.y),
        //       cb
        //     );


    }

    playSceneAction(sceneAction:any){
        //{"requestId":0,"userNo":"2414Z311NJ15V0","type":1,"command":10105,"code":0,"data":{"type":1,"npcId":10006}}
        // let actionData = sceneAction.data
        // console.log("sceneAction",JSON.stringify(sceneAction.data))
        // if(actionData.data.npcId == 10006){
        //     //type为1是用户的type指令生效，satoshi场景执行机器人type行为
        //     if(actionData.data.type == 1){
        //         if(actionData.userNo == GlobalConfig.instance.LoginData.data.player.playerId){
        //             console.log("self type")
        //             let botNode = instantiate(this.fixPrefab);
        //             this.typeBotNode.addChild(botNode);
        //             botNode.getComponent(fixPrefab).setNormalStatus();
        //             botNode.setPosition(v3(-135,-580,0));
        //             tween(botNode).to(1,{position:this._typeComputerInfo[1].node.position}).call(()=>{
        //                 console.log("aaaaaaa")
        //                 this._typeComputerInfo[1].botNum++;
        //                 this.checkTypeComputer();
        //                 botNode.getComponent(fixPrefab).setTypeStatus(true);
        //             }).delay(2).call(()=>{
        //                 console.log("bbbbb")
        //                 botNode.getComponent(fixPrefab).setPaperStatus(1);
        //             }).delay(2).call(()=>{
        //                 console.log("cccccc")
        //                 botNode.getComponent(fixPrefab).setPaperStatus(2);
        //                 //请求物品信息
        //                 let json_2 = new network.getPlayerItemInfo();
        //                 json_2.command = 100081;
        //                 socket.sendWebSocketBinary(json_2);
        //             }).delay(1.5).call(()=>{
        //                 console.log("ddddddd")
        //                 this._typeComputerInfo[1].botNum--;
        //                 this.checkTypeComputer();
        //                 botNode.getComponent(fixPrefab).setTypeStatus(false);
        //                 botNode.getComponent(fixPrefab).playExitAction();
        //             }).start()
        //         }
        //         else{
        //             console.log("other type")
        //             let botNode = instantiate(this.fixPrefab);
        //             this.typeBotNode.addChild(botNode);
        //             botNode.getComponent(fixPrefab).setNormalStatus();
        //             let randomPosition = this.getRandomPosition();
        //             botNode.setPosition(randomPosition);
        //             let computerID = Math.floor(Math.random() * 11) + 2;
        //             let offsetPositionX = (this._typeComputerInfo[computerID].botNum % 3) * 10;
        //             let offsetPostionY = Math.floor(this._typeComputerInfo[computerID].botNum / 3) * 10;
        //             let targetPos = v3(this._typeComputerInfo[computerID].node.position.x + offsetPositionX,this._typeComputerInfo[computerID].node.position.y + offsetPostionY,0);
        //             tween(botNode).to(2,{position:targetPos}).call(()=>{
        //                 console.log("aaaaaaa")
        //                 let offsetX = (this._typeComputerInfo[computerID].botNum % 3) * 10;
        //                 let offsetY = Math.floor(this._typeComputerInfo[computerID].botNum / 3) * 10;
        //                 let offSetPos = v3(this._typeComputerInfo[computerID].node.position.x + offsetX,this._typeComputerInfo[computerID].node.position.y + offsetY,0);
        //                 botNode.setPosition(offSetPos);
        //                 this._typeComputerInfo[computerID].botNum++;
        //                 botNode.setPosition(offSetPos);
        //                 botNode.getComponent(fixPrefab).setTypeStatus(true);
        //                 this.checkTypeComputer();
        //             }).delay(2).call(()=>{
        //                 console.log("bbbbb")
        //                 botNode.getComponent(fixPrefab).setPaperStatus(1);
        //             }).delay(2).call(()=>{
        //                 // console.log("cccccc")
        //                 // botNode.getComponent(fixPrefab).setPaperStatus(2);
        //                 // //请求物品信息
        //                 // let json_2 = new network.getPlayerItemInfo();
        //                 // json_2.command = 100081;
        //                 // socket.sendWebSocketBinary(json_2);
        //             }).delay(1.5).call(()=>{
        //                 console.log("ddddddd")
        //                 this._typeComputerInfo[computerID].botNum--;
        //                 botNode.getComponent(fixPrefab).setTypeStatus(false);
        //                 botNode.getComponent(fixPrefab).playExitAction();
        //                 this.checkTypeComputer();
        //             }).start()
        //         }
        //     }
        // }
    }

    _getTilePos(posInPixel: { x: number; y: number }) {
        const mapSize = this.node.getComponent(UITransform).contentSize;
        const tileSize = size(32,32);
        const x = Math.floor(posInPixel.x / tileSize.width);
        const y = Math.floor((mapSize.height - posInPixel.y) / tileSize.height);
        return new Vec2(x, y - 1);
    }

    //!npc睡觉相关
    getSleepNode(npcId: number) {
        return new Node();
        // switch (npcId) {
        //   case 10002:
        //     return {
        //       head: this.farmHomeBedHead,
        //       bubble: this.farmHomeBedBubble,
        //     };
        //   case 10004:
        //     return {
        //       head: this.herdmanHomeBedHead,
        //       bubble: this.herdmanBedBubble,
        //     };
        //   case 10005:
        //     return {
        //       head: this.bakerHomeBedHead,
        //       bubble: this.bakerBedBubble,
        //     };
        //   case 10003:
        //     return {
        //       head: this.grocerHomeBedHead,
        //       bubble: this.grocerBedBubble,
        //     };
        //     //按照id分别返回对应的node
        //     break;
        //   default:
        //     return {
        //       head: this.farmHomeBedHead,
        //       bubble: this.farmHomeBedBubble,
        //     };
        //     break;
        // }
    }

    npcSleep(npc: Node, head: Node, bubble: Node, cb?: () => void) {
        WebUtils.getResouceImg(
            BubbleImgUrl.sleep,
            npc.getChildByName("bubble"),
            () => {
                npc.getChildByName("bubble").active = true;
            }
        );
        this.scheduleOnce(() => {
            cb();
            head.active = true;
            bubble.active = true;
        }, bubbleTime);
    }

    speak(npc, actionData) {
        // npc.getChildByName('speakContent').getComponent(Label).string = actionData.params.content
        // npc.getChildByName('speakContent').active = true
    }

    // stopActionNormal(npc: Node, actionData: DataEvents, cb?: () => void) {
    //     //this.stopNpcSleep(npc, actionData, cb);
    //     npc.getChildByName("bubble").active = false;
    // }

    //{"id":10002,"name":"Alice Farmer","type":1,"model":10002,"career":"","keyword":"","hair":100,"top":90,"bottoms":100,"speed":0,"x":536,"y":736}

    initOtherNPC(NPC: NPCServerD) {
        if (!this.speakLayer) {
            console.log('speakLayer is not assigned in the editor!');
            return;
        }

        if (!this.replyNode) {
            console.log('replyNode prefab is not assigned in the editor!');
            return;
        }

        if (!this.speakNode) {
            console.log('speakNode prefab is not assigned in the editor!');
            return;
        }

        if (!this.speakNode_Ex) {
            console.log('speakNode_Ex prefab is not assigned in the editor!');
            return;
        }

        let replyNode = instantiate(this.replyNode);
        this.speakLayer.addChild(replyNode);
        this._npcSpeakObj["reply_" + NPC.id] = replyNode;
        replyNode.active = false;
        console.log("reply init finish");

        let speakNode = instantiate(this.speakNode);
        this.speakLayer.addChild(speakNode);
        this._npcSpeakObj["speak_" + NPC.id] = speakNode;
        speakNode.active = false;
        console.log("speak init finish");

        let speakNodeEx = instantiate(this.speakNode_Ex);
        this.speakLayer.addChild(speakNodeEx);
        this._npcSpeakObj["speakEx_" + NPC.id] = speakNodeEx;
        speakNodeEx.active = false;
        console.log("speakEx init finish");


        let item: Node = null;
        let npcBundle =  assetManager.getBundle("npcAnimation");
        npcBundle.load("npc_" + NPC.id + "/npcPrefab_" + NPC.id,(error,npcPrefab:Prefab)=>{
            if(error || !this._isValid){
                console.log("load npc prefab error====" + error);
                return;
            }
            else{
                item = instantiate(npcPrefab);
                item.active = true;
                this.playerLayer.addChild(item);
                item.getComponent(NpcManager).initNpcData(NPC)
                //this.tiledLayer.addUserNode(item)
                // let canvas = director.getScene().getChildByName("Canvas")
                // item.getComponentInChildren(NPCControl).setCanvasInfo(canvas);
                // item.getComponentInChildren(NPCControl).setBuildingInfo(this.building);
                // item.getComponentInChildren(AStar).setMapInfo(this.tileMap);
                this.otherNPCarr.push(item);
                let npcTile = new Vec2();
                npcTile = this._getTilePos(
                    new Vec2(NPC.x * PosAdapt, this._height - NPC.y * PosAdapt)
                );
                const pos = this._layerFloor.getPositionAt(npcTile)!;
                this._npcTileArray.push(npcTile);
                console.log("npcTile======" + npcTile);
                item.getComponent(NpcManager)._curTile = npcTile;
                item.setPosition(pos.x, pos.y);
                item.getComponent(NpcManager).npcIndex = NpcIndex[NPC.id];
                observer.post(EventType.INIT_DIR_PREFAB,NPC.id);
                if(this._initActionData[NPC.id]){

                }
                else{
                    this._initActionData[NPC.id] = []

                }
                if(NPC.requestData){
                    this._initActionData[NPC.id].push(NPC.requestData);
                }
                this._initActionData[NPC.id].sort((a,b)=>{
                    a.startTime - b.startTime
                })
                //console.log("actionArr===" + JSON.stringify(this._initActionData[NPC.id]))
                this._initActionData[NPC.id].forEach(data=>{
                    if(data.actionId == NpcEventType.move && data.params.path.length > 0){
                        let npcControl = item.getComponent(NpcManager);
                        npcControl.sleepFinish();
                        npcControl.hideBubble();
                        if(NPC.id == 10008){
                            this.stopPepeBoxAction();
                            this.pepeFoodNode.active = false;
                        }
                        else if(NPC.id == 10007){
                            this.catFoodNode.active = false;
                            npcControl.fishFinish();
                        }
                        else if(NPC.id == 10009){
                            // this.muskFoodNode.active = false;
                            // this.muskCoffeeNode.active = false;
                            // this.stopMuskMeeting();
                        }
                        else if(NPC.id == 10010){
                            this.pippinFoodNode.active = false;
                        }
                        let pathEnd = data.params.path[data.params.path.length-1]
                        let height = this.node.getComponent(UITransform).contentSize.height
                        let nowTile = this._getTilePos(v2(pathEnd.x * 32, height - (pathEnd.y*32)));
                        let npcPos = this._layerFloor.getPositionAt(nowTile)!;
                        item.setPosition(npcPos.x,npcPos.y);
                    }
                    else{
                        this.playNPCAction({data:{data:data}});
                    }
                })

            }
        })
        
    }

    // getPartInfo(label: string, id: number) {
    //     return NPCPartDisplayInfo[label]?.find((part) => part.id === id) ?? null;
    // }

    onbtnTest1() {
        //console.log("testPos=====" + JSON.stringify(this.testNode.position));
        // this.startTypeComputer(1);
        // this.startTypeComputer(3);
        // this.startTypeComputer(5);
        // this.startTypeComputer(7);
        // let testData = { "id": 10006, "name": "zhongbencong", "type": 1, "model": 10006, "career": "", "keyword": "", "hair": 100, "top": 90, "bottoms": 100, "speed": 0, "x": 32 * 11, "y": 32 * 9 }
        // this.initOtherNPC(testData);
        //    //npc.setPosition(v3(0,0,0));
        //    console.log("POS=======" + JSON.stringify(npc.position));
    }

    // onbtnTest2(){
    //     // const npc = _.find(this.otherNPCarr, (item) => {
    //     //     return (
    //     //         item.getChildByName("NPCinstantiate").getComponent(NPCControl).NpcID ===
    //     //         10006
    //     //     );
    //     //     });
    //     //   npc
    //     //     .getChildByName("NPCinstantiate")
    //     //     .getComponent(NPCControl)
    //     //     .aStartMove(
    //     //       new Vec2(startTile.x, startTile.y),
    //     //       new Vec2(finishTile.x, finishTile.y),
    //     //     );
    //     //this.node.getComponentInChildren(fixPrefab).setFixOnStatus();
    //     // let startTile = this._getTilePos(
    //     //     new Vec2(npc.getPosition().x, npc.getPosition().y)
    //     // );
    //     // let path = this.node.getComponent(AStar).moveToward(start, finish);
    //     //                 let startTile = this._getTilePos(
    //     //                 new Vec2(npc.getPosition().x, npc.getPosition().y)
    //     //         );
    //     //         eventPox = this.tileObject.getObject(actionData.params.oid);
    //     //         finishTile =  this._getTilePos(new Vec2(eventPox.x, eventPox.y));

    //     //                 console.log("samTest1====" + JSON.stringify(finishTile))
    //     //                 this.stopActionNormal(npc, actionData);
    //     //                 npc
    //     //                     .getChildByName("NPCinstantiate")
    //     //                     .getComponent(NPCControl)
    //     //                     .aStartMove(
    //     //                     new Vec2(startTile.x, startTile.y),
    //     //                     new Vec2(finishTile.x, finishTile.y),
    //     //                     );
    //     this.playNPCAction({
    //         data:{
    //           data:{
    //             bid: 10,
    //             actionId: 112,
    //             npcId: 10006,
    //             params: {
    //               oid: "zhongbencongFix",
    //               path:
    //             },
    //           }
    //         }
    //     })
    // }

    onbtnTest3() {
        //let arr = [{ x: 11, y: 9 }, { x: 11, y: 10 }, { x: 11, y: 11 }, { x: 11, y: 12 }, { x: 11, y: 13 }, { x: 11, y: 14 }, { x: 11, y: 15 }, { x: 11, y: 16 }, { x: 11, y: 17 }, { x: 11, y: 18 }, { x: 11, y: 19 }, { x: 11, y: 20 }, { x: 11, y: 21 }, { x: 11, y: 22 }, { x: 11, y: 23 }, { x: 11, y: 24 }, { x: 11, y: 25 }, { x: 11, y: 26 }, { x: 11, y: 27 }, { x: 11, y: 28 },{ x: 11, y: 29 }, { x: 11, y: 30 }, { x: 11, y: 31 }, { x: 11, y: 32 }, { x: 11, y: 33 }, { x: 11, y: 34 }, { x: 11, y: 35 }, { x: 11, y: 36 },{ x: 11, y: 37 },{ x: 11, y: 38 },{ x: 11, y: 39},{ x: 11, y: 40}];
        let arr = [{ x: 11, y: 9 },{ x:12 , y: 9 },{ x: 13, y: 9 },{ x: 14, y: 9 },{ x: 15, y: 9 },{ x: 16, y: 9 },{ x: 17, y: 9 },{ x: 18, y: 9 },{ x: 19, y: 9 }];
        this.playNPCAction({
            data: {
                data: {
                    bid: 10,
                    actionId: 112,
                    npcId: 10006,
                    params: {
                        oid: "zhongbencongFix",
                        path: arr
                    },
                }
            }
        })
    }


    onbtnUp() {
        this.otherNPCarr[0].getComponent(NpcManager).setUpAnimation()
    }

    onbtnDown() {
        this.otherNPCarr[0].getComponent(NpcManager).setDownAnimation()
    }

    onbtnleft() {
        this.otherNPCarr[0].getComponent(NpcManager).setLeftAnimation()
    }

    onbtnright() {
        this.otherNPCarr[0].getComponent(NpcManager).setRightAnimation()
    }

    startScrollNum() {
        // tween(this.scrollNumNode).repeatForever(tween(this.scrollNumNode).call(() => {
        //     let nodes = this.getRandomElements(this.scrollNumNode.children, 30);
        //     nodes.forEach(node => {
        //         node.getComponent(numScrollNode).startRunDownAction();
        //     })
        // }).delay(3).start()).start();
        // tween(this.node).delay(3).call(() => {
        //     tween(this.scrollNumNode_2).repeatForever(tween(this.scrollNumNode_2).delay(3).call(() => {
        //         let nodes = this.getRandomElements(this.scrollNumNode_2.children, 30);
        //         nodes.forEach(node => {
        //             node.getComponent(numScrollNode).startRunDownAction();
        //         })
        //     }).start()).start();
        // }).start()
    }

    getRandomElements(arr, count) {
        // 创建一个数组的副本以避免修改原始数组  
        let shuffled = arr.slice();
        let result = [];

        // 当还需要选择的元素数量大于0，且数组还有元素时继续  
        while (count > 0 && shuffled.length > 0) {
            // 从数组中随机选择一个索引  
            let randomIndex = Math.floor(Math.random() * shuffled.length);
            // 将选中的元素添加到结果数组中  
            result.push(shuffled[randomIndex]);
            // 从数组中移除该元素  
            shuffled.splice(randomIndex, 1);
            // 减少还需要选择的元素数量  
            count--;
        }
        return result;
    }

    satoshiComputerAction() {
        // this.typeComputerNode.active = true;
        // let delayTime = Math.random() * 1 + 0.5;
        // tween(this.typeComputerNode).delay(delayTime).call(() => {
        //     this.typeComputerNode.children.forEach(node => {
        //         node.active = !node.active;
        //     })
        //     this.satoshiComputerAction();
        // }).start()
    }

    startTypeComputer(computerId){
        // if(this._typeComputerInfo[computerId]){
        //     if(this._typeComputerInfo[computerId].status == "off"){
        //         this._typeComputerInfo[computerId].status = "on";
        //         Tween.stopAllByTarget(this._typeComputerInfo[computerId].node);
        //         this._typeComputerInfo[computerId].node.active = true;
        //         this.startTypeAction(computerId)
        //     }
        // }
    }

    startTypeAction(computerId){
        // let delayTime = Math.random() * 1 + 0.5;
        // Tween.stopAllByTarget(this._typeComputerInfo[computerId].node);
        // tween(this._typeComputerInfo[computerId].node).delay(delayTime).call(()=>{
        //     this._typeComputerInfo[computerId].node.children.forEach(node=>{
        //         node.active = !node.active;
        //         this.startTypeAction(computerId);
        //     })
        // }).start()
    }

    endTypeComputer(computerId){
        // this._typeComputerInfo[computerId].status = "off";
        // Tween.stopAllByTarget(this._typeComputerInfo[computerId].node);
        // this._typeComputerInfo[computerId].node.active = false;
    }

    checkTypeComputer(){
        // for(let i = 1 ; i <=12; i++){
        //     if(this._typeComputerInfo[i].botNum > 0){
        //         this.startTypeComputer(i);
        //     }
        //     else{
        //         this.endTypeComputer(i)
        //     }
        // }    
    }

    getRandomPosition(){
        let dirX = Math.random() > 0.5 ? 1 : -1;
        let dirY = Math.random() > 0.5 ? 1 : -1;
        let posY = 1400 * dirY;
        let posX =  dirX * 1400 * Math.random();

        return v3(posX,posY,0);
    }

    // startPepeBoxAction(){
    //     this.pepeBoxNode.active = true;
    //     Tween.stopAllByTarget(this.pepeBoxNode);
    //     let layoutNode = this.pepeBoxNode.getChildByName("Layout");
    //     layoutNode.children.forEach(spr=>{
    //         spr.active = false;
    //     })
    //     tween(this.pepeBoxNode).repeatForever(tween(this.pepeBoxNode).delay(0.2).call(()=>{
    //         for(let i = 0 ; i< layoutNode.children.length;i++){
    //             if(!layoutNode.children[i].active){
    //                 layoutNode.children[i].active = true;
    //                 break;
    //             }
    //             else if( i== layoutNode.children.length - 1){
    //                 layoutNode.children.forEach(spr=>{
    //                     spr.active = false;
    //                 })
    //             }
    //         }
    //     }).start()).start()
    // }

    stopPepeBoxAction(){
        this.pepeBoxNode.active = false;
    }

    startMuskMeeting(){
        // this.meetingNode.active = true;
        // this.meetingNode.children[0].active = true;
        // this.meetingNode.children[1].active = false;
        // tween(this.meetingNode).repeatForever(tween(this.meetingNode).delay(0.5).call(()=>{
        //     this.meetingNode.children[0].active = !this.meetingNode.children[0].active;
        //     this.meetingNode.children[1].active = !this.meetingNode.children[1].active
        // }).start()).start();
    }

    stopMuskMeeting(){
        //this.meetingNode.active = false;
    }

    async reconect(){
        console.log("reconct 111111111");
        let json = new network.GetAllNPCRequest();
        json.command = 10012;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = GlobalConfig.instance.chooseScene;
        socket.sendWebSocketBinary(json);

        director.getScene().getComponentInChildren(UILayer).initVoteInfo();
    }

    async reEnterRoom(){
        await this.playerLayer.destroyAllChildren();
        await this.speakLayer.destroyAllChildren();
        this.otherNPCarr = [];
        this._initActionData = {};
        this._npcSpeakObj = {};
        this._playerArr = [];
        await this._initData();
    }

    // async createPlayer(playerData){
        
    //     console.log("playerData========" + JSON.stringify(playerData.data.data.myNpc));
    //     let npcData = playerData.data.data.myNpc;

    //     let speakNode = instantiate(this.speakNode);
    //     this.speakLayer.addChild(speakNode);
    //     this._npcSpeakObj["speak_" + npcData.id] = speakNode;
    //     speakNode.active = false;
    //     console.log("speak init finish");

    //     let speakNodeEx = instantiate(this.speakNode_Ex);
    //     this.speakLayer.addChild(speakNodeEx);
    //     this._npcSpeakObj["speakEx_" + npcData.id] = speakNodeEx;
    //     speakNodeEx.active = false;
    //     console.log("speakEx init finish");

    //     let item: Node = null;
    //     let npcBundle =  assetManager.getBundle("createPlayer");
    //     this._createTimeInfo[npcData.model] = new Date().getTime();
    //     GlobalConfig.instance.CreateIDArr[npcData.model] = 300000;
    //     npcBundle.load("playerModel_" + npcData.model,(error,playerPrefab:Prefab)=>{
    //         if(error){
    //             console.log("load npc prefab error====" + error);
    //             return;
    //         }
    //         else{
    //             item = instantiate(playerPrefab);
    //             item.active = true;
    //             this.playerLayer.addChild(item);
    //             item.getComponent(PlayerManager).initPlayerData(npcData)
    //             //this.tiledLayer.addUserNode(item)
    //             // let canvas = director.getScene().getChildByName("Canvas")
    //             // item.getComponentInChildren(NPCControl).setCanvasInfo(canvas);
    //             // item.getComponentInChildren(NPCControl).setBuildingInfo(this.building);
    //             // item.getComponentInChildren(AStar).setMapInfo(this.tileMap);
    //             this._playerArr.push(item);
    //             let npcTile = new Vec2();
    //             npcTile = this._getTilePos(
    //                 new Vec2(npcData.x * PosAdapt, this._height - npcData.y * PosAdapt)
    //             );
    //             const pos = this._layerFloor.getPositionAt(npcTile)!;
    //             this._npcTileArray.push(npcTile);
    //             item.setPosition(pos.x, pos.y);
    //             console.log("npcTile======" + npcTile);
    //             item.getComponent(PlayerManager)._curTile = npcTile;
    //             if(this._initActionData[npcData.id]){

    //             }
    //             else{
    //                 this._initActionData[npcData.id] = []

    //             }
    //             if(npcData.requestData){
    //                 this._initActionData[npcData.id].push(npcData.requestData);
    //             }
    //             this._initActionData[npcData.id].sort((a,b)=>{
    //                 a.startTime - b.startTime
    //             })
    //             //console.log("actionArr===" + JSON.stringify(this._initActionData[NPC.id]))
    //             this._initActionData[npcData.id].forEach(data=>{
    //                 if(data.actionId == NpcEventType.move && data.params.path.length > 0){
    //                     item.getComponent(PlayerManager).serverPathMove(data.params.path);
    //                 }
    //                 else{
    //                     this.playPlayerAction({data:{data:data}});
    //                 }
    //             })
    //             if(GlobalConfig.instance.isStoryModel){
    //                 return;
    //             }
    //             if(npcData.userNo == GlobalConfig.instance.LoginData.data.player.playerId){
    //                 GlobalConfig.instance.isCreatePlayer = true;
    //                 this._myPlayerNode = item;
    //                 director.getScene().getComponentInChildren(GameScene).flollowPlayer();
    //             }
    //         }
    //     })
    // }

    getPlayerNode(){
        return this._myPlayerNode;
    }

    // playPlayerAction(actionData){
    //     console.log("playeractionData======" + JSON.stringify(actionData));
    //     let playerNode = null;
    //     this.playerLayer.getComponentsInChildren(PlayerManager).forEach(playerScript=>{
    //         if(playerScript._id == actionData.npcId){
    //             playerNode = playerScript.node
    //         }
    //     })
    //     if(!playerNode){
    //         console.log("npc 初始化未完成")
    //         if(this._initActionData[actionData.npcId]){
    //             this._initActionData[actionData.npcId].push(actionData)
    //         }
    //         else{
    //             this._initActionData[actionData.npcId] = [];
    //             this._initActionData[actionData.npcId].push(actionData)
    //         }
    //         return;
    //     }
    //     let eventPox
    //     let finishTile
    //     let playerControl = playerNode.getComponent(PlayerManager)
    //     try {
    //         //前端执行NPC动作逻辑
    //         if (actionData.actionId === NpcEventType.move) {
    //             if (actionData.params.path.length > 0) {
    //                 console.log( actionData.npcId + "startMove");
    //                 playerControl.serverPathMove(actionData.params.path);
    //             }
    //         }
    //         else if (actionData.actionId === NpcEventType.speak) {
    //             playerControl.speak(actionData.params.content);
    //         }
    //         else {
    //             console.log("error actionID======" + actionData.actionId);
    //             if (actionData.params.oid) {
    //             }
    //         }

    //         ////前端执行NPC动作引发的场景逻辑

    //     } catch (error) {

    //     }
    // }

    onPlayerTouch(event: EventTouch){
        const touchLocation = event.getLocation();
        console.log("touchLocation====" + touchLocation)
        //const worldPoint = this.node.getComponent(UITransform).convertToWorldSpaceAR(new Vec3(touchLocation.x, touchLocation.y, 0));
        const worldPoint = this.screenToWorld(touchLocation);
        const targetPos = this.playerLayer.getComponent(UITransform).convertToNodeSpaceAR(worldPoint);
        const touchTile = this._getTilePos(targetPos)
        console.log("touchPoint======" + JSON.stringify(targetPos));
        console.log("touchTile=======" + JSON.stringify(touchTile));
        if(this._myPlayerNode){
            let json = new network.GetAllNPCRequest();
            json.command = 10024;
            json.type = 1;
            json["data"] = {
                gridX:touchTile.x,
                gridY:touchTile.y + 1,
            }
            socket.sendWebSocketBinary(json);
        }
    }

    screenToWorld(screenPosition: Vec2): Vec3 {
        // 调用摄像机的 screenToWorld 方法
        const worldPosition = new Vec3();
        director.getScene().getComponentInChildren(Camera).screenToWorld(new Vec3(screenPosition.x, screenPosition.y, 0), worldPosition);
        return worldPosition;
    }

    // destroyPlayer(repData){
    //     repData.data.data.npcIds.forEach(playerId=>{
    //         this.playerLayer.getComponentsInChildren(PlayerManager).forEach(playerScript=>{
    //             if(playerScript._id == playerId){
    //                 if(playerScript._isSelf){
    //                     let sceneScript = director.getScene().getComponentInChildren(GameScene);
    //                     sceneScript.cancelFollowPlayer();
    //                     this._myPlayerNode = null;
    //                     observer.post(EventType.DESTROYGUIDE);
    //                 }
    //                 playerScript.node.destroy();
    //             }   
    //         })
    //     })
    //     // let isMyself = false;
    //     // if(this._myPlayerNode && isMyself){
    //     //     this._myPlayerNode.destroy();
    //     //     this._myPlayerNode = null;
    //     // }
    // }

    // initStoryNpcs(){
    //     let satoshiData:NPCServerD = {"id":10006,"name":"satoshi","type":6,"model":10006,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1136,"y":3532,requestData:null,items: null};
    //     this.initOtherNPC(satoshiData);

    //     let popcatData:NPCServerD = {"id":10007,"name":"popcat","type":6,"model":10007,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1072,"y":3532,requestData:null,items: null};
    //     this.initOtherNPC(popcatData);

        
    //     let pepeData:NPCServerD = {"id":10008,"name":"popcat","type":6,"model":10008,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1008,"y":3532,requestData:null,items: null};
    //     this.initOtherNPC(pepeData);

    //     let muskData:NPCServerD = {"id":10009,"name":"popcat","type":6,"model":10009,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1008,"y":3596,requestData:null,items: null};
    //     this.initOtherNPC(muskData);

    //     let pipinData:NPCServerD = {"id":10010,"name":"popcat","type":6,"model":10010,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1008,"y":3660,requestData:null,items: null};
    //     this.initOtherNPC(pipinData);

        
    //     let elizaData:NPCServerD = {"id":10011,"name":"popcat","type":6,"model":10011,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1072,"y":3660,requestData:null,items: null};
    //     this.initOtherNPC(elizaData);

                
    //     let trumData:NPCServerD = {"id":10012,"name":"popcat","type":6,"model":10012,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1136,"y":3660,requestData:null,items: null};
    //     this.initOtherNPC(trumData);

    //     let morhpeus:NPCServerD = {"id":10013,"name":"popcat","type":6,"model":10013,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1200,"y":3660,requestData:null,items: null};
    //     this.initOtherNPC(morhpeus);

    //     let ava:NPCServerD = {"id":10014,"name":"popcat","type":6,"model":10014,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1264,"y":3660,requestData:null,items: null};
    //     this.initOtherNPC(ava);

    //     let player_1 = {"id":11111,"name":"popcat","type":6,"model":1,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1300,"y":3660,requestData:null,userNo:"xxxxx"};
    //     this.createPlayer({data:{data:{myNpc:player_1}}});

        
    //     let player_2 = {"id":22222,"name":"popcat","type":6,"model":2,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1300,"y":3596,requestData:null,userNo:"xxxxx"};
    //     this.createPlayer({data:{data:{myNpc:player_2}}});

    //     let player_3 = {"id":33333,"name":"popcat","type":6,"model":3,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1300,"y":3532,requestData:null,userNo:"xxxxx"};
    //     this.createPlayer({data:{data:{myNpc:player_3}}});

        
    //     let player_4 = {"id":44444,"name":"popcat","type":6,"model":4,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1264,"y":3532,requestData:null,userNo:"xxxxx"};
    //     this.createPlayer({data:{data:{myNpc:player_4}}});

    //     let player_5 = {"id":55555,"name":"popcat","type":6,"model":5,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1200,"y":3532,requestData:null,userNo:"xxxxx"};
    //     this.createPlayer({data:{data:{myNpc:player_5}}});

    //     let player_6 = {"id":66666,"name":"popcat","type":6,"model":6,"career":"","keyword":"","hair":100,"top":100,"bottoms":100,"speed":0,"x":1168,"y":3532,requestData:null,userNo:"xxxxx"};
    //     this.createPlayer({data:{data:{myNpc:player_6}}});
    //     // tween(this.node).delay(2).call(()=>{
    //     //     let start = new Vec2(35, 109); // 确保起点为有效坐标
    //     //     let finish = new Vec2(50, 104); // 确保终点为有效坐标
    //     //     let paths  = this.node.getComponent(AStar).moveToward(start,finish);
    //     //     console.log("paths======" + JSON.stringify(paths));
    //     //     this.node.getComponentInChildren(NpcManager).serverPathMove(paths);
    //     // }).start()
    //     //35,108
    //     //50,104

    // }
    // initAllNftStatus(data){
    //     console.log("nftData=======" + JSON.stringify(data.data.data.nftInfo));
    //     for(let i in data.data.data.nftInfo){
    //         this.node.getComponentsInChildren(nftPrefab).forEach(nftSript=>{
    //             if(Number(i) == nftSript.nftId){
    //                 nftSript.setStatus(data.data.data.nftInfo[i]);
    //             }
    //         })
    //     }
    // }

    // changeNftStatus(data){
    //     console.log("changeNftData=======" + JSON.stringify(data.data.data.nftInfo));
    //     this.node.getComponentsInChildren(nftPrefab).forEach(nftSript=>{
    //         if(data.data.data.nftInfo.id == nftSript.nftId){
    //             nftSript.setStatus(data.data.data.nftInfo);
    //         }
    //     })
    // }
}


