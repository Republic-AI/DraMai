import { _decorator, Camera, Component, director, instantiate, Node, Prefab, ScrollView, tween } from 'cc';
import { network } from '../../../src/model/RequestData';
import { observer, socket } from '../../../src/game/App';
import { EventType } from '../../../src/EventType';
import { GlobalConfig } from '../../../src/game/config/GlobalConfig';
import { optionPrefab } from './optionPrefab';
import { sceneItem } from './sceneItem';
import { twitterModeType_1 } from './twitterModeType_1';
import { twitterModeType_2 } from './twitterModeType_2';
const { ccclass, property } = _decorator;

@ccclass('lobbyScene')
export class lobbyScene extends Component {
    @property(Node)
    optionBtnLayout:Node = null;

    @property(Node)
    sceneViewContent:Node = null;

    @property(Node)
    worldNode:Node = null;

    @property(Prefab)
    optionPrefab:Prefab = null;

    @property(Prefab)
    sceneItem:Prefab = null;

    @property(Camera)
    mainCamera:Camera = null;

    @property(ScrollView)
    sceneItemView:ScrollView = null;

    @property(ScrollView)
    twitterView:ScrollView = null;

    @property(Node)
    twitterViewContent:Node = null;

    @property(Node)
    maskNode:Node = null;

    @property(Prefab)
    twitterModeType_1:Prefab = null;

    @property(Prefab)
    twitterModeType_2:Prefab = null;

    @property(Node)
    status_1:Node = null;
    
    @property(Node)
    status_2:Node = null;

    _orignOrthoHeight = null;
    _chooseSceneId = 1;
    _pageStatus = null
    protected onLoad(): void {
        director.addPersistRootNode(this.worldNode);
        observer.on(EventType.GETLOBBYINFO,this.getLobbyInfo,this);
        observer.on(EventType.REQUESTENTERSCENE,this.enterScene,this);
        observer.on("ChooseBtnScene",this.onChooseClick,this);
        observer.on(EventType.INITTWITTERVIEW,this.initTwitterView,this);
    }
    
    _lobbyRoomInfo = null;
    _isInit
    start() {
        this._initData()
    }

    update(deltaTime: number) {
        
    }

    protected onDestroy(): void {
        observer.off(EventType.GETLOBBYINFO,this.getLobbyInfo,this);
        observer.off(EventType.REQUESTENTERSCENE,this.enterScene,this);
        observer.off("ChooseBtnScene",this.onChooseClick,this);
        observer.off(EventType.INITTWITTERVIEW,this.initTwitterView,this);
    }

    _initData(){
        this._pageStatus = 1;
        let json = new network.GetAllNPCRequest();
        json.command = 10109;
        json.type = 1;
        socket.sendWebSocketBinary(json);

        this.twitterView.node.active = false;
        this.sceneItemView.node.active = true;

        this.initRequestTwitter()

    }

    initRequestTwitter(){
        let json = new network.GetAllNPCRequest();
        json.command = 10112;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = 0;
        json["data"]["page"] = 0;
        json["data"]["size"] = 999;
        socket.sendWebSocketBinary(json);
    }

    getLobbyInfo(data){
        if(!this._lobbyRoomInfo){
            console.log("lobby data========" + JSON.stringify(data.data.data.roomDataList));
            this._lobbyRoomInfo = data.data.data.roomDataList;
            this._lobbyRoomInfo.forEach((scene,index)=>{
                let optionNode = instantiate(this.optionPrefab);
                this.optionBtnLayout.addChild(optionNode);
                optionNode.getComponent(optionPrefab).initData(scene.id)
                scene.npcList.forEach(npcID=>{
                    let sceneItemNode = instantiate(this.sceneItem);
                    this.sceneViewContent.addChild(sceneItemNode);
                    sceneItemNode.getComponent(sceneItem).initData(npcID,scene.id)
                })
            })
            this.node.getComponentsInChildren(optionPrefab).forEach((option,index)=>{
                if(option._sceneId == 1){
                    option.onBtnClick();
                }
                else{
                    option.setUnChooseStatus();
                }
            })
            this.optionBtnLayout.setPosition(800,0,0);
        }
    }

    onBtnTest1(){
        // let json = new network.GetAllNPCRequest();
        // json.command = 10112;
        // json.type = 1;
        // json["data"] = {};
        // json["data"]["roomId"] = 1;
        // json["data"]["page"] = 0;
        // json["data"]["size"] = 999;
        // socket.sendWebSocketBinary(json);


    }

    enterScene(data){
        // GlobalConfig.instance.nowSceneData = data.data.data;
        // console.log("scene data=========" + JSON.stringify(data.data));
    }

    onChooseClick(data){
        console.log("chooseClick" + data.data);
        this._chooseSceneId = data.data;
        this.node.getComponentsInChildren(sceneItem).forEach(itemScript=>{
            if(itemScript._sceneId == Number(data.data)){
                itemScript.node.active = true;
            }
            else{
                itemScript.node.active = false;
            }
        })
        this.twitterViewContent.children.forEach(node=>{
            if(node.getComponent(twitterModeType_1)){
                if(node.getComponent(twitterModeType_1)._roomId == Number(data.data)){
                    node.active = true;
                }
                else{
                    node.active = false;
                }
            }
            else if(node.getComponent(twitterModeType_2)){
                if(node.getComponent(twitterModeType_2)._roomId == Number(data.data)){
                    node.active = true;
                }
                else{
                    node.active = false;
                }
            }
        })
    }

    showMaskNode(){
        this.maskNode.active = true;
        tween(this.maskNode).delay(15).call(()=>{
            this.maskNode.active = false;
        }).start();
    }

    initTwitterView(){
        if(this.twitterViewContent.children.length == 0){
            GlobalConfig.instance.twitterData.forEach(twitInfo=>{
                if(twitInfo.tweetType == 2){
                    let twitterNode = instantiate(this.twitterModeType_1);
                    this.twitterViewContent.addChild(twitterNode);
                    twitterNode.getComponent(twitterModeType_1).initData(twitInfo)
                }
                else{
                    let twitterNode = instantiate(this.twitterModeType_2);
                    this.twitterViewContent.addChild(twitterNode);
                    twitterNode.getComponent(twitterModeType_2).initData(twitInfo)
                }
            })

            this.twitterViewContent.children.forEach(node=>{
                if(node.getComponent(twitterModeType_1)){
                    if(node.getComponent(twitterModeType_1)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
                else if(node.getComponent(twitterModeType_2)){
                    if(node.getComponent(twitterModeType_2)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
            })
        }
    }

    onBtnChangeStatus(){
        if(this._pageStatus == 1){
            this._pageStatus = 2;
        }
        else{
            this._pageStatus = 1;
        }
        this.status_1.active = this._pageStatus == 1 ? true : false;
        this.status_2.active = this._pageStatus == 2 ? true : false;
        if(this._pageStatus == 2){
            this.sceneItemView.node.active = false;
            this.twitterView.node.active = true;
            this.twitterViewContent.children.forEach(node=>{
                if(node.getComponent(twitterModeType_1)){
                    if(node.getComponent(twitterModeType_1)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
                else if(node.getComponent(twitterModeType_2)){
                    if(node.getComponent(twitterModeType_2)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
            })
        }
        else{
            this.sceneItemView.node.active = true;
            this.twitterView.node.active = false;
        }
    }

}


