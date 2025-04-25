import { _decorator, Component, Label, Node, resources, Sprite, SpriteFrame } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('npcItemPrefab')
export class npcItemPrefab extends Component {
    @property(Sprite)
    imgItem:Sprite = null;

    @property(Label)
    lblItem:Label = null;

    private _isValid: boolean = true;

    protected onDestroy(): void {
        this._isValid = false;
    }

    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data) {
        this.lblItem.string = data.count;
        resources.load("gameUI/image/item_" + data.itemId + "/spriteFrame",(error,itemSpr:SpriteFrame)=>{
            if (!this._isValid) return;
            if(error){
                console.log("load itemspr error:" + error)
            }
            else{
                this.imgItem.spriteFrame = itemSpr;
            }
        })
    }
}


