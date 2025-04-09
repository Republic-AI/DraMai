import { _decorator, assetManager, Camera, Component, director, ImageAsset, instantiate, Label, Node, Prefab, ScrollView, Sprite, SpriteFrame, Texture2D, tween, UITransform, v3 } from 'cc';
import { network } from '../../../src/model/RequestData';
import { observer, socket } from '../../../src/game/App';
import { EventType } from '../../../src/EventType';
import { GlobalConfig } from '../../../src/game/config/GlobalConfig';
import { optionPrefab } from './optionPrefab';
import { sceneItem } from './sceneItem';
import { twitterModeType_1 } from './twitterModeType_1';
import { twitterModeType_2 } from './twitterModeType_2';
import { lobbyChatRecord } from './lobbyChatRecord';
import { GuideMask } from '../../../src/game/gameUI/GuideMask';
import { storyItemPrefab } from './storyItemPrefab';
const { ccclass, property } = _decorator;

@ccclass('lobbyScene')
export class lobbyScene extends Component {
    @property(Node)
    optionBtnLayout:Node = null;

    @property(Node)
    sceneViewContent:Node = null;

    @property(Node)
    worldNode:Node = null;

    @property(Prefab)
    optionPrefab:Prefab = null;

    @property(Prefab)
    sceneItem:Prefab = null;

    @property(Camera)
    mainCamera:Camera = null;

    @property(ScrollView)
    sceneItemView:ScrollView = null;

    @property(ScrollView)
    twitterView:ScrollView = null;

    @property(Node)
    twitterViewContent:Node = null;

    @property(Node)
    maskNode:Node = null;

    @property(Prefab)
    twitterModeType_1:Prefab = null;

    @property(Prefab)
    twitterModeType_2:Prefab = null;

    @property(Node)
    status_1:Node = null;
    
    @property(Node)
    status_2:Node = null;

    @property(Node)
    btnBanner:Node = null;

    @property(Node)
    bannerNode:Node = null;

    @property(Sprite)
    imgBannerContent:Sprite = null;

    @property(Node)
    chatRecordView:Node = null;
    
    @property(Node)
    chatRecordViewContent:Node = null;

    @property(Node)
    btnPageNode:Node = null;

    @property(Prefab)
    lobbyChatRecord:Prefab = null;

    @property(Node)
    userView:Node = null;

    @property(Node)
    userViewContent:Node = null;

    @property(Sprite)
    imgPlayerHead:Sprite = null;

    @property(Label)
    lblPlayerName:Label = null;

    @property(Label)
    lblUserNo:Label = null;

    @property(Label)
    lblLocation:Label = null;

    @property(Label)
    lblGoldNum:Label = null;

    @property(Node)
    storyContent:Node = null;

    @property(Prefab)
    storyItemPrefab:Prefab = null;

    _orignOrthoHeight = null;
    _chooseSceneId = 1;
    _pageStatus = 1
    _bannerStatus = "show"
    _sceneBannerFrame = {};
    _userViewContentPosY = 0;
    protected onLoad(): void {
        director.addPersistRootNode(this.worldNode);
        observer.on(EventType.GETLOBBYINFO,this.getLobbyInfo,this);
        observer.on(EventType.REQUESTENTERSCENE,this.enterScene,this);
        observer.on("ChooseBtnScene",this.onChooseClick,this);
        observer.on(EventType.INITTWITTERVIEW,this.initTwitterView,this);
        observer.on(EventType.INITCHATRECORD,this.initChatRecord,this);
        observer.on(EventType.UPDATE_ITEM,this.updateItem,this);
        observer.on(EventType.INITSTORYINFO,this.getStoryInfo,this);
    }
    
    _lobbyRoomInfo = null;
    _isInit
    start() {
        console.log("address=========" + GlobalConfig.instance.address)
        this._initData()
    }

    update(deltaTime: number) {
        if(this._sceneBannerFrame[this._chooseSceneId]){
            this.imgBannerContent.spriteFrame = this._sceneBannerFrame[this._chooseSceneId];
        }
        this.status_1.active = this._pageStatus == 1 ? true : false;
        this.status_2.active = (this._pageStatus == 2 || this._pageStatus == 4 )? true : false;
    }

    protected onDestroy(): void {
        observer.off(EventType.GETLOBBYINFO,this.getLobbyInfo,this);
        observer.off(EventType.REQUESTENTERSCENE,this.enterScene,this);
        observer.off("ChooseBtnScene",this.onChooseClick,this);
        observer.off(EventType.INITTWITTERVIEW,this.initTwitterView,this);
        observer.off(EventType.INITCHATRECORD,this.initChatRecord,this);
        observer.off(EventType.UPDATE_ITEM,this.updateItem,this);
        observer.off(EventType.INITSTORYINFO,this.getStoryInfo,this);
    }

    _initData(){
        this._pageStatus = 1;
        let json = new network.GetAllNPCRequest();
        json.command = 10109;
        json.type = 1;
        socket.sendWebSocketBinary(json);

        this.twitterView.node.active = false;
        this.sceneItemView.node.active = true;

        this.initRequestTwitter()

        let viewSize = this.sceneItemView.getComponent(UITransform).contentSize;
        let posY =  viewSize.height / 2  - 130;
        this.bannerNode.setPosition(0,posY);
        //this.btnBanner.setScale(1,-1);

        this.initRequestChatRecord();

        this.initUserView();

    }

    initUserView(){
        const headBundle = assetManager.getBundle("headFrame");
        let randomIndex = Number(localStorage.getItem("avatarId"))
        //console.log("headFrame/imgHeadFrame_" + randomIndex + "/spriteFrame");
        headBundle.load("imgHeadFrame_" + randomIndex + "/spriteFrame",SpriteFrame,(error,spFrame)=>{
            if(error){
                console.log("loadHeadError" + error)
            }
            else{
                this.imgPlayerHead.spriteFrame = spFrame
            }
        })

        this.lblUserNo.string = GlobalConfig.instance.LoginData.data.player.playerId;
        this.lblLocation.string = GlobalConfig.instance.address;
        this.lblPlayerName.string = GlobalConfig.instance.nickName;
        //物品信息拉取
        let json_2 = new network.getPlayerItemInfo();
        json_2.command = 100081;
        socket.sendWebSocketBinary(json_2);

        let json = new network.GetAllNPCRequest();
        json.command = 10119;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = this._chooseSceneId;
        socket.sendWebSocketBinary(json);
    }

    initRequestTwitter(){
        let json = new network.GetAllNPCRequest();
        json.command = 10112;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = 0;
        json["data"]["page"] = 0;
        json["data"]["size"] = 999;
        socket.sendWebSocketBinary(json);
    }

    initRequestChatRecord(){
        let json = new network.GetAllNPCRequest();
        json.command = 10114;
        json.type = 1;
        socket.sendWebSocketBinary(json);
    }

    getLobbyInfo(data){
        if(!this._lobbyRoomInfo){
            console.log("lobby data========" + JSON.stringify(data.data.data.roomDataList));
            this._lobbyRoomInfo = data.data.data.roomDataList;
            GlobalConfig.instance.roomDataList = data.data.data.roomDataList;
            this._lobbyRoomInfo.forEach((scene,index)=>{
                let optionNode = instantiate(this.optionPrefab);
                this.optionBtnLayout.addChild(optionNode);
                optionNode.getComponent(optionPrefab).initData(scene.id)
                scene.npcList.forEach(npcID=>{
                    let sceneItemNode = instantiate(this.sceneItem);
                    this.sceneViewContent.addChild(sceneItemNode);
                    sceneItemNode.getComponent(sceneItem).initData(npcID,scene.id)
                })
                assetManager.loadRemote<ImageAsset>(scene.bannerUrl, { ext: '.png' }, (err, imageAsset) => {
                    if (err) {
                        console.error("图片加载失败:", err);
                        return;
                    }
                
                    // 确保 imageAsset 有效
                    if (!(imageAsset instanceof ImageAsset)) {
                        console.error("imageAsset 不是 ImageAsset 类型");
                        return;
                    }
                
                    // 创建 Texture2D
                    const texture = new Texture2D();
                    texture.image = imageAsset;
                
                    // 创建 SpriteFrame
                    const spriteFrame = new SpriteFrame();
                    spriteFrame.texture = texture;
                
                    // 赋值给 Sprite
                    this._sceneBannerFrame[scene.id] = spriteFrame;
                });
            })
            this.node.getComponentsInChildren(optionPrefab).forEach((option,index)=>{
                if(option._sceneId == 1){
                    option.onBtnClick();
                }
                else{
                    option.setUnChooseStatus();
                }
            })
            this.optionBtnLayout.setPosition(800,0,0);
            // tween(this.node).delay(0.1).call(()=>{
            //     this.node.getComponentInChildren(GuideMask).updateHoleByTarget(this.optionBtnLayout.children[0]);
            // }).start()
        }
    }

    onBtnTest1(){
        // let json = new network.GetAllNPCRequest();
        // json.command = 10112;
        // json.type = 1;
        // json["data"] = {};
        // json["data"]["roomId"] = 1;
        // json["data"]["page"] = 0;
        // json["data"]["size"] = 999;
        // socket.sendWebSocketBinary(json);


    }

    enterScene(data){
        // GlobalConfig.instance.nowSceneData = data.data.data;
        // console.log("scene data=========" + JSON.stringify(data.data));
    }

    onChooseClick(data){
        console.log("chooseClick" + data.data);
        this._chooseSceneId = data.data;
        this.node.getComponentsInChildren(sceneItem).forEach(itemScript=>{
            if(itemScript._sceneId == Number(data.data)){
                itemScript.node.active = true;
            }
            else{
                itemScript.node.active = false;
            }
        })
        this.twitterViewContent.children.forEach(node=>{
            if(node.getComponent(twitterModeType_1)){
                if(node.getComponent(twitterModeType_1)._roomId == Number(data.data)){
                    node.active = true;
                }
                else{
                    node.active = false;
                }
            }
            else if(node.getComponent(twitterModeType_2)){
                if(node.getComponent(twitterModeType_2)._roomId == Number(data.data)){
                    node.active = true;
                }
                else{
                    node.active = false;
                }
            }
        })

        let json3 = new network.GetAllNPCRequest();
        json3.command = 10119;
        json3.type = 1;
        json3["data"] = {};
        json3["data"]["roomId"] = this._chooseSceneId;
        socket.sendWebSocketBinary(json3);
    }

    showMaskNode(){
        this.maskNode.active = true;
        tween(this.maskNode).delay(15).call(()=>{
            this.maskNode.active = false;
        }).start();
    }

    initTwitterView(){
        if(this.twitterViewContent.children.length == 0){
            GlobalConfig.instance.twitterData.forEach(twitInfo=>{
                if(twitInfo.tweetType == 2){
                    let twitterNode = instantiate(this.twitterModeType_1);
                    this.twitterViewContent.addChild(twitterNode);
                    twitterNode.getComponent(twitterModeType_1).initData(twitInfo)
                }
                else{
                    let twitterNode = instantiate(this.twitterModeType_2);
                    this.twitterViewContent.addChild(twitterNode);
                    twitterNode.getComponent(twitterModeType_2).initData(twitInfo)
                }
            })

            this.twitterViewContent.children.forEach(node=>{
                if(node.getComponent(twitterModeType_1)){
                    if(node.getComponent(twitterModeType_1)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
                else if(node.getComponent(twitterModeType_2)){
                    if(node.getComponent(twitterModeType_2)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
            })
        }
    }

    onBtnChangeStatus(){
        if(this._pageStatus == 1){
            this._pageStatus = 2;
        }
        else{
            this._pageStatus = 1;
        }
        this.status_1.active = this._pageStatus == 1 ? true : false;
        this.status_2.active = this._pageStatus == 2 ? true : false;
        if(this._pageStatus == 2){
            this.sceneItemView.node.active = false;
            this.twitterView.node.active = true;
            this.twitterViewContent.children.forEach(node=>{
                if(node.getComponent(twitterModeType_1)){
                    if(node.getComponent(twitterModeType_1)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
                else if(node.getComponent(twitterModeType_2)){
                    if(node.getComponent(twitterModeType_2)._roomId == Number(this._chooseSceneId)){
                        node.active = true;
                    }
                    else{
                        node.active = false;
                    }
                }
            })
        }
        else{
            this.sceneItemView.node.active = true;
            this.twitterView.node.active = false;
        }
    }

    onBtnHome(){
        this._pageStatus = 1;
        this.sceneItemView.node.active = true;
        this.twitterView.node.active = false;
        this.bannerNode.active = true;
        this.btnPageNode.active = true;
        this.chatRecordView.active = false;
        this.userView.active = false;
    }

    onBtnTwitt(){
        this._pageStatus = 2
        this.sceneItemView.node.active = false;
        this.bannerNode.active = false;
        this.twitterView.node.active = true;
        this.btnPageNode.active = true;
        this.chatRecordView.active = false;
        this.userView.active = false;
        this.twitterViewContent.children.forEach(node=>{
            if(node.getComponent(twitterModeType_1)){
                if(node.getComponent(twitterModeType_1)._roomId == Number(this._chooseSceneId)){
                    node.active = true;
                }
                else{
                    node.active = false;
                }
            }
            else if(node.getComponent(twitterModeType_2)){
                if(node.getComponent(twitterModeType_2)._roomId == Number(this._chooseSceneId)){
                    node.active = true;
                }
                else{
                    node.active = false;
                }
            }
        })

    }

    onBtnChatRecord(){
        this._pageStatus = 3;
        this.sceneItemView.node.active = false;
        this.twitterView.node.active = false;
        this.bannerNode.active = false;
        this.btnPageNode.active = false;
        this.chatRecordView.active = true;
        this.userView.active = false;
    }

    onBtnBanner(){
        // if(this._bannerStatus == "show"){
        //     this._bannerStatus = "hide";
        // }
        // else{
        //     this._bannerStatus = "show";
            
        // }
        // if(this._bannerStatus == "show"){
        //     let viewSize = this.userView.getComponent(UITransform).contentSize;
        //     let posY =  this.userViewContent.position.y - 612;
        //     tween(this.bannerNode)
        //     .to(0.4, { position: v3(0, posY, 0) }, { easing: 'elasticOut' })
        //     .call(() => {

        //     })
        //     .start();
        //     this.btnBanner.setScale(1, 1);
        //     // this.bannerNode.setPosition(0,posY);
        //     // this.btnBanner.setScale(1,1);
        // }
        // else{
        //     let viewSize = this.sceneItemView.getComponent(UITransform).contentSize;
        //     let posY =  viewSize.height / 2  - 130;
        //     tween(this.bannerNode)
        //     .to(0.4, { position: v3(0, posY, 0) }, { easing: 'elasticOut' })
        //     .call(() => {

        //     })
        //     .start();
        //     this.btnBanner.setScale(1, -1);
        // }
    }

    onBtnBannerEnterScene(){
        let json = new network.GetAllNPCRequest();
        json.command = 10012;
        json.type = 1;
        json["data"] = {};
        json["data"]["roomId"] = this._chooseSceneId;
        GlobalConfig.instance.chooseNpc = null;
        GlobalConfig.instance.chooseScene = this._chooseSceneId;
        socket.sendWebSocketBinary(json);
        director.getScene().getComponentInChildren(lobbyScene).showMaskNode();
    }

    initChatRecord(data){
        console.log("chatRecordData======" + JSON.stringify(data.data));
        let allChatData = data.data.playerNpcChatDataMap;
        let chatRecords = [];  // 用于存储所有要显示的聊天记录

        for(let i in allChatData){
            if(allChatData[i] && allChatData[i].length > 0){
                let npcId = i;
                let initChatInfo = null; 
                allChatData[i] = allChatData[i].reverse()
                allChatData[i].forEach(chatInfo=>{
                    if(chatInfo.npcSend){
                        initChatInfo = chatInfo
                    }
                })
                if(initChatInfo){
                    chatRecords.push({
                        chatInfo: initChatInfo,
                        npcId: npcId
                    });
                }
            }
        }

        // 根据time进行排序，从小到大
        chatRecords.sort((a, b) => a.chatInfo.time - b.chatInfo.time);

        // 按排序后的顺序创建并添加节点
        chatRecords.forEach(record => {
            let recordItemNode = instantiate(this.lobbyChatRecord);
            recordItemNode.getComponent(lobbyChatRecord).initData(record.chatInfo, record.npcId);
            this.chatRecordViewContent.addChild(recordItemNode);
        });
    }

    updateItem(itemInfo){
        let itemArr = itemInfo.data.data;
        console.log("itemArr=====",itemArr)
        itemArr.forEach(item=>{
            if(item.goodsId == 10101000){
                GlobalConfig.instance.goldNum = item.count;
                this.lblGoldNum.string = GlobalConfig.instance.goldNum.toString();
            }
        })
    }

    onBtnUserInfo(){
       this._pageStatus = 4;
       this.sceneItemView.node.active = false;
       this.twitterView.node.active = false;
       this.bannerNode.active = false;
       this.chatRecordView.active = false;
       this.userView.active = true;
       
    }

    onBtnShowHideUserView(){
        if(!this._userViewContentPosY){
            this._userViewContentPosY = this.userViewContent.position.y;
        }
        if(this._bannerStatus == "show"){
            this._bannerStatus = "hide";
        }
        else{
            this._bannerStatus = "show";
            
        }
        if(this._bannerStatus == "show"){
            //let viewSize = this.sceneItemView.getComponent(UITransform).contentSize;
            let posY =  this._userViewContentPosY;
            tween(this.userViewContent)
            .to(0.4, { position: v3(0, posY, 0) }, { easing: 'elasticOut' })
            .call(() => {

            })
            .start();
            this.btnBanner.setScale(1, 1);
            // this.bannerNode.setPosition(0,posY);
            // this.btnBanner.setScale(1,1);
        }
        else{
            //let viewSize = this.sceneItemView.getComponent(UITransform).contentSize;
            let posY =  this.userViewContent.position.y - 617;
            tween(this.userViewContent)
            .to(0.4, { position: v3(0, posY, 0) }, { easing: 'elasticOut' })
            .call(() => {

            })
            .start();
            this.btnBanner.setScale(1, -1);
        }
    }

    getStoryInfo(data){
        console.log("storyInfoData======" + JSON.stringify(data.data));
        this.storyContent.destroyAllChildren();
        
        let storyData = data.data.voteHistoryInfoList;
        storyData.forEach((item,index)=>{
            let storyItemNode = instantiate(this.storyItemPrefab);
            this.storyContent.addChild(storyItemNode);
            storyItemNode.getComponent(storyItemPrefab).initData(this._chooseSceneId,item,index)
        })
    }


    

}


