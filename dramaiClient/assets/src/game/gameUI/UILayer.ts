import { _decorator, assetManager, Button, color, Component, director, dragonBones, EditBox, game, instantiate, Label, Layout, Node, Prefab, ProgressBar, resources, ScrollView, Sprite, SpriteFrame, sys, Tween, tween, UIOpacity, UITransform, v2, v3, VideoPlayer, view } from 'cc';
import { GameScene } from '../scene/GameScene';
import { network } from '../../model/RequestData';
import { observer, socket } from '../App';
import { EventType } from '../../EventType';
import { chatMsgPrefab } from './chatMsgPrefab';
import { GlobalConfig } from '../config/GlobalConfig';
import { bulletMsgPrefab } from './bulletMsgPrefab';
import WebUtils from '../../utils/WebUtils';
import { npcDirPrefab } from './npcDirPrefab';
import { NpcManager } from '../../NPC/NpcManager';
import { NpcName, NpcRoomIndex } from '../../StaticUtils/NPCConfig';
import { voteLayer } from '../voteLayer/voteLayer';
import { chatRecordLayer } from '../chatRecord/chatRecordLayer';
import { createPlayerLayer } from '../createPlayer/createPlayerLayer';
import { ScreenshotUploader } from '../../utils/Screenshot';
import { JietuComponent } from '../../manager/JietuComponent';
import { linkLayer } from '../linkLayer/linkLayer';
import { PlayerManager } from '../../NPC/PlayerManager';
import { npcHeadPrefab } from './npcHeadPrefab';
import { maxBy } from 'lodash';
const { ccclass, property } = _decorator;

//金币
const ITEM_MONEY_ID = 10101000;
//文档
const ITEM_DOC_ID = 10101008;

//nft说明

const nftDes = {
    "2":"Pepe dreams of sweetness.",
    "0":"Ava moves to the rhythm of the market.",
    "6":"Trump dances to make the Republic great again.",
    "4":"Popcat is ready to swallow the world.",
    "5":"Satoshi paves the way for the future of blockchain, step by step.",
    "3":"Happy Pippin, joyfully surrounded by mushrooms and rainbows.",
    "1":"Elon Musk sets his sights on Mars.",
    "7":"Luna won the Grammy Award, making history worldwide.",
}
@ccclass('UILayer')
export class UILayer extends Component {
    @property(Sprite)
    viewNode:Sprite = null;

    @property(Node)
    msgNode:Node = null;
    
    @property(Prefab)
    bulletMsgPrefab:Prefab = null;

    @property(Node)
    chatNode:Node = null;

    @property(Prefab)
    chatMsgPrefab:Prefab = null;

    @property(EditBox)
    chatEditBox:EditBox = null;

    @property(Sprite)
    imgHeadFrame:Sprite = null;

    @property(ScrollView)
    chatScroll:ScrollView = null;

    @property(Button)
    btnChat:Button = null;

    @property(Button)
    btnRank:Button = null;

    @property(Node)
    TEXT_LABEL:Node = null;

    @property(Label)
    lblGoldNum:Label = null;

    @property(Label)
    lblPaperNum:Label = null;

    @property(Node)
    addPaperStatus:Node = null;

    @property(Node)
    npcDirRoot:Node = null;

    @property(Prefab)
    npcDirPreafab:Prefab = null;

    @property(Prefab)
    newsLayer:Prefab = null;

    @property(Node)
    guideNode:Node = null;

    @property(Node)
    guideContent:Node = null;

    @property(Node)
    guideEffect:Node = null;

    @property(Label)
    lblGuideText:Label = null;

    @property(Node)
    createStatus_1:Node = null;

    @property(Node)
    createStatus_2:Node = null;

    @property(Label)
    lblCreateNum:Label = null;

    @property(Node)
    getNftNode:Node = null;

    @property(Node)
    imgNftBg_1:Node = null;

    @property(Node)
    imgNftBg_2:Node = null;

    @property(Sprite)
    imgNftContent:Sprite = null;

    @property(Node)
    npcHeadLayout:Node = null;

    @property(Node)
    npcHeadNode:Node = null;

    @property(Prefab)
    npcHeadPrefab:Prefab = null;

    @property(ProgressBar)
    progressNpcHeadCD:ProgressBar = null;

    @property(Button)
    btnChooseNpcChat:Button = null;

    @property(Button)
    btnChooseGift:Button = null;

    @property(ProgressBar)
    progressGiftCD:ProgressBar = null;

    @property(Node)
    giftNode:Node = null;

    @property(Node)
    giftLayout:Node = null;

    @property(Node)
    lblNameNode:Node = null;

    @property(Label)
    lblTestGoldNum:Label = null;

    @property(dragonBones.ArmatureDisplay)
    npcChooseEffect:dragonBones.ArmatureDisplay = null;

    @property(dragonBones.ArmatureDisplay)
    giftChooseEffect:dragonBones.ArmatureDisplay = null;

    @property(Node)
    btnVoteStatus_1:Node = null;

    @property(Node)
    btnVoteStatus_2:Node = null;

    @property(Label)
    lblVoteTime:Label = null;
    
    @property(Node)
    voteStartNode:Node = null;

    @property(Node)
    voteGiftNode:Node = null;

    @property(ProgressBar)
    giftProgressBar_1:ProgressBar = null;

    @property(ProgressBar)
    giftProgressBar_2:ProgressBar = null;

    @property(Node)
    voteResultNode:Node = null;

    @property(Node)
    voteUnOpenNode:Node = null;
    
    @property(Node)
    helpNode:Node = null;
    
    _chatInfo = [];
    _randomState = null;
    _isInitItem = false;
    _itemInfo = [];
    _isLive = false;
    _guideContentIndex = 0;
    _sendNpcId = 0;
    _chooseNpcId = 0;
    _chooseGiftNum = 0;
    _isBulletCD = false;

    _voteInfo = null;
    _voteCreateTime = 0;
    _voteEndTime = 0;
    private _isValid: boolean = true;

    protected onLoad(): void {
        //director.root.pipeline.profiler.enabled = false;
        observer.on(EventType.GETCHAT_BYID,this.initChatDataInfo,this);
        observer.on(EventType.UPDATE_CHAT,this.updateChat,this);
        observer.on(EventType.UPDATE_ITEM,this.updateItem,this);
        observer.on(EventType.INIT_DIR_PREFAB,this.initNpcDirNode,this);
        //observer.on(EventType.FOLLOWNPC,this.talkToNpc,this);
        //observer.on(EventType.PLAYERCLICK,this.playerTalkToNpc,this);
        observer.on(EventType.DESTROYGUIDE,this.showDestroyGuide,this);
        observer.on(EventType.CHOOSENPCHEAD, this.chooseNpcHead,this);
        observer.on(EventType.SENDBULLETMSG,this.sendBulletMsg,this);
        observer.on(EventType.INITVOTEINFO,this.getVoteInfo,this);
        observer.on(EventType.UPDATEVOTEINFO,this.updateVoteInfo,this);
        observer.on(EventType.UPDATEMYVOTEINFO,this.updateMyVoteInfo,this);
    }

    protected onDestroy(): void {
        this._isValid = false;
        observer.off(EventType.GETCHAT_BYID,this.initChatDataInfo,this);
        observer.off(EventType.UPDATE_CHAT,this.updateChat,this);
        observer.off(EventType.UPDATE_ITEM,this.updateItem,this);
        observer.off(EventType.INIT_DIR_PREFAB,this.initNpcDirNode,this);
        //observer.off(EventType.FOLLOWNPC,this.talkToNpc,this);
        //observer.off(EventType.PLAYERCLICK,this.playerTalkToNpc,this);
        observer.off(EventType.DESTROYGUIDE,this.showDestroyGuide,this);
        observer.off(EventType.CHOOSENPCHEAD, this.chooseNpcHead,this);
        observer.off(EventType.SENDBULLETMSG,this.sendBulletMsg,this);
        observer.off(EventType.INITVOTEINFO,this.getVoteInfo,this);
        observer.off(EventType.UPDATEVOTEINFO,this.updateVoteInfo,this);
        observer.off(EventType.UPDATEMYVOTEINFO,this.updateMyVoteInfo,this);
        // 移除EditBox的事件监听
        // this.chatEditBox.node.off('editing-began', this.onEditBoxBegan, this);
        // this.chatEditBox.node.off('editing-ended', this.onEditBoxEnded, this);
    }

    start() {
        this.initData();
        // 添加EditBox的事件监听
        // this.chatEditBox.node.on('editing-began', this.onEditBoxBegan, this);
        // this.chatEditBox.node.on('editing-ended', this.onEditBoxEnded, this);
    }

    update(deltaTime: number) {
        //this.node.getComponent(UITransform).setContentSize(view.getVisibleSize().width,view.getVisibleSize().height);
        
        // 更新投票倒计时
        if (this._voteInfo && this.btnVoteStatus_1 && this.btnVoteStatus_1.active) {
            const currentTime = new Date().getTime();
            const elapsedTime = Math.floor((currentTime - this._voteCreateTime) / 1000); // 转换为秒
            const remainingTime = Math.max(0, this._voteEndTime - elapsedTime);
            
            const lblVoteTime = this.btnVoteStatus_1.getComponentInChildren(Label);
            if (lblVoteTime) {
                lblVoteTime.string = this.formatCountdownTime(remainingTime);
            }
        }

        //计算摄像机中心点与NPC位置的夹角和距离，实时更新NPC头像小图标的位置和指向
        this.npcDirRoot.children.forEach(node=>{
            let dirNpcId =  node.getComponent(npcDirPrefab)._npcId;
            director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
                if(npcScript.NpcID == dirNpcId){
                    let npcworldBox = npcScript.node.getComponent(UITransform).getBoundingBoxToWorld();
                    let canvasWorldBox = director.getScene().getChildByName("Canvas").getComponent(UITransform).getBoundingBoxToWorld(); 
                    if(canvasWorldBox.intersects(npcworldBox)){
                        node.active = false;
                    }
                    else{
                        node.active = true;
                        let nodePos = npcScript.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
                        let convertPos = director.getScene().getChildByName("Canvas").getComponent(UITransform).convertToNodeSpaceAR(nodePos);
                        let pointA = v2(0,0);
                        let pointB = convertPos;
                        
                        // 计算两点之间的向量
                        let deltaX = pointB.x - pointA.x;
                        let deltaY = pointB.y - pointA.y;
                        
                        // 计算夹角（弧度）
                        let angleInRadians = Math.atan2(deltaY, deltaX);
                        
                        // 将弧度转换为角度
                        let angleInDegrees = angleInRadians * 180 / Math.PI;
                        
                        node.getChildByName("imgDir").angle = angleInDegrees-90;
                        let MaxY = (view.getVisibleSize().height / 2 - node.getComponent(UITransform).height/2)
                        let MaxX = (view.getVisibleSize().width / 2 - node.getComponent(UITransform).width/2)
                        let roteY = deltaY > 0 ? 1: -1;
                        let roteX =  deltaX > 0 ? 1: -1;
                        if(Math.abs(deltaY) > Math.abs(deltaX)){
                            let targetX =  Math.abs(deltaX) > MaxX ? (MaxX * roteX) : deltaX;
                            node.setPosition(targetX ,MaxY * roteY);
                        }
                        else{
                            let targetY =  Math.abs(deltaY) > MaxY ? (MaxY * roteY) : deltaY;
                            node.setPosition(MaxX * roteX, targetY);
                        }
                        //console.log(dirNpcId + "  Angle in degrees:", angleInDegrees);
                        
                    }
                }
            })
        })
        if(!this.giftProgressBar_1.progress){
            this.giftProgressBar_1.progress = 0.01
        }
        if(!this.giftProgressBar_2.progress){
            this.giftProgressBar_2.progress = 0.01
        }
        //let mapScript = director.getScene().getComponentInChildren(GameScene).getMapScript();
        // if(mapScript && mapScript._myPlayerNode){
        //     this.createStatus_1.active = false;
        //     this.createStatus_2.active = true;
        //     this.chatEditBox.node.active = false;
        //     this.playerEditBox.node.active = true;
        //     let offsetSec =  Math.floor((new Date().getTime() -GlobalConfig.instance.playerCreateTime) / 1000);
        //     if((offsetSec) > Math.floor(GlobalConfig.instance.playerEndTime/1000)){

        //         this.createStatus_2.getComponentInChildren(Label).string ="00:00"
        //     }
        //     else{
        //         let leftTime =  Math.floor(GlobalConfig.instance.playerEndTime/1000) - offsetSec;
        //         this.createStatus_2.getComponentInChildren(Label).string = WebUtils.formatDateTime(leftTime);
        //     }
            
        //     this.btnSend_2.active = true;
        //     this.imgHeadBg_2.active = true;
        //     this.btnHelp_2.active = true;
        // }
        // else{
        //     this.createStatus_1.active = true;
        //     this.createStatus_2.active = false;
        //     this.chatEditBox.node.active = true;
        //     this.playerEditBox.node.active = false;

        //     this.btnSend_2.active = false;
        //     this.imgHeadBg_2.active = false;
        //     this.btnHelp_2.active = false;
        // }
        // const currentUrl = window.location.href;
        // if(currentUrl.includes("capture") || currentUrl.includes("morpheus") || currentUrl.includes("pippin")){
        //     this.chatEditBox.node.active = false;
        //     this.btnHelp_2.parent.active = false;
        // }


    }

    /**
     * 将秒数转换为 MM:SS 格式的倒计时字符串
     * @param seconds 剩余秒数
     * @returns 格式化后的时间字符串
     */
    private formatCountdownTime(seconds: number): string {
        if (seconds <= 0) {
            return "00:00";
        }
        
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        
        // 使用三元运算符和字符串拼接代替padStart
        const minutesStr = minutes < 10 ? "0" + minutes : minutes.toString();
        const secondsStr = remainingSeconds < 10 ? "0" + remainingSeconds : remainingSeconds.toString();
        
        return `${minutesStr}:${secondsStr}`;
    }

    onBtnSend(){
        if(this.chatEditBox.node.active && this.chatEditBox.string.length < 1){
            return;
        }
        // if(this.playerEditBox.node.active && this.playerEditBox.string.length < 1){
        //     return;
        // }
        let isCD = GlobalConfig.instance.getBulletMsgCDTime();
        if(isCD || !this._chooseNpcId){
            let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
            let json = new network.RequestSendChatData();
            json.command = 10016;
            json.data.npcId = this._sendNpcId;
            json.data.roomId = GlobalConfig.instance.chooseScene;
            json.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
            if(this.chatEditBox.node.active){
                json.data.context = this.chatEditBox.string;
            }
            // if(this.playerEditBox.node.active){
            //     json.data.context = this.playerEditBox.string;
            // }
            socket.sendWebSocketBinary(json);
            this.chatEditBox.string = "";
        }
        else{

            if(Number(this.lblTestGoldNum.string) < this._chooseGiftNum){
                WebUtils.showToast("You don't have enough gold");
                return;
            }
            let json = new network.GetAllNPCRequest();
            json.command = 10115;
            json["data"] = {};
            json["data"].npcId = this._chooseNpcId;
            json["data"].content = this.lblNameNode.getComponentInChildren(Label).string + this.chatEditBox.string;
            json["data"].reward  =  this._chooseGiftNum;
            socket.sendWebSocketBinary(json);

            // 发送私聊消息接口用来回复
            let json2 = new network.RequestSendChatData();
            json2.command = 10106;
            json2.data.npcId = this._chooseNpcId;
            json2.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
            json2.data.context = this.chatEditBox.string;
            json2.data.privateMsg = false;
            socket.sendWebSocketBinary(json2);


            this.chatEditBox.string = "";
            this.lblNameNode.active = false;
            this.npcHeadNode.active = false;
            this.giftNode.active = false;

            this.progressGiftCD.node.active = true;
            this.progressNpcHeadCD.node.active = true;
            this.npcChooseEffect.node.active = false;
            this.giftChooseEffect.node.active = false;

            tween(this.progressGiftCD).to(1, {progress:0}).start();
            tween(this.progressNpcHeadCD).to(1, {progress:0}).start();
            this._chooseNpcId = 0;
            this._chooseGiftNum = 0;

            this._isBulletCD = true;

        }
        //this.playerEditBox.string = "";
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

    initData(){
        let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
        this.btnChat.interactable = false;
        this.btnRank.interactable = true;
        //测试聊天信息拉取
        let json = new network.GetAllNPCRequest();
        json.command = 10017;
        json["data"] = {};
        json["data"].npcId = SceneNpcID;
        json["data"].roomId = GlobalConfig.instance.chooseScene;
        socket.sendWebSocketBinary(json);

        //测试物品信息拉取
        let json_2 = new network.getPlayerItemInfo();
        json_2.command = 100081;
        socket.sendWebSocketBinary(json_2);

        this.randomHead();
        // this.btnCheckLogin.active = GlobalConfig.instance.loginType == 1 ? false : true;
        // this.btnCheckLogin.active = false;
        //this.node.getChildByName("btnEnterVote").active = false;
        //this.node.getChildByName("btnLayout").getChildByName("btnEditMap").active = false;
        // if(GlobalConfig._instance.isDebug){
        //     this.btnCheckLogin.active = false;
        //     this.node.getChildByName("btnLayout").getChildByName("btnEditMap").active = false;
        //     this.node.getChildByName("btnEnterVote").active = false;
        // }
        for(let i in NpcRoomIndex){
            if(NpcRoomIndex[i] == GlobalConfig.instance.chooseScene){
                let npcHeadNode = instantiate(this.npcHeadPrefab);
                this.npcHeadLayout.addChild(npcHeadNode);
                npcHeadNode.getComponent(npcHeadPrefab).initData(i);
            }
        }
        // GlobalConfig.instance.roomDataList.forEach((scene,index)=>{
        //     if(scene.id == GlobalConfig.instance.chooseScene){
        //         scene.npcList.forEach(npcID=>{
        //             let npcHeadNode = instantiate(this.npcHeadPrefab);
        //             this.npcHeadLayout.addChild(npcHeadNode);
        //             npcHeadNode.getComponent(npcHeadPrefab).initData(npcID);
        //         })
        //     }
        // })

        this.initVoteInfo()

        if(GlobalConfig.instance.isWebFrame){
            this.node.getChildByName("btnExit").active = false;
        }
    }

    initVoteInfo(){
        let json = new network.GetAllNPCRequest();
        json.command = 10116;
        json["data"] = {};
        json["data"].roomId = GlobalConfig.instance.chooseScene;
        socket.sendWebSocketBinary(json);
    }

    updateViewNode(status:String){
        // let frameType = status == "normal" ? 1 : 2; 
        // let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
        // let framePath = "imgTitle_" + SceneNpcID +"_" + frameType;
        // this.viewFrameArr.forEach(frame=>{
        //     if(frame.name == framePath){
        //         this.viewNode.spriteFrame =  frame;
        //     }
        // })
    }

    initChatDataInfo(chatData){
        let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
        if(Number(chatData.data.data.npcId) == Number(SceneNpcID)){
            this.chatNode.destroyAllChildren();
            this._chatInfo=[];
            chatData.data.data.chats.forEach(chatInfo=>{
                this._chatInfo.push(chatInfo)
            })
            this._chatInfo.reverse();
            this._chatInfo.forEach(chatInfo=>{
                let chatMsgPrefabNode = instantiate(this.chatMsgPrefab);
                this.chatNode.addChild(chatMsgPrefabNode);
                chatMsgPrefabNode.getComponent(chatMsgPrefab).initData(chatInfo);
            })
            tween(this.chatNode).delay(0.1).call(()=>{
                this.chatScroll.scrollToBottom();
            }).start();
        }
    }

    updateChat(chatInfo){
        console.log("ChatInfo =====",chatInfo);
        let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
            this._chatInfo.push(chatInfo.data.data.chatData);
            let chatMsgPrefabNode = instantiate(this.chatMsgPrefab);
            this.chatNode.addChild(chatMsgPrefabNode);
            chatMsgPrefabNode.getComponent(chatMsgPrefab).initData(chatInfo.data.data.chatData);
            director.getScene().getComponentsInChildren(PlayerManager).forEach(playerScript=>{
                if(playerScript._playerData.userNo == chatInfo.data.data.chatData.sender){
                    playerScript.speak(chatInfo.data.data.chatData.content);
                }
            })
            if(this.chatNode.children.length > 20){
                this.chatNode.children[0].destroy();
            }
            tween(this.chatNode).delay(0.1).call(()=>{
                this.chatScroll.scrollToBottom();
            }).start();
            // if(chatInfo.data.data.chatData.replyMsgId){
            //     // let npcBulletNode = instantiate(this.bulletMsgPrefab);
            //     // this.msgNode.addChild(npcBulletNode);
            //     // npcBulletNode.getComponent(bulletMsgPrefab).initData(chatInfo.data.data.chatData)
            //     this._chatInfo.forEach(msg=>{
            //         if(msg.msgId == chatInfo.data.data.chatData.replyMsgId){
            //             let playerBulletNode = instantiate(this.bulletMsgPrefab);
            //             this.msgNode.addChild(playerBulletNode);
            //             playerBulletNode.getComponent(bulletMsgPrefab).initData(msg)
            //         }
            //     })
            // }
    }

    showBulletMsg(data){
        this._chatInfo.forEach(msg=>{
            if(msg.msgId == data.replyMsgId){
                let playerBulletNode = instantiate(this.bulletMsgPrefab);
                this.msgNode.addChild(playerBulletNode);
                playerBulletNode.getComponent(bulletMsgPrefab).initData(msg)
            }
        })
    }

    randomHead(){
        const headBundle = assetManager.getBundle("headFrame");
        if(headBundle){
            let randomIndex = Number(localStorage.getItem("avatarId"))
            //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
            headBundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                if(error||!this._isValid){
                    console.log("loadHeadError" + error)
                    return;
                }
                else{
                    this.imgHeadFrame.spriteFrame = spFrame
                }
            })
        }
        else{
            assetManager.loadBundle("headFrame",{},(error,bundle)=>{
                let randomIndex = Number(localStorage.getItem("avatarId"));
                //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
                bundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                    if(error||!this._isValid){
                        console.log("loadHeadError" + error)
                        return;
                    }
                    else{
                        this.imgHeadFrame.spriteFrame = spFrame
                    }
                })
            })
        }
    }

    onBtnTwitterLogin(){
        // WebUtils.showToast("Function not open yet");
        // return;
        // const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        // let result = '';
        // for (let i = 0; i < 10; i++) {
        //   const randomIndex = Math.floor(Math.random() * charset.length);
        //   result += charset[randomIndex];
        // }
        // this._randomState = result;
        // const clientId = 'aWVBTTZKV2xGSURfbVFwYzBaWWQ6MTpjaQ';
        // const redirectUri = 'https://aitown.infinitytest.cc';
        // const scope = 'users.read';
        // const state = result;  // 推荐使用随机值以防止CSRF
        // const authUrl = "https://twitter.com/i/oauth2/authorize?client_id="+ encodeURIComponent(clientId) + "&redirect_uri=" + encodeURIComponent(redirectUri) + "&response_type=code" + "&scope=" + encodeURIComponent(scope) +"&state=" + encodeURIComponent(state);

        // // this.webNode.getComponentInChildren(WebView).url = authUrl;
        // // this.webNode.active = true;
        // // tween(this.webNode).repeatForever(tween(this.webNode).delay(0.01).call(()=>{
        // //     this.checkWebCallBack();
        // // }).start()).start()
        // window.location.href = authUrl;
    }

    onBTnWalletLogin(){
        // if (typeof window["ethereum"] !== 'undefined') {
        //     console.log('MetaMask is installed!');
        //     this.connectWallet();
        // } else {
        //     WebUtils.showToast("Please install MetaMask wallet");
        // }
        // const clientId = "5e0b6a27-bb7f-4e80-b596-aab2847767e0"; // 替换为你自己的 Client ID
        // const redirectUri = "https://aitown.infinitytest.cc/"; // 替换为你的回调 URL
        // const scope = "wallet:accounts:read"; // 请求的权限
    
        // const authUrl = `https://www.coinbase.com/oauth/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}`;
    
        // window.location.href = authUrl;
    }

    onBtnTest(){
        console.log("onBtnTest");
        let canvas = director.getScene().getChildByName("Canvas");
        let videoPlayer = canvas.getComponentInChildren(VideoPlayer);
        videoPlayer.node.on(Node.EventType.TOUCH_START, (event) => {
           console.log("onBtnTest");
        });
        if(videoPlayer.isPlaying){
            videoPlayer.pause();
        }
        else{
            videoPlayer.play();
        }
        // canvas.getComponent(JietuComponent).startRecording("123123");
        // tween(this.node).delay(60).call(()=>{
        //     canvas.getComponent(JietuComponent).stopRecording();
        // }).start()
        // return;
        //canvas.getComponent(ScreenshotUploader).takeScreenshotAndUpload("http://192.168.0.100:9000/upload-image")
        // if (typeof window["ethereum"] !== 'undefined') {
        //     console.log('MetaMask is installed!');
        //     this.connectWallet();
        // } else {
        //     WebUtils.showToast("Please install MetaMask wallet");
        // }
        return;
    }
    
    onBtnTest2(){
        // let canvas = director.getScene().getChildByName("Canvas");
        // canvas.getComponent(JietuComponent).stopRecording();
        // return;
        // canvas.getComponent(ScreenshotUploader).takeScreenshotAndUpload("http://192.168.0.100:9000/upload-image")
        // if (typeof window["ethereum"] !== 'undefined') {
        //     console.log('MetaMask is installed!');
        //     this.connectWallet();
        // } else {
        //     WebUtils.showToast("Please install MetaMask wallet");
        // }
        return;
    }

    async connectWallet() {
        // if (typeof window["ethereum"] !== 'undefined') {
        //     try {
        //         // 使用标准的provider API
        //         const provider = window["ethereum"];
                
        //         // 请求连接钱包
        //         const accounts = await provider.request({ 
        //             method: 'eth_requestAccounts',
        //             params: []
        //         });

        //         console.log('连接成功，钱包地址:', accounts[0]);
                
        //         // 监听账户变化
        //         provider.on('accountsChanged', (newAccounts) => {
        //             console.log('钱包账户已更改:', newAccounts[0]);
        //             localStorage.setItem("walletID", newAccounts[0]);
        //         });

        //         // 监听链变化
        //         provider.on('chainChanged', (chainId) => {
        //             console.log('网络已更改:', chainId);
        //             window.location.reload();
        //         });

        //         let callback = () => {
        //             window.location.reload();
        //         }
        //         let str = "Link wallet successfully, click confirm to log in again.";
        //         WebUtils.showAlert(str, alert_cb_status.ok, callback);
        //         localStorage.setItem("walletID", accounts[0]);
        //         return accounts[0];
        //     } catch (error) {
        //         console.log('连接钱包失败:', error);
        //         WebUtils.showToast("Link wallet failed");
        //     }
        // } else {
        //     WebUtils.showToast("Please install MetaMask wallet");
        // }
    }

    onEditBoxBegan() {
        // 编辑开始时隐藏名字节点
        this.lblNameNode.active = false;
    }

    onEditBoxEnded() {
        // 编辑结束时，如果有选中的NPC，显示名字节点
        if (this._chooseNpcId && this._chooseNpcId !== 0) {
            this.lblNameNode.active = true;
        }
    }

    updateItem(itemInfo){
        this._itemInfo = itemInfo.data.data;
        //this.lblTestGoldNum.node.active = false;
        console.log("itemInfo=====",this._itemInfo)
        if(!this._itemInfo){
            return;
        }
        this._itemInfo.forEach(item=>{
            if(item.goodsId == ITEM_DOC_ID){
                if(this._isInitItem  && Number(item.count) > Number(this.lblPaperNum.string)){
                    this.addPaperAction(item.count)
                }
                this.lblPaperNum.string = item.count;
            }
            if(item.goodsId == ITEM_MONEY_ID){
                this.lblTestGoldNum.string = item.count;
            }
        })
        this._isInitItem = true;
    }

    addPaperAction(paperNum){
        if(paperNum){
            this.addPaperStatus.active = true;
            tween(this.addPaperStatus).call(()=>{
                this.addPaperStatus.getComponentInChildren(Label).string = paperNum;
            }).delay(1.5).call(()=>{
                this.addPaperStatus.active = false;
            }).start()
        }
    }

    initNpcDirNode(npcId){
        let npcDirNode = instantiate(this.npcDirPreafab);
        npcDirNode.getComponent(npcDirPrefab).initData(npcId.data);
        npcDirNode.active = false;
        this.npcDirRoot.addChild(npcDirNode);
        //console.log("initnpcID" ,npcId.data);
    }

    talkToNpc(npcId){
        if(!NpcName[npcId.data]){
            return;
        }
        this.chatEditBox.string = "@" + NpcName[npcId.data] + ":";
    }

    onBtnNews(){
        let newsLayerNode = instantiate(this.newsLayer);
        this.node.addChild(newsLayerNode);
    }

    onBtnEditMap(){
        WebUtils.showToast("Function not open yet");
        return;
    }

    onBtnGift(){
        WebUtils.showToast("Function not open yet");
        return;
    }

    onBtnChooseGift(eventTarget, CustomData: string) {
        console.log("eventTarget=====",eventTarget.target.name);

        const targetSprite = eventTarget.target.getComponent(Sprite);
        if (!targetSprite) {
            console.log('Target node does not have Sprite component');
            return;
        }

        this.giftLayout.active = false;
        this.giftNode.active = true;
        this.giftNode.getComponent(Sprite).spriteFrame = targetSprite.spriteFrame;
        this._chooseGiftNum = Number(CustomData);  
        this.giftChooseEffect.node.active = false;
    }

    getLiveStatues(){
        return this._isLive;
    }

    async onBtnVote(){
        let voteBundle = assetManager.getBundle("voteLayer");
        await voteBundle.load("voteLayer",Prefab,(err,voteLayerPrafb:Prefab)=>{
            if(err || !this._isValid){
                console.log("votelayer lode===" + err);
                return;
            }
            if(this.node.getComponentInChildren(voteLayer)){
                return;
            }
            let voteNode = instantiate(voteLayerPrafb);
            this.node.addChild(voteNode);
            return voteNode;
        })
        
    }

    onBtnCreatePlayer(){
        if(this.createStatus_2.active){
            WebUtils.showToast("The npc already exists");
            return;
        }
        if(localStorage.getItem("createGuide")){
            this.showCreatePlayer();
        }
        else{
            localStorage.setItem("createGuide","true");
            this.startCreatePlayerGuide();
        }
    }

   async showCreatePlayer(){
        let createBundle = assetManager.getBundle("createPlayer");
        await createBundle.load("createPlayerLayer",async (error,createLayer:Prefab)=>{
            if(!this._isValid || this.node.getComponentInChildren(createPlayerLayer)){
                return;
            }
            let createPlayerNode = instantiate(createLayer);
            await this.node.addChild(createPlayerNode);
        })
    }

    startCreatePlayerGuide(){
        this.guideNode.active = true;
        this.guideContent.getComponent(UIOpacity).opacity = 0;
        this.lblGuideText.string = ""
        Tween.stopAllByTarget(this.guideEffect);
        tween(this.guideContent.getComponent(UIOpacity)).to(0.2,{opacity:255}).call(()=>{
            tween(this.guideEffect.getComponent(UIOpacity)).repeatForever(tween(this.guideEffect.getComponent(UIOpacity)).to(0.2,{opacity:255}).to(0.2,{opacity:0}).start()).start();
            this.showCreatePlayerText_1();
        }).start();
    }

    showCreatePlayerText_1(){
        let content = "don't want just comment,want to really dive into this game?"
        this._guideContentIndex = 0;
        tween(this.guideNode).repeat(content.length,tween(this.guideNode).delay(0.1).call(()=>{
            if(content[this._guideContentIndex]){
                this.lblGuideText.string = this.lblGuideText.string + content[this._guideContentIndex];
                this._guideContentIndex++;
            }
            if(this.lblGuideText.string == content){
                tween(this.guideNode).delay(0.6).call(()=>{
                    this.showCreatePlayerText_2();
                }).start()
            }
        }).start()).start()
    }
    showCreatePlayerText_2(){
        let content = "Here is a chance!"
        this.lblGuideText.string = ""
        this._guideContentIndex = 0;
        tween(this.guideNode).repeat(content.length,tween(this.guideNode).delay(0.1).call(()=>{
            if(content[this._guideContentIndex]){
                this.lblGuideText.string = this.lblGuideText.string + content[this._guideContentIndex];
                this._guideContentIndex++;
            }
            // if(this.lblGuideText.string == content){
            //     tween(this.guideNode).delay(0.6).call(()=>{
            //         this.hideGuideCreatePlayer();
            //     }).start()
            // }
        }).start()).delay(0.6).call(()=>{
            this.hideGuideCreatePlayer();
        }).start()
    }

    hideGuideCreatePlayer(){
        tween(this.guideContent.getComponent(UIOpacity)).to(0.2,{opacity:0}).call(()=>{
            this.showCreatePlayer();
            this.guideNode.active = false;

        }).start();
    }

    async onBtnChatRecord(){
        let chatBundle = assetManager.getBundle("chatRecord");
        await chatBundle.load("chatRecordLayer",Prefab,(err,chatRecordPrefab:Prefab)=>{
            if(err || !this._isValid){
                console.log("votelayer lode===" + err);
                return;
            }
            if(this.node.getComponentInChildren(chatRecordLayer)){
                return;
            }
            let chatRecordNode = instantiate(chatRecordPrefab);
            this.node.addChild(chatRecordNode);
            return chatRecordNode;
        })
        
    }

    async onBtnLink(){
        let linkBundle = assetManager.getBundle("linkLayer");
        // const currentUrl = window.location.href;
        // const url = new URL(currentUrl);
        await linkBundle.load("linkLayer",Prefab,(err,linkLayerPrefab:Prefab)=>{
            if(err || !this._isValid){
                console.log("votelayer lode===" + err);
                return;
            }
            if(this.node.getComponentInChildren(linkLayer)){
                return;
            }
            let linkLayerNode = instantiate(linkLayerPrefab);
            this.node.addChild(linkLayerNode);
            return linkLayerNode;
        })
        
    }

    onBtnHelp(){
        this.helpNode.active = true;
    }

    onBtnCloseHelp(){
        this.helpNode.active = false;
    }

    playerTalkToNpc(npcId){
        if(!NpcName[npcId.data]){
            return;
        }
        // this.playerEditBox.string = "@" + NpcName[npcId.data] + ":";
    }

    onBtnShowHide(){
        // this.chatMsgScrollNode.active = !this.chatMsgScrollNode.active;
        // this.btnChatHide.active = this.chatMsgScrollNode.active;
        // this.btnChatShow.active = !this.btnChatHide.active;
    }

    showDestroyGuide(){
        this.guideNode.active = true;
        this.guideContent.getComponent(UIOpacity).opacity = 0;
        this.lblGuideText.string = ""
        Tween.stopAllByTarget(this.guideEffect);
        tween(this.guideContent.getComponent(UIOpacity)).to(0.2,{opacity:255}).call(()=>{
            tween(this.guideEffect.getComponent(UIOpacity)).repeatForever(tween(this.guideEffect.getComponent(UIOpacity)).to(0.2,{opacity:255}).to(0.2,{opacity:0}).start()).start();
            this.showDestroyText();
        }).start();
    }

    showDestroyText(){
        let content = "See you next time!";
        this._guideContentIndex = 0;
        tween(this.guideNode).repeat(content.length,tween(this.guideNode).delay(0.1).call(()=>{
            if(content[this._guideContentIndex]){
                this.lblGuideText.string = this.lblGuideText.string + content[this._guideContentIndex];
                this._guideContentIndex++;
            }
            if(this.lblGuideText.string == content){
                tween(this.guideNode).delay(1.2).call(()=>{
                    this.guideNode.active = false;
                }).start()
            }
        }).start()).start()
    }

    onBtnNftNodeClose(){
        this.getNftNode.active = false;
    }

    showNftNode(nftId){
        this.getNftNode.active = true;
        this.imgNftBg_1.active = Math.random() > 0.5 ? true : false;
        this.imgNftBg_2.active = !this.imgNftBg_1.active;
        if(this.imgNftBg_1.active){
            this.getNftNode.getComponentInChildren(Label).color = color(255,255,255,255);
        }
        else{
            this.getNftNode.getComponentInChildren(Label).color = color(0,0,0,255);
        }
        this.getNftNode.getComponentInChildren(Label).string = nftDes[nftId];
        this.initNftImage(nftId);
    }

    initNftImage(nftId) {
        resources.load("common/image/imgNft_" + nftId +"/spriteFrame",(error,sprFrame:SpriteFrame)=>{
            if (!this._isValid) return;
            if(error){
                console.log(error);
            }
            else{
                this.imgNftContent.spriteFrame = sprFrame;
            }
        })
    }

    getHeadFrame(){
        return this.imgHeadFrame.spriteFrame;
    }

    onBtnExit(){
        let json = new network.GetAllNPCRequest();
        json.command = 10110;
        json.type = 1;
        // json["data"] = {};
        //json["data"]["roomId"] = this._sceneId;
        GlobalConfig.instance.chooseNpc = null
        GlobalConfig.instance.chooseScene = null
        socket.sendWebSocketBinary(json);
    }

    onBtnChooseNpcChat(){
        if(this._isBulletCD){
            return;
        }
        this.npcHeadLayout.active = !this.npcHeadLayout.active;
        this._chooseNpcId = 0;
        this.npcHeadNode.active = false;
        this.npcChooseEffect.node.active = true;
    }

    onBtnChooseNpcGift(){
        if(this._isBulletCD){
            return;
        }
        this.giftLayout.active = !this.giftLayout.active;
        this._chooseGiftNum = 0;
        this.giftNode.active = false;
        this.giftChooseEffect.node.active = true;
    }

    chooseNpcHead(npcId: { data: number }){
        this.npcHeadLayout.active = false;
        this.npcHeadNode.active = true;
        this.npcHeadNode.getComponent(npcHeadPrefab).initData(String(npcId.data));
        this._chooseNpcId = npcId.data;
        this.lblNameNode.active = true;
        this.lblNameNode.getComponentInChildren(Label).string = "@" + String(NpcName[npcId.data]) + " ";
        this.progressNpcHeadCD.node.active = false;
        this.npcChooseEffect.node.active = false;
        if(!this.chatEditBox.string){
            this.chatEditBox.string = " ";
        }
    }

    sendBulletMsg(data){
        console.log("sendBulletMsg=====",data.data);
        let bulletMsgNode = instantiate(this.bulletMsgPrefab);
        this.msgNode.addChild(bulletMsgNode);
        bulletMsgNode.getComponent(bulletMsgPrefab).initData(data.data);
        if(data.data.userNo == GlobalConfig.instance.LoginData.data.player.playerId){
            GlobalConfig.instance.setBulletMsgCDTime(data.data.cdTime);
            let cdTime = Math.ceil(data.data.cdTime / 1000);
            console.log("cdTime=====",cdTime);
            tween(this.progressGiftCD).delay(1.1).to(cdTime, {progress:1}).call(()=>{
                this.giftChooseEffect.node.active = true;
                this.giftChooseEffect.playAnimation("newAnimation",1);
            }).start();
            tween(this.progressNpcHeadCD).delay(1.1).to(cdTime, {progress:1}).call(()=>{
                this._isBulletCD = false;
                this.npcChooseEffect.node.active = true;
                this.npcChooseEffect.playAnimation("newAnimation",1);
            }).start();
        }
    }

    onBtnVoteYes(){
        let json = new network.GetAllNPCRequest()
        json.command = 10117;
        json["data"] = {};
        json["data"]["roomId"] = GlobalConfig.instance.chooseScene;
        json["data"]["choose"] = true;
        json["data"]["reward"] = 0;
        socket.sendWebSocketBinary(json);

        this.voteStartNode.active = false;
        this.voteGiftNode.active = true;
        let giftNode_1 = this.giftProgressBar_1.node.getChildByName("giftNode");
        giftNode_1.getComponent(Sprite).spriteFrame = null;

        let giftNode_2 = this.giftProgressBar_2.node.getChildByName("giftNode");
        giftNode_2.getComponent(Sprite).spriteFrame = null;
    }

    onBtnVoteNo(){
        let json = new network.GetAllNPCRequest()
        json.command = 10117;
        json["data"] = {};
        json["data"]["roomId"] = GlobalConfig.instance.chooseScene;
        json["data"]["choose"] = false;
        json["data"]["reward"] = 0;
        socket.sendWebSocketBinary(json);

        this.voteStartNode.active = false;
        this.voteGiftNode.active = true;

        let giftNode_1 = this.giftProgressBar_1.node.getChildByName("giftNode");
        giftNode_1.getComponent(Sprite).spriteFrame = null;

        let giftNode_2 = this.giftProgressBar_2.node.getChildByName("giftNode");
        giftNode_2.getComponent(Sprite).spriteFrame = null;
    }

    onBtnVoteStatus_1(){
        if(this._voteInfo){
            let myVoteCount = this._voteInfo.myYesCount + this._voteInfo.myNoCount;
            if(myVoteCount > 0){
                this.voteGiftNode.active = true;
            }
            else{
                this.voteStartNode.active = true;
            }
        }
    }

    onBtnVoteStatus_2(){
        if(this._voteInfo){
            this.voteUnOpenNode.active = true;
        }
    }

    onBtnCloseVoteStart(){
        this.voteStartNode.active = false;
    }

    onBtnCloseVoteGiftNode(){
        this.voteGiftNode.active = false;
    }

    onBtnChooseGift_1(){
        let layoutNode = this.giftProgressBar_1.node.getComponentInChildren(Layout).node;
        layoutNode.active = !layoutNode.active;

        let giftNode = this.giftProgressBar_1.node.getChildByName("giftNode");
        giftNode.active = false;

        let layoutNode2 = this.giftProgressBar_2.node.getComponentInChildren(Layout).node;
        layoutNode2.active = false;

    }

    onBtnChooseGift_2(){
        let layoutNode = this.giftProgressBar_2.node.getComponentInChildren(Layout).node;
        layoutNode.active = !layoutNode.active; 

        let giftNode = this.giftProgressBar_2.node.getChildByName("giftNode");        let giftNode2 = this.giftProgressBar_2.node.getChildByName("giftNode");
        giftNode.active = false;

        let layoutNode2 = this.giftProgressBar_1.node.getComponentInChildren(Layout).node;
        layoutNode2.active = false;
    }
    
    
    onBtnChooseVoteGift_1(eventTarget, CustomData: string) {
        console.log("eventTarget=====",eventTarget.target.name);

        const targetSprite = eventTarget.target.getComponent(Sprite);
        if (!targetSprite) {
            console.log('Target node does not have Sprite component');
            return;
        }
        let layoutNode = this.giftProgressBar_1.node.getComponentInChildren(Layout).node;
        layoutNode.active = false;
        let chooseGiftNum = Number(CustomData);
        let json = new network.GetAllNPCRequest()
        json.command = 10117;
        json["data"] = {};
        json["data"]["roomId"] = GlobalConfig.instance.chooseScene;
        json["data"]["choose"] = true;
        json["data"]["reward"] = chooseGiftNum;
        socket.sendWebSocketBinary(json);
    }

    onBtnChooseVoteGift_2(eventTarget, CustomData: string) {
        console.log("eventTarget=====",eventTarget.target.name);

        const targetSprite = eventTarget.target.getComponent(Sprite);
        if (!targetSprite) {
            console.log('Target node does not have Sprite component');
            return;
        }
        let layoutNode = this.giftProgressBar_2.node.getComponentInChildren(Layout).node;
        layoutNode.active = false;
        let chooseGiftNum = Number(CustomData);
        let json = new network.GetAllNPCRequest()
        json.command = 10117;
        json["data"] = {};
        json["data"]["roomId"] = GlobalConfig.instance.chooseScene;
        json["data"]["choose"] = false;
        json["data"]["reward"] = chooseGiftNum;
        socket.sendWebSocketBinary(json);  
    }

    onBtnCloseVoteResult(){
        this.voteResultNode.active = false;
    }

    onBtnVoteUnOpenNode(){
        this.voteUnOpenNode.active = false;
    }

    getVoteInfo(data){
        //{"roomId":1,"state":0,"endTime":275660,"yesCount":0,"noCount":0,"myYesCount":0,"myNoCount":0,"content":"Should Popcat ask Pippin to go fishing?","yesContent":"Popcat will ask Pippin to go fishing.","noContent":"Popcat will not ask Pippin to go fishing."}
        console.log("getVoteInfo=====",data.data);
        this._voteInfo = data.data;

        if(this._voteInfo.state == 0){ 
            this.btnVoteStatus_1.active = true;
            this.btnVoteStatus_2.active = false;
            this._voteCreateTime = new Date().getTime();
            this._voteEndTime = Math.ceil(this._voteInfo.endTime / 1000);
            this.voteGiftNode.getComponentInChildren(Label).string = this._voteInfo.content;
            this.voteStartNode.getComponentInChildren(Label).string = this._voteInfo.content;
            this.giftProgressBar_1.progress =  this._voteInfo.yesCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
            this.giftProgressBar_2.progress =  this._voteInfo.noCount / (this._voteInfo.yesCount + this._voteInfo.noCount);


            this.giftProgressBar_1.node.getComponentInChildren(Label).string = this._voteInfo.yesCount;
            this.giftProgressBar_2.node.getComponentInChildren(Label).string = this._voteInfo.noCount;
        }
        else{
            this.btnVoteStatus_1.active = false;
            this.btnVoteStatus_2.active = true;
        }
    }

    updateVoteInfo(data){
        console.log("updateVoteInfo=====",data.data);
        if(this._voteInfo){
            if(data.data.state == 1){
                this.showVoteResult()
                this.voteGiftNode.active = false;
                this.voteStartNode.active = false;
                this.voteUnOpenNode.active = false;
                this.btnVoteStatus_1.active = false;
                this.btnVoteStatus_2.active = true;
            }
            else{
                this.voteUnOpenNode.active = false;
                this.voteResultNode.active = false;
                this._voteInfo.yesCount = data.data.yesCount;
                this._voteInfo.noCount = data.data.noCount; 
                if(this._voteInfo.state == 0){
                    if(this.voteGiftNode.active){
                        let progress1 =  this._voteInfo.yesCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
                        let progress2 =  this._voteInfo.noCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
                        console.log("progress1=====",progress1);
                        console.log("progress2=====",progress2);
                        tween(this.giftProgressBar_1).to(0.25, {progress:progress1}).start();
                        tween(this.giftProgressBar_2).to(0.25, {progress:progress2}).start();
                    }
                    else{
                        let progress1 =  this._voteInfo.yesCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
                        let progress2 =  this._voteInfo.noCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
                        this.giftProgressBar_1.progress = progress1;
                        this.giftProgressBar_2.progress = progress2;
                    }
                    this.giftProgressBar_1.node.getComponentInChildren(Label).string = this._voteInfo.yesCount;
                    this.giftProgressBar_2.node.getComponentInChildren(Label).string = this._voteInfo.noCount;
                }
                else{
                    this.btnVoteStatus_1.active = true;
                    this.btnVoteStatus_2.active = false;
                    this._voteCreateTime = new Date().getTime();
                    this._voteEndTime = Math.ceil(data.data.endTime / 1000);
                    this.voteGiftNode.getComponentInChildren(Label).string = this._voteInfo.content;
                    this.voteStartNode.getComponentInChildren(Label).string = this._voteInfo.content;
                    this.giftProgressBar_1.progress =  this._voteInfo.yesCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
                    this.giftProgressBar_2.progress =  this._voteInfo.noCount / (this._voteInfo.yesCount + this._voteInfo.noCount);
                    this.giftProgressBar_1.node.getComponentInChildren(Label).string = this._voteInfo.yesCount;
                    this.giftProgressBar_2.node.getComponentInChildren(Label).string = this._voteInfo.noCount;
                    this._voteInfo.myYesCount = 0;
                    this._voteInfo.myNoCount  = 0;
                }
            }
            this._voteInfo.state = data.data.state;

        }   
        
        //{"roomId":4,"state":0,"endTime":479966,"yesCount":0,"noCount":0,"content":"Should Leo tell Ivy his secret?","yesContent":" Leo will tell Ivy his secret.","noContent":"Leo will not tell Ivy his secret."}
    }
    
    updateMyVoteInfo(data){
        console.log("updateMyVoteInfo=====",data.data);
        //{"roomId":0,"choose":true,"reward":0}
        if(data.data.choose){
            if(data.data.reward > 0){
                this._voteInfo.myYesCount += data.data.reward;
            }
            else{
                this._voteInfo.myYesCount += 1;
            }
        }
        else{
            if(data.data.reward > 0){
                this._voteInfo.myNoCount += data.data.reward;
            }
            else{
                this._voteInfo.myNoCount += 1;
            }
        }
    }
    showVoteResult(){
        if(this._voteInfo.yesCount == this._voteInfo.noCount){
            return;
        }
        else{
            if(this._voteInfo.myYesCount || this._voteInfo.myNoCount){
                this.voteResultNode.active = true;
                let contentStr = this._voteInfo.yesCount > this._voteInfo.noCount ? this._voteInfo.yesContent : this._voteInfo.noContent;
                let resultNum = this._voteInfo.yesCount > this._voteInfo.noCount ? 1 : 2;
                let myNum = this._voteInfo.myYesCount > this._voteInfo.myNoCount ? 1 : 2;
                let frontStr = resultNum == myNum ? "Your vote made ite happen!" : "Oh noes!Your vote didn't make it.";
                this.voteResultNode.getComponentInChildren(Label).string = frontStr  + contentStr;
                if(myNum == resultNum){
                    this.voteResultNode.getChildByName("voteResultNode").getChildByName("imgFail").active = false;
                    this.voteResultNode.getChildByName("voteResultNode").getChildByName("imgSuccess").active = true;
                }
                else{
                    this.voteResultNode.getChildByName("voteResultNode").getChildByName("imgFail").active = true;
                    this.voteResultNode.getChildByName("voteResultNode").getChildByName("imgSuccess").active = false;
                }

            }
            else{
                return;
            }
        }
    }
    onBtnShowVoteConfirm(eventTarget, CustomData: string) {
        let layout = eventTarget.target.parent;
        layout.children.forEach(child=>{
            child.children[0].active = false;
        })
        eventTarget.target.children[0].active = true;
    }


}


