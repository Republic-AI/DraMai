import { _decorator, color, Component, Label, Node } from 'cc';
import { GlobalConfig } from '../config/GlobalConfig';
const { ccclass, property } = _decorator;

@ccclass('chatMsgPrefab')
export class chatMsgPrefab extends Component {
    @property(Node)
    isMeNode:Node = null;

    @property(Label)
    lblPlayerName:Label = null;

    @property(Label)
    lblShowContent:Label = null;

    @property(Label)
    lblOpacityContent:Label = null;

    _data = null;

    protected onLoad(): void {
        
    }

    start() {

    }

    initData(data){
        this._data = data;
        //{"npcId":10006,"chatData":{"msgId":26,"sname":"1729502624233","sender":"2416VV11U807L3","type":0,"content":"asdasd","time":1730970534065,"barrage":0}}
        let myID = GlobalConfig.instance.LoginData.data.player.playerId;
        if(myID == data.sender){
            console.log("my self chat");
            this.isMeNode.active = true;
        } 
        else{
            console.log("other player chat");
            this.isMeNode.active = false;            
        }
        let fixname = ""
        if(data.sender == "10006"){
            fixname = "wSatoshi";
            this.lblPlayerName.color = color(55,255,250);
            this.node.destroy();
        }
        else if(data.sname && data.sname.length > 8){
            fixname = data.sname.substr(0,6) + "..";
        }
        else{
            fixname = "0xBuild2..";
        }
 
        this.lblPlayerName.string = fixname + ":";
        this.lblShowContent.string = data.content;  
        this.lblOpacityContent.string = data.content;
    }

    //{"msgId":6,"sender":5221343466314801153,"type":0,"content":"写一段七言绝句诗，题目是：火锅！","time":1730899225961,"barrage":0}
    update(deltaTime: number) {
        
    }
}


