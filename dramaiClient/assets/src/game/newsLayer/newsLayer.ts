import { _decorator, assetManager, Component, instantiate, JsonAsset, Node, Prefab, SpriteFrame, tween, v3 } from 'cc';
import { newsItem } from './newsItem';
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
        let cfgBundle = assetManager.getBundle("newsCfg");
        cfgBundle.load("json/NewsConfig",JsonAsset,(err,data:JsonAsset)=>{
            console.log("data======" + JSON.stringify(data.json));
            if(data.json[0].id == 1){
                data.json.reverse();
            }
            data.json.forEach(data=>{
                let newsItemNode = instantiate(this.newsItem);
                newsItemNode.getComponent(newsItem).initData(data);
                this.content.addChild(newsItemNode);
            })
        })
    }

    onBtnClose(){
        tween(this.node).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.node.destroy()
        }).start()
    }
}


