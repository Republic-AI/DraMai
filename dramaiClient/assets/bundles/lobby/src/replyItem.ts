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

    _data = null;
    _twitterId = null;
    _replyId = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    onBtnReply(){

    }

    initData(twitterId,replyId,data){
        this._twitterId = twitterId;
        this._replyId = replyId;
        this._data = data;
    }
}


