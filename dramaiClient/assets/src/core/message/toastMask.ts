
import { _decorator, Component, Node, Sprite, Animation } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('ToastMask')
export class ToastMask extends Component {
    // [1]
    // dummy = '';

    // [2]
    // @property
    // serializableDummy = 0;
    @property({ type: Sprite })
    public spr_bg: Sprite = null!;
    @property({ type: Node })
    public roots: Node = null!;
    start () {
        // [3]
        
    }

    onEnable () {
        // [4]
        this.node.getComponent(Animation).play();
    }
    private _timer;
    effectHandler(param){
        console.log("effectHandler");
        this._timer = setTimeout(() => {
           if( this.roots.isValid) this.roots.removeFromParent();
        }, 3100);
    }
    onDisable(){
        clearTimeout(this._timer);
    }
}



/**
 * [1] Class member could be defined like this.
 * [2] Use `property` decorator if your want the member to be serializable.
 * [3] Your initialization goes here.
 * [4] Your update function goes here.
 *
 * Learn more about scripting: https://docs.cocos.com/creator/3.0/manual/en/scripting/
 * Learn more about CCClass: https://docs.cocos.com/creator/3.0/manual/en/scripting/ccclass.html
 * Learn more about life-cycle callbacks: https://docs.cocos.com/creator/3.0/manual/en/scripting/life-cycle-callbacks.html
 */
