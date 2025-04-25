import { _decorator, assetManager, Component, EditBox, instantiate, Label, Node, Prefab, Sprite, SpriteFrame, UITransform } from 'cc';
import { network } from '../../../src/model/RequestData';
import { observer, socket } from '../../../src/game/App';
import { replyItem } from './replyItem';
import { EventType } from '../../../src/EventType';
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

    @property(Node)
    contentLayout:Node = null;

    _data = null;
    _twitterId = null;
    private _isValid: boolean = true;

    protected onLoad(): void {
        
    }

    protected onDestroy(): void {
        this._isValid = false;
    }
    start() {

    }

    update(deltaTime: number) {
        let targetSize = this.contentLayout.getComponent(UITransform).contentSize;
        this.node.getComponent(UITransform).setContentSize(750,targetSize.height + 40);
    }

    onBtnReply(){
        this.node.getComponentInChildren(EditBox).setFocus();
    }

    initData(data,twitterId){
        const headBundle = assetManager.getBundle("headFrame");
        if(headBundle){
            let randomIndex = Math.floor(Math.random() * 160) + 1
            //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
            headBundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                if(error || !this._isValid || !this.imgPlayerHeadFrame || !this.imgPlayerHeadFrame.isValid){
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
                if(error || !this._isValid || !this.imgPlayerHeadFrame || !this.imgPlayerHeadFrame.isValid){
                    console.log("loadHeadError" + error)
                    return;
                }
                let randomIndex = Math.floor(Math.random() * 160) + 1
                //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
                bundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                    if(error || !this._isValid || !this.imgPlayerHeadFrame || !this.imgPlayerHeadFrame.isValid){
                        console.log("loadHeadError" + error)
                        return;
                    }
                    else{
                        this.imgPlayerHeadFrame.spriteFrame = spFrame
                    }
                })
            })
        }
        this._data = data;
        if(this._data.commentId){
            this._data.id = this._data.commentId
        }
        this._twitterId = twitterId;
        this.lblPlayerName.string = data.nickName;
        this.lblContent.string = data.content;
        if(data.tweetCommentVo){
            this.btnViewMore.active = true;
            data.tweetCommentVo.forEach((replyInfo,index)=>{
                let replyNode = instantiate(this.replyItem);
                this.replyLayout.addChild(replyNode);
                replyNode.getComponent(replyItem).initData(this._twitterId,this._data.id,replyInfo);
                if(index > 2){
                    replyNode.active = false;
                }
            })
            if(data.tweetCommentVo.length < 3){
                this.btnViewMore.active = false;
            }
            else{
                this.btnViewMore.active = true;
            }
        }
        else{
            this.btnViewMore.active = false;
        }
        this.btnViewMore.active = false;
        //{"id":9,"content":"dadsadsadasd","nickName":"0xBuilder","tweetCommentVo":{"id":22,"content":"bbbbbbbbbbbbb","nickName":"0xBuilder"}}
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
        json["data"].replyId = this._data.id;
        json["data"].tweetId = this._twitterId;
        json["data"].chooseIndex = 0;
        socket.sendWebSocketBinary(json);
        editComonent.string = "";
    }

    onBtnViewMore(){
        this.replyLayout.children.forEach(node=>{
            node.active = true;
        })
        this.btnViewMore.active = false;
    }
    
    addReplyItem(replyInfo){
        let replyNode = instantiate(this.replyItem);
        this.replyLayout.addChild(replyNode);
        replyNode.getComponent(replyItem).initData(this._twitterId,this._data.id,replyInfo);
    }
}


