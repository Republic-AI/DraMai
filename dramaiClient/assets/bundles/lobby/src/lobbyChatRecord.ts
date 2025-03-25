import { _decorator, Component, director, Label, Node, resources, Sprite, SpriteFrame } from 'cc';
import { NpcName, NpcRoomIndex } from '../../../src/StaticUtils/NPCConfig';
import { network } from '../../../src/model/RequestData';
import { GlobalConfig } from '../../../src/game/config/GlobalConfig';
import { socket } from '../../../src/game/App';
import { lobbyScene } from './lobbyScene';
const { ccclass, property } = _decorator;

@ccclass('lobbyChatRecord')
export class lobbyChatRecord extends Component {
    @property(Sprite)
    imgNpcHead:Sprite = null;

    @property(Label)
    lblNpcName:Label = null;

    
    @property(Label)
    lblTime:Label = null;

    @property(Label)
    lblContent:Label = null;

    _data = null;
    _npcId = null;
    start() {

    }

    update(deltaTime: number) {

    }

    onBtnClick(){
        let roomId = NpcRoomIndex[this._npcId];

        let json = new network.GetAllNPCRequest();
        json.command = 10012;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = roomId;
        GlobalConfig.instance.chooseNpc = this._npcId
        GlobalConfig.instance.chooseScene = roomId
        socket.sendWebSocketBinary(json);
        director.getScene().getComponentInChildren(lobbyScene).showMaskNode();
    }

    initData(chatInfo,npcId){
        this._data = chatInfo;
        this._npcId = npcId;
        if(npcId){
            resources.load("gameUI/image/headDir_" + npcId + "/spriteFrame",SpriteFrame,(error,spr:SpriteFrame)=>{
                this.imgNpcHead.spriteFrame = spr;
            })
            this.lblNpcName.string = NpcName[npcId];
            let input = this.truncateString(chatInfo.content)
            this.lblContent.string = input;
        }
        let durTime = Math.floor(chatInfo.time/1000);

        if(durTime >= 86400){
            let str = "d";
            let time = Math.floor(durTime / 86400);
            this.lblTime.string = time + str;
        }
        else if(durTime > 3600){
            let str = "h";
            let time = Math.floor(durTime / 3600);
            this.lblTime.string = time + str;
        }
        else if(durTime > 60){
            let str = "m";
            let time = Math.floor(durTime / 60);
            this.lblTime.string = time + str;
        }
        else{
            let str = "s";
            let time = durTime;
            this.lblTime.string = time + str;
        }
    }

    truncateString(input: string): string {
        const maxLength = 36;
        if (input.length > maxLength) {
          return input.slice(0, maxLength) + '...';
        }
        return input;
    }
}


