import { _decorator, Component, director, dragonBones, KeyCode, Label, Node, TiledLayer, tween, Tween, UITransform, v2, v3, Vec2, Vec3 } from 'cc';
import { GameScene } from '../game/scene/GameScene';
import { GlobalConfig } from '../game/config/GlobalConfig';
const { ccclass, property } = _decorator;

@ccclass('PlayerManager')
export class PlayerManager extends Component {
    @property(Node)
    playerNode:Node = null;

    _tileMapLayer:Node = null;
    speakNode:Node = null;
    speakNodeEx:Node = null;
    _playerData = null;
    _status = null;
    _curTile = null;
    _id = null;
    _isMoving = false;
    _contentIndex = 0;
    _isSelf = false;
    protected onLoad(): void {
        this.initData()
    }

    protected onDestroy(): void {
        this.speakNode.active = false;
        this.speakNodeEx.active = false;
    }
    start() {
        this.setIdleStatus(KeyCode.KEY_S);
    }

    update(deltaTime: number) {
        let mapScript = director.getScene().getComponentInChildren(GameScene).getMapScript();
        let npcWorldPos =  this.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
        let speakLayer  = mapScript.speakLayer
        let targetPos = speakLayer.getComponent(UITransform).convertToNodeSpaceAR(npcWorldPos);
        if(this.speakNode){
            this.speakNode.setPosition(targetPos)
        }
        if(this.speakNodeEx){
            this.speakNodeEx.setPosition(targetPos);
        }
    }

    initData(){
        this._tileMapLayer = this.node.parent.parent;
    }

    onBtnClose(){
        
    }

    initPlayerData(data){
        //console.log("playerData=====" + JSON.stringify(data))
        this._playerData = data;
        this._id = this._playerData.id;
        //this.playerNode.getComponentInChildren(Label).string = this._playerData.name;
        this.checkSpeakObj();
        if(GlobalConfig.instance.isStoryModel){
            this.playerNode.children[0].active = false;
            return;
        }
        if(data.userNo == GlobalConfig.instance.LoginData.data.player.playerId){
            GlobalConfig.instance.playerCreateTime =  new Date().getTime();
            GlobalConfig.instance.playerEndTime = data.endTime;
            this._isSelf = true;
        }

    }

    
    //设置静止状态朝向
    setIdleStatus(dir:number = null){
        if(!dir){
            switch(this._status){
                case "up":
                    this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "up";
                    break;
                case "down":
                    this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "down";
                    break;  
                case "left":
                    this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "left";
                    break;
                case "right":
                    this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "right";
                    break;        
            }
            this._status = "idle"
            Tween.stopAllByTarget(this.playerNode);
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("standby",0);
            return;
        }
        this._status = "idle"
        Tween.stopAllByTarget(this.playerNode);
        if(dir == KeyCode.KEY_S){
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "down";
            // this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "right";
            
        }
        else if(dir == KeyCode.KEY_W){
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "up";
            //this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "right";
            
        }
        else if(dir == KeyCode.KEY_A){
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "left"
        }
        else if(dir == KeyCode.KEY_D){
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "right";
        }
        this.playerNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("standby",0);
    }

    //向上运动动画播放
    setUpAnimation(){
        if(this._status != "up"){
            this._status = "up";
            Tween.stopAllByTarget(this.playerNode);
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "up";
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("walk",0);
        }
    }

            //向下运动动画播放
    setDownAnimation(){
        if(this._status != "down"){
            this._status = "down";
            Tween.stopAllByTarget(this.playerNode);
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "down";
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("walk",0);
        }
    }

    //向左运动动画播放
    setLeftAnimation(){
        if(this._status != "left"){
            this._status = "left";
            Tween.stopAllByTarget(this.playerNode);
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "left";
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("walk",0);
        }
    }

    //向右运动动画播放
    setRightAnimation(){
        if(this._status != "right"){
            this._status = "right";
            Tween.stopAllByTarget(this.playerNode);
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).armatureName = "right";
            this.playerNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("walk",0);
        }
    }

        //聊天框绑定
    checkSpeakObj(){
        let mapScript = director.getScene().getComponentInChildren(GameScene).getMapScript();
        if(!mapScript){
            console.log("mapScript find error")
            return;
        }
        if(!this.speakNode){
            this.speakNode = mapScript._npcSpeakObj["speak_" + this._id];
            console.log(this._id + "finish init")
        }
        if(!this.speakNodeEx){
            this.speakNodeEx = mapScript._npcSpeakObj["speakEx_" + this._id];
            console.log(this._id + "finish init")
        }
    }

    //根据路径移动
    serverPathMove(path:any){
        //console.log("serverpath========" + JSON.stringify(path));
        Tween.stopAllByTarget(this.node);
        let sequence = [];
        let prePos = this.node.getPosition()
        let height = this._tileMapLayer.getComponent(UITransform).contentSize.height;
        for (let i = 1; i < path.length; i++) {
            let npcTile =  this._getTilePos(v2(path[i].x * 32, height - (path[i].y*32)));
            let actionPosition = this._tileMapLayer.getChildByName("building").getComponent(TiledLayer).getPositionAt(npcTile);
            // actionPosition.x += tileSize.width / 2;
            // actionPosition.y += tileSize.width / 2;
            sequence.push(tween(this.node)
            .to(0.18, {position: new Vec3(actionPosition.x, actionPosition.y)}).call(()=>{
                let steepKey: KeyCode
                if(prePos.x < actionPosition.x){
                    steepKey = KeyCode.KEY_D
                    this.setRightAnimation();
                }if(prePos.x > actionPosition.x){
                    steepKey = KeyCode.KEY_A
                    this.setLeftAnimation();
                }if(prePos.y < actionPosition.y){
                    this.setUpAnimation();
                }if(prePos.y > actionPosition.y){
                    steepKey = KeyCode.KEY_S
                    this.setDownAnimation();
                }
                this._curTile = this._getTilePos(this.node.getPosition())
                prePos.x = actionPosition.x
                prePos.y = actionPosition.y
            }))
        }
        if(sequence.length > 0){
            this._isMoving = true;
        }
        tween(this.node).sequence(...sequence).call(()=>{
            console.log("22222222");
            this.setIdleStatus();
            this._isMoving = false;
        }).start()
    }

    //根据坐标划算NPC所在格子(输入坐标参考系锚点为地图左上角)
    _getTilePos (posInPixel:{x:number, y:number}) {
        const mapSize = this._tileMapLayer.getComponent(UITransform).contentSize;       
        const x = Math.floor(posInPixel.x / 32);
        const y = Math.floor((mapSize.height - posInPixel.y) / 32);
        return new Vec2(x, y - 1);
    }

           //说话行为
    speak(str:string){
        this.checkSpeakObj();
        const content = str;
        this.speakNodeEx.active = false;
        Tween.stopAllByTarget(this.speakNode);
        this.speakNode.getComponentInChildren(Label).string = "";
        this.speakNode.active = true;
        this._contentIndex = 0;
        tween(this.speakNode).repeat(content.length,tween(this.speakNode).delay(0.1).call(()=>{
            if(!this.speakNode){
                return;
            }
            if(content[this._contentIndex]){
                if(this.speakNode.getComponentInChildren(Label).string.length < 260){
                    this.speakNode.getComponentInChildren(Label).string = this.speakNode.getComponentInChildren(Label).string + content[this._contentIndex];
                }
                else{
                    this.speakNode.getComponentInChildren(Label).string = "" + content[this._contentIndex];
                }
                this._contentIndex++
            }
        }).start()).start()

        let endTime = 3 + content.length * 0.1
        tween(this.speakNode).delay(endTime).call(()=>{
            if(!this.speakNode){
                return;
            }
            this.speakNode.active = false;
        }).start()
    }

    onBtnTest(){
        console.log("myTile=====" + JSON.stringify(this._curTile));
    }
}


