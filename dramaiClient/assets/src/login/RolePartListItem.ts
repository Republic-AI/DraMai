import { _decorator, Component, Label, Node, Sprite } from 'cc';
import { AudioManager } from '../manager/AudioManager';
import { ToysInfo } from '../model/StaticTextConfig';
import WebUtils from '../utils/WebUtils';
import  Log  from '../../src/utils/LogUtils'
import { observer } from '../game/App';
import { EventType } from '../EventType';
import loginModel from '../model/loginModel';
const { ccclass, property } = _decorator;

const TAG = 'RolePartListItem'

@ccclass('RolePartListItem')
export class RolePartListItem extends Component {

    @property(Node)
    public on:Node = null;

    @property(Node)
    public off:Node = null;

    @property(Node)
    public icon:Node = null;

    @property(Node)
    public npc:Node = null;

    @property(Node)
    public townNPCPlayer:Node = null;

    public _da;
    start() {
        this.node.on("update_data", this.updateData, this);
        observer.on(EventType.CHANGE_ROLE_PART_SELECT, this.refreshRolePart, this)
        
    }

    update(deltaTime: number) {

    }
    protected onDestroy(): void {
        this.node.off("update_data", this.updateData, this);
        observer.off(EventType.SOCKET_ONOPEN, this.refreshRolePart, this);
    }
    /**
    * 视图更新
    * @param da 
    */
    public updateData(da: any) {
        this._da = da;
        this.node.setScale(1,1)
        if (this._da) {
            this.icon.active = true
            WebUtils.getResouceImg(da.icon_path, this.icon);
            if(da.icon_path === loginModel.currentType.iconValue){
                this.on.active = true
            }
            console.log(TAG, da, loginModel)
        }
    }

    /**
     * 切换购买道具
     * @param evt 
     * @param da 
     * @returns 
     */
    public clickHandler(evt, da) {
        // AudioManager.instance.playSound("new_NormanBtn");
        // if (this._da) {
        //     this.buy_page.active = true
        //     this.buy_page.getComponent(ShopPurchaseComponent).setItemData(this._da)

        // }
        // console.log(TAG, 'click', this._da)
    }

    refreshRolePart(){
        this.on.active = false
    }

    clickSelect(){
        observer.post(EventType.CHANGE_ROLE_PART_SELECT)
        loginModel.currentType.id = this._da.id
        loginModel.currentType.iconValue = this._da.icon_path
        loginModel.currentType.roleValue = this._da.frame_path
        WebUtils.getResouceImg(this._da.frame_path, this.npc.getChildByName(loginModel.currentName))
        WebUtils.getResouceImg(this._da.frame_path, this.townNPCPlayer.getChildByName(loginModel.currentName))
        this.on.active = true
    }

}

