import { _decorator, Component, Node, Sprite, SpriteFrame, tween, UIOpacity, v2, v3, view } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('numScrollNode')
export class numScrollNode extends Component {
    @property(SpriteFrame)
    num_0:SpriteFrame = null;

    @property(SpriteFrame)
    num_1:SpriteFrame = null;

    _status = "stop"
    protected onLoad(): void {
        this.node.children.forEach(node=>{
            node.active = false;
        })
    }

    start() {

    }

    update(deltaTime: number) {
        
    }

    startRunDownAction(){
        if(this._status != "stop"){
            return;
        }
        this._status = "play";
        this.node.active = true;
        let randomShow = Math.floor(Math.random() * 18) + 12;
        let randomOpacity = Math.floor(Math.random() * 231) + 25
        let addOpacity = Math.ceil((255 - randomOpacity) / randomShow);
        this.node.children.forEach((node,index)=>{
            node.active = index <= randomShow ? true : false;
            let targetOpacity = randomOpacity + addOpacity * index;
            targetOpacity = targetOpacity > 255 ? 255 : targetOpacity;
            node.getComponent(UIOpacity).opacity = targetOpacity;
            if(node.active){
                let randomNum = Math.random();
                node.getComponent(Sprite).spriteFrame = randomNum > 0.5 ? this.num_1 : this.num_0;
            }
        })
        let speed = Math.floor(Math.random() * 10)  + 7;
        let contentHeight =  7 * (randomShow + 1)
        let viewSize =  view.getVisibleSize();
        tween(this.node).by(speed,{position:v3(0,-viewSize.height-contentHeight,0)}).call(()=>{
            this.node.active = false;
            this.node.setPosition(v3(this.node.position.x,0,0));
            this._status = "stop";
        }).start()
    }
}


