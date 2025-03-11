import {Node, Component, _decorator, Button, Color, Enum, instantiate, js, Label, Layout, log, size, Sprite, EventHandler, UITransform } from "cc";
import { EDITOR } from "cc/env";

const { ccclass, property } = _decorator;

enum PARAM_TYPE {
	NODE_INDEX,
	NODE_NAME
}
///不可用
@ccclass("TabButtonGroup")
export default class TabButtonGroup extends Component {

	private _isAddBtn : boolean = false;

	@property({ type: Node })
	defaultTab: Node = null

	@property({ type: [Node] })
	_tabsNode: Array<Node> = new Array<Node>(1);
	@property({
		tooltip: 'tab节点',
		type: [Node],
	})
	public set TabsNode(tabArr: Array<Node>) {
		// log("gen init")
		if (tabArr.length < 1) {
			this.TabsNode = new Array<Node>(1);
			return;
		}
		this._tabsNode = tabArr;
		this._genTabs(tabArr);
		this._updateLayout();
	}
	public get TabsNode(): Array<Node> {
		return this._tabsNode
	}

	@property({})
	_onLabColor: Color = new Color(255, 255, 255);
	@property({
		tooltip: 'tab激活时的label的颜色',
	})
	public set OnLabColor(color: Color) {
		this._onLabColor = color;
		if (this.defaultTab) {
            let lb = this.defaultTab.getChildByName("on").getChildByName("label");
			if(lb) lb.getComponent(Label).color = this._onLabColor;
		}
	}
	public get OnLabColor(): Color {
		return this._onLabColor
	}
	@property({})
	_offLabColor: Color = new Color(235, 181, 255);
	@property({
		tooltip: 'tab未激活时的label的颜色',
	})
	public set OffLabColor(color: Color) {
		this._offLabColor = color;
		if (this.defaultTab) {
            let lb = this.defaultTab.getChildByName("on").getChildByName("label");

			lb.getComponent(Label).color = this._offLabColor;
		}
	}
	public get OffLabColor(): Color {
		return this._offLabColor
	}

	@property({
		type: Enum(PARAM_TYPE),
		tooltip: '节点按钮事件的自定义数据, node_index为节点的的index, node_name为节点名字'
	})
	customData: PARAM_TYPE = PARAM_TYPE.NODE_INDEX;

	@property([EventHandler])
	touchEvents: EventHandler[] = [];


	// LIFE-CYCLE CALLBACKS:
	/**
	 * 生成初始化tab
	 * @param tabArr tab数量
	 */
	private _genTabs(tabArr) {
		if (EDITOR) {
			//删除已有
			let children = this.node.children.concat()
			for (let i = 0; i < children.length; i++) {
				if (i == 0) {
					continue;
				}
				if (children[i] != null) {
					children[i].removeFromParent();
					children[i].destroy()
				}
			}
			//generate 
			for (let i = 0; i < tabArr.length; i++) {
				if (i == 0 && this.node.children.length == 1) {
					this.TabsNode[i] = this.node.children[i];
					continue;
				}
				let tab = null;
				if (this.defaultTab && this.node.children.length > 0) {
					tab = instantiate(this.defaultTab);
				} else {
					tab = this._genNewTab(i);
					this.defaultTab = tab;
				}
				tab.parent = this.node;
				this.TabsNode[i] = tab;
			}
		}
	}

	/**
	 * 生成一个新的tab模版
	 * @param i 下标
	 */
	private _genNewTab(i) {
		let tab = new Node("tab");
		let onNode = this._genButtonNode("on", this.OnLabColor);
		let offNode = this._genButtonNode("off", this.OffLabColor);
		onNode.active = false;
        const uiTrans = tab.getComponent(UITransform)!;
        uiTrans.anchorX = 0.5;
		uiTrans.setContentSize(size(100, 100));
		tab.addChild(offNode);
		tab.addChild(onNode);
		return tab;
	}

	private _genButtonNode(name, color) {
		let root = new Node(name);
		root.addComponent(Sprite);
		let label = this._genLabel(color);
		root.addChild(label);
		return root;
	}

	private _genLabel(color) {
		let label = new Node("label");
		label.addComponent(Label);
        let lb = label.getComponent(Label);
		lb.string = "TAB Name";
		lb.color = color;
		return label;
	}

	/**
	 * 根据tab 调整layout大小
	 */
	private _updateLayout() {
		if (!this.defaultTab) {
			return;
		}
		let w, h = null;
		let layout = this.node.getComponent(Layout);
		if (layout.type == Layout.Type.VERTICAL) {
			h = (this.defaultTab.getComponent(UITransform).height + layout.spacingY) * this.TabsNode.length;
			w = this.defaultTab.getComponent(UITransform).width;
		} else if (layout.type == Layout.Type.HORIZONTAL) {
			w = (this.defaultTab.getComponent(UITransform).width + layout.spacingX) * this.TabsNode.length;
			h = this.defaultTab.getComponent(UITransform).height;
		}
		this.node.getComponent(UITransform).width = w;
		this.node.getComponent(UITransform).height = h;
	}

	/**
	 * 添加button组件
	 */
	private _addTabButtonComp() {
		if (this._isAddBtn) {
			return;
		}
		this._isAddBtn = true;
		this.node.children.forEach((node, nodeIndex) => {
			let btnComp:Button =  node.getComponent(Button) as Button;

			if (btnComp == null) {
				node.addComponent(Button)
				btnComp = node.getComponent(Button) as Button;
			}
			// 判断button，将ccButton替换为自定义的UICustomButton
			let btnCompName = js.getClassName(btnComp)
			if ( btnCompName === 'Button') {
				let newBtnComp = node.addComponent("Button") as Button;

				newBtnComp.transition = btnComp.transition;
				newBtnComp.zoomScale = btnComp.zoomScale;

				newBtnComp.disabledSprite = btnComp.disabledSprite;
				newBtnComp.hoverSprite = btnComp.hoverSprite;
				newBtnComp.normalSprite = btnComp.normalSprite;
				newBtnComp.pressedSprite = btnComp.pressedSprite;

				newBtnComp.hoverColor = btnComp.hoverColor;
				newBtnComp.normalColor = btnComp.normalColor;
				newBtnComp.pressedColor = btnComp.pressedColor;
				newBtnComp.disabledColor = btnComp.disabledColor;

				newBtnComp.target = btnComp.target

				btnComp = newBtnComp;
				node.removeComponent(Button) // 移除老button
			}

			//绑定回调事件
			this.touchEvents.forEach((event: EventHandler) => {
				//克隆数据，每个节点获取的都是不同的回调
				let hd = new EventHandler() //copy对象
				hd.component = event['_componentName']
				hd.handler = event.handler
				hd.target = event.target
				if (this.customData === PARAM_TYPE.NODE_INDEX) {
					hd.customEventData = nodeIndex.toString()
				} else {
					hd.customEventData = node.name
				}
				btnComp.clickEvents.push(hd)
			})

		})
	}


	protected onLoad() {
		if (EDITOR) {
			//添加layout
			if (!this.node.getComponent(Layout)) {
				this.node.getComponent(UITransform).anchorY = 1;
				log("TabButtonGroup add Layout");
				this.node.addComponent(Layout);
				this.node.getComponent(Layout).type = Layout.Type.VERTICAL;
				this.node.getComponent(UITransform).anchorY = 1;
				this._updateLayout();
			}
			if (!this.defaultTab || this.TabsNode.length < 1) {
				this.TabsNode = this.TabsNode;
			}
			return;
		}
		// this._addTabButtonComp();
	}



	/**
	 * tab状态切换
	 * @param index tab下标
	 */
	public changeTab(index) {
		if (!this._isAddBtn) {
			this._addTabButtonComp();
		}
		for (let i = 0; i < this.TabsNode.length; i++) {
			this.TabsNode[i].getComponent(Button).interactable = true;
			this.TabsNode[i].getChildByName("on").active = false;
			this.TabsNode[i].getChildByName("off").active = true;
		}
		this.TabsNode[index].getComponent(Button).interactable = false;
		this.TabsNode[index].getChildByName("on").active = true;
		this.TabsNode[index].getChildByName("off").active = false;
        console.log("change tab..")

	}

	/**
	 * 设置默认激活tab
	 */
	public initTab(index = 1) {
		this.changeTab(index - 1);
	}

	/**
	 * 设置tab label string
	 * @param list 
	 */
	public setTabLabel(list) {
		for (let index = 0; index < list.length; index++) {
			if (this.TabsNode[index]) {
				this.TabsNode[index].active = true;
				this.TabsNode[index].getChildByName("on").getChildByName("label").getComponent(Label).string = list[index].title;
				this.TabsNode[index].getChildByName("off").getChildByName("label").getComponent(Label).string = list[index].title;
			} else {
				let newtab = instantiate(this.TabsNode[0]);
				newtab.getChildByName("on").getChildByName("label").getComponent(Label).string = list[index].title;
				newtab.getChildByName("off").getChildByName("label").getComponent(Label).string = list[index].title;
				let btnComp =  newtab.getComponent(Button)
				btnComp.clickEvents[0].customEventData = index.toString();
				this.node.addChild(newtab);
				this.TabsNode.push(newtab);
			}
		}
	}

	public hideAll() {
		for (let i = 0; i < this.TabsNode.length; i++) {
			this.TabsNode[i].active = false;
		}
	}
}
