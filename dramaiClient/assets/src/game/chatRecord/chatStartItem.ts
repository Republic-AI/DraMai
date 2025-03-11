import { _decorator, Component, Label, Node } from 'cc';
import { NpcName } from '../../StaticUtils/NPCConfig';
const { ccclass, property } = _decorator;

@ccclass('chatStartItem')
export class chatStartItem extends Component {
    @property(Label)
    lblTitle:Label = null
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data){
        this.lblTitle.string = "Dialogue <" + NpcName[data.sender] + "-" + NpcName[data.receiver] +">";
    }
}


