import { _decorator, assetManager, Component, EditBox, Label, Node, Sprite, SpriteFrame, v3 } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('voteItem')
export class voteItem extends Component {
    @property(Label)
    lblTitle:Label = null;

    @property(Label)
    lblCountDownNum:Label = null;

    @property(Label)
    lblTotalNum:Label = null;

    @property(Label)
    lblStory:Label = null;

    @property(Sprite)
    imgNPCFrame:Sprite = null;

    @property(Label)
    lblName:Label = null;

    @property(Label)
    lblAgentContent:Label = null;

    @property(EditBox)
    voteEditBox:EditBox = null;

    @property(Node)
    helpNode:Node = null;

    @property(Node)
    loginNode:Node = null;

    _data = null;
    _voteNum = 0;
    start() {

    }

    update(deltaTime: number) {
        this.voteEditBox.node.getChildByName("TEXT_LABEL").setPosition(v3(0,0,0))
    }

    initData(data){
        this._data = data;
        this.lblTitle.string = data.title;
        this.lblStory.string = data.story;
        let cfgBundle = assetManager.getBundle("voteLayer");
        cfgBundle.load("image/" + data.agent +"/spriteFrame",SpriteFrame,(err,spr:SpriteFrame)=>{
            this.imgNPCFrame.spriteFrame = spr;
        })
        this.lblName.string = data.agent;
        this.lblAgentContent.string = data.agentContent;
        this.voteEditBox.string = this._voteNum.toString();

    }

    onBtnAdd(){
        this._voteNum++;
        this.voteEditBox.string = this._voteNum.toString();
    }

    onBtnMiner(){
        this._voteNum--;
        this._voteNum = this._voteNum < 0 ? 0 : this._voteNum
        this.voteEditBox.string = this._voteNum.toString();
    }

    onBtnEditEnd(){
        this._voteNum = Number(this.voteEditBox.string);
    }

    onBtnVote(){
        this.loginNode.active = true;
    }

    onBtnHelp(){
        this.helpNode.active = true;
    }

    onBtnBg(){
        this.loginNode.active = false;
        this.helpNode.active = false;
    }

    onBtnLoginWallet(){
        console.log("login wallet");
    }
}


