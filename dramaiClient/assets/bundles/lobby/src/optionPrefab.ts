import { _decorator, assert, assetManager, Button, Component, Node, Sprite, SpriteFrame } from 'cc';
import { observer } from '../../../src/game/App';
const { ccclass, property } = _decorator;

@ccclass('optionPrefab')
export class optionPrefab extends Component {
    @property(Node)
    imgChooseStatus_2:Node = null;

    _sceneId = null;
    protected onLoad(): void {
        observer.on("ChooseBtnScene",this.onChooseClick,this);
    }

    protected onDestroy(): void {
        observer.off("ChooseBtnScene",this.onChooseClick,this);
    }

    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(id){
        this._sceneId = id;
        console.log("Option id =====" + id);
        let lobbyBundle = assetManager.getBundle("lobby")
        lobbyBundle.load("image/btnScene_" + id +"_1/spriteFrame",(error,spr:SpriteFrame)=>{
            if(error){
                console.log("load btnscene error" + error)
                return;
            }
            else{
                this.node.getComponent(Sprite).spriteFrame = spr;
            }
        })
        lobbyBundle.load("image/btnScene_" + id +"_2/spriteFrame",(error,spr:SpriteFrame)=>{
            if(error){
                console.log("load btnscene error" + error)
                return;
            }
            else{
                this.imgChooseStatus_2.getComponent(Sprite).spriteFrame = spr;
            }
        })
    }

    setUnChooseStatus(){
        this.node.getComponent(Button).interactable = true;
        this.imgChooseStatus_2.active = false;
    }

    setChooseStatus(){
        this.node.getComponent(Button).interactable = false;
        this.imgChooseStatus_2.active = true;
    }

    onBtnClick(){
        observer.post("ChooseBtnScene",this._sceneId);
    }

    onChooseClick(data){
        console.log("chooseClick" + data.data);
        if(Number(data.data) == Number(this._sceneId)){
            this.setChooseStatus()
        }
        else{
            this.setUnChooseStatus();
        }
    }
}


