import { _decorator, Component, Label, Node, Sprite } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('replyItem')
export class replyItem extends Component {
    @property(Sprite)
    imgPlayerHeadFrame:Sprite = null;

    @property(Label)
    lblPlayerName:Label = null;

    @property(Label)
    lblContent:Label = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    onBtnReply(){
        
    }
}


