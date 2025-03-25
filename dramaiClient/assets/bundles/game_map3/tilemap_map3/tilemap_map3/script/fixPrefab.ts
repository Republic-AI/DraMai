import { _decorator, Component, Node, Sprite, SpriteFrame, Tween, tween, v3 } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('fixPrefab')
export class fixPrefab extends Component {
    @property(SpriteFrame)
    fixOnArr:SpriteFrame [] = [];

    @property(SpriteFrame)
    fixIdleArr:SpriteFrame [] = [];

    @property(Sprite)
    imgFix:Sprite = null;

    @property(Node)
    bubbleNode : Node = null;

    _frameIndex = 0;

    _fixPosition = v3(18,90,0);
    _idlePosition = v3(356,1150,0);
    _status = null;
    start() {
        //this.setIdleStatus()
    }

    update(deltaTime: number) {
        
    }

    setIdleStatus(){
        if(this._status != "idle"){
            this._status = "idle";
            Tween.stopAllByTarget(this.node);
            this.node.setPosition(this._idlePosition);
            this._frameIndex = 0;
            this.imgFix.spriteFrame = this.fixIdleArr[this._frameIndex];
            if(this.fixIdleArr.length > 0){
                tween(this.node).repeatForever(tween(this.node).delay(0.24).call(()=>{
                    if(this._frameIndex < this.fixIdleArr.length - 1){
                        this._frameIndex++;
                    }
                    else{
                        this._frameIndex = 0
                    }
                    this.imgFix.spriteFrame = this.fixIdleArr[this._frameIndex];
                }).start()).start();
                tween(this.node).repeatForever(tween(this.node).by(0.5,{position:v3(0,10)}).by(0.5,{position:v3(0,-10)}).start()).start();
            }
        }
    }

    setFixOnStatus(){
        if(this._status != "fix"){
            this._status = "fix";
            Tween.stopAllByTarget(this.node);
            this.node.setPosition(this._fixPosition);
            this._frameIndex = 0;
            this.imgFix.spriteFrame = this.fixOnArr[this._frameIndex];
            if(this.fixOnArr.length > 0){
                tween(this.node).repeatForever(tween(this.node).delay(0.12).call(()=>{
                    if(this._frameIndex < this.fixOnArr.length - 1){
                        this._frameIndex++;
                    }
                    else{
                        this._frameIndex = 0
                    }
                    this.imgFix.spriteFrame = this.fixOnArr[this._frameIndex];
                }).start()).start();
            }
        }
    }

    setTypeStatus(status){
        this.bubbleNode.active = status;
        this.bubbleNode.children.forEach(node=>{
            node.active = node.name == "imgType" ? true : false;
        })
    }

    setPaperStatus(status){
        this.bubbleNode.active = true;
        this.bubbleNode.children.forEach(node=>{
            node.active = node.name == "imgPaper_" + status ? true : false;
        })
    }

    playExitAction(){
        //随机方向
            let dirX = Math.random() > 0.5 ? 1 : -1;
            let dirY = Math.random() > 0.5 ? 1 : -1;
            let posY = 1400 * dirY;
            let posX =  dirX * 1400 * Math.random();
            if(this.node.position.x > posX){
                this.node.setScale(-1,1,1);
            }
            else{
                this.node.setScale(1,1,1);
            }
            tween(this.node).to(3,{position:v3(posX,posY,0)}).call(()=>{
                this.node.destroy()
            }).start();
    }

    
    setNormalStatus(){
        this._frameIndex = 0;
        this.imgFix.spriteFrame = this.fixIdleArr[this._frameIndex];
        if(this.fixIdleArr.length > 0){
            tween(this.node).repeatForever(tween(this.node).delay(0.24).call(()=>{
                if(this._frameIndex < this.fixIdleArr.length - 1){
                    this._frameIndex++;
                }
                else{
                    this._frameIndex = 0
                }
                this.imgFix.spriteFrame = this.fixIdleArr[this._frameIndex];
            }).start()).start();
        }
    }
    
}


