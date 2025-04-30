import { _decorator, assetManager, Component, director, EditBox, instantiate, JsonAsset, Label, Node, Prefab, resources, ScrollView, Sprite, SpriteFrame, tween, UITransform, v3, Overflow, Slider, HorizontalTextAlignment } from 'cc';
import { NpcManager } from '../../NPC/NpcManager';
import { npcDes, NpcName, NpcRoomIndex, npcSkinCfg, sceneItemAllCfg } from '../../StaticUtils/NPCConfig';
import { npcItemPrefab } from './npcItemPrefab';
import { network } from '../../model/RequestData';
import { GlobalConfig } from '../config/GlobalConfig';
import { socket } from '../App';
import { chatPrefab } from './chatPrefab';
import { GameScene } from '../scene/GameScene';
import { changeSkinItem } from './changeSkinItem';
import { changeSceneItem } from './changeSceneItem';
import { ItemConfigMap } from '../../StaticUtils/ItemConfig';
import { NPCSkillMap } from '../../StaticUtils/NPCSkillConfig';
import { itemPrefab } from './itemPrefab';
const { ccclass, property } = _decorator;
@ccclass('changeSkinPrefab')
export class changeSkinPrefab extends Component {
    @property(ScrollView)
    skinView:ScrollView = null;

    @property(Label)
    lblNpcDes:Label = null;

    @property(Label)
    lblGoldNum:Label = null;

    @property(EditBox)
    chatEditBox:EditBox = null;

    @property(Node)
    chatViewContent:Node = null;

    @property(Prefab)
    chatPrefab:Prefab = null;

    @property(Node)
    skinViewContent:Node = null;

    @property(Node)
    sceneViewContent:Node = null;

    @property(Sprite)
    imgNpcFrame:Sprite = null;

    @property(Prefab)
    changeSkinItem:Prefab = null;

    
    @property(Prefab)
    changeSkinItem_lock:Prefab = null;

    @property(Prefab)
    changeSceneItem:Prefab = null;

    @property(Prefab)
    changeSceneItem_lock:Prefab = null;

    @property(Node)
    chatNode:Node = null;

    @property(Node)
    changeNode:Node = null;

    @property(Label)
    lblTwitterContent:Label = null;

    @property(Label)
    lblTwitterName:Label = null;

    @property(Label)
    lblTwitterTime:Label = null;

    @property(Sprite)
    imgBigFrame:Sprite = null;

    @property(Node)
    helpNode:Node = null;

    @property(Prefab)
    itemPrefab:Prefab = null;

    @property(Prefab)
    skillTagNode:Prefab = null;

    @property(Node)
    skillContent:Node = null;

    @property(Node)
    itemContent:Node = null;

    @property(Node)
    emoLayout:Node = null;

    _goldNum = 0;
    _itemCfg = null;
    _ownItem = {}
    _npcId = null;
    _isEditing = false;

    _nowSkinData = null;
    _skinIndex = 0;
    _skinFrameArr = null;
    _isInit = false;

    private _isValid: boolean = true;

    protected onLoad(): void {
        // resources.load("json/itemCfg",(error,itemCfg:JsonAsset)=>{
        //     if(error){
        //         console.log("load item Cfg error:" + error);
        //         return
        //     }
        //     this._itemCfg = itemCfg.json;
        // })
        this.changeNode.active = false;
        this.chatNode.active = true;
    }
    start() {

    }

    update(deltaTime: number) {
        if(!this.chatEditBox.string && !this._isEditing){
            this.chatEditBox.string = " ";
        }
    }

    initData(npcId){
        this.lblTwitterTime.string = "";
        this.chatViewContent.getComponentsInChildren(chatPrefab).forEach(prefab=>{
            if(prefab._npcId == npcId || prefab.toNpcId == npcId){
                prefab.node.active = true;
            }
            else{
                prefab.node.active = false;
            }
        })
        this._npcId = npcId
        director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
            if(npcScript.NpcID == this._npcId){
                this._skinFrameArr = npcScript.getComponent(NpcManager).skinFrameArr;
                this._nowSkinData = npcScript._skinId;
                if(npcScript.npcBigFrame){
                    this.imgBigFrame.spriteFrame = npcScript.npcBigFrame;
                }
                npcSkinCfg[this._npcId].forEach((skinId,index)=>{
                    if(skinId == this._nowSkinData){
                        this._skinIndex = index;
                        this.setSkinStatus();
                    }
                })
            }
        })
        this._skinIndex = 0;
        this.setSkinStatus();
        // this.skinViewContent.destroyAllChildren();
        // npcSkinCfg[this._npcId].forEach((skinId,index)=>{
        //     if(skinId){
        //         let skinItemNode = instantiate(this.changeSkinItem);
        //         this.skinViewContent.addChild(skinItemNode);
        //         skinItemNode.getComponent(changeSkinItem).initData(this._npcId,skinId,this._skinFrameArr[index]);
        //     }
        // })

        // for(let i = 0 ;i<6;i++){
        //     let skinLockNode = instantiate(this.changeSkinItem_lock);
        //     this.skinViewContent.addChild(skinLockNode);
        // }
        // tween(this.node).delay(0.1).call(()=>{
        //     this.skinViewContent.setPosition(850,0,0);
        // }).start()

        // this.sceneViewContent.destroyAllChildren();
        // sceneItemAllCfg[this._npcId].forEach(sceneItemInfo=>{
        //     let sceneItemNode = instantiate(this.changeSceneItem);
        //     this.sceneViewContent.addChild(sceneItemNode);
        //     sceneItemNode.getComponent(changeSceneItem).initData(sceneItemInfo)

        // })

        // for(let i = 0 ;i<6;i++){
        //     let sceneItem_lock = instantiate(this.changeSceneItem_lock);
        //     this.sceneViewContent.addChild(sceneItem_lock);
        // }
        // tween(this.node).delay(0.1).call(()=>{
        //     this.sceneViewContent.setPosition(920,0,0);
        // }).start()
        //this.skinViewContent.setPosition(0,0,0);
        // this.skinViewContent.children.forEach((skinNode,index)=>{
        //     if(index < this._skinFrameArr.length){
        //         console.log("frameArr=====" + JSON.stringify(this._skinFrameArr[index].name))
        //         skinNode.getComponentInChildren(Sprite).spriteFrame = this._skinFrameArr[index];
        //     }
        // })
        // let npcBundle =  assetManager.getBundle("npcAnimation");
        // npcBundle.load("npc_" + npcId+ "/npcPrefab_" + npcId,(error,npcPrefab:Prefab)=>{
        //     if(error){
        //         console.log("load npc prefab error====" + error);
        //         return;
        //     }
        //     else{
        //         let node = instantiate(npcPrefab);
        //         this._skinFrameArr = node.getComponent(NpcManager).skinFrameArr;
        //     }
        // })
        if(npcDes[npcId]){
            this.lblNpcDes.string = npcDes[npcId];
            // tween(this.lblNpcDes.node).delay(0.1).call(()=>{
            //     let targetY = this.lblNpcDes.node.getComponent(UITransform).contentSize.height
            //     this.lblNpcDes.node.setPosition(0,-targetY/4 - 20);
            // }).start();
        }
        this.updateTwitterContent(npcId);
        resources.load("json/itemCfg",(error,itemCfg:JsonAsset)=>{
            if (!this._isValid) return;
            if(error){
                console.log("load item Cfg error:" + error);
                return
            }
            this._itemCfg = itemCfg.json;
            let ownItem = {}
            this._itemCfg.forEach(itemInfo=>{
                if(Number(itemInfo.npcId) == Number(npcId)){
                    ownItem[itemInfo.id] = 1;
                }
            })
            console.log("ownItem=====" + JSON.stringify(ownItem));
            let npcItems = [];
            director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
                if(npcScript.NpcID == npcId){
                    npcItems = npcScript._initData.items
                }
            })
            let myItemInfo = [];
            let otherItemInfo = [];
            let coinNum = 0;
            npcItems.forEach(itemInfo=>{
                if(itemInfo.itemId == 10101000){
                    coinNum = itemInfo.count;
                    this.setGoldNum(coinNum);
                }
                else if(ownItem[itemInfo.itemId]){
                    myItemInfo.push(itemInfo)
                }
                else{
                    otherItemInfo.push(itemInfo);
                }
            })
            console.log("myItemInfo======" + JSON.stringify(myItemInfo));
            console.log("otherItemInfo======" + JSON.stringify(otherItemInfo));
            const mergedArray = myItemInfo.concat(otherItemInfo);
            this.node.getComponentsInChildren(npcItemPrefab).forEach((itemPrefab,index)=>{
                if(mergedArray[index]){
                    itemPrefab.initData(mergedArray[index])
                }
                else{
                    itemPrefab.imgItem.spriteFrame = null;
                    itemPrefab.lblItem.string = "";
                }
            })
            
        })
        tween(this.node).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 900){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()
        if(!this._isInit){
            let chatAllRecord = GlobalConfig.instance.chatRecord;
            for(let i in chatAllRecord){
                if(NpcRoomIndex[i] == GlobalConfig.instance.chooseScene){
                    let chatRecordData = chatAllRecord[i]
                    let chatNpcId = i;
                    if(chatRecordData && chatRecordData.length > 0){
                        chatRecordData.forEach(chatInfo=>{
                            let chatPrefabNode = instantiate(this.chatPrefab);
                            if(chatInfo.npcSend){
                                chatPrefabNode.getComponent(chatPrefab).initData(chatNpcId,chatInfo.content)
                            }
                            else{
                                chatPrefabNode.getComponent(chatPrefab).initData(0,chatInfo.content)
                                chatPrefabNode.getComponent(chatPrefab).setToNpcId(chatNpcId);
                            }
                            this.chatViewContent.addChild(chatPrefabNode);
                            if(chatNpcId!=this._npcId){
                                tween(this.node).delay(0.02).call(()=>{
                                    chatPrefabNode.active = false;
                                }).start()
                            }
                        })
                    }
                }
            }
        }
        this._isInit = true;
        // tween(this.node).delay(0.1).call(()=>{
        //     if(this.chatViewContent.getComponent(UITransform).contentSize.height > 700){
        //         this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
        //     }
        // }).start()
        if(NPCSkillMap[this._npcId]){
            this.skillContent.destroyAllChildren();
            NPCSkillMap[this._npcId].skills.forEach(skillInfo=>{
                let skillTagNode = instantiate(this.skillTagNode);
                skillTagNode.getComponentInChildren(Label).string = skillInfo;
                this.skillContent.addChild(skillTagNode);
            })

            NPCSkillMap[this._npcId].MBTI.forEach((mbtiInfo,index)=>{
                this.emoLayout.children[index].getComponent(Slider).enabled = true;
                this.emoLayout.children[index].getComponent(Slider).progress = mbtiInfo;
                this.emoLayout.children[index].getComponent(Slider).enabled = false;
            })
            this.itemContent.destroyAllChildren();
            NPCSkillMap[this._npcId].itemIds.forEach(itemId=>{
                let itemPrefabNode = instantiate(this.itemPrefab);
                itemPrefabNode.getComponent(itemPrefab).initData(itemId);
                this.itemContent.addChild(itemPrefabNode);
            })
        }

        this.hideHelpNode();

        this.lblTwitterName.string = NpcName[this._npcId];
    }

    setGoldNum(coinNum){
        this._goldNum = coinNum;
        this.lblGoldNum.string = this._goldNum.toString();
        if(this._goldNum > 999999){
            tween(this.node).delay(0.1).call(()=>{
                let targetX = this.lblGoldNum.node.getComponent(UITransform).contentSize.width / 2  + this.lblGoldNum.node.parent.getComponent(UITransform).contentSize.width/2;
                this.lblGoldNum.node.setPosition(v3(targetX,-2,0))
                tween(this.lblGoldNum.node).repeatForever(
                    tween(this.lblGoldNum.node).
                    to(3,{position:v3(-targetX,-2,0)}).
                    call(()=>{
                        this.lblGoldNum.node.setPosition(v3(targetX,-2,0))
                    }).start())
                    .start()
            }).start()
        }
        else{
            this.lblGoldNum.node.setPosition(v3(0,-2,0))
        }
    }
    onBtnSend(){
        if(this.chatEditBox.node.active && this.chatEditBox.string.length < 1){
            return;
        }
        let isOnlySpace = true;
        for (let i = 0 ; i < this.chatEditBox.string.length -1; i++) {
            if (this.chatEditBox.string[i] != ' ') {
                isOnlySpace = false;
                break;
            }
        
        }
        if(isOnlySpace){
            return;
        } 
        let json = new network.RequestSendChatData();
        json.command = 10106;
        json.data.npcId = this._npcId;
        json.data.sender =  GlobalConfig.instance.LoginData.data.player.playerId;
        json.data.context = this.chatEditBox.string;
        json.data.privateMsg = true;
        socket.sendWebSocketBinary(json);
        this.chatEditBox.string = "";
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
        let chatPrefabNode = instantiate(this.chatPrefab);
        chatPrefabNode.getComponent(chatPrefab).initData(0,json.data.context)
        chatPrefabNode.getComponent(chatPrefab).setToNpcId(this._npcId);
        this.chatViewContent.addChild(chatPrefabNode)
        tween(this.node).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 900){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()

        this.chatViewContent.getComponentsInChildren(chatPrefab).forEach(prefab=>{
            if(prefab._npcId == this._npcId && prefab.imgNpcTypeNode.active){
                prefab.node.destroy();
            }
        })

        tween(this.node).delay(1.2).call(()=>{
            let chatTypeNode = instantiate(this.chatPrefab);
            chatTypeNode.getComponent(chatPrefab).initData(this._npcId,null)
            this.chatViewContent.addChild(chatTypeNode)
        }).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 900){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()

        


    }    
    showNpcReplyPlayer(npcId,content){
        let chatPrefabNode = instantiate(this.chatPrefab);
        chatPrefabNode.getComponent(chatPrefab).initData(npcId,content)
        this.chatViewContent.addChild(chatPrefabNode)
        if(npcId != this._npcId){
            chatPrefabNode.active = false;
        }
        this.chatViewContent.getComponentsInChildren(chatPrefab).forEach(prefab=>{
            if(prefab._npcId == this._npcId && prefab.imgNpcTypeNode.active){
                prefab.node.destroy();
            }
        })
        tween(this.node).delay(0.1).call(()=>{
            if(this.chatViewContent.getComponent(UITransform).contentSize.height > 900){
                this.chatViewContent.parent.parent.getComponent(ScrollView).scrollToBottom();
            }
        }).start()
    }

    onEditBegin(){
        this._isEditing = true;
        if(this.chatEditBox.string == " "){
            this.chatEditBox.string = "";
        }
    }

    onEditTextEnd(){
        this._isEditing = false;
    }

    onEditTextReturn(){ 
        this._isEditing = false;
    }

    onBtnSkinClick(target,CustomData){
        if([Number(CustomData)] < this._skinFrameArr.length){
            this._nowSkinData = npcSkinCfg[this._npcId][Number(CustomData)];
            this._skinIndex = Number(CustomData);
            //this.setSkinStatus();
        }
        else{
            console.log("no skin id" )
        }
        
    }

    setSkinStatus(){
        this.imgNpcFrame.spriteFrame = this._skinFrameArr[this._skinIndex];
    }

    onBtnConfirm(){
        let json = new network.GetAllNPCRequest();
        json.command = 10108;
        json.type = 1;
        json["data"] = {
            npcId:this._npcId,
            dressId:this._nowSkinData,
        }
        socket.sendWebSocketBinary(json);
        director.getScene().getComponentInChildren(GameScene).cancelFollowNpc();
    }

    onBtnClose(){
        // console.log("pos====" + JSON.stringify(this.skinViewContent.position))
        // return;
        director.getScene().getComponentInChildren(GameScene).cancelFollowNpc();
    }

    onBtnSceneItem(){

    }

    onBtnDir(){
        this.changeNode.active = !this.changeNode.active;
        this.chatNode.active = !this.chatNode.active;
    }

    private truncateTextToLines(text: string, maxLines: number = 3): string {
        if (!text) return '';
        
        const label = this.lblTwitterContent;
        if (!label) return text;

        // 保存原始设置
        const originalString = label.string;
        
        // 计算每行大约能显示多少字符
        const lineWidth = label.node.getComponent(UITransform).width;
        const avgCharWidth = label.fontSize * 0.75; // 估算每个字符的平均宽度
        const charsPerLine = Math.floor(lineWidth / avgCharWidth);
        
        // 估算总行数
        const totalLines = Math.ceil(text.length / charsPerLine);
        
        // 如果估算的行数小于等于最大行数，直接返回原文
        if (totalLines <= maxLines) {
            return text;
        }
        
        // 计算大约需要的字符数
        const maxChars = charsPerLine * maxLines - 3; // 减去3个字符留给"..."
        
        // 截断文本
        return text.substring(0, maxChars) + '...';
    }

    private updateTwitterContent(npcId: number) {
        this.lblTwitterContent.string = null;
        // 设置自动换行
        this.lblTwitterContent.overflow = Overflow.RESIZE_HEIGHT;
        this.lblTwitterContent.enableWrapText = true;
        const uiTrans = this.lblTwitterContent.getComponent(UITransform);
        uiTrans.width = 450;
        // 设置水平对齐方式
        this.lblTwitterContent.horizontalAlign = HorizontalTextAlignment.LEFT;
        // 设置行高，避免字符被截断
        this.lblTwitterContent.lineHeight = 40;

        GlobalConfig.instance.twitterData.forEach(twitInfo => {
            if (twitInfo.npcId == npcId && !this.lblTwitterContent.string) {
                const maxLength = 87;
                // 替换连续的空格为单个空格，避免不必要的换行
                const content = twitInfo.content.length > maxLength 
                    ? twitInfo.content.replace(/\s+/g, ' ').substring(0, maxLength) + '...' 
                    : twitInfo.content.replace(/\s+/g, ' ');
                
                this.lblTwitterContent.string = content;
                let durTime = Math.floor(twitInfo.createTime/1000);

                if(durTime >= 86400){
                    let str = "d";
                    let time = Math.floor(durTime / 86400);
                    this.lblTwitterTime.string = time + str;
                }
                else if(durTime > 3600){
                    let str = "h";
                    let time = Math.floor(durTime / 3600);
                    this.lblTwitterTime.string = time + str;
                }
                else if(durTime > 60){
                    let str = "m";
                    let time = Math.floor(durTime / 60);
                    this.lblTwitterTime.string = time + str;
                }
                else{
                    let str = "s";
                    let time = durTime;
                    this.lblTwitterTime.string = time + str;
                }

                this.lblTwitterName.string = NpcName[twitInfo.npcId];
            }
        });
    }

    showHelpNode(itemId){
        let iteminfo = ItemConfigMap[itemId];
        this.helpNode.active = true;
        if(iteminfo){                
            this.helpNode.getComponentInChildren(Label).string = iteminfo.des;
        }
        else{
            this.helpNode.getComponentInChildren(Label).string = "No description";
        }
    }

    hideHelpNode(){
        this.helpNode.active = false;
    }

    protected onDestroy(): void {
        this._isValid = false;
    }
}


