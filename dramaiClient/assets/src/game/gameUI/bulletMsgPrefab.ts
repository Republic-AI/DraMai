import { _decorator, assetManager, Component, Label, Node, Sprite, SpriteFrame, tween, UITransform, v2, v3, view } from 'cc';
const { ccclass, property } = _decorator;
import { Color } from 'cc';

// 六种标签颜色配置
export const LABEL_COLORS = [
    new Color(228, 228, 228, 255),    // 浅灰色
    new Color(76, 76, 76, 255),       // 深灰色
    new Color(64, 150, 170, 255),     // 青绿色
    new Color(109, 36, 173, 255),     // 深紫色
    new Color(167, 50, 97, 255),      // 深红色
    new Color(0, 0, 0, 255)     // 纯黑色
]; 

@ccclass('bulletMsgPrefab')
export class bulletMsgPrefab extends Component {
    @property(Sprite)
    npcBullet:Sprite = null;

    @property(SpriteFrame)
    bgFrameArr:SpriteFrame[] = [];

    @property(SpriteFrame)
    headBoxFrameArr:SpriteFrame[] = [];

    @property(Sprite)
    imgHeadFrame:Sprite = null;

    @property(Sprite)
    headBoxFrame:Sprite = null;
    
    @property(Label) 
    lblContent:Label = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data){
        this.lblContent.string = data.content;
        switch(data.reward){
            case 5:
                this.npcBullet.spriteFrame = this.bgFrameArr[2];
                this.lblContent.color = LABEL_COLORS[2];
                this.headBoxFrame.spriteFrame = this.headBoxFrameArr[0];
                break;
            case 10:
                this.npcBullet.spriteFrame = this.bgFrameArr[3];
                this.lblContent.color = LABEL_COLORS[3];
                this.headBoxFrame.spriteFrame = this.headBoxFrameArr[1];
                break;
            case 50:
                this.npcBullet.spriteFrame = this.bgFrameArr[4];
                this.lblContent.color = LABEL_COLORS[4];
                this.headBoxFrame.spriteFrame = this.headBoxFrameArr[2];
                break;
            case 100:
                this.npcBullet.spriteFrame = this.bgFrameArr[5];
                this.lblContent.color = LABEL_COLORS[5];
                this.headBoxFrame.spriteFrame = this.headBoxFrameArr[3];
                break;
            default:
                this.npcBullet.spriteFrame = this.bgFrameArr[1];
                this.lblContent.color = LABEL_COLORS[1];
                this.headBoxFrame.node.active = false;
                break;
        }
        let headBundle = assetManager.getBundle("headFrame");
        //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
        headBundle.load("imgHeadFrame_" + data.avatar + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
            if(error){
                console.log("loadHeadError" + error)
            }
            else{
                this.imgHeadFrame.spriteFrame = spFrame
            }
        })
        let randomHeight = Math.floor(Math.random() * this.node.parent.getComponent(UITransform).contentSize.height /2);
        let randomDir = Math.random() > 0.5 ? 1 : -1;
        this.node.setPosition(0,randomHeight * randomDir)
        tween(this.node).delay(0.1).call(()=>{
            let targetX = view.getVisibleSize().width + this.node.getComponent(UITransform).width;
            let speedTime = (targetX / 100) * 2;
            tween(this.node).by(speedTime,{position:v3(-targetX * 2,0,0)}).call(()=>{
                this.node.destroy()
            }).start();
        }).start()
    }
}


