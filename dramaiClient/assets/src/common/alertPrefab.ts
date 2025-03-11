import { _decorator, Component, Label, Node, tween, v3 } from 'cc';
const { ccclass, property } = _decorator;
export enum alert_cb_status {
    none = 0x00,
    ok = 0x01,
    cancel = 0x02,
    // close = 0x04,
    both = 0x03
}
@ccclass('alertPrefab')
export class alertPrefab extends Component {
    @property(Label)
    lblShowContent:Label = null;

    @property(Node)
    btnConfirm:Node = null;

    @property(Node)
    btnCancel:Node = null;

    @property(Node)
    contentNode:Node = null;

    _confirmCallBack = null;
    _cancelCallBack = null;
    protected onLoad(): void {
        this.contentNode.setScale(v3(0, 0, 1))
        tween(this.contentNode).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).start()
    }

    start() {

    }

    update(deltaTime: number) {
        
    }

    onBtnMask(){
        
    }

    initData(content:string,status:alert_cb_status,confirmcallback: () => void = null,cancelCallBack: () => void = null){
        this.lblShowContent.string = content;
        this.btnConfirm.active = (status & alert_cb_status.ok) == alert_cb_status.ok
        this.btnCancel.active = (status & alert_cb_status.cancel) == alert_cb_status.cancel
        this._confirmCallBack = confirmcallback;
        this._cancelCallBack = cancelCallBack;
    }

    onBtnConfirm(){
        if(this._confirmCallBack){
            this._confirmCallBack();
        }
        this.exitAction();
    }

    onBtnCancel(){
        if(this._cancelCallBack){
            this._cancelCallBack();
        }
        this.exitAction();
    }

    exitAction(){
        tween(this.node).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.node.destroy()
        }).start()
    }
}


