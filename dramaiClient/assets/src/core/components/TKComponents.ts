 
import { _decorator, Component, Node,Animation, Sprite, UIOpacity, Color, CCBoolean, tween, Vec4, Vec3, CCFloat } from 'cc';
import { EventType } from '../../EventType';
import { uiMgr } from '../../game/App';
const { ccclass, property } = _decorator;

@ccclass('TKComponents')
export class TKComponents extends Component {
   @property({ type: CCBoolean, displayName: '是否播动效'})
   /**
    * 是否播放动画
    */
   isplayAni  = true;
   @property({  })
   /**
    * 是否点击背景关闭
    */
   isShapeClose  = false;
   @property({  })
   /**
    * 是否有背景遮罩
    */
   isShowShape  = false;
   

   @property({ type: CCFloat, displayName: '弹窗动效', tooltip: '1.缩放;' })
   public actionType: number = 1;

   @property({ type: CCFloat, displayName: '动效时长', tooltip: '动效时长' })
   public actionTime: number = 0.3;

   @property({ type: CCFloat, displayName: '初始缩放', tooltip: '初始缩放：弹窗动效为 1 时生效' })
   public startScale: number = 0.8; 

   @property({ type: CCFloat, displayName: '动效对象'})
   public tweenTarget : Node = null;

   
   start () {
       // [3]
       let s = this;
       
   }

   onEnable(){
       let s = this;
       if(s.isplayAni){
           this.tweenTarget = this.tweenTarget ||  s.node.getChildByName("container") as Node;
          if (!this.tweenTarget) {
              return;
          }
          this.showAni();
       }
       if(!s.isShowShape){
           let spr_bg = s.node.getChildByName("spr_bg") as Node;
           // spr_bg.color = "#0C0B0B9F";?
           spr_bg.getComponent(Sprite).color = new Color(12,11,11,0);
           // spr_bg.node.addComponent(UIOpacity);
           // const opacityComp = spr_bg.getComponent(UIOpacity);
           // opacityComp.opacity = 0;
       }else{

       }
       if(s.isShapeClose){
           let spr_bg = s.node.getChildByName("spr_bg") ;
           spr_bg.on(Node.EventType.TOUCH_START,s.closeHandler,s);
       }
   }

   showAni() {

       const dialogTween = tween(this.node)
       
       switch (this.actionType) {
           case 1:
               this.node.scale = new Vec3(this.startScale, this.startScale, this.startScale);
               dialogTween.to(this.actionTime, {
                   scale : new Vec3(1, 1, 1)
               }, {
                   easing : 'backOut'
               })
               break;
           default:
               break;
       }

       dialogTween.start();
   }
   /**
    * close 关闭
    * @param e 
    */
   private closeHandler(e,evtdata = null){
       let s = this;
       uiMgr.removeParent(s.node.name);
   }
}

/**
* [1] Class member could be defined like this.
* [2] Use `property` decorator if your want the member to be serializable.
* [3] Your initialization goes here.
* [4] Your update function goes here.
*
* Learn more about scripting: https://docs.cocos.com/creator/3.0/manual/en/scripting/
* Learn more about CCClass: https://docs.cocos.com/creator/3.0/manual/en/scripting/ccclass.html
* Learn more about life-cycle callbacks: https://docs.cocos.com/creator/3.0/manual/en/scripting/life-cycle-callbacks.html
*/