import { _decorator, Camera, Component, director, instantiate, Node, Prefab, tween } from 'cc';
import { network } from '../../../src/model/RequestData';
import { observer, socket } from '../../../src/game/App';
import { EventType } from '../../../src/EventType';
import { GlobalConfig } from '../../../src/game/config/GlobalConfig';
import { optionPrefab } from './optionPrefab';
import { sceneItem } from './sceneItem';
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

    @property(Node)
    maskNode:Node = null;
    _orignOrthoHeight = null;
    protected onLoad(): void {
        director.addPersistRootNode(this.worldNode);
        observer.on(EventType.GETLOBBYINFO,this.getLobbyInfo,this);
        observer.on(EventType.REQUESTENTERSCENE,this.enterScene,this);
        observer.on("ChooseBtnScene",this.onChooseClick,this);
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
    }

    _initData(){

        let json = new network.GetAllNPCRequest();
        json.command = 10109;
        json.type = 1;
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
                    option.setUnChooseStatus;
                }
            })
        }
    }

    onBtnTest1(){
        // let json = new network.GetAllNPCRequest();
        // json.command = 10012;
        // json.type = 1;
        // json["data"] = {};
        // json["data"]["roomId"] = 1;
        // socket.sendWebSocketBinary(json);
    }

    enterScene(data){
        // GlobalConfig.instance.nowSceneData = data.data.data;
        // console.log("scene data=========" + JSON.stringify(data.data));
    }

    onChooseClick(data){
        console.log("chooseClick" + data.data);
        this.node.getComponentsInChildren(sceneItem).forEach(itemScript=>{
            if(itemScript._sceneId == Number(data.data)){
                itemScript.node.active = true;
            }
            else{
                itemScript.node.active = false;
            }
        })

    }

    showMaskNode(){
        this.maskNode.active = true;
        tween(this.maskNode).delay(15).call(()=>{
            this.maskNode.active = false;
        }).start();
    }
}


