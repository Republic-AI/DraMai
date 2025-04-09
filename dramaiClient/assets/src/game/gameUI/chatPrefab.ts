import { _decorator, Component, director, Label, Node, resources, Sprite, SpriteFrame, UITransform } from 'cc';
import { UILayer } from './UILayer';
import { ChatBubble } from './ChatBubble';
const { ccclass, property } = _decorator;

@ccclass('chatPrefab')
export class chatPrefab extends Component {
    @property(Node)
    imgNpcHeadBox:Node = null;

    @property(Sprite)
    imgNpcHeadFrame:Sprite = null;

    @property(Node)
    imgPlayerHeadBox:Node = null;

    @property(Sprite)
    imgPlayerHeadFrame:Sprite = null;

    @property(SpriteFrame)
    playerChatFrame:SpriteFrame = null;

    @property(Node)
    imgNpcTypeNode:Node = null;

    @property(Sprite)
    imgNpcHeadFrame_2:Sprite = null;

    toNpcId = 0;
    _npcId = 0;
    start() {

    }

    update(deltaTime: number) {
        if(this.imgNpcHeadBox.active){
            let targetSize = this.imgNpcHeadBox.getComponentInChildren(ChatBubble).node.getComponent(UITransform).contentSize;
            this.node.getComponent(UITransform).setContentSize(targetSize.width,targetSize.height + 20)
        }
        if(this.imgPlayerHeadBox.active){
            let targetSize = this.imgPlayerHeadBox.getComponentInChildren(ChatBubble).node.getComponent(UITransform).contentSize;
            this.node.getComponent(UITransform).setContentSize(targetSize.width,targetSize.height + 20)
        }
        if(this.imgNpcTypeNode.active){
            this.node.getComponent(UITransform).setContentSize(500,90);
        }
    }
    initData(npcId,content){
        this._npcId = npcId
        if(!content){
            this.imgNpcHeadBox.active = false;
            this.imgPlayerHeadBox.active = false;

            this.imgNpcTypeNode.active = true;

            resources.load("gameUI/image/headDir_" + npcId +"/spriteFrame",(error,sprFrame:SpriteFrame)=>{
                if(error){
                    return;
                }
                this.imgNpcHeadFrame_2.spriteFrame = sprFrame
            })
        }
        else if(npcId){
            console.log("111111111111")
            this.imgNpcHeadBox.active = true;
            this.imgPlayerHeadBox.active = false;
            resources.load("gameUI/image/headDir_" + npcId +"/spriteFrame",(error,sprFrame:SpriteFrame)=>{
                if(error){
                    return;
                }
                this.imgNpcHeadFrame.spriteFrame = sprFrame
            })

            this.imgNpcHeadBox.getComponentInChildren(ChatBubble).setText(content);
        }
        else{
            console.log("222222222222")
            this.imgNpcHeadBox.active = false;
            this.imgPlayerHeadBox.active = true;
            let spFrame = director.getScene().getComponentInChildren(UILayer).getHeadFrame();
            this.imgPlayerHeadFrame.spriteFrame = spFrame;
            console.log("spFrame====" + spFrame.name)
            // this.node.getComponent(Sprite).spriteFrame = this.playerChatFrame;
            this.imgPlayerHeadBox.getComponentInChildren(ChatBubble).setText(content);
        }
    }

    setToNpcId(npcId){
        this.toNpcId = npcId;
    }
}


