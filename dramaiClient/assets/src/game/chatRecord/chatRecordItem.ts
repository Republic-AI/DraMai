import { _decorator, assetManager, Component, Label, Node, Sprite, SpriteFrame, UITransform } from 'cc';
import WebUtils from '../../utils/WebUtils';
import { NpcName } from '../../StaticUtils/NPCConfig';
const { ccclass, property } = _decorator;

@ccclass('chatRecordItem')
export class chatRecordItem extends Component {
    @property(Label)
    lblChatInfo:Label = null;

    @property(Label)
    lblContent:Label = null;

    @property(Sprite)
    imgHead:Sprite = null;
    start() {

    }

    update(deltaTime: number) {
        let contentHeight = this.lblContent.getComponent(UITransform).contentSize.height;
        this.node.getComponent(UITransform).setContentSize(400,contentHeight + 25);
    }

    initData(data,gameNowTime){
        // console.log("data=======" + JSON.stringify(data));
        // console.log("serverTime====="  + gameNowTime);
        let chatRecordBundle = assetManager.getBundle("chatRecord");
        chatRecordBundle.load("image/head_" + data.sender +"/spriteFrame",SpriteFrame,(err,spr:SpriteFrame)=>{
            this.imgHead.spriteFrame = spr;
        })

        let nowGameDate = new Date(gameNowTime).getDate();
        let nowGameMonth = new Date(gameNowTime).getMonth();

        const _gameTime = new Date(data.gameTime);
        let _mGameDate = _gameTime.getDate();
        let _mGameMonth = _gameTime.getMonth();
        let hourStr = _gameTime.getHours();
        let minute = _gameTime.getMinutes();

        // console.log("_mGameMonth=======" + _mGameMonth);
        // console.log("_mGameDate=======" + _mGameDate);

        // console.log("serverGameMonth====" + nowGameMonth)
        // console.log("serverGameDate====" + nowGameDate)
        if(_mGameDate == nowGameDate && _mGameMonth == nowGameMonth){
            this.lblChatInfo.string = WebUtils.prefixInt(hourStr,2) + ":" +WebUtils.prefixInt(minute,2) + "<" + NpcName[data.sender] + ">" + ":";
        }
        else if ( _mGameMonth == nowGameMonth){
            let offsetDay = Math.abs( _mGameDate - nowGameDate);
            if(offsetDay == 1){
                this.lblChatInfo.string = WebUtils.prefixInt(hourStr,2) + ":"  +WebUtils.prefixInt(minute,2) +"+1day"+ "<" + NpcName[data.sender] + ">" + ":";
            }
            else{
                this.lblChatInfo.string = WebUtils.prefixInt(hourStr,2) + ":"  +WebUtils.prefixInt(minute,2) +  "+"+ offsetDay +"days" + "<" + NpcName[data.sender] + ">" + ":";
            }
        }
        else{
            let offsetDay = nowGameDate +  (Math.abs(_mGameMonth - nowGameMonth) -1 ) * 30 + Math.ceil(Math.abs((gameNowTime - data.gameTime))/(1000 *3600*24));
            this.lblChatInfo.string = WebUtils.prefixInt(hourStr,2) + ":"  +WebUtils.prefixInt(minute,2) + "+"+ offsetDay +"days"+ "<" + NpcName[data.sender] + ">" + ":";
        }
        this.lblContent.string =  data.content

    }
}


