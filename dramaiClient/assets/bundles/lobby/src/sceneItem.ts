import { _decorator, assetManager, Component, director, Label, Node, Sprite, SpriteFrame, UITransform } from 'cc';
import { npcTitleDes } from '../../../src/StaticUtils/NPCConfig';
import { network } from '../../../src/model/RequestData';
import { observer, socket } from '../../../src/game/App';
import { GlobalConfig } from '../../../src/game/config/GlobalConfig';
import { lobbyScene } from './lobbyScene';
const { ccclass, property } = _decorator;

@ccclass('sceneItem')
export class sceneItem extends Component {
    @property(Sprite)
    imgSceneBg:Sprite = null;

    @property(Label)
    lblSceneName:Label = null;

    _npcId = null;
    _sceneId = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(npcId,sceneId){
        this._npcId = npcId;
        this._sceneId = sceneId;
        let str = npcTitleDes[npcId];
        //str = str.split('').join('\u200B');
        this.node.getComponentInChildren(Label).string = str;
        let lobbyBundle = assetManager.getBundle("lobby")
        if(lobbyBundle){
            lobbyBundle.load("image/imgNpc_" + this._npcId+"/spriteFrame",(err,spr:SpriteFrame)=>{
                if(err){
                    console.log("load scene npcframe error" + err);
                    return;
                }
                else{
                    this.imgSceneBg.spriteFrame = spr;
                }
            })
        }

    }

    onBtnEnterScene(){
        let json = new network.GetAllNPCRequest();
        json.command = 10012;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = this._sceneId;
        GlobalConfig.instance.chooseNpc = this._npcId
        GlobalConfig.instance.chooseScene = this._sceneId
        socket.sendWebSocketBinary(json);
        director.getScene().getComponentInChildren(lobbyScene).showMaskNode();
    }
}


