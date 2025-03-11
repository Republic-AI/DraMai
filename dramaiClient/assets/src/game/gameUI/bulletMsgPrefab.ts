import { _decorator, Component, Label, Node, Sprite, SpriteFrame, tween, UITransform, v2, v3, view } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('bulletMsgPrefab')
export class bulletMsgPrefab extends Component {
    @property(Node)
    npcBullet:Node = null;

    @property(Node)
    playerBullet:Node = null;

    @property(SpriteFrame)
    bgFrameArr:SpriteFrame[] = [];

    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data){
        if(data.replyMsgId){
            this.npcBullet.active = true;
            this.playerBullet.active = false;
        }
        else{
            this.npcBullet.active = false;
            this.playerBullet.active = true;
        }
        this.node.getComponentsInChildren(Label).forEach(lblContent=>{
            lblContent.string = data.content;
        })
        let randomBgIndex = Math.floor(Math.random() * this.bgFrameArr.length);
        this.node.getComponentsInChildren(Sprite).forEach(bgSpr=>{
            bgSpr.spriteFrame = this.bgFrameArr[randomBgIndex];
        });
        let randomHeight = Math.floor(Math.random() * this.node.parent.getComponent(UITransform).contentSize.height /2);
        let randomDir = Math.random() > 0.5 ? 1 : -1;
        this.node.setPosition(0,randomHeight * randomDir)
        tween(this.node).delay(0.1).call(()=>{
            let targetX = view.getVisibleSize().width + this.node.getComponent(UITransform).width;
            let speedTime = (targetX / 100) * 2;
            tween(this.node).by(speedTime,{position:v3(-targetX * 2,0,0)}).call(()=>{
                this.node.destroy()
            }).start();;
        }).start()
    }
}


