import { _decorator, assetManager, Component, director, instantiate, Label, Node, Prefab, Sprite, SpriteFrame } from 'cc';
import { spriteFrame } from '../../NPC/NPCControl';
import { videoPrefab } from './videoPrefab';
const { ccclass, property } = _decorator;

@ccclass('newsItem')
export class newsItem extends Component {
    @property(Node)
    btnNewsOff:Node = null;

    @property(Node)
    btnNewsOn:Node = null;

    @property(Node)
    contentsLayout:Node = null;

    @property(Label)
    lblNewsTitle:Label = null;

    @property(Prefab)
    newsImageNode:Prefab = null;

    @property(Node)
    imgVideoContent:Node = null;

    @property(Prefab)
    videoPrefab:Prefab = null;

    protected onLoad(): void {

    }
    _data = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data){
        this._data = data;
        this.lblNewsTitle.string = "";
        this.btnNewsOff.getComponentInChildren(Label).string = data.date;
        this.btnNewsOn.getComponentInChildren(Label).string = data.date;
        if(data.newsTitle){
            this.lblNewsTitle.node.active = true;
            this.lblNewsTitle.string = data.newsTitle;
            let cfgBundle = assetManager.getBundle("newsCfg");
            cfgBundle.load("image/" + data.imgName +"/spriteFrame" ,SpriteFrame,(err,spr:SpriteFrame)=>{
                if(err){
                    console.log("news image error" + err);
                }
            })
        }
        else{
            this.lblNewsTitle.node.active = false;
        }
        if(data.imgNameArr.length > 0){
            data.imgNameArr.forEach(element => {
                let cfgBundle = assetManager.getBundle("newsCfg");
                cfgBundle.load("image/" + element +"/spriteFrame" ,SpriteFrame,(err,spr:SpriteFrame)=>{
                    if(err){
                        console.log("news image error" + err);
                        return;
                    }
                    let newsImageNode = instantiate(this.newsImageNode);
                    newsImageNode.getComponent(Sprite).spriteFrame = spr;
                    this.contentsLayout.addChild(newsImageNode);
                })
            });
        }
        if(data.videoNameArr.length > 0){
            let cfgBundle = assetManager.getBundle("newsCfg");
            cfgBundle.load("video/" + data.videoNameArr[0] +"/spriteFrame" ,SpriteFrame,(err,spr:SpriteFrame)=>{
                if(err){
                    console.log("news image error" + err);
                    return;
                }
                this.imgVideoContent.active = true;
                this.imgVideoContent.getComponent(Sprite).spriteFrame = spr;
            }) 
        }
        this.btnNewsOff.active = false;
        this.btnNewsOn.active = true;
    }
    onBtnNewsOff(){
        // this.btnNewsOff.active = false;
        // this.btnNewsOn.active = true;
        // if(this.lblNewsTitle.string && this.lblNewsContent.string){
        //     this.contentsLayout.active = true;
        // }
    }
    onBtnNewsOn(){
        // this.btnNewsOff.active = true;
        // this.btnNewsOn.active = false;
        // this.contentsLayout.active = false;
    }
    onBtnVideoPlay(){
        let videoPrefabNode = instantiate(this.videoPrefab);
        videoPrefabNode.getComponent(videoPrefab).initData(this._data.videoNameArr[0]);
        let canvas = director.getScene().getChildByName("Canvas");
        canvas.addChild(videoPrefabNode);
    }

}


