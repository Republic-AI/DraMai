import { _decorator, Component, Node, Button, EventTouch } from 'cc';
import { modelMgr, observer } from '../game/App';
import { EventType } from '../EventType';
import { AudioManager } from '../manager/AudioManager';
const { ccclass, property } = _decorator;

@ccclass('TabGroup')
export class TabGroup extends Component {
    @property({ type: [Node] })
    public tabbtns: Node[] = [];
    @property({ type: [Node] })
    public tabcontents: Node[] = [];
    @property(String)
    public tabtype: string = "";
    start() {
        //记录打开当前页index,即使被销毁后
        //this.on_tab_button_click(null, modelMgr.mainModel.currentTapIndex[this.tabtype]);
        this.node.on(Node.EventType.TOUCH_END, this._onTouchEnd, this);
        this.node.on(Node.EventType.TOUCH_CANCEL, this._onTouchEnd, this);
        // for (let index = 0; index <  this.tabbtns.length; index++) {
        //     const element =  this.tabbtns[index];
        //     if(!element.hasEventListener(Button.EventType.CLICK,this.on_tab_button_click,this)){
        //         element.on(Button.EventType.CLICK,this.on_tab_button_click,this);
        //     }
        // }

    }
    _onTouchEnd(touchEvent: EventTouch) {
    }
    update(deltaTime: number) {

    }
    private disable_tab(index: any) {
        if (this.tabbtns[index]) {
            this.tabbtns[index].getComponent(Button).interactable = true;
            this.tabbtns[index].getChildByName("on").active = false;
            this.tabbtns[index].getChildByName("off").active = true;
            this.tabcontents[index].active = false;
        }



    };
    private enable_tab(index) {
        if (this.tabbtns[index]) {
            this.tabbtns[index].getComponent(Button).interactable = false;//禁用,已处于点击状态
            this.tabbtns[index].getChildByName("on").active = true;
            this.tabbtns[index].getChildByName("off").active = false;
            this.tabcontents[index].active = true;
            //modelMgr.mainModel.currentTapIndex[this.tabtype] = index;
            this.node.emit("update_tab_content", this.tabcontents[index]);
        }

    };
    private on_tab_button_click(e, index) {
        //AudioManager.instance.playSound("new_NormanBtn");
        index = parseInt(index);
        for (let i = 0; i < this.tabbtns.length; i++) {
            if (i == index) {
                // this.enable_tab(i);
            }
            else {
                this.disable_tab(i);
            }
        }
        //支持操作同一个content
        if (this.tabbtns[index]) {
            this.enable_tab(index);
            this.node.emit("tabgroupeclick", index);
        }

    }

    private refresh_tab_content(e, index){
        index = parseInt(index);
        for (let i = 0; i < this.tabbtns.length; i++) {
            if (i == index) {
                // this.enable_tab(i);
            }
            else {
                if (this.tabbtns[i]) {
                    this.tabbtns[i].getComponent(Button).interactable = true;
                    this.tabbtns[i].getChildByName("on").active = false;
                    this.tabbtns[i].getChildByName("off").active = true;
                }
            }
        }
        //支持操作同一个content
        if (this.tabbtns[index]) {
            //this.enable_tab(index);
            if (this.tabbtns[index]) {
                this.tabbtns[index].getComponent(Button).interactable = false;//禁用,已处于点击状态
                this.tabbtns[index].getChildByName("on").active = true;
                this.tabbtns[index].getChildByName("off").active = false;
                //this.tabcontents[index].active = true;
                //modelMgr.mainModel.currentTapIndex[this.tabtype] = index;
            }
        }
        observer.post(EventType.UPDATE_ROLE_PLAY_INDEX, index + 1)
    }

    /**
     * 外部改变tabgroup
     * @param ind 
     */
    public chgTab(ind) {
        this.on_tab_button_click(null, ind);
    }
    /**
     * 显示/隐藏某个tab
     * @param tabIndex 
     * @param needShow 
     */
    public setShowTab(tabIndex: number, needShow: boolean) {
        if (this.tabbtns[tabIndex]) {
            this.tabbtns[tabIndex].active = needShow;
            if (this.tabcontents[tabIndex].active) {
                if (tabIndex == 0) {
                    this.on_tab_button_click(null, tabIndex + 1);
                } else {
                    this.on_tab_button_click(null, 0);
                }

            }
        }
    }

    /**
     * 隐藏所有tabs
     */
    public resetAllTabs() {
        for (let i = 0; i < this.tabbtns.length; i++) {
            this.disable_tab(i);
        }
        ///modelMgr.mainModel.currentTapIndex[this.tabtype] = -1;
    }

}

/**
 * 
 */