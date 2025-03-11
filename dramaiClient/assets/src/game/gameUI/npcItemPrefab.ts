import { _decorator, Component, Label, Node, resources, Sprite, SpriteFrame } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('npcItemPrefab')
export class npcItemPrefab extends Component {
    @property(Sprite)
    imgItem:Sprite = null;

    @property(Label)
    lblItem:Label = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data){
        console.log("data======" + JSON.stringify(data));
        resources.load("gameUI/image/item_" + data.itemId + "/spriteFrame",(error,itemSpr:SpriteFrame)=>{
            if(error){
                console.log("load itemspr error:" + error)
            }
            this.imgItem.spriteFrame = itemSpr;
            this.lblItem.string = data.count;
        })
    }
}


