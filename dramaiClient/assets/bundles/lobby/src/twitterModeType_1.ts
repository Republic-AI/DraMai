import { _decorator, assetManager, Component, EditBox, ImageAsset, instantiate, Label, Node, Prefab, resources, Sprite, SpriteFrame, Texture2D } from 'cc';
import { NpcName } from '../../../src/StaticUtils/NPCConfig';
import { network } from '../../../src/model/RequestData';
import { observer, socket } from '../../../src/game/App';
import { commitItem } from './commitItem';
import { EventType } from '../../../src/EventType';
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

        
    @property(SpriteFrame)
    btnZan_2:SpriteFrame = null;

    _data = null;
    _roomId = null;
    _twitterId = null;

    protected onLoad(): void {
        observer.on(EventType.UPDATETWITTER,this.updateTwitter,this);
    }

    protected onDestroy(): void {
        observer.off(EventType.UPDATETWITTER,this.updateTwitter,this);
    }
    start() {
        this.editNode.active = false;
        this.btnShowHideNode.active = false;
        this.commitLayout.active = false;
    }

    update(deltaTime: number) {
        
    }

    onBtnReply(){
        this.editNode.active = true;
        this.btnShowHideNode.active = true;
        this.commitLayout.active = true;
    }

    onBtnZan(){
        if(!this._data.like){
            let json = new network.GetAllNPCRequest();
            json.command = 10113;
            json["data"] = {}
            json["data"].type = 1;
            json["data"].content = "";
            json["data"].replyId = null;
            json["data"].tweetId = this._twitterId;
            socket.sendWebSocketBinary(json);
        }
    }

    onBtnExpand(){
        this.commitLayout.children.forEach(node=>{
            node.active = true;
        })
    }

    onBtnFold(){
        this.commitLayout.children.forEach((node,index)=>{
            if(index > 2){
                node.active = false;
            }
        })
    }

    onBtnHeadEnterScene(){

    }

    initData(data){
        this._roomId = data.roomId;
        console.log("roomId=====" + this._roomId);
        this._data = data;
        this._twitterId = data.id;
        this.lblContent.string = data.content;
        if(data.imgUrl){
            // const image = new Image();
            // image.crossOrigin = "anonymous"; // 允许跨域加载
            // image.src = data.imgUrl;
            // image.onload = () => {
            //     // 创建 ImageAsset
            //     const imageAsset = new ImageAsset(image);

            //     // 创建 Texture2D
            //     const texture = new Texture2D();
            //     texture.image = imageAsset;

            //     // 创建 SpriteFrame
            //     const spriteFrame = new SpriteFrame();
            //     spriteFrame.texture = texture;

            //     // 获取 Sprite 组件并赋值
            //     this.imgContent.spriteFrame = spriteFrame
            // };
            // image.onerror = (err) => {
            //     console.error("图片加载失败:", err);
            // };

            
            assetManager.loadRemote<Texture2D>(data.imgUrl, { ext: '.png' }, (err, texture) => {
                if (err) {
                    console.error("图片加载失败:", err);
                    return;
                }
                const spriteFrame = new SpriteFrame();
                spriteFrame.texture = texture;
                this.imgContent.spriteFrame = spriteFrame;
            });
        }
        else{
            this.imgContent.node.active = false;
        }
        this.lblReplyNum.string = data.commentCount;
        this.lblZanNum.string = data.likeCount;
        if(data.like){
            this.btnZan.spriteFrame = this.btnZan_2;
        }
        else{

        }

        let durTime = Math.floor(data.createTime/1000);

        if(durTime >= 86400){
            let str = "d";
            let time = Math.floor(durTime / 86400);
            this.lblTime.string = time + str;
        }
        else if(durTime > 3600){
            let str = "h";
            let time = Math.floor(durTime / 3600);
            this.lblTime.string = time + str;
        }
        else if(durTime > 60){
            let str = "m";
            let time = Math.floor(durTime / 60);
            this.lblTime.string = time + str;
        }
        else{
            let str = "s";
            let time = durTime;
            this.lblTime.string = time + str;
        }

        if(data.npcId){
            resources.load("gameUI/image/headDir_" + data.npcId + "/spriteFrame",SpriteFrame,(error,spr:SpriteFrame)=>{
                this.imgNpcHead.spriteFrame = spr;
            })
            this.lblNpcName.string = NpcName[data.npcId];
        }
        if(data.tweetCommentVoList){
            data.tweetCommentVoList.forEach((commentInfo,index)=>{
                let commentItemNode = instantiate(this.commitItem);
                commentItemNode.getComponent(commitItem).initData(commentInfo,this._twitterId);
                this.commitLayout.addChild(commentItemNode);
                if(index > 2){
                    commentItemNode.active = false;
                }
            })
        }
        //{"id":6,"content":"Hey Pippin!  How's the coffee business?  Need any supplies?","imgUrl":"https://dramai.world/img/img_1.png","tweetCommentVoList":[],"commentCount":0,"likeCount":0,"like":false,"createTime":8616500,"tweetType":2},

    }
    onBtnSend(){
        let editComonent = this.editNode.getComponentInChildren(EditBox);
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
        json["data"].replyId = null;
        json["data"].tweetId = this._twitterId;
        socket.sendWebSocketBinary(json);
        editComonent.string = "";
        // if(this.chatEditBox.node.active){
        //     let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
        //     let json = new network.RequestSendChatData();
        //     json.command = 10016;
        //     json.data.npcId = SceneNpcID;
        //     json.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
        //     json.data.context = this.chatEditBox.string;
        //     socket.sendWebSocketBinary(json);
        //     this.chatEditBox.string = "";
        // }
        // if(this.playerEditBox.node.active){
        //     let json = new network.GetAllNPCRequest();
        //     json.command = 10026;
        //     json["data"] = {
        //         content:this.playerEditBox.string,
        //     }
        //     socket.sendWebSocketBinary(json);
        //     this.playerEditBox.string = "";
        // }
    }

    updateTwitter(data){
        let updateData = data.data;
        if(updateData.tweetId == this._twitterId){
            console.log("updateTwitterData=====" + JSON.stringify(data.data));
            if(updateData.type == 2 && !updateData.replyId){
                let commentItemNode = instantiate(this.commitItem);
                commentItemNode.getComponent(commitItem).initData(updateData,this._twitterId);
                this.commitLayout.addChild(commentItemNode);
            }
        }
    }
}


