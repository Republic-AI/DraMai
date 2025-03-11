import { _decorator, assetManager, Component, instantiate, JsonAsset, Node, PageView, Prefab } from 'cc';
import { voteItem } from './voteItem';
const { ccclass, property } = _decorator;

@ccclass('voteLayer')
export class voteLayer extends Component {
    @property(Node)
    content:Node = null;

    @property(Prefab)
    voteItem:Prefab = null;

    @property(PageView)
    PageView:PageView = null;

    start() {
        this.initData()
    }

    update(deltaTime: number) {
        
    }

    onBtnClose(){
        this.node.destroy();
    }

    initData(){
        let cfgBundle = assetManager.getBundle("voteLayer");
        cfgBundle.load("json/voteCfg",JsonAsset,(err,data:JsonAsset)=>{
            console.log("data======" + JSON.stringify(data.json));
            this.content.children.forEach((node,index)=>{
                node.getComponent(voteItem).initData(data.json[index]);
            })
            // data.json.forEach(data=>{
            //     let voteItemNode = instantiate(this.voteItem);
            //     voteItemNode.getComponent(voteItem).initData(data);
            //     this.content.addChild(voteItemNode);
            //     console.log("viewLength====" + this.content.children.length)
            // })
        })
    }
}


