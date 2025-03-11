import { _decorator, assetManager, Component, director, Label, Node, Sprite, SpriteFrame, Tween, tween, UITransform } from 'cc';
import { GlobalConfig } from '../config/GlobalConfig';
import { observer, socket } from '../App';
import { network } from '../../model/RequestData';
import { GameScene } from '../scene/GameScene';
import { furnitureNode } from './furnitureNode';
import WebUtils from '../../utils/WebUtils';
import { EventType } from '../../EventType';
const { ccclass, property } = _decorator;

@ccclass('changeSceneItem')
export class changeSceneItem extends Component {
    @property(Sprite)
    imgItem:Sprite = null;

    @property(Node)
    timeNode:Node = null;

    @property(Node)
    priceNode:Node = null;

    _data = null;
    start() {

    }

    update(deltaTime: number) {

    }

    
    initData(data){
        this._data = data;
        console.log("itemData=====" + JSON.stringify(data));
        let bundle = assetManager.getBundle("game_map"+ GlobalConfig.instance.chooseScene);
        if(bundle){
            bundle.load("tilemap_map" + GlobalConfig.instance.chooseScene + "/tilemap_map" + GlobalConfig.instance.chooseScene+"/image/"+data.type+"_"+data.id+"/spriteFrame",(err,spr:SpriteFrame)=>{
                if(err){
                    console.log("loadScene error" + err)
                    return;
                }
                else{
                    this.imgItem.spriteFrame = spr;
                    if(this.imgItem.node.getComponent(UITransform).contentSize.width > this.node.getComponent(UITransform).contentSize.width){
                        let itemSize = this.imgItem.node.getComponent(UITransform).contentSize
                        this.node.getComponent(UITransform).setContentSize(itemSize);
                    }
                }
            })
        }
        this.priceNode.getComponentInChildren(Label).string = data.price;
        director.getScene().getComponentsInChildren(furnitureNode).forEach(itemScript=>{
            if(itemScript.type == this._data.type){
                if(itemScript._nowId == this._data.id){
                    this.priceNode.active = false;
                    this.timeNode.active = true;
                    let nowLeftTime = Math.floor(itemScript.calLeftTime()/1000);
                    let timeStr = WebUtils.formatDateTime(nowLeftTime);
                    this.timeNode.getComponentInChildren(Label).string = timeStr;
                    Tween.stopAllByTarget(this.node);
                    tween(this.node).repeatForever(tween(this.node).delay(1).call(()=>{
                        let nowLeftTime = Math.floor(itemScript.calLeftTime()/1000);
                        let timeStr = WebUtils.formatDateTime(nowLeftTime);
                        this.timeNode.getComponentInChildren(Label).string = timeStr;
                        if(nowLeftTime <= 0){
                            Tween.stopAllByTarget(this.node);
                            this.timeNode.active = false;
                            this.priceNode.active = true;
                        }
                    }).start()).start()
                }
            }
        })

    }

    onBtnClick(){
        console.log("clickChangeSceneItem");
        if(this.timeNode.active){
            WebUtils.showToast("Cannot replace furniture in use");
            return;
        }
        //director.getScene().getComponentInChildren(GameScene).cancelFollowNpc();
        observer.post(EventType.POPCHANGEITEM,this._data);
    }
}


