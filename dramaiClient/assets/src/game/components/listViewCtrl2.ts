import { _decorator, Component, Node, ScrollView, Label, Button, Vec3, UITransform, instantiate, error, Vec2 } from "cc";
import { ListItemComponent } from "./ListItemComponent";
const { ccclass, property, menu } = _decorator;

const _temp_vec3 = new Vec3();
/**
 * 竖版listview
 * 复用cell实例
 * 可指定复用数量
 */
@ccclass("ListViewCtrl2")
@menu('UI/ListViewCtrl2')
export class ListViewCtrl2 extends Component {
    @property(Node)
    public itemTemplate: Node = null!;
    @property(ScrollView)
    public scrollView: ScrollView = null!;
    @property
    public spawnCount = 20; // 初始化 item 数量
    @property
    public totalCount = 0; // 滚动列表里总的 item 数量
    @property
    public spacing = 0; // item 垂直排布间隔
    @property
    public bufferZone = 0; // when item is away from bufferZone, we relocate it


    private _content: Node = null!;
    private _items: Node[] = [];
    private _updateTimer = 0;
    private _updateInterval = 0.2;
    private _lastContentPosY = 0;
    private _itemTemplateUITrans!: UITransform;
    private _contentUITrans!: UITransform;

    onLoad() {
        this._content = this.scrollView.content!;
        // this.initialize();//编辑器数据设置
        this._updateTimer = 0;
        this._updateInterval = 0.2;
        this._lastContentPosY = 0; // use this variable to detect if we are scrolling up or down
    }

    //=================外部调用设置=======================
    private _itemdata: any[] = null;
    /**
     * 设置spwan,itemdata;
     * 
     */
    public initData(spawnCount, itemdata) {
        this.reset();
        this.spawnCount = spawnCount;
        this.totalCount = itemdata ? itemdata.length : 0;
        this._itemdata = itemdata;
        this.initialize();

        this.scheduleOnce(() => {//等待初始化完成后，可获取组件
            for (let i = 0; i < this._items.length; i++) {
                this._items[i].getComponent(ListItemComponent).setData(this._itemdata[i]);
            }
            // this._content.getComponent(UITransform).height = this._contentUITrans.height;
        }, 0.1)
    }

    public reset() {
        this.spawnCount = 0;
        this.totalCount = 0;
        this._itemdata = [];
        this._content.removeAllChildren();
        this._items = [];
    }
    //=================外部调用设置end=======================
    // 初始化 item
    initialize() {

        this._itemTemplateUITrans = this.itemTemplate.getComponent(UITransform);
        this._contentUITrans = this._content.getComponent(UITransform);
        this._contentUITrans.height = this.totalCount * (this._itemTemplateUITrans.height + this.spacing) + this.spacing; // get total content height
        for (let i = 0; i < this.spawnCount; i++) { // spawn items, we only need to do this once
            let item = instantiate(this.itemTemplate) as Node;
            this._content.addChild(item);
            let itemUITrans = item.getComponent(UITransform);
            item.setPosition(0, -itemUITrans.height * (0.5 + i) - this.spacing * (i), 0);
            // const labelComp = item.getComponentInChildren(Label)!;

            // const indexlabelComp = item.getComponent(ListItemComponent)!.index_label;
            // indexlabelComp.string = i + 1 + "";
            this._items.push(item);
        }
    }

    getPositionInView(item: Node) {
        // get item position in scrollview's node space
        let worldPos = item.parent!.getComponent(UITransform)!.convertToWorldSpaceAR(item.position);
        let viewPos = this.scrollView.node.getComponent(UITransform)!.convertToNodeSpaceAR(worldPos);
        return viewPos;
    }

    update(dt: number) {
        // if (!this._itemTemplateUITrans) {
        //     return;//未初始化；
        // }
        // this._updateTimer += dt;
        // if (this._updateTimer < this._updateInterval) return; // we don't need to do the math every frame
        // this._updateTimer = 0;
        // let items = this._items;
        // let buffer = this.bufferZone;
        // let isDown = this.scrollView.content!.position.y < this._lastContentPosY; // scrolling direction
        // let offset = (this._itemTemplateUITrans.height + this.spacing) * items.length;
        // for (let i = 0; i < items.length; ++i) {
        //     let viewPos = this.getPositionInView(items[i]);
        //     items[i].getPosition(_temp_vec3);
        //     if (isDown) {
        //         // if away from buffer zone and not reaching top of content
        //         if (viewPos.y < -buffer && _temp_vec3.y + offset < 0) {
        //             _temp_vec3.y += offset;
        //             items[i].setPosition(_temp_vec3);
        //             const indexlabelComp = items[i].getComponent(ListItemComponent)!.index_label;
        //             let ind = Math.floor(-_temp_vec3.y / this._contentUITrans.height * this.totalCount) ;
        //             indexlabelComp.string = ind + 1 + "";
        //             items[i].getComponent(ListItemComponent).setData(this._itemdata[ind]);
        //         }
        //     } else {
        //         // if away from buffer zone and not reaching bottom of content
        //         if (viewPos.y > buffer && _temp_vec3.y - offset > -this._contentUITrans.height) {
        //             _temp_vec3.y -= offset;
        //             items[i].setPosition(_temp_vec3);
        //             const indexlabelComp = items[i].getComponent(ListItemComponent)!.index_label;
        //             let ind = Math.floor(-_temp_vec3.y / this._contentUITrans.height * this.totalCount) ;
        //             indexlabelComp.string = ind + 1 + "";
        //             items[i].getComponent(ListItemComponent).setData(this._itemdata[ind]);
        //         }
        //     }
        // }
        // // update lastContentPosY
        // this._lastContentPosY = this.scrollView.content!.position.y;
        // // console.log("Total Items: " + this.totalCount);
    }

    addItem() {
        this._contentUITrans.height = (this.totalCount + 1) * (this._itemTemplateUITrans.height + this.spacing) + this.spacing; // get total content height
        this.totalCount = this.totalCount + 1;
    }

    removeItem() {
        if (this.totalCount - 1 < 30) {
            error("can't remove item less than 30!");
            return;
        }

        this._contentUITrans.height = (this.totalCount - 1) * (this._itemTemplateUITrans.height + this.spacing) + this.spacing; // get total content height
        this.totalCount = this.totalCount - 1;

        this.moveBottomItemToTop();
    }

    moveBottomItemToTop() {
        let offset = (this._itemTemplateUITrans.height + this.spacing) * this._items.length;
        let length = this._items.length;
        let item = this.getItemAtBottom();
        item.getPosition(_temp_vec3);

        // whether need to move to top
        if (_temp_vec3.y + offset < 0) {
            _temp_vec3.y = _temp_vec3.y + offset;
            item.setPosition(_temp_vec3);
        }
    }

    getItemAtBottom() {
        let item = this._items[0];
        for (let i = 1; i < this._items.length; ++i) {
            if (item.position.y > this._items[i].position.y) {
                item = this._items[i];
            }
        }
        return item;
    }

    scrollToFixedPosition() {
        this.scrollView.scrollToOffset(new Vec2(0, 500), 2, true);
    }
}

