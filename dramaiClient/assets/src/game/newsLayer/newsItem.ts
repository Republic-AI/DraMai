import { _decorator, assetManager, Component, director, instantiate, Label, Node, Prefab, Sprite, SpriteFrame } from 'cc';
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

    private _isValid: boolean = true;

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
                if(err || !this._isValid){
                    console.log("news image error" + err);
                    return;
                }
                this.imgVideoContent.active = true;
                this.imgVideoContent.getComponent(Sprite).spriteFrame = spr;
            })
        }
        else{
            this.lblNewsTitle.node.active = false;
        }
        if (data.imgNameArr && data.imgNameArr.length > 0) {
            this.loadImagesSequentially(data.imgNameArr);
        }
        if(data.videoNameArr.length > 0){
            let cfgBundle = assetManager.getBundle("newsCfg");
            cfgBundle.load("video/" + data.videoNameArr[0] +"/spriteFrame" ,SpriteFrame,(err,spr:SpriteFrame)=>{
                if(err || !this._isValid){
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

    protected onDestroy(): void {
        this._isValid = false;
    }

    private async loadImagesSequentially(imgNameArr: string[]) {
        for (const element of imgNameArr) {
            if (!this._isValid) {
                console.log("Component destroyed, stopping image loading");
                return;
            }
            try {
                const spr = await this.loadImage(element);
                if (spr && this._isValid) {
                    let newsImageNode = instantiate(this.newsImageNode);
                    newsImageNode.getComponent(Sprite).spriteFrame = spr;
                    this.contentsLayout.addChild(newsImageNode);
                }
            } catch (err) {
                console.log("Failed to load image:", element, err);
            }
        }
    }

    private loadImage(element: string): Promise<SpriteFrame> {
        return new Promise((resolve, reject) => {
            if (!this._isValid) {
                reject(new Error("Component destroyed"));
                return;
            }

            let cfgBundle = assetManager.getBundle("newsCfg");
            if (!cfgBundle) {
                reject(new Error("newsCfg bundle not found"));
                return;
            }
            
            cfgBundle.load("image/" + element + "/spriteFrame", SpriteFrame, (err, spr: SpriteFrame) => {
                if (!this._isValid) {
                    reject(new Error("Component destroyed"));
                    return;
                }
                if (err) {
                    console.log("news image error:", err);
                    reject(err);
                    return;
                }
                resolve(spr);
            });
        });
    }

}


