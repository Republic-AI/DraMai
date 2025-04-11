import { _decorator, Component, Node, ScrollView, Sprite, SpriteFrame, Tween, tween, UIOpacity, v3 } from 'cc';
import { GlobalConfig } from '../config/GlobalConfig';
import { network } from '../../model/RequestData';
import { observer, socket } from '../App';
import { EventType } from '../../EventType';
import { NpcEmoji } from '../../StaticUtils/NPCConfig';
const { ccclass, property } = _decorator;

@ccclass('linkLayer')
export class linkLayer extends Component {
    @property(ScrollView)
    node_1:ScrollView = null;
    @property(ScrollView)
    node_2:ScrollView = null;
    @property(ScrollView)
    node_3:ScrollView = null;
    @property(ScrollView)
    node_4:ScrollView = null;
    @property(SpriteFrame)
    emojiFrameArr:SpriteFrame[] = [];

    protected onLoad(): void {
        observer.on(EventType.GEEMOJIINFO,this.onGEEMOJIINFO,this);
    }

    protected onDestroy(): void {
        observer.off(EventType.GEEMOJIINFO,this.onGEEMOJIINFO,this);
    }

    onGEEMOJIINFO(data:any){
        console.log("onGEEMOJIINFO=====" + JSON.stringify(data.data));
        data.data.relationDataList.forEach(element => {
            let imageName = "emoji_" + element.npcId1 + "_" + element.npcId2;
            if(this["node_" + GlobalConfig._instance.chooseScene].content.getChildByName(imageName)){
                let imageSprite = this["node_" + GlobalConfig._instance.chooseScene].content.getChildByName(imageName).getComponent(Sprite);
                let emojiId = NpcEmoji[element.relation];
                imageSprite.spriteFrame = this.emojiFrameArr[emojiId];
            }
        });
    }

    start() {
        this["node_" + GlobalConfig._instance.chooseScene].node.active = true;
        let json = new network.GetAllNPCRequest();
        json.command = 10120;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = GlobalConfig.instance.chooseScene;
        socket.sendWebSocketBinary(json);
    }

    update(deltaTime: number) {
        
    }

    onBtnClose(){
        this.node.destroy()
    }

    // onBtnLink(target,CustomData){
    //     if(linkCfg[CustomData] && linkCfg[CustomData].url){
    //         window.location.href = linkCfg[CustomData].url;
    //     }
    // }

    // onBtnCaKey(target,CustomData){
    //     if(linkCfg[CustomData] && linkCfg[CustomData].caKey){
    //         const textToCopy = linkCfg[CustomData].caKey;  // 要复制的字符串
    //         // 使用 Clipboard API 来复制文本
    //         navigator.clipboard.writeText(textToCopy).then(()=>{
    //             Tween.stopAllByTarget(this.copyNode);
    //             Tween.stopAllByTarget(this.copyNode.getComponent(UIOpacity));
    //             this.copyNode.setScale(v3(0, 0, 1))
    //             this.copyNode.active = true;
    //             this.copyNode.getComponent(UIOpacity).opacity = 255;
    //             tween(this.copyNode).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).delay(2).call(()=>{
    //                 tween(this.copyNode.getComponent(UIOpacity)).to(0.5,{opacity:0}).call(()=>{
    //                     this.copyNode.active = false;
    //                 }).start()
    //             }).start()
    //         }).catch(function(err) {
    //           alert("复制失败: " + err);  // 复制失败时的提示
    //         });
    //     }
    // }
}


