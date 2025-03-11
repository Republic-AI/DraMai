import { _decorator, Component, instantiate, Node, Prefab, ScrollView, tween, UITransform, v3 } from 'cc';
import { network } from '../../model/RequestData';
import { modelMgr, observer, socket } from "../../game/App";
import { EventType } from '../../EventType';
import { chatRecordItem } from './chatRecordItem';
import { chatStartItem } from './chatStartItem';
const { ccclass, property } = _decorator;

@ccclass('chatRecordLayer')
export class chatRecordLayer extends Component {
    @property(Node)
    dialogContent:Node = null;

    @property(Node)
    freeContent:Node = null;

    @property(Prefab)
    chatRecordItem:Prefab = null;

    @property(Prefab)
    chatStartItem:Prefab = null;

    @property(Prefab)
    chatEndItem:Prefab = null;

    _dialogDataArr = [];
    _freeDataArr = [];
    _dialogInit = false;
    _freeInit = false;
    _isSendUpdate = false;
    protected onLoad(): void {
        observer.on(EventType.GETCHATRECORD, this.updateRecord, this);
        this.node.setScale(v3(0, 0, 1))
        tween(this.node).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).start()
    }
    start() {
        this.initData()
    }

    protected onDestroy(): void {
        observer.off(EventType.GETCHATRECORD, this.updateRecord, this);
    }

    update(deltaTime: number) {
        
    }

    onBtnClose(){
        tween(this.node).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.node.destroy()
        }).start()
    }

    initData(){
        let json = new network.GetAllNPCRequest();
        json.command = 10025;
        json.type = 1;
        json["data"] = {
            type:2,
            page:0,
            pageSize:5
        }
        socket.sendWebSocketBinary(json);
        this._dialogInit = true;
        this.dialogContent.parent.parent.active = true;
        this.freeContent.parent.parent.active = false;
    }

    onBtnFree(){
        this.freeContent.parent.parent.active = true;
        this.dialogContent.parent.parent.active = false;
        if(!this._freeInit){
            let json = new network.GetAllNPCRequest();
            json.command = 10025;
            json.type = 1;
            json["data"] = {
                type:1,
                page:0,
                pageSize:15
            }
            socket.sendWebSocketBinary(json);
            this._freeInit = true;
        }
    }

    onBtnDialog(){
        this.dialogContent.parent.parent.active = true;
        this.freeContent.parent.parent.active = false;
    }

    async updateRecord(record:any){
        console.log("recordData" ,record.data);
        if(record.data.code != 0){
            console.log("request error====" + record.data.code)
            return;
        }
        if(record.data.data.type == 1){
            console.log("free speak page======" +record.data.data.page);
        }
        else{
            console.log("dialog page======" +record.data.data.page);
        }
        if(record.data.data.speakDataList && record.data.data.speakDataList.length > 0){
                record.data.data.speakDataList.forEach(chatInfo=>{
                    this._freeDataArr.push(chatInfo);
                    let itemNode = instantiate(this.chatRecordItem);
                    itemNode.getComponent(chatRecordItem).initData(chatInfo,record.data.data.gameTime);
                    this.freeContent.addChild(itemNode);
                })
        }        
        else if(record.data.data.chatDataList && record.data.data.chatDataList.length > 0){
            record.data.data.chatDataList.forEach(chatDataArr=>{
                this._dialogDataArr.push(chatDataArr);
                let endNode = instantiate(this.chatEndItem);
                this.dialogContent.addChild(endNode);
                chatDataArr.speakDataList.reverse();
                chatDataArr.speakDataList.forEach(chatInfo=>{
                    let itemNode = instantiate(this.chatRecordItem);
                    chatInfo["gameTime"] = chatDataArr.gameTime;
                    itemNode.getComponent(chatRecordItem).initData(chatInfo,record.data.data.gameTime);
                    this.dialogContent.addChild(itemNode);
                })
                let startNode = instantiate(this.chatStartItem);
                this.dialogContent.addChild(startNode);
                startNode.getComponent(chatStartItem).initData(chatDataArr.speakDataList[0]);
            })
        }
    }

    onDialogScrolling () {
        if(this._isSendUpdate){
            return;
        }
        const scrollViewContent = this.dialogContent;
        const scrollViewHeight = 740;
        const contentHeight = scrollViewContent.getComponent(UITransform).height;

        const scrollPosition = this.dialogContent.parent.parent.getComponent(ScrollView).getScrollOffset().y;
        const isAtTop = scrollPosition <= 0;
        const isAtBottom = scrollPosition + scrollViewHeight >= contentHeight;

        if (isAtTop) {
            let json = new network.GetAllNPCRequest();
            json.command = 10025;
            json.type = 1;

            let pageIndex = 0;
            let num_1 = this._dialogDataArr.length/5;
            if(this._dialogDataArr.length > 50){
                console.log("over 50 count")
                return;
            }
            if(Math.floor(num_1) == num_1){
                pageIndex = num_1
                json["data"] = {
                    type:2,
                    page:pageIndex,
                    pageSize:5
                }
                socket.sendWebSocketBinary(json);
                this._isSendUpdate = true;
                tween(this.node).delay(3).call(()=>{
                    this._isSendUpdate = false;
                }).start()
            }
            else{
                console.log("max dialog count")
            }
            console.log('已到达顶部');
        }

    }

    onFreeScrolling () {
        if(this._isSendUpdate){
            return;
        }
        const scrollViewContent = this.freeContent;
        const scrollViewHeight = 740;
        const contentHeight = scrollViewContent.getComponent(UITransform).height;

        const scrollPosition = this.freeContent.parent.parent.getComponent(ScrollView).getScrollOffset().y;
        const isAtTop = scrollPosition <= 0;
        const isAtBottom = scrollPosition + scrollViewHeight >= contentHeight;

        if (isAtTop) {
            let json = new network.GetAllNPCRequest();
            json.command = 10025;
            json.type = 1;

            let pageIndex = 0;
            let num_1 = this._freeDataArr.length/15;
            if(this._freeDataArr.length > 75){
                console.log("over 75 count")
                return;
            }
            if(Math.floor(num_1) == num_1){
                pageIndex = num_1
                json["data"] = {
                    type:1,
                    page:pageIndex,
                    pageSize:15
                }
                socket.sendWebSocketBinary(json);
                this._isSendUpdate = true;
                tween(this.node).delay(3).call(()=>{
                    this._isSendUpdate = false;
                }).start()
            }
            else{
                console.log("max free count")
            }
            console.log('已到达顶部');
        }

        if (isAtBottom) {
            console.log('已到达底部');
        }
    }
}


