import { _decorator, Component, Node, Sprite, SpriteFrame, tween } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('waterNode')
export class waterNode extends Component {

    @property(SpriteFrame)
    waterFrameArr:SpriteFrame [] = [];

    @property(Sprite)
    waterNode:Sprite = null;

    _frameIndex = 0;
    start() {
        this.initData();
    }

    update(deltaTime: number) {
        
    }

    initData(){
        this._frameIndex = 0;
        this.waterNode.spriteFrame = this.waterFrameArr[this._frameIndex];
        if(this.waterFrameArr.length > 0){
            tween(this.node).repeatForever(tween(this.node).delay(0.24).call(()=>{
                if(this._frameIndex < this.waterFrameArr.length - 1){
                    this._frameIndex++;
                }
                else{
                    this._frameIndex = 0
                }
                this.waterNode.spriteFrame = this.waterFrameArr[this._frameIndex];
            }).start()).start();
        }
    }
}


