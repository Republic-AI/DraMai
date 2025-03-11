import { _decorator, Component, Node, Label } from 'cc';
import WebUtils from '../../utils/WebUtils';
import { ListViewCtrl2 } from './listViewCtrl2';
const { ccclass, property } = _decorator;

@ccclass('TabViewComponent')
export class TabViewComponent extends Component {
    @property(Node)
    public sprite_jiantou: Node = null;

    @property(Label)
    public label_current: Label = null;
    private grpArr = [];

    @property({ type: [Node] })
    public toggleArrs: Node[] = [];

    @property(Node)
    public togglecontainer: Node = null;


    private _isShow: boolean;
    start() {
        this.grpArr = [ { name: "稀有度", index: 0 }, { name: "颜色", index: 1 }, { name: "系列", index: 2 }];
        this.setListData(0);

    }

    onEnable() {
        for (let i = 0; i < this.toggleArrs.length; i++) {
            this.toggleArrs[i].on("toggle", this.itemTouchendHandler, this);
        }
        this.showList(false);
    }

    onDisable() {
        for (let i = 0; i < this.toggleArrs.length; i++) {
            this.toggleArrs[i].off("toggle", this.itemTouchendHandler, this);
        }
    }

    /**
     * 
     * @param isshow 
     */
    private showList(isshow: boolean) {
        this._isShow = isshow;
        this.togglecontainer.active = this._isShow;
        if(this._isShow){
            WebUtils.getResouceImg("newjishi/tabview_6",this.sprite_jiantou);
        }else{
            WebUtils.getResouceImg("newjishi/tabview_2",this.sprite_jiantou);
        }
    }
    /**
     * top 点击
     * @param evt 
     */
    topClickHandler(evt) {

        this.showList(!this._isShow);
    }

    /**
     * 
     * @param evt 
     */
    private itemTouchendHandler(evt: TouchEvent) {
        let tar = evt.target as any as Node;
        for (let i = 0; i < this.toggleArrs.length; i++) {
            if (tar == this.toggleArrs[i]) {
                this.setListData(i);
            }
        }
    }
    /**
     * 根据index 修改数据
     * @param selectInd 
     */
    public setListData(selectInd: number) {
        this.label_current.string = this.grpArr[selectInd].name;
        this.showList(false);
    }



    update(deltaTime: number) {

    }

    onDestroy() {

    }
}

