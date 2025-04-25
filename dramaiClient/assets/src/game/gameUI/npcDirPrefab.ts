import { _decorator, assetManager, Component, Label, Node, resources, Sprite, SpriteFrame } from 'cc';
import { observer } from '../App';
import { EventType } from '../../EventType';
const { ccclass, property } = _decorator;

@ccclass('npcDirPrefab')
export class npcDirPrefab extends Component {
    @property(Node)
    imgDir:Node = null;

    @property(Sprite)
    imgNpcHead:Sprite = null;

    _npcId:number = null;
    private _isValid: boolean = true;

    start() {

    }

    update(deltaTime: number) {
        
    }

    protected onDestroy(): void {
        this._isValid = false;
    }

    initData(npcID:number){
        this._npcId = npcID;
        resources.load("gameUI/image/headDir_" + this._npcId + "/spriteFrame",SpriteFrame,(error,spr:SpriteFrame)=>{
            if (!this._isValid) return;
            if(error){
                console.log(error);
            }
            else{
                this.imgNpcHead.spriteFrame = spr;
            }
        })
    }

    onBtnClick(){
        console.log("npcId=======" + this._npcId);
        observer.post(EventType.TRANSPORT,this._npcId);
    }
}


