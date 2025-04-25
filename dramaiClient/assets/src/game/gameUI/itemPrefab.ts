import { _decorator, Component, Node, Sprite, Label, UITransform, director, assetManager, SpriteFrame, resources } from 'cc';
import { changeSkinPrefab } from './changeSkinPrefab';
import { ItemConfigMap } from '../../StaticUtils/ItemConfig';
const { ccclass, property } = _decorator;

@ccclass('itemPrefab')
export class itemPrefab extends Component {
    @property(Sprite)
    itemFrame: Sprite = null;

    @property(Label)
    lblItemName: Label = null;

    @property(Node)
    helpNode: Node = null;

    _id = null;
    _data = null;
    private _isValid: boolean = true;

    start() {
        // 注册触摸事件
        this.helpNode.on(Node.EventType.TOUCH_START, this.onHelpPress, this);
        this.helpNode.on(Node.EventType.TOUCH_END, this.onHelpRelease, this);
        this.helpNode.on(Node.EventType.TOUCH_CANCEL, this.onHelpRelease, this);
    }

    update(deltaTime: number) {
        
    }

    protected onDestroy(): void {
        this._isValid = false;
    }

    private onHelpPress() {
        console.log('Help button pressed');
        this.helpNode.setScale(1.1,1.1,1)
        director.getScene().getComponentInChildren(changeSkinPrefab).showHelpNode(this._id);
    }

    private onHelpRelease() {
        console.log('Help button released');
        this.helpNode.setScale(1,1,1)
        //director.getScene().getComponentInChildren(changeSkinPrefab).hideHelpNode();
    }

    initData(id: any) {
        this._id = id;
        resources.load("gameUI/image/item_"+this._id+"/spriteFrame",SpriteFrame,(err,spriteFrame:SpriteFrame)=>{
            if (!this._isValid) return;
            if(err){
                console.log(err);
            }
            else{
                this.itemFrame.spriteFrame = spriteFrame;
            }
        })
        this.lblItemName.string = ItemConfigMap[this._id].name;

    }
}


