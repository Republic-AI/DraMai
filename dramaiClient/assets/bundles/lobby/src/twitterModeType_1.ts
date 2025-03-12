import { _decorator, Component, Label, Node, Prefab, Sprite } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('twitterModeType_1')
export class twitterModeType_1 extends Component {
    @property(Sprite)
    imgNpcHead:Sprite = null;

    @property(Label)
    lblNpcName:Label = null;

    
    @property(Label)
    lblTime:Label = null;

    @property(Label)
    lblContent:Label = null;

    @property(Sprite)
    imgContent:Sprite = null;

    @property(Sprite)
    btnZan:Sprite = null;

    @property(Node)
    commitLayout:Node = null;

    @property(Node)
    editNode:Node = null;

    
    @property(Node)
    btnShowHideNode:Node = null;

    @property(Prefab)
    commitItem:Prefab = null;

    @property(Label)
    lblReplyNum:Label = null;

    
    @property(Label)
    lblZanNum:Label = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    onBtnReply(){

    }

    onBtnZan(){
        
    }

    onBtnExpand(){

    }

    onBtnFold(){

    }
}


