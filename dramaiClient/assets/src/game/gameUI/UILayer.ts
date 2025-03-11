import { _decorator, assetManager, Button, color, Component, director, EditBox, game, instantiate, Label, Node, Prefab, resources, ScrollView, Sprite, SpriteFrame, sys, Tween, tween, UIOpacity, UITransform, v2, v3, view } from 'cc';
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
import { NpcName } from '../../StaticUtils/NPCConfig';
import { alert_cb_status } from '../../common/alertPrefab';
import { voteLayer } from '../voteLayer/voteLayer';
import { chatRecordLayer } from '../chatRecord/chatRecordLayer';
import { createPlayerLayer } from '../createPlayer/createPlayerLayer';
import { ScreenshotUploader } from '../../utils/Screenshot';
import { JietuComponent } from '../../manager/JietuComponent';
import { linkLayer } from '../linkLayer/linkLayer';
import { PlayerManager } from '../../NPC/PlayerManager';
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
    loginNode:Node = null;

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
    imgHeadBg_2:Node = null;

    @property(Node)
    chatMsgScrollNode:Node = null;

    @property(Node)
    getNftNode:Node = null;

    @property(Node)
    imgNftBg_1:Node = null;

    @property(Node)
    imgNftBg_2:Node = null;

    @property(Sprite)
    imgNftContent:Sprite = null;

    _chatInfo = [];
    _randomState = null;
    _isInitItem = false;
    _itemInfo = [];
    _isLive = false;
    _guideContentIndex = 0;
    protected onLoad(): void {
        //director.root.pipeline.profiler.enabled = false;
        observer.on(EventType.GETCHAT_BYID,this.initChatDataInfo,this);
        observer.on(EventType.UPDATE_CHAT,this.updateChat,this);
        observer.on(EventType.UPDATE_ITEM,this.updateItem,this);
        observer.on(EventType.INIT_DIR_PREFAB,this.initNpcDirNode,this);
        //observer.on(EventType.FOLLOWNPC,this.talkToNpc,this);
        //observer.on(EventType.PLAYERCLICK,this.playerTalkToNpc,this);
        observer.on(EventType.DESTROYGUIDE,this.showDestroyGuide,this);
    }

    protected onDestroy(): void {
        observer.off(EventType.GETCHAT_BYID,this.initChatDataInfo,this);
        observer.off(EventType.UPDATE_CHAT,this.updateChat,this);
        observer.off(EventType.UPDATE_ITEM,this.updateItem,this);
        observer.off(EventType.INIT_DIR_PREFAB,this.initNpcDirNode,this);
        //observer.off(EventType.FOLLOWNPC,this.talkToNpc,this);
        //observer.off(EventType.PLAYERCLICK,this.playerTalkToNpc,this);
        observer.off(EventType.DESTROYGUIDE,this.showDestroyGuide,this);
    }

    start() {
        this.initData();
    }

    update(deltaTime: number) {
        //this.node.getComponent(UITransform).setContentSize(view.getVisibleSize().width,view.getVisibleSize().height);
        // if(this.chatEditBox.string){
        //     this.TEXT_LABEL.active = false;
        //     this.tempEditView.active = true;
        //     this.chatEditBox.node.active = false
        // }
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
        
        let mapScript = director.getScene().getComponentInChildren(GameScene).getMapScript();
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

    onBtnSend(){
        if(this.chatEditBox.node.active && this.chatEditBox.string.length < 1){
            return;
        }
        // if(this.playerEditBox.node.active && this.playerEditBox.string.length < 1){
        //     return;
        // }
        let SceneNpcID = director.getScene().getComponentInChildren(GameScene).getNpcID();
        let json = new network.RequestSendChatData();
        json.command = 10016;
        json.data.npcId = SceneNpcID;
        json.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
        if(this.chatEditBox.node.active){
            json.data.context = this.chatEditBox.string;
        }
        // if(this.playerEditBox.node.active){
        //     json.data.context = this.playerEditBox.string;
        // }
        socket.sendWebSocketBinary(json);
        this.chatEditBox.string = "";
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
        let json = new network.getChatDataInfo();
        json.command = 10017;
        json.data.npcId = SceneNpcID;
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

        const currentUrl = window.location.href;
        const url = new URL(currentUrl);
        // 获取查询参数
        let codeParam = url.searchParams.get("version");
        if(codeParam == "live"){
            this._isLive = true;
        }
        if(codeParam == "capture"){

        }

        this.viewNode.node.active = false;
    //    if(currentUrl.includes("morpheus") || currentUrl.includes("pippin")){
    //         this.viewNode.node.active = false;
    //         if(!sys.isMobile){
    //             this.msgNode.active = false;
    //             this.node.getChildByName("btnLayout").active = false;
    //             this.node.getChildByName("btnEnterVote").active = false;
    //             this.viewNode.node.active = false;
    //             this.node.getChildByName("chatLayout").active = false;
    //             this.chatEditBox.node.active = false;
    //             this.node.getChildByName("btnSend").active = false;
    //             this.node.getChildByName("imgHeadBox").active = false;
    //             this.node.getChildByName("btnCreatePlayer").active = false;
    //         }
    //    }
    //    if(currentUrl.includes("capture")){
    //         this.msgNode.active = false;
    //         this.node.getChildByName("btnLayout").active = false;
    //         this.node.getChildByName("btnEnterVote").active = false;
    //         this.viewNode.node.active = false;
    //         this.node.getChildByName("chatLayout").active = false;
    //         this.chatEditBox.node.active = false;
    //         this.node.getChildByName("btnSend").active = false;
    //         this.node.getChildByName("imgHeadBox").active = false;
    //         this.node.getChildByName("btnCreatePlayer").active = false;
    //    }
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
        if(Number(chatInfo.data.data.npcId) == Number(SceneNpcID)){
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
            let randomIndex = Math.floor(Math.random() * 160) + 1
            //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
            headBundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                if(error){
                    console.log("loadHeadError" + error)
                }
                else{
                    this.imgHeadFrame.spriteFrame = spFrame
                }
            })
        }
        else{
            assetManager.loadBundle("headFrame",{},(error,bundle)=>{
                let randomIndex = Math.floor(Math.random() * 160) + 1
                //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
                bundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
                    if(error){
                        console.log("loadHeadError" + error)
                    }
                    else{
                        this.imgHeadFrame.spriteFrame = spFrame
                    }
                })
            })
        }
    }

    onBtnLoginClose(){
        tween(this.loginNode).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.loginNode.active = false;
        }).start()
    }

    onBtnTwitterLogin(){
        WebUtils.showToast("Function not open yet");
        return;
        const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let result = '';
        for (let i = 0; i < 10; i++) {
          const randomIndex = Math.floor(Math.random() * charset.length);
          result += charset[randomIndex];
        }
        this._randomState = result;
        const clientId = 'aWVBTTZKV2xGSURfbVFwYzBaWWQ6MTpjaQ';
        const redirectUri = 'https://aitown.infinitytest.cc';
        const scope = 'users.read';
        const state = result;  // 推荐使用随机值以防止CSRF
        const authUrl = "https://twitter.com/i/oauth2/authorize?client_id="+ encodeURIComponent(clientId) + "&redirect_uri=" + encodeURIComponent(redirectUri) + "&response_type=code" + "&scope=" + encodeURIComponent(scope) +"&state=" + encodeURIComponent(state);

        // this.webNode.getComponentInChildren(WebView).url = authUrl;
        // this.webNode.active = true;
        // tween(this.webNode).repeatForever(tween(this.webNode).delay(0.01).call(()=>{
        //     this.checkWebCallBack();
        // }).start()).start()
        window.location.href = authUrl;
    }

    onBTnWalletLogin(){
        // if (typeof window["ethereum"] !== 'undefined') {
        //     console.log('MetaMask is installed!');
        //     this.connectWallet();
        // } else {
        //     WebUtils.showToast("Please install MetaMask wallet");
        // }
        const clientId = "5e0b6a27-bb7f-4e80-b596-aab2847767e0"; // 替换为你自己的 Client ID
        const redirectUri = "https://aitown.infinitytest.cc/"; // 替换为你的回调 URL
        const scope = "wallet:accounts:read"; // 请求的权限
    
        const authUrl = `https://www.coinbase.com/oauth/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&scope=${scope}`;
    
        window.location.href = authUrl;
    }

    onBtnTest(){
        let canvas = director.getScene().getChildByName("Canvas");
        canvas.getComponentsInChildren(NpcManager).forEach(npcScript=>{
            npcScript.testFunction();
        })
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
        //console.log("pos=====" + JSON.stringify(this.TEXT_LABEL.position))
        this.loginNode.active = true;
        return;
        tween(this.node).repeatForever(tween(this.node).delay(1).call(()=>{
            console.log("pos=====" + JSON.stringify(this.TEXT_LABEL.position))
        })).start()
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
        //console.log("pos=====" + JSON.stringify(this.TEXT_LABEL.position))
        this.loginNode.active = true;
        return;
        tween(this.node).repeatForever(tween(this.node).delay(1).call(()=>{
            console.log("pos=====" + JSON.stringify(this.TEXT_LABEL.position))
        })).start()
    }

    async connectWallet() {
        if (typeof window["ethereum"] !== 'undefined') {
            try {
                // 请求连接钱包
                const accounts = await window["ethereum"].request({ method: 'eth_requestAccounts' });
                console.log('连接成功，钱包地址:', accounts[0]);
                let callback = ()=>{
                    window.location.reload();
                }
                let str = "Link wallet successfully,click confirm to log in again.";
                WebUtils.showAlert(str,alert_cb_status.ok,callback);
                localStorage.setItem("walletID",accounts[0]);
                return accounts[0]; // 返回第一个钱包地址
            } catch (error) {
                WebUtils.showToast("Link wallet failed");
            }
        } else {
            WebUtils.showToast("Please install MetaMask wallet");
        }
    }

    onEditBegin(){
        // this.chatEditBox.node.active = false;
        // this.tempEditView.active = true;
    }

    onEditEnd(){
        // this.chatEditBox.node.active = true;
        // this.chatEditBox.blur();
        // this.tempEditView.active = true;
    }

    updateItem(itemInfo){
        this._itemInfo = itemInfo.data.data;
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
                this.lblGoldNum.string = item.count;
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

    onBtnCheckLogin(){
        this.loginNode.active = true;
        this.loginNode.setScale(v3(0, 0, 1))
        tween(this.loginNode).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).start()
    }

    onBtnEditMap(){
        WebUtils.showToast("Function not open yet");
        return;
    }

    onBtnGift(){
        WebUtils.showToast("Function not open yet");
        return;
    }

    getLiveStatues(){
        return this._isLive;
    }

    async onBtnVote(){
        let voteBundle = assetManager.getBundle("voteLayer");
        await voteBundle.load("voteLayer",Prefab,(err,voteLayerPrafb:Prefab)=>{
            if(err){
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
            if(this.node.getComponentInChildren(createPlayerLayer)){
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
            if(err){
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
        const currentUrl = window.location.href;
        const url = new URL(currentUrl);
        if(currentUrl.includes("aitown_pc")){
            await linkBundle.load("linkLayer_pc",Prefab,(err,linkLayerPrefab:Prefab)=>{
                if(err){
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
        else{
            await linkBundle.load("linkLayer",Prefab,(err,linkLayerPrefab:Prefab)=>{
                if(err){
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
        
    }

    // onBtnHelp(){
    //     this.helpNode.active = true;
    // }

    // onBtnCloseHelp(){
    //     this.helpNode.active = false;
    // }

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
        resources.load("common/image/imgNft_" + nftId +"/spriteFrame",(error,sprFrame:SpriteFrame)=>{
            if(error){
                console.log("load nft frame error" + error);
                return;
            }
            this.imgNftContent.spriteFrame = sprFrame;
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
}


