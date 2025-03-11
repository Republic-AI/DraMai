import { _decorator, Component, Node, Label, EventTouch ,Animation} from 'cc';
import { uiMgr } from '../App';
const { ccclass, property } = _decorator;

@ccclass('ListItemComponent2')
export class ListItemComponent2 extends Component {
    @property(Label)
    public Label_title:Label = null;
    @property(Label)
    public Label_num:Label = null;
    @property(Node)
    public contentNode:Node = null;
    @property(Node)
    public btnsNode:Node = null;
    @property(Node)
    public Sprite_touxiang:Node = null;
    start() {
        this.node.on(Node.EventType.TOUCH_START, this.touchStart, this);
        this.node.on(Node.EventType.TOUCH_MOVE, this.touchMove, this);
        this.node.on(Node.EventType.TOUCH_END, this.touchEnd, this);
        this.node.on(Node.EventType.TOUCH_CANCEL, this.touchEnd, this);
        this.node.on("setdata",this.setData,this);
    }
    
    private touchStartX:number  = 0;
    touchStart(event: EventTouch) {
        this.touchStartX = event.touch.getLocationX();
    }
    /**
     * 是否已打开
     */
    private _hasPlaying:boolean = false;
    touchMove(event: EventTouch) {
        const currTouchX = event.touch.getLocationX();
        if( !this._hasPlaying&&(this.touchStartX - currTouchX>30)){
            this.contentNode.getComponent(Animation).play("gedanitem");
            this._hasPlaying = true;
        }
        if((this.touchStartX - currTouchX<-30)&&this._hasPlaying){
            this.contentNode.getComponent(Animation).play("gedanitem2");
            this._hasPlaying = false;
        }
    }
    touchEnd(event: EventTouch) {
    }

    /**
     * 更新数据
     */
    public setData(){
        let v =this.contentNode.getPosition();
        v.x= -344;
        this.contentNode.setPosition(v);
        this._hasPlaying = false;
    }

    /**
     * 
     * @param evt 
     */
    public openGedanXiangqing(evt){
        uiMgr.popToParent("prefabs/pre_gequlist");
    }
    update(deltaTime: number) {
        
    }
}

