import { _decorator, Component, Label, Node, resources, Sprite, SpriteFrame } from 'cc';
import { ItemConfigMap } from '../../StaticUtils/ItemConfig';
const { ccclass, property } = _decorator;

@ccclass('popItemPrefab')
export class popItemPrefab extends Component {
    @property(Sprite)
    private itemImage: Sprite = null;

    @property(Label)
    private itemDes: Label = null;
    
    private _isValid: boolean = true;

    start() {

    }

    update(deltaTime: number) {
        
    }

    protected onDestroy(): void {
        this._isValid = false;
    }

    initData(data:any){
        this.itemDes.string = ItemConfigMap[data].des;
        resources.load("gameUI/image/item_" + data + "/spriteFrame",SpriteFrame,(err,spr:SpriteFrame)=>{
            if (!this._isValid) return;
            if(err){
                console.log("item load error" + err);
            }
            else{
                this.itemImage.spriteFrame = spr;
            }
        });
    }

    onBtnClose(){
        this.node.destroy();
    }
}


