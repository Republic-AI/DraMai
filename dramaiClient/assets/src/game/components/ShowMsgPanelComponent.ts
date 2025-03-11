import { _decorator, Component, Node, Label } from 'cc';
import { uiMgr } from '../App';
const { ccclass, property } = _decorator;

@ccclass('ShowMsgPanelComponent')
export class ShowMsgPanelComponent extends Component {
    @property(Label)
    public Label_content:Label = null;
    public static CONTENT:string = "";
    start() {
        // this.scheduleOnce(this.closeself,3);
        this.Label_content.string = ShowMsgPanelComponent.CONTENT;
    }
    public closeself(d){
        uiMgr.removeParent("prefabs/rewards/pre_showMsg");
    }

    update(deltaTime: number) {
        
    }
}

