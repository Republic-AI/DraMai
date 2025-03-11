import { _decorator, assetManager, Component, Label, Node, Sprite, SpriteFrame } from 'cc';
import { spriteFrame } from '../../NPC/NPCControl';
const { ccclass, property } = _decorator;

@ccclass('newsItem')
export class newsItem extends Component {
    @property(Node)
    btnNewsOff:Node = null;

    @property(Node)
    btnNewsOn:Node = null;

    @property(Node)
    contentsLayout:Node = null;

    @property(Sprite)
    imgNews:Sprite = null;

    @property(Label)
    lblNewsTitle:Label = null;

    @property(Label)
    lblNewsContent:Label = null;
    protected onLoad(): void {

    }
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(data){
        this.lblNewsTitle.string = "";
        this.lblNewsContent.string = "";
        this.btnNewsOff.getComponentInChildren(Label).string = data.date;
        this.btnNewsOn.getComponentInChildren(Label).string = data.date;
        if(data.newsContent && data.newsTitle){
            this.lblNewsTitle.string = data.newsTitle;
            this.lblNewsContent.string = data.newsContent;
            let cfgBundle = assetManager.getBundle("newsCfg");
            cfgBundle.load("image/imgNews_" + data.id +"/spriteFrame" ,SpriteFrame,(err,spr:SpriteFrame)=>{
                if(err){
                    console.log("news image error" + err);
                }
                if(this.imgNews){
                    this.imgNews.spriteFrame = spr;
                }
            })
        }
        this.btnNewsOff.active = false;
        this.btnNewsOn.active = true;
        if(this.lblNewsTitle.string && this.lblNewsContent.string){
            this.contentsLayout.active = true;
        }
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
}


