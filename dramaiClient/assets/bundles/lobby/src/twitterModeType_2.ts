import { _decorator, Component, Label, Node, Prefab, Sprite, SpriteFrame } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('twitterModeType_2')
export class twitterModeType_2 extends Component {
    @property(Sprite)
    imgNpcHead:Sprite = null;

    @property(Label)
    lblNpcName:Label = null;

    
    @property(Label)
    lblTime:Label = null;

    @property(Label)
    lblContent:Label = null;

    @property(Node)
    chooseStatus_1:Node = null;
    
    @property(Node)
    chooseStatus_2:Node = null;

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

    @property(SpriteFrame)
    btnZan_2:SpriteFrame = null;

    _data = null;
    _roomId = null;
    _twitterId = null;
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

    onBtnHeadEnterScene(){

    }

    onBtnChooseVoteIndex(target,CutomeData){

    }

    initData(data){
        this._roomId = data.roomId;
        this._data = data;
    }
}


