import { _decorator, Component, Node, resources, Sprite, SpriteFrame } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('scenItemNode')
export class scenItemNode extends Component {

    _itemId:number = 0;
    _uniqid:number = 0;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(itemData:any){
        if(!itemData.itemId){
            this.node.destroy();
            return;
        }
        this._itemId = itemData.itemId;
        this._uniqid = itemData.id;
        resources.load("common/image/item_" + itemData.itemId + "/spriteFrame",SpriteFrame,(err,spr:SpriteFrame)=>{
            if(err){
                console.log("item load error" + err);
                return;
            }
            this.node.getComponent(Sprite).spriteFrame = spr;
        });
    }
}


