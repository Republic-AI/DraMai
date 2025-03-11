
import { _decorator, Component, Node,Animation } from 'cc';
import { uiMgr } from '../../game/App';
const { ccclass, property } = _decorator;

@ccclass('Sign')
export class Sign extends Component {
    @property({ type: Node })
    btn_close: Node | null = null;

    
    // [1] 
    // dummy = '';

    // [2]
    // @property
    // serializableDummy = 0;

    start () {
        // [3]
        this.btn_close.on(Node.EventType.TOUCH_START, this.closehandler.bind(this));
    }
    
    onEnable(){
        let s = this;
        
    }

    private closehandler(e){
    console.log(this);
    uiMgr.removeParent("prefabs/pre_dialog");
    // this.node.destroy();
}

    // update (deltaTime: number) {
    //     // [4]
    // }
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
