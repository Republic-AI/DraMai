import { _decorator, assetManager, Component, Node, Sprite, SpriteFrame, Tween, tween } from 'cc';
import { GlobalConfig } from '../config/GlobalConfig';
import { observer } from '../App';
import { EventType } from '../../EventType';
const { ccclass, property } = _decorator;

@ccclass('furnitureNode')
export class furnitureNode extends Component {
    @property(String)
    type:String = "";

    _nowId = 0;
    _endTime = null;
    _recordTime = 0;
    protected onLoad(): void {
        observer.on(EventType.CHANGESCENEITEM,this.changeSceneItem,this);
        observer.on(EventType.POPCHANGEITEM,this.popChangeItem,this);
        observer.on(EventType.CLOSEPOPCHANGE,this.closeChangeItem,this);
    }

    start() {
        this.node.children.forEach(node=>{
            node.active = false;
        })
    }

    protected onDestroy(): void {
        observer.off(EventType.CHANGESCENEITEM,this.changeSceneItem,this);
        observer.off(EventType.POPCHANGEITEM,this.popChangeItem,this);
        observer.off(EventType.CLOSEPOPCHANGE,this.closeChangeItem,this);
    }

    update(deltaTime: number) {
        
    }

    onBtnClick(target,CustomData){
        
    }

    setItemFrame(itemID,endTime:number = 0){
        this._nowId = itemID;
        this._endTime = endTime;
        this._recordTime = new Date().getTime();
        Tween.stopAllByTarget(this.node);
        let bundle = assetManager.getBundle("game_map"+ GlobalConfig.instance.chooseScene);
        if(bundle){
            bundle.load("tilemap_map" + GlobalConfig.instance.chooseScene + "/tilemap_map" + GlobalConfig.instance.chooseScene+"/image/"+this.type+"_"+itemID+"/spriteFrame",(err,spr:SpriteFrame)=>{
                if(err){
                    console.log("loadScene error" + err)
                    return;
                }
                else{
                    this.node.getComponent(Sprite).spriteFrame = spr;
                }
            })
        }
    }

    calLeftTime(){
        if(this._endTime){
            let nowTime = new Date().getTime();
            let durTime = nowTime - this._recordTime;
            let leftTime = this._endTime - durTime;
            if(leftTime > 0){
                return leftTime;
            }
            else{
                return 0;
            }
        }
        else{
            return 0;
        }
    }

    changeSceneItem(data){
        console.log("data.data=======" + JSON.stringify(data.data));
        let netWorkData = data.data.data;
        if(this.type == netWorkData.type){
            this.setItemFrame(netWorkData.furnitureMsgData.id,netWorkData.furnitureMsgData.endTime);
        }
    }

    popChangeItem(data){
        if(data.data.type == this.type){
            this.node.children.forEach(node=>{
                node.active = true;
            })
        }
    }

    closeChangeItem(){
        this.node.children.forEach(node=>{
            node.active = false;
        })
    }
}


