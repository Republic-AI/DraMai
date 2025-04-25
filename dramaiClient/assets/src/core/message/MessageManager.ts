import { director, instantiate, isValid, Label, Node, Prefab, resources, Script } from "cc";
import { uiMgr } from "../../game/App";
import { ShowMsg2Component } from "../../game/components/ShowMsg2Component";
import { ShowMsgPanelComponent } from "../../game/components/ShowMsgPanelComponent";
import SingleMessage from "./SingleMessage";

var _showMsg: MessageManager;
/**
 * 累加消息样式
 * @param msg 
 */
export function showMsg(msg: string, _par: Node): void {
    if (!_showMsg) _showMsg = new MessageManager();

    _showMsg.showMassage(msg, "add", _par);
}
/**
 * 覆盖提示消息样式
 * @param msg 
 */
export function showMsg2(msg: string, _par: Node = null): void {
    if (!_showMsg) _showMsg = new MessageManager();
    _showMsg.showMassage(msg, "recover", _par);
}
export function delMsg(msg: any): void {
    if (!_showMsg) _showMsg = new MessageManager();
    // _showMsg.deletemsg(msg);
}

export function showMsgPanel(msg: string, _par: Node = null) {
    if (!_showMsg) _showMsg = new MessageManager();
    _showMsg.showMsgPanel(msg, _par);
}
/**
 * MessageManager 
 * @usage 通用提示框
 */
export default class MessageManager {
    private showGroup: Node;
    private static _instance: MessageManager;
    private _isValid: boolean = true;

    constructor() {
        let self = this;
    }

    private _recovertest: string = "";
    /**显示提示文本*/
    public showMassage(str: string, type: string = "add", _par: Node): void {
        let s = this;
        s._recovertest = str;
        s.showGroup = _par; 
        switch (type) {
            case "add":
                break;
            case "recover":
                if (s.recovMessage && isValid(s.recovMessage)) {
                    s.recovMessage.removeFromParent();
                }
                s.loadtoast();
                break;
        }

    }

    private _msgpanel: Node = null;
    /**
     * 
     * @param str 
     */
    public showMsgPanel(str: string, _par: Node) {
        let s = this;
        ShowMsgPanelComponent.CONTENT = str;
        s.showGroup = director.getScene().getChildByName('Canvas') as unknown as Node;
        uiMgr.popToParent("prefabs/rewards/pre_showMsg", s.showGroup);

    }


    private recovMessage: any;
    private loadtoast() {
        // director.loadScene("sign");
        let s = this;
        if (!s.recovMessage) {
            resources.load("prefabs/pre_toast", Prefab, s.loadcom.bind(s));
        } else {
            s.addmmessage();
        }
    }
    loadcom(err: Error, data: Prefab) {
        if (!this._isValid) return;
        if (err) {
            console.log(err);
            return;
        }
        let s = this;
        s.recovMessage = instantiate(data);
        s.addmmessage();
    }

    private addmmessage() {
        let s = this;
        try {
            let lab: Label = s.recovMessage.getChildByName("spr_bg").getChildByName("label_desc");
            lab.getComponent(Label).string = s._recovertest;
            if (!s.showGroup || !s.showGroup.isValid) {
                s.showGroup = director.getScene().getChildByName('Canvas') as unknown as Node;
                s.showGroup.addChild(s.recovMessage);
            } else {
                s.showGroup.addChild(s.recovMessage);
            }
        } catch (err) {
            console.log(err); 
        }

    }

    // private addMessage2(str: string): void {
    //     let s = this;
    //     if(s.recovMessage){
    //         TweenMax.killTweensOf(s.recovMessage);
    //     }else{
    //         s.recovMessage = new SingleMessage2(s.showGroup, s.wordGroup);
    //     }

    //     s.recovMessage.messageStr = str;
    //     s.recovMessage.x = (Laya.stage.width - s.recovMessage.img_bg.width) / 2;
    //     s.recovMessage.y = Laya.stage.height / 2 - s.recovMessage.img_bg.height ;
    //     TweenMax.to(s.recovMessage,0.1,{delay:1.4,onComplete:()=>{
    //         TweenMax.killTweensOf(s.recovMessage);
    //         s.recovMessage.remove();
    //         s.recovMessage = null;

    //     }})
    // }
    // private _timer: Laya.Timer;
    // /**添加提示信息 */
    // private addMessage(str: string): void {
    //     let self = this;
    //     let len = self._msgs.length + self._msgArr.length;
    //     if (len >= self.max_num) {
    //         self._msgs.shift();
    //         self._msgs.push(str);
    //     } else {
    //         self._msgs.push(str);
    //     }
    //     loopMgr.addToFrame(self.doNextFrame, self);
    // }

    // private doNextFrame() {
    //     let self = this;
    //     if (self._msgs.length == 0) {
    //         loopMgr.removeFromFrame(self.doNextFrame, self);
    //         return;
    //     }
    //     if (self._msgArr.length > self.max_num) {
    //         self._msgs.shift();
    //         return;
    //     }
    //     let str = self._msgs.shift();
    //     let message = self.getSingleMessage();
    //     self._msgArr.push(message);
    //     message.messageStr = str;
    //     message.x = (Laya.stage.width - message.img_bg.width) / 2;
    //     message.y = (Laya.stage.height) / 2;
    //     message.startTweenAlpha();
    //     self.refreshShow();
    // }

    // private refreshShow(): void {
    //     let self = this;
    //     let len = self._msgArr.length;
    //     for (let i: number = 0; i < len; i++) {
    //         let per = self._msgArr[i];
    //         let moveY = Laya.stage.height / 2 - per.img_bg.height * (len - i);
    //        TweenMax.killTweensOf(per);
    //         if (per.y > moveY) {
    //             TweenMax.to(per,0.4,{ y: moveY });
    //         }
    //     }
    // }

    // public deletemsg(msg: SingleMessage): void {
    //     let self = this;
    //     let dex = self._msgArr.indexOf(msg);
    //     TweenMax.killTweensOf(msg);
    //     if (dex != -1) {
    //         self._msgArr.splice(dex, 1);
    //     }
    //     self._pools.push(msg);
    // }

    // private _msgs: string[] = [];
    // private _msgArr: SingleMessage[] = [];
    // private _pools: SingleMessage[] = [];
    // private max_num: number = 5;
    // /**获取单个消息显示对象 */
    // private getSingleMessage(): SingleMessage {
    //     let self = this;
    //     let message: SingleMessage;
    //     if (self._pools.length > 0) {
    //         let replace = self._pools.shift();
    //         message = replace;
    //     }
    //     else {
    //         message = new SingleMessage(self.showGroup, self.wordGroup);
    //     }
    //     return message;
    // }

    protected onDestroy(): void {
        this._isValid = false;
    }
}