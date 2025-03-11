import { _decorator, Component, Node } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('ShowMsg2Component')
export class ShowMsg2Component extends Component {
    start() {
        this.scheduleOnce(this.closeself,8);
    }
    public closeself(d){
        if(this.node.parent){
            this.node.removeFromParent();
            this.node.destroy();
        }
    }

    update(deltaTime: number) {
        
    }
}

