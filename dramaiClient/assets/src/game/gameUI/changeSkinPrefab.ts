import { _decorator, assetManager, Component, director, EditBox, instantiate, JsonAsset, Label, Node, Prefab, resources, ScrollView, Sprite, SpriteFrame, tween, UITransform, v3 } from 'cc';
import { NpcManager } from '../../NPC/NpcManager';
import { npcDes, NpcRoomIndex, npcSkinCfg, sceneItemAllCfg } from '../../StaticUtils/NPCConfig';
import { npcItemPrefab } from './npcItemPrefab';
import { network } from '../../model/RequestData';
import { GlobalConfig } from '../config/GlobalConfig';
import { socket } from '../App';
import { chatPrefab } from './chatPrefab';
import { GameScene } from '../scene/GameScene';
import { changeSkinItem } from './changeSkinItem';
import { changeSceneItem } from './changeSceneItem';
const { ccclass, property } = _decorator;
@ccclass('changeSkinPrefab')
export class changeSkinPrefab extends Component {
    @property(ScrollView)
    skinView:ScrollView = null;

    @property(Label)
    lblNpcDes:Label = null;

    @property(Label)
    lblGoldNum:Label = null;

    @property(EditBox)
    chatEditBox:EditBox = null;

    @property(Node)
    chatViewContent:Node = null;

    @property(Prefab)
    chatPrefab:Prefab = null;

    @property(Node)
    skinViewContent:Node = null;

    @property(Node)
    sceneViewContent:Node = null;

    @property(Sprite)
    imgNpcFrame:Sprite = null;

    @property(Prefab)
    changeSkinItem:Prefab = null;

    
    @property(Prefab)
    changeSkinItem_lock:Prefab = null;

    @property(Prefab)
    changeSceneItem:Prefab = null;

    @property(Prefab)
    changeSceneItem_lock:Prefab = null;

    @property(Node)
    chatNode:Node = null;

    @property(Node)
    changeNode:Node = null;

    @property
    _goldNum = 0;
    _itemCfg = null;
    _ownItem = {}
    _npcId = null;
    _isEditing = false;

    _nowSkinData = null;
    _skinIndex = 0;
    _skinFrameArr = null;
    _isInit = false;
    protected onLoad(): void {
        // resources.load("json/itemCfg",(error,itemCfg:JsonAsset)=>{
        //     if(error){
        //         console.log("load item Cfg error:" + error);
        //         return
        //     }
        //     this._itemCfg = itemCfg.json;
        // })
        this.changeNode.active = false;
        this.chatNode.active = true;
    }
    start() {

    }

    update(deltaTime: number) {
        if(!this.chatEditBox.string && !this._isEditing){
            this.chatEditBox.string = " ";
        }
    }

    initData(npcId){
        this.chatViewContent.getComponentsInChildren(chatPrefab).forEach(prefab=>{
            if(prefab._npcId == npcId || prefab.toNpcId == npcId){
                prefab.node.active = true;
            }
            else{
                prefab.node.active = false;
            }
        })
        this._npcId = npcId
        director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
            if(npcScript.NpcID == this._npcId){
                this._skinFrameArr = npcScript.getComponent(NpcManager).skinFrameArr;
                this._nowSkinData = npcScript._skinId;
                npcSkinCfg[this._npcId].forEach((skinId,index)=>{
                    if(skinId == this._nowSkinData){
                        this._skinIndex = index;
                        this.setSkinStatus();
                    }
                })
            }
        })
        this.skinViewContent.destroyAllChildren();
        npcSkinCfg[this._npcId].forEach((skinId,index)=>{
            if(skinId){
                let skinItemNode = instantiate(this.changeSkinItem);
                this.skinViewContent.addChild(skinItemNode);
                skinItemNode.getComponent(changeSkinItem).initData(this._npcId,skinId,this._skinFrameArr[index]);
            }
        })

        for(let i = 0 ;i<6;i++){
            let skinLockNode = instantiate(this.changeSkinItem_lock);
            this.skinViewContent.addChild(skinLockNode);
        }
        tween(this.node).delay(0.1).call(()=>{
            this.skinViewContent.setPosition(850,0,0);
        }).start()

        this.sceneViewContent.destroyAllChildren();
        sceneItemAllCfg[this._npcId].forEach(sceneItemInfo=>{
            let sceneItemNode = instantiate(this.changeSceneItem);
            this.sceneViewContent.addChild(sceneItemNode);
            sceneItemNode.getComponent(changeSceneItem).initData(sceneItemInfo)

        })

        for(let i = 0 ;i<6;i++){
            let sceneItem_lock = instantiate(this.changeSceneItem_lock);
            this.sceneViewContent.addChild(sceneItem_lock);
        }
        tween(this.node).delay(0.1).call(()=>{
            this.sceneViewContent.setPosition(920,0,0);
        }).start()
        //this.skinViewContent.setPosition(0,0,0);
        // this.skinViewContent.children.forEach((skinNode,index)=>{
        //     if(index < this._skinFrameArr.length){
        //         console.log("frameArr=====" + JSON.stringify(this._skinFrameArr[index].name))
        //         skinNode.getComponentInChildren(Sprite).spriteFrame = this._skinFrameArr[index];
        //     }
        // })
        // let npcBundle =  assetManager.getBundle("npcAnimation");
        // npcBundle.load("npc_" + npcId+ "/npcPrefab_" + npcId,(error,npcPrefab:Prefab)=>{
        //     if(error){
        //         console.log("load npc prefab error====" + error);
        //         return;
        //     }
        //     else{
        //         let node = instantiate(npcPrefab);
        //         this._skinFrameArr = node.getComponent(NpcManager).skinFrameArr;
        //     }
        // })
        if(npcDes[npcId]){
            this.lblNpcDes.string = npcDes[npcId];
            tween(this.lblNpcDes.node).delay(0.1).call(()=>{
                let targetY = this.lblNpcDes.node.getComponent(UITransform).contentSize.height
                this.lblNpcDes.node.setPosition(0,-targetY/4 - 40);
            }).start();
        }
        resources.load("json/itemCfg",(error,itemCfg:JsonAsset)=>{
            if(error){
                console.log("load item Cfg error:" + error);
                return
            }
            this._itemCfg = itemCfg.json;
            let ownItem = {}
            this._itemCfg.forEach(itemInfo=>{
                if(Number(itemInfo.npcId) == Number(npcId)){
                    ownItem[itemInfo.id] = 1;
                }
            })
            console.log("ownItem=====" + JSON.stringify(ownItem));
            let npcItems = [];
            director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
                if(npcScript.NpcID == npcId){
                    npcItems = npcScript._initData.items
                }
            })
            let myItemInfo = [];
            let otherItemInfo = [];
            let coinNum = 0;
            npcItems.forEach(itemInfo=>{
                if(itemInfo.itemId == 10101000){
                    coinNum = itemInfo.count;
                    this.setGoldNum(coinNum);
                }
                else if(ownItem[itemInfo.itemId]){
                    myItemInfo.push(itemInfo)
                }
                else{
                    otherItemInfo.push(itemInfo);
                }
            })
            console.log("myItemInfo======" + JSON.stringify(myItemInfo));
            console.log("otherItemInfo======" + JSON.stringify(otherItemInfo));
            const mergedArray = myItemInfo.concat(otherItemInfo);
            this.node.getComponentsInChildren(npcItemPrefab).forEach((itemPrefab,index)=>{
                if(mergedArray[index]){
                    itemPrefab.initData(mergedArray[index])
                }
                else{
                    itemPrefab.imgItem.spriteFrame = null;
                    itemPrefab.lblItem.string = "";
                }
            })
            
        })
        tween(this.node).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 700){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()
        if(!this._isInit){
            let chatAllRecord = GlobalConfig.instance.chatRecord;
            for(let i in chatAllRecord){
                if(NpcRoomIndex[i] == GlobalConfig.instance.chooseScene){
                    let chatRecordData = chatAllRecord[i]
                    let chatNpcId = i;
                    if(chatRecordData && chatRecordData.length > 0){
                        chatRecordData.forEach(chatInfo=>{
                            let chatPrefabNode = instantiate(this.chatPrefab);
                            if(chatInfo.npcSend){
                                chatPrefabNode.getComponent(chatPrefab).initData(chatNpcId,chatInfo.content)
                            }
                            else{
                                chatPrefabNode.getComponent(chatPrefab).initData(0,chatInfo.content)
                                chatPrefabNode.getComponent(chatPrefab).setToNpcId(chatNpcId);
                            }
                            this.chatViewContent.addChild(chatPrefabNode);
                            if(chatNpcId!=this._npcId){
                                tween(this.node).delay(0.02).call(()=>{
                                    chatPrefabNode.active = false;
                                }).start()
                            }
                        })
                    }
                }
            }
        }
        this._isInit = true;
        // tween(this.node).delay(0.1).call(()=>{
        //     if(this.chatViewContent.getComponent(UITransform).contentSize.height > 700){
        //         this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
        //     }
        // }).start()
    }

    setGoldNum(coinNum){
        this._goldNum = coinNum;
        this.lblGoldNum.string = this._goldNum.toString();
        if(this._goldNum > 999999){
            tween(this.node).delay(0.1).call(()=>{
                let targetX = this.lblGoldNum.node.getComponent(UITransform).contentSize.width / 2  + this.lblGoldNum.node.parent.getComponent(UITransform).contentSize.width/2;
                this.lblGoldNum.node.setPosition(v3(targetX,-2,0))
                tween(this.lblGoldNum.node).repeatForever(
                    tween(this.lblGoldNum.node).
                    to(3,{position:v3(-targetX,-2,0)}).
                    call(()=>{
                        this.lblGoldNum.node.setPosition(v3(targetX,-2,0))
                    }).start())
                    .start()
            }).start()
        }
        else{
            this.lblGoldNum.node.setPosition(v3(0,-2,0))
        }
    }
    onBtnSend(){
        if(this.chatEditBox.node.active && this.chatEditBox.string.length < 1){
            return;
        }
        let isOnlySpace = true;
        for (let i = 0 ; i < this.chatEditBox.string.length -1; i++) {
            if (this.chatEditBox.string[i] != ' ') {
                isOnlySpace = false;
                break;
            }
        
        }
        if(isOnlySpace){
            return;
        } 
        let json = new network.RequestSendChatData();
        json.command = 10106;
        json.data.npcId = this._npcId;
        json.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
        json.data.context = this.chatEditBox.string;
        socket.sendWebSocketBinary(json);
        this.chatEditBox.string = "";
        // if(this.chatEditBox.node.active){
        //     let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
        //     let json = new network.RequestSendChatData();
        //     json.command = 10016;
        //     json.data.npcId = SceneNpcID;
        //     json.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
        //     json.data.context = this.chatEditBox.string;
        //     socket.sendWebSocketBinary(json);
        //     this.chatEditBox.string = "";
        // }
        // if(this.playerEditBox.node.active){
        //     let json = new network.GetAllNPCRequest();
        //     json.command = 10026;
        //     json["data"] = {
        //         content:this.playerEditBox.string,
        //     }
        //     socket.sendWebSocketBinary(json);
        //     this.playerEditBox.string = "";
        // }
        let chatPrefabNode = instantiate(this.chatPrefab);
        chatPrefabNode.getComponent(chatPrefab).initData(0,json.data.context)
        chatPrefabNode.getComponent(chatPrefab).setToNpcId(this._npcId);
        this.chatViewContent.addChild(chatPrefabNode)
        tween(this.node).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 700){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()
    }

    showNpcReplyPlayer(npcId,content){
        let chatPrefabNode = instantiate(this.chatPrefab);
        chatPrefabNode.getComponent(chatPrefab).initData(npcId,content)
        this.chatViewContent.addChild(chatPrefabNode)
        if(npcId != this._npcId){
            chatPrefabNode.active = false;
        }
        tween(this.node).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 700){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()
    }

    onEditBegin(){
        this._isEditing = true;
        if(this.chatEditBox.string == " "){
            this.chatEditBox.string = "";
        }
    }

    onEditTextEnd(){
        this._isEditing = false;
    }

    onEditTextReturn(){ 
        this._isEditing = false;
    }

    onBtnSkinClick(target,CustomData){
        if([Number(CustomData)] < this._skinFrameArr.length){
            this._nowSkinData = npcSkinCfg[this._npcId][Number(CustomData)];
            this._skinIndex = Number(CustomData);
            //this.setSkinStatus();
        }
        else{
            console.log("no skin id" )
        }
        
    }

    setSkinStatus(){
        this.imgNpcFrame.spriteFrame = this._skinFrameArr[this._skinIndex];
    }

    onBtnConfirm(){
        let json = new network.GetAllNPCRequest();
        json.command = 10108;
        json.type = 1;
        json["data"] = {
            npcId:this._npcId,
            dressId:this._nowSkinData,
        }
        socket.sendWebSocketBinary(json);
        director.getScene().getComponentInChildren(GameScene).cancelFollowNpc();
    }

    onBtnClose(){
        // console.log("pos====" + JSON.stringify(this.skinViewContent.position))
        // return;
        director.getScene().getComponentInChildren(GameScene).cancelFollowNpc();
    }

    onBtnSceneItem(){

    }

    onBtnDir(){
        this.changeNode.active = !this.changeNode.active;
        this.chatNode.active = !this.chatNode.active;
    }
}


