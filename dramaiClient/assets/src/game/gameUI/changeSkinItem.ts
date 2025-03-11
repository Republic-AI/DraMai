import { _decorator, Component, director, Label, Node, Sprite, tween, Tween } from 'cc';
import { observer, socket } from '../App';
import { network } from '../../model/RequestData';
import { npcSkinInfo } from '../../StaticUtils/NPCConfig';
import { NpcManager } from '../../NPC/NpcManager';
import WebUtils from '../../utils/WebUtils';
import { EventType } from '../../EventType';
const { ccclass, property } = _decorator;

@ccclass('changeSkinItem')
export class changeSkinItem extends Component {
    @property(Sprite)
    imgSkin:Sprite = null;

    @property(Node)
    timeNode:Node = null;

    @property(Node)
    priceNode:Node = null;

    _skinID = null;
    _npcID = null;
    _data = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(npcID,skinId,skinFrame){
        this._npcID = npcID;
        this._skinID = skinId;
        this.imgSkin.spriteFrame = skinFrame;
        npcSkinInfo[this._npcID].forEach(data=>{
            if(data.id == this._skinID){
                this._data = data;
            }
        })
        this.priceNode.getComponentInChildren(Label).string = this._data.price;
        director.getScene().getComponentsInChildren(NpcManager).forEach(itemScript=>{
            if(itemScript.NpcID == this._npcID){
                if(itemScript._skinId == this._data.id){
                    this.priceNode.active = false;
                    this.timeNode.active = true;
                    let nowLeftTime = Math.floor(itemScript.calLeftTime()/1000);
                    let timeStr = WebUtils.formatDateTime(nowLeftTime);
                    this.timeNode.getComponentInChildren(Label).string = timeStr;
                    Tween.stopAllByTarget(this.node);
                    tween(this.node).repeatForever(tween(this.node).delay(1).call(()=>{
                        let nowLeftTime = Math.floor(itemScript.calLeftTime()/1000);
                        let timeStr = WebUtils.formatDateTime(nowLeftTime);
                        this.timeNode.getComponentInChildren(Label).string = timeStr;
                        if(nowLeftTime <= 0){
                            Tween.stopAllByTarget(this.node);
                            this.timeNode.active = false;
                            this.priceNode.active = true;
                        }
                    }).start()).start()
                }
            }
        })
    }

    onBtnClick(){
        console.log("clickChangeSkin");
        if(this.timeNode.active){
            WebUtils.showToast("Cannot replace skin in use");
            return;
        }
        // let json = new network.GetAllNPCRequest();
        // json.command = 10108;
        // json.type = 1;
        // json["data"] = {
        //     npcId:this._npcID,
        //     dressId:this._skinID,
        // }
        // socket.sendWebSocketBinary(json);

        let data = this._data;
        data["npcId"] = this._npcID
        observer.post(EventType.POPCHANGESKIN,data);
    }
}


