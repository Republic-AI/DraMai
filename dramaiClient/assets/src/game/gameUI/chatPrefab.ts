import { _decorator, assetManager, Component, director, Label, Node, resources, Sprite, SpriteFrame, UITransform } from 'cc';
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

    @property(Label)
    lblChatContent: Label = null;

    toNpcId = 0;
    _npcId = 0;
    private _isValid: boolean = true;

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

    protected onDestroy(): void {
        this._isValid = false;
    }

    initData(npcId: any, chatContent: string) {
        this._npcId = npcId;
        if(chatContent && npcId){
            this.imgNpcHeadBox.active = true;
            this.imgNpcHeadBox.getComponentInChildren(ChatBubble).setText(chatContent);
        }
        else{
            if(npcId){
                this.imgNpcTypeNode.active = true;
            }
            else{
                this.imgPlayerHeadBox.active = true;
                this.imgPlayerHeadBox.getComponentInChildren(ChatBubble).setText(chatContent);
            }
        }
        if(npcId){
            resources.load("gameUI/image/headDir_" + npcId +"/spriteFrame",(error,sprFrame:SpriteFrame)=>{
                if (!this._isValid) return;
                if(error){
                console.log(error);
            }
            else{
                this.imgNpcHeadFrame.spriteFrame = sprFrame;
                this.imgNpcHeadFrame_2.spriteFrame = sprFrame;
            }
        })
        }
        else{
            assetManager.loadBundle("headFrame",{},(error,bundle)=>{
                let randomIndex = Number(localStorage.getItem("avatarId"));
                //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
                bundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                    if(error||!this._isValid){
                        console.log("loadHeadError" + error)
                        return;
                    }
                    else{
                        this.imgPlayerHeadFrame.spriteFrame = spFrame
                    }
                })
            })
        }

    }

    onBtnChat() {
        resources.load("gameUI/image/headDir_" + this._npcId +"/spriteFrame",(error,sprFrame:SpriteFrame)=>{
            if (!this._isValid) return;
            if(error){
                console.log(error);
            }
            else{
                this.imgNpcHeadFrame.spriteFrame = sprFrame;
            }
        })
    }

    setToNpcId(npcId){
        this.toNpcId = npcId;
    }
}


