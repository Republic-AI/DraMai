import { _decorator, assetManager, Component, instantiate, JsonAsset, Node, Prefab, SpriteFrame, tween, v3 } from 'cc';
import { newsItem } from './newsItem';
import { GlobalConfig } from '../config/GlobalConfig';
import { newsInfo } from '../../StaticUtils/NPCConfig';
const { ccclass, property } = _decorator;

@ccclass('newsLayer')
export class newsLayer extends Component {
    @property(Prefab)
    newsItem:Prefab = null;

    @property(Node)
    content:Node = null;

    protected onLoad(): void {
        this.node.setScale(v3(0, 0, 1))
        tween(this.node).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).start()
    }
    start() {
        this.initData()
    }

    update(deltaTime: number) {
        
    }
    initData(){
        let sceneId = GlobalConfig.instance.chooseScene
        if(newsInfo[sceneId]){
            newsInfo[sceneId].forEach(data=>{
                let newsItemNode = instantiate(this.newsItem);
                newsItemNode.getComponent(newsItem).initData(data);
                this.content.addChild(newsItemNode);
            })
        }
    }

    onBtnClose(){
        tween(this.node).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.node.destroy()
        }).start()
    }
}


