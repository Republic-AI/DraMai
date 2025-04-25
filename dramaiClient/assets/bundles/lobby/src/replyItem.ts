import { _decorator, assetManager, Component, EditBox, Label, Node, Sprite, SpriteFrame, UITransform } from 'cc';
import { network } from '../../../src/model/RequestData';
import { socket } from '../../../src/game/App';
const { ccclass, property } = _decorator;

@ccclass('replyItem')
export class replyItem extends Component {
    @property(Sprite)
    imgPlayerHeadFrame:Sprite = null;

    @property(Label)
    lblPlayerName:Label = null;

    @property(Label)
    lblContent:Label = null;

    @property(Node)
    contentLayout:Node = null;

    _data = null;
    _twitterId = null;
    _replyId = null;
    private _isValid: boolean = true;
    start() {

    }

    protected onDestroy(): void {
        this._isValid = false;
    }

    update(deltaTime: number) {
        let targetSize = this.contentLayout.getComponent(UITransform).contentSize;
        this.node.getComponent(UITransform).setContentSize(750,targetSize.height + 40);
    }

    onBtnReply(){
        this.node.getComponentInChildren(EditBox).setFocus();
    }

    initData(twitterId,replyId,data){
        this._twitterId = twitterId;
        this._replyId = replyId;
        this._data = data;
        console.log("replyData======" + JSON.stringify(data));

        const headBundle = assetManager.getBundle("headFrame");
        if(headBundle){
            let randomIndex = Math.floor(Math.random() * 160) + 1
            //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
            headBundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                if(error||!this._isValid || !this.imgPlayerHeadFrame || !this.imgPlayerHeadFrame.isValid){
                    console.log("loadHeadError" + error)
                    return;
                }
                else{
                    this.imgPlayerHeadFrame.spriteFrame = spFrame
                }
            })
        }
        else{
            assetManager.loadBundle("headFrame",{},(error,bundle)=>{
                let randomIndex = Math.floor(Math.random() * 160) + 1
                //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
                bundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                    if(error||!this._isValid || !this.imgPlayerHeadFrame || !this.imgPlayerHeadFrame.isValid){
                        console.log("loadHeadError" + error)
                        return;
                    }
                    else{
                        this.imgPlayerHeadFrame.spriteFrame = spFrame
                    }
                })
            })
        }

        this.lblPlayerName.string = data.nickName;
        this.lblContent.string = data.content;
    }
    onEditReplyReturn(){
        // console.log("this.data=======" + JSON.stringify(this._data));
        // console.log("this.data.id=======" + JSON.stringify(this._data.id));
        // return;
        let editComonent = this.node.getComponentInChildren(EditBox);
        if(editComonent.string.length < 1){
            return;
        }
        let isOnlySpace = true;
        for (let i = 0 ; i < editComonent.string.length -1; i++) {
            if (editComonent.string[i] != ' ') {
                isOnlySpace = false;
                break;
            }
        
        }
        if(isOnlySpace){
            return;
        } 
        let json = new network.GetAllNPCRequest();
        json.command = 10113;
        json["data"] = {}
        json["data"].type = 2;
        json["data"].content = editComonent.string;
        console.log("this._data.id======" + this._data.id);
        json["data"].replyId = this._replyId;
        json["data"].tweetId = this._twitterId;
        json["data"].chooseIndex = 0;
        socket.sendWebSocketBinary(json);
        editComonent.string = "";
    }
}


