import { _decorator, Canvas, Component, director, instantiate, Node, Prefab, resources, Sprite, SpriteFrame } from 'cc';
import { popItemPrefab } from '../game/gameUI/popItemPrefab';
const { ccclass, property } = _decorator;

@ccclass('scenItemNodeEx')
export class scenItemNodeEx extends Component {

    @property(Prefab)
    private popItemPrefab:Prefab = null;


    itemId:string = "";
    start() {
        this.itemId = this.node.getComponent(Sprite).spriteFrame.name;
    }

    update(deltaTime: number) {
        
    }

    protected onDestroy(): void {
        
    }

    initData(){

    }

    onBtnClick(){
        let popItemPrefabNode = instantiate(this.popItemPrefab);
        popItemPrefabNode.getComponent(popItemPrefab).initData(this.itemId);
        let canvas =  director.getScene().getComponentInChildren(Canvas);
        canvas.node.addChild(popItemPrefabNode);
    }
}


