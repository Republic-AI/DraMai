import { _decorator, Component, Node, ScrollView, Label, Button, Vec3, UITransform, instantiate, error, Vec2 } from "cc";
import { ListItemComponent } from "./ListItemComponent";
const { ccclass, property, menu } = _decorator;

const _temp_vec3 = new Vec3();
/**
 * 调整版本
 */
@ccclass("ListViewCtrl2H")
export class ListViewCtrl2H extends Component {
    @property(Node)
    public itemTemplate: Node  = null!;
    @property(ScrollView)
    public scrollView: ScrollView  = null!;
    @property
    public spawnCount = 0; // 初始化 item 数量
    @property
    public totalCount = 0; // 滚动列表里总的 item 数量
    @property
    public spacing = 0; // item 水平排布间隔
    @property
    public bufferZone = 0; // when item is away from bufferZone, we relocate it


    private _content: Node = null!;
    private _items: Node[] = [];
    private _updateTimer = 0;
    private _updateInterval = 0.2;
    private _lastContentPosX = 0;
    private _itemTemplateUITrans!: UITransform;
    private _contentUITrans!: UITransform;

    onLoad() {
        this._content = this.scrollView.content!;
        this.initialize();
        this._updateTimer = 0;
        this._updateInterval = 0.2;
        this._lastContentPosX = 0; // use this variable to detect if we are scrolling up or down
    }

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

    // 初始化 item
    initialize() {
        this._itemTemplateUITrans = this.itemTemplate.getComponent(UITransform);
        this._contentUITrans = this._content.getComponent(UITransform);
        this._contentUITrans.width = this.totalCount * (this._itemTemplateUITrans.width + this.spacing) + this.spacing; // get total content height
        for (let i = 0; i < this.spawnCount; ++i) { // spawn items, we only need to do this once
            let item = instantiate(this.itemTemplate) as Node;
            this._content.addChild(item);
            let itemUITrans = item.getComponent(UITransform);
            item.setPosition(itemUITrans.width * (0.5 + i) + this.spacing * (i) - 60,0, 0);

            // const labelComp = item.getComponentInChildren(Label)!;
            // const labelComp = item.getComponent(ListItemComponent)!["item_label"];
            // labelComp.string = `item_${i}`;
            const indexlabelComp = item.getComponent(ListItemComponent)!.index_label;
            indexlabelComp.string = i+"";
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
        // this._updateTimer += dt;
        // if (this._updateTimer < this._updateInterval) return; // we don't need to do the math every frame
        // this._updateTimer = 0;
        // let items = this._items;
        // let buffer = this.bufferZone;
        // let isDown = this.scrollView.content!.position.x < this._lastContentPosX; // scrolling direction
        // let offset = (this._itemTemplateUITrans.width + this.spacing) * items.length;
        // for (let i = 0; i < items.length; ++i) {
        //     let viewPos = this.getPositionInView(items[i]);
        //     items[i].getPosition(_temp_vec3);
        //     if (isDown) {
        //         // if away from buffer zone and not reaching top of content
        //         if (viewPos.x < -buffer && _temp_vec3.x + offset < 0) {
        //             _temp_vec3.x += offset;
        //             items[i].setPosition(_temp_vec3);
        //             const indexlabelComp =  items[i].getComponent(ListItemComponent)!.index_label;
        //             indexlabelComp.string = Math.floor(_temp_vec3.x/this._contentUITrans.width * this.totalCount)+"";
        //             items[i].getComponent(ListItemComponent).setData();
        //         }
        //     } else {
        //         // if away from buffer zone and not reaching bottom of content
        //         if (viewPos.x > buffer && _temp_vec3.x - offset > -this._contentUITrans.width) {
        //             _temp_vec3.x -= offset;
        //             items[i].setPosition(_temp_vec3);
        //             const indexlabelComp =  items[i].getComponent(ListItemComponent)!.index_label;
        //             indexlabelComp.string = Math.floor(_temp_vec3.x/this._contentUITrans.width * this.totalCount)+"";
        //             items[i].getComponent(ListItemComponent).setData();
        //         }
        //     }
        // }
        // // update lastContentPosY
        // this._lastContentPosX = this.scrollView.content!.position.x;
        //console.log("Total Items: " + this.totalCount);
    }

    addItem() {
        this._contentUITrans.width = (this.totalCount + 1) * (this._itemTemplateUITrans.width + this.spacing) + this.spacing; // get total content height
        this.totalCount = this.totalCount + 1;
    }

    removeItem() {
        if (this.totalCount - 1 < 30) {
            error("can't remove item less than 30!");
            return;
        }

        this._contentUITrans.width = (this.totalCount - 1) * (this._itemTemplateUITrans.width + this.spacing) + this.spacing; // get total content height
        this.totalCount = this.totalCount - 1;

        this.moveBottomItemToTop();
    }

    moveBottomItemToTop() {
        let offset = (this._itemTemplateUITrans.width + this.spacing) * this._items.length;
        let length = this._items.length;
        let item = this.getItemAtBottom();
        item.getPosition(_temp_vec3);

        // whether need to move to top
        if (_temp_vec3.x + offset < 0) {
            _temp_vec3.x = _temp_vec3.x + offset;
            item.setPosition(_temp_vec3);
        }
    }

    getItemAtBottom() {
        let item = this._items[0];
        for (let i = 1; i < this._items.length; ++i) {
            if (item.position.x > this._items[i].position.y) {
                item = this._items[i];
            }
        }
        return item;
    }

    scrollToFixedPosition() {
        this.scrollView.scrollToOffset(new Vec2(0, 500), 2, true);
    }
}

