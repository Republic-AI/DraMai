import { _decorator, Asset, assetManager, Component, director, Node, SceneAsset } from 'cc';
import { observer } from '../game/App';
import { EventType } from '../EventType';
import { network } from '../model/RequestData';
import WebUtils from '../utils/WebUtils';
import { GlobalConfig } from '../game/config/GlobalConfig';
const { ccclass, property } = _decorator;

@ccclass('SocketDataManager')
export class SocketDataManager extends Component {

    _itemData = {};
    protected onLoad(): void {
        observer.on(EventType.SOCKET_ONMESSAGE, this.onMessage,this);
    }

    protected onDestroy(): void {
        observer.off(EventType.SOCKET_ONMESSAGE, this.onMessage,this);
    }
    start() {

    }

    update(deltaTime: number) {
        
    }
    _chatData = {};
    _actionData = [];
    public onMessage(da: any) {
        let repData :network.InetwarkResponseData  =  da.data;
        //拉取聊天记录全量回执
        //console.log("socketData=====" + JSON.stringify(repData));
        if(repData.code){
            console.log("socket Data error code=====" + repData.code);
            console.log("socket Data error msg=====" + repData.message);
            return;
        }
        if(repData.command == 10007){
            console.log("1111111111test");
            observer.post(EventType.SOCKET_NPC_ACTION, repData);
        }
        if (repData.command == 10017) {
            //{"requestId":1730951022109,"type":1,"command":10017,"code":0,"data":{"chats":[{"msgId":6,"sender":5221343466314801153,"type":0,"content":"写一段七言绝句诗，题目是：火锅！","time":1730899225961,"barrage":0}]}}
            observer.post(EventType.GETCHAT_BYID,repData);
        }
        if (repData.command == 10016) {
            //{"requestId":6025933171,"playerId":5212530193423409000,"type":2,"command":10016,"code":0,"data":{"barrage":0,"type":0,"content":"assasasasasas","voice":"","npcId":10006,"sender":"24201W10EG21WM"}}
            observer.post(EventType.UPDATE_CHAT,repData);
        }
        if (repData.command == 100081) {
            //{"requestId":6025933171,"playerId":5212530193423409000,"type":2,"command":10016,"code":0,"data":{"barrage":0,"type":0,"content":"assasasasasas","voice":"","npcId":10006,"sender":"24201W10EG21WM"}}
            observer.post(EventType.UPDATE_ITEM,repData);
        }
        if(repData.command == 10105){
            observer.post(EventType.SCENE_ACTION,repData);
        }
        if(repData.command == 10000){
            observer.post(EventType.RELOGIN,repData)
        }
        if(repData.command == 10025){
            observer.post(EventType.GETCHATRECORD,repData)
        }
        if(repData.command == 10004){
            observer.post(EventType.CREATEPLAYER,repData)
        }
        if(repData.command == 10005){
            observer.post(EventType.DESTROYPLAYER,repData)
         }

        if(repData.command == 10001){
        }
        if(repData.command == 10024){

        }
        if(repData.command == 10028 ){
            observer.post(EventType.GETALLNFTSTATUS,repData)
        }
        if(repData.command == 10027){
            observer.post(EventType.CHANGENFTSTATUS,repData)
        }
        if(repData.command == 10108){
            observer.post(EventType.CHANGESKIN,repData)
        }
        if(repData.command == 10109){
            observer.post(EventType.GETLOBBYINFO,repData)
        }
        if(repData.command == 10012){
            let sceneName = director.getScene().name
            GlobalConfig.instance.nowSceneData = repData["data"];
            this._actionData = []
            if(sceneName == "lobbyScene"){
                let bundle = assetManager.getBundle("game_map"+ GlobalConfig.instance.chooseScene);
                if(bundle){
                    bundle.loadScene("game_map" + GlobalConfig.instance.chooseScene,(err,scene:SceneAsset)=>{
                        if(err){
                            console.log("loadScene error" + err)
                            return;
                        }
                        else{
                            director.runScene(scene);
                        }
                    })
                }
            }
            else{
                observer.post(EventType.RECONECTSCENE);
            }
        }
        if(repData.command == 10110){
            let lobbyBundle = assetManager.getBundle("lobby")
            lobbyBundle.loadScene("lobbyScene",(err,scene:SceneAsset)=>{
              if(err){
                console.log("loadScene error" + err)
                return;
              }
              else{
                director.runScene(scene);
              }
      
            });
        }
        if(repData.command == 10111){
            observer.post(EventType.CHANGESCENEITEM,repData)
        }
        if(repData.command == 10112){
            console.log("twitterData====" + JSON.stringify(repData["data"]));
            GlobalConfig.instance.twitterData = repData["data"].tweetVoList;
            observer.post(EventType.INITTWITTERVIEW)
        }
        if(repData.command == 10113){
            console.log("twitterData====" + JSON.stringify(repData["data"]));
            GlobalConfig.instance.twitterData.forEach(twitterInfo=>{
                if(twitterInfo.id == repData["data"].tweetId){
                    if(repData["data"].type == 2){
                        if(!twitterInfo.tweetCommentVoList){
                            twitterInfo.tweetCommentVoList = [];
                        }
                        if(!repData["data"].replyId){
                            twitterInfo.tweetCommentVoList.push(repData["data"])
                        }
                        else{
                            
                        }
                    }
                }
            })
            observer.post(EventType.UPDATETWITTER,repData["data"]);
        }
    }
}

