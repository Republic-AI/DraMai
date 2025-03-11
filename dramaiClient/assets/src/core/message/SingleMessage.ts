import { Label ,Sprite,resources, SpriteFrame, UITransform, view, tween} from "cc";
    export default class SingleMessage {


        // constructor(bg: Sprite, word: Sprite) {
        //     let self = this;
        //     self._bgCon = bg;
        //     self._wordCon = word;view.getCanvasSize
        //     self.img_bg = new Sprite();
        //     resources.load('common/msgbg.png',SpriteFrame,(err,spr:SpriteFrame)=>{
        //         self.img_bg.spriteFrame = spr;
        //         // self.img_bg.sizeGrid="13,32,12,36,0";
        //     })
            
        //     self.lbl_message = new Label();
        //     self.img_bg.node.getComponent(UITransform).setContentSize(100,110)// = self.lbl_message.width = 380;
        //     self.img_bg.getComponent()
        //     self.lbl_message.fontSize = 24;
        //     self.lbl_message.color = '0xffffff';
        //     self.lbl_message.align = 'center';
        //     self.lbl_message.valign = 'middle';
        //     self.lbl_message.wordWrap = true;
        //     // self.lbl_message.lineSpacing = 3;
        // }

        // private _x: number;
        // private _y: number;

        // public get x(): number {
        //     let self = this;
        //     return self._x;
        // }
        // public set x(value: number) {
        //     let self = this;
        //     self._x = value;
        //     // if(self.img_bg.stage)
        //     // {
        //     self.img_bg.x = value;
        //     self.lbl_message.x = value;
        //     // }
        // }
        // public get y(): number {
        //     let self = this;
        //     return self._y;
        // }
        // public set y(value: number) {
        //     let self = this;
        //     self._y = value;
        //     // if(self.img_bg.stage)
        //     // {
        //     // self.img_bg.y = value;
        //     // self.lbl_message.y = value;
        //     self.img_bg.y = value;
        //     self.lbl_message.y = value + ((self.img_bg.height - self.lbl_message.height) / 2);
        //     // }
        // }

        // public startTweenAlpha() {
        //     let self = this;
        //     self.img_bg.alpha = 1;
        //     self.lbl_message.alpha = 1;
        //     self._bgCon.addChild(self.img_bg);
        //     self._wordCon.addChild(self.lbl_message);
        //     tween.to(self.img_bg,0.5,{alpha: 0,delay:1.5});
        //     TweenMax.to(self.lbl_message,0.5,{alpha: 0,delay:1.5,onComplete:()=>{
        //         self.tweenEnd();
        //     }})
        // }

        // private tweenEnd() {
        //     let self = this;
        //     if (self._bgCon.contains(self.img_bg)) {
        //         self._bgCon.removeChild(self.img_bg);
        //     }
        //     if (self._wordCon.contains(self.lbl_message)) {
        //         self._wordCon.removeChild(self.lbl_message);
        //     }
        //     delMsg(this);
        // }

        // /**
        //  * 设置显示文本
        // */
        // public set messageStr($str: string) {
        //     let self = this;
        //     self._messageStr = $str;
        //     // self.lbl_message.textFlow = HtmlUtil.getHtmlStr($str);
        //     self.lbl_message.text = $str;
        //     self.img_bg.height = self.lbl_message.height + 40;
        // }

    }