import { _decorator, Component, Node, ScrollView, Label, Button, Vec3, UITransform, instantiate, error, Vec2, System, Color } from "cc";
import { ListItemComponent } from "./ListItemComponent";
import { observer } from "../App";
const { ccclass, property, menu } = _decorator;

const _temp_vec3 = new Vec3();
/**
 * scrollview 操作版
 * 遮罩显示list cell
 * cell length 不限制 
 * 适用cell 高度自适应显示
 */
@ccclass("ListViewCtrl5")
@menu('UI/ListViewCtrl5')
export class ListViewCtrl5 extends Component {
    @property(Node)
    public itemTemplate: Node = null!;


    @property(Node)
    public content: Node = null!;
    @property(ScrollView)
    public scrollView: ScrollView = null!;
    private _items: Node[] = [];
    private _itemTemplateUITrans!: UITransform;

    onLoad() {
    }

    //=================外部调用设置=======================
    private _itemdata: any[] = null;
    /**
     * 设置spwan,itemdata;
     * 
     */
    public initData(itemdata) {
        this.reset();
        this._itemdata = itemdata;
        this.initialize();

        this.scheduleOnce(() => {
            for (let i = 0; i < this._itemdata.length; i++) {
                this._items[i].getComponent(ListItemComponent).setData(this._itemdata[i]);
            }
            this.scheduleOnce(()=>{
                this.scrollView.scrollToBottom(1);
            },0.1);
        }, 0.1)
        this.scrollView.node.on('scroll-ended', this.onScrollEnded, this);
    }

    public reset() {
        this._itemdata = [];
        this.content.removeAllChildren();
        this._items = [];
    }
    //=================外部调用设置end=======================
    // 初始化 item
    initialize() {

        for (let i = 0; i < this._itemdata.length; i++) { // spawn items, we only need to do this once
            let item = instantiate(this.itemTemplate) as Node;
            item.setPosition(0, 0, 0);
            this.content.addChild(item);
            // let itemUITrans = item.getComponent(UITransform);
            const indexlabelComp = item.getComponent(ListItemComponent)!.index_label;
            indexlabelComp.string = i + 1 + "";
            this._items.push(item);
        }
    }



    update(dt: number) {
        if (!this._itemTemplateUITrans) {
            return;//未初始化；
        }
    }

    addItem() {
        // 创建新元素
        let newItem = instantiate(this.itemTemplate) as Node;
        // 将新元素添加到内容节点
        this.content.addChild(newItem);
        // 更新 ScrollView 的 content 大小
        //  this.content.getComponent(UITransform).height += newItem.getComponent(UITransform).height;
    }

    removeItem() {

    }

    onScrolling() {
        let scrollOffset = this.scrollView.getScrollOffset(); // 获取滚动偏移量
        let maxScrollOffset = this.scrollView.getMaxScrollOffset(); // 获取最大滚动偏移量
        console.log(scrollOffset,maxScrollOffset);
        if (maxScrollOffset.y - scrollOffset.y > 300 ) {//还差300滚动到顶部的时候，开始获取数据
            // this.addItem();
        }
    }
    onScrollEnded(event: Event) {
        // 滚动结束事件被触发，可以在这里处理回弹结束的逻辑
        console.log('Scrolling has ended');
        // 你可以在这里添加更多的判断逻辑来确定是否是回弹事件
        this.node.emit("scrollended");
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

}

