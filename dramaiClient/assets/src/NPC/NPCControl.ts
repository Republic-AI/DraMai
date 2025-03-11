import { _decorator, Camera, Component, director, Event, EventKeyboard, input, Input, js, KeyCode, macro, Node, sys, System, SystemEvent, TiledLayer, TiledMap, Tween,tween, UITransform, v3, Vec2, Vec3, view, View } from 'cc';
import { NPCDirect, NPCDisplayInfo } from '../StaticUtils/KeyCodeUtils';
import WebUtils from '../utils/WebUtils';
import {  NPCPartDisplay } from '../StaticUtils/NPCConfig';
import { network } from '../model/RequestData';
import { socket } from '../game/App';
import { AStar } from './AStar';
import { GlobalConfig } from '../game/config/GlobalConfig';
const { ccclass, property } = _decorator;
export const distance = -256 //每一组人物组成部分的间隔
export const spriteFrame = 32 //每一帧的距离
export const originRootX = -5.463
export const frameSpeed = 0.05

export interface NPCPartBaseDataMap {
    body: number,
    hair: {
        sexy: 'man' | 'woman',
        index: number
    },
    pants: number,
    shirt: number
}

@ccclass('NPCControl')
export class NPCControl extends Component {
    @property(Node)
    public npcRoot!: Node;
    @property(Node)
    public npc!: Node;

    @property(Node)
    public hair!: Node;

    @property(Node)
    public pants!: Node;

    @property(Node)
    public shirt!: Node;

    @property(Node)
    public body!: Node;

    @property(Camera)
    public camera!: Camera;

    @property(Node)
    public building!: Node;

    @property(Node)
    public bubble!: Node;

    @property(Node)
    public canvas!: Node;

    keyDown: boolean = false            //是否处于按下状态
    currentDirectKey!: KeyCode          //当前按下的按键是那个
    stepIndex: number = 0

    frameAdd = 0
    aStarPath = []
    rightPos: Vec2 = new Vec2()
    rightDirect: KeyCode
    NpcID: number
    _curTile = new Vec2();
    //npc外形部分
    npcBasePartInfo: NPCPartBaseDataMap = {
        body: 0,
        hair: {
            sexy: 'man',
            index: 0
        },
        pants: 0,
        shirt: 0
    }

    //当前选择的人物的服装信息
    npcDisplayInfo: NPCDisplayInfo = {
        body: 0,
        hair: 0,
        pants: 0,
        shirt: 0,
    }     
    npcIndex: any;
    
    start() {
       //View.instance.setOrientation(macro.ORIENTATION_LANDSCAPE)
        this.setNPCDisplayColor()
        this.setNPCDisplayBase()
    }

    update(deltaTime: number) {
        // this.frameAdd = this.frameAdd + deltaTime
        // if(this.keyDown && this.frameAdd > frameSpeed){
        //     this.setMoveSprite(this.currentDirectKey)
        //     this.frameAdd = 0
        // }
    }

    setNPCDisplayBase(partBaseInfo: NPCPartBaseDataMap = {
        body: 0,
        hair: {
            sexy: 'man',
            index: 0
        },
        pants: 0,
        shirt: 1
    }){
        this.npcBasePartInfo = partBaseInfo
        //WebUtils.getResouceImg(NPCPartDisplay.body[this.npcBasePartInfo.body], this.body)
        // WebUtils.getResouceImg(NPCPartDisplay.hair[this.npcBasePartInfo.hair.sexy][this.npcBasePartInfo.hair.index], this.hair)
        // WebUtils.getResouceImg(NPCPartDisplay.shirt[this.npcBasePartInfo.shirt], this.shirt)
        // WebUtils.getResouceImg(NPCPartDisplay.pants[this.npcBasePartInfo.pants], this.pants)
    }

    setNPCDisplayColor(npcDisplayInfo: NPCDisplayInfo = {
        body: 0,
        hair: 0,
        pants: 0,
        shirt: 0
    }){
        this.npcDisplayInfo = npcDisplayInfo
        this.body.setPosition(this.npcDisplayInfo.body * distance, 0)
        this.hair.setPosition(this.npcDisplayInfo.hair * distance, 0)
        this.pants.setPosition(this.npcDisplayInfo.pants * distance, 0)
        this.shirt.setPosition(this.npcDisplayInfo.shirt * distance, 0)
    }

    setNPCMoveDirect(key: KeyCode){
        this.npcRoot.setPosition(this.npcRoot.getPosition().x, NPCDirect[key].directPos)
    }

    setNPCPosition(key: KeyCode){
        try {
            const block = this?.building?.getComponent(TiledLayer)?.getTileGIDAt(this._curTile.x + NPCDirect[key].offsetX, this._curTile.y - NPCDirect[key].offsetY) as unknown as any
            if(block){
                return 
            }
            const gameView = view.getVisibleSize();
            this._curTile.x = this._curTile.x + NPCDirect[key].offsetX
            this._curTile.y = this._curTile.y - NPCDirect[key].offsetY
            const pos = this.building.getComponent(TiledLayer).getPositionAt(this._curTile)!;
            NPCDirect[key]
            const originPos = this.npc.getPosition()
            //this.npc.setPosition(originPos.x + NPCDirect[key].offsetX, originPos.y + NPCDirect[key].offsetY)
            this.npc.setPosition(pos.x, pos.y)
            console.log('=========',this.camera.node.getPosition())
            if(this.npcIndex === GlobalConfig.instance.currentNpcIndex && this.canvas.getScale().x === 2){

            }
            // if(this.camera){
            //     this.camera.node.setPosition(this.npc.getPosition().x - gameView.width / 2,  - ((hight - this.npc.getPosition().y) - gameView.height + 100))
            // }
            // if(this.camera){
            //     if((this.npc.getPosition().x > view.getVisibleSize().x && this.npc.getPosition().x < hight - view.getVisibleSize().x)){
            //         this.camera.node.setPosition(this.npc.getPosition().x, this.camera.node.getPosition().y)
            //     }
            //     if((this.npc.getPosition().y > view.getVisibleSize().y && this.npc.getPosition().y < hight - view.getVisibleSize().y)){
            //         this.camera.node.setPosition(this.camera.node.getPosition().x, this.npc.getPosition().y)
            //     }
            //     let json = new network.NPCMoveRequest();
            //     json.data.npcId = this.NpcID
            //     json.data.x = this.npc.getPosition().x,
            //     json.data.y = this.npc.getPosition().y,
            //     json.command = 10006;
            //     json.type = 1;
            //     socket.sendWebSocketBinary(json);
            // }
        } catch (error) {
            
        }

    }

    setMoveSprite(key: KeyCode){
        if(this.stepIndex++ > 6){
            this.stepIndex = 0
            this.npcRoot.setPosition(originRootX ,this.npcRoot.getPosition().y)
        }else{
            this.stepIndex = this.stepIndex++
            this.npcRoot.setPosition(this.npcRoot.getPosition().x - spriteFrame, this.npcRoot.getPosition().y)
        }
        if(this.camera){
            this.setNPCPosition(key)
        }
    }

    _getTilePos (posInPixel:{x:number, y:number}) {
        const mapSize = this.building.parent.getComponent(UITransform).contentSize;       
        const x = Math.floor(posInPixel.x / 32);
        const y = Math.floor((mapSize.height - posInPixel.y) / 32);
        return new Vec2(x, y - 1);
    }

    aStartMove(start, finish, cb?: ()=>void){
        console.log("start========" + JSON.stringify(start));
        console.log("finish=======" + JSON.stringify(finish));
        this.aStarPath = this.node.getComponent(AStar).moveToward(start, finish);
        console.log("astarPath======" + JSON.stringify(this.aStarPath));
        if (this.aStarPath.length <= 1) {
            cb && cb()
            return;
        }
        // for (let i = 0; i < this._paths.length; ++i) {
        //     this._debugDraw(this._paths[i], this._debugTileColor, i);
        // }

        let sequence = [];
        let prePos = this.npc.getPosition()
        let tileSize = this.building.parent.getComponent(TiledMap).getTileSize();
        for (let i = 1; i < this.aStarPath.length; ++i) {
            let actionPosition = this.building.getComponent(TiledLayer).getPositionAt(this.aStarPath[i]);
            // actionPosition.x += tileSize.width / 2;
            // actionPosition.y += tileSize.width / 2;
            sequence.push(tween(this.npc)
            .to(0.1, {position: new Vec3(actionPosition.x, actionPosition.y)}).call(()=>{
                let steepKey: KeyCode
                if(prePos.x < actionPosition.x){
                    steepKey = KeyCode.KEY_D
                }if(prePos.x > actionPosition.x){
                    steepKey = KeyCode.KEY_A
                }if(prePos.y < actionPosition.y){
                    steepKey = KeyCode.KEY_W
                }if(prePos.y > actionPosition.y){
                    steepKey = KeyCode.KEY_S
                }
                this.setNPCMoveDirect(steepKey)
                this.setMoveSprite(steepKey)
                this._curTile = this._getTilePos(this.node.parent.getPosition())
                prePos.x = actionPosition.x
                prePos.y = actionPosition.y
            }))
        }
        tween(this.npc).sequence(...sequence).call(()=>{
            cb && cb()
        }).start()
    }

    serverPathMove(path:any){
        //console.log("serverpath========" + JSON.stringify(path));
        let sequence = [];
        let prePos = this.npc.getPosition()
        let height = this.building.parent.getComponent(UITransform).contentSize.height;
        for (let i = 1; i < path.length; ++i) {
            let npcTile =  this._getTilePos(
                new Vec2(path[i].x * 32, height - (path[i].y*32))
              );
            let actionPosition = this.building.getComponent(TiledLayer).getPositionAt(npcTile);
            // actionPosition.x += tileSize.width / 2;
            // actionPosition.y += tileSize.width / 2;
            sequence.push(tween(this.npc)
            .to(0.2, {position: new Vec3(actionPosition.x, actionPosition.y)}).call(()=>{
                let steepKey: KeyCode
                if(prePos.x < actionPosition.x){
                    steepKey = KeyCode.KEY_D
                }if(prePos.x > actionPosition.x){
                    steepKey = KeyCode.KEY_A
                }if(prePos.y < actionPosition.y){
                    steepKey = KeyCode.KEY_W
                }if(prePos.y > actionPosition.y){
                    steepKey = KeyCode.KEY_S
                }
                this.setNPCMoveDirect(steepKey)
                this.setMoveSprite(steepKey)
                this._curTile = this._getTilePos(this.node.parent.getPosition())
                prePos.x = actionPosition.x
                prePos.y = actionPosition.y
            }))
        }
        tween(this.npc).sequence(...sequence).call(()=>{

        }).start()
    }

    setCanvasInfo(canvas:Node){
        this.canvas = canvas;   
    }

    setCameraInfo(camera:Camera){
        this.camera = camera;
    }

    setBuildingInfo(building:Node){
        this.building = building
    }

    showBubble(bubbleType:string){
        this.bubble.active = true;
        WebUtils.getResouceImg(
            bubbleType,
            this.bubble
          );
        Tween.stopAllByTarget(this.bubble)
        tween(this.bubble).repeatForever(tween(this.bubble).to(0.2,{scale:v3(1.2,1.2,1)}).to(0.2,{scale:v3(1,1,1)}).start()).start()
    }

    hideBubble(){
        this.bubble.active = false;
        Tween.stopAllByTarget(this.bubble)
    }

}

