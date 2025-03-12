import { _decorator, Component, Label, Node, Prefab, Sprite } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('commitItem')
export class commitItem extends Component {
    @property(Sprite)
    imgPlayerHeadFrame:Sprite = null;

    @property(Label)
    lblPlayerName:Label = null;

    @property(Label)
    lblContent:Label = null;

    @property(Node)
    replyLayout:Node = null;

    @property(Prefab)
    replyItem:Prefab = null;

    @property(Node)
    btnViewMore:Node = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    onBtnReply(){
        
    }
}


