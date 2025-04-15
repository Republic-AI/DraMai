import { _decorator, Component, dragonBones, KeyCode, Node, Sprite, SpriteFrame } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('testScene_2')
export class testScene_2 extends Component {
    @property(dragonBones.ArmatureDisplay)
    npcNode:dragonBones.ArmatureDisplay = null;

    @property(Node)
    itemNode:Node = null;

    @property(SpriteFrame)
    itemSpriteFrame:SpriteFrame[] = [];

    protected onLoad(): void {
        
    }

    start() {

    }

    update(deltaTime: number) {
        
    }
    NpcID = 0;

    _inde = 0

    onBtnTest1(){
        this.itemNode.active = false;
        this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("down_standby",0);
    }

    onBtnTest2(){
        this.itemNode.active = true;
        this.itemNode.getComponent(Sprite).spriteFrame = this.itemSpriteFrame[0];
        this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("send",0);
    }

    onBtnTest3(){
        this.itemNode.active = true;
        this.itemNode.getComponent(Sprite).spriteFrame = this.itemSpriteFrame[1];
        this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("send",0);
    }


    //设置静止状态朝向
    // setIdleStatus(dir:number){
    //     if(dir == KeyCode.KEY_S){
    //         if(Number(this.NpcID) == 10010){
    //             this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("right_standby",0);
    //         }
    //         else{
    //             this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("up_standby",0);
    //         }
    //     }
    //     else if(dir == KeyCode.KEY_W){
    //         this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("down_standby",0);
    //     }
    //     else if(dir == KeyCode.KEY_A){
    //         this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("left_standby",0);
    //     }
    //     else if(dir == KeyCode.KEY_D){
    //         this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("right_standby",0);
    //     }
    // }

    // setUpAnimation(){
    //     this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("up_walk",0);
    // }

    // setDownAnimation(){
    //     this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("down_walk",0);
    // }

    // //向左运动动画播放
    // setLeftAnimation(){
    //     this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("left_walk",0);
    // }
    
    // //向右运动动画播放
    // setRightAnimation(){
    //     this.npcNode.getComponent(dragonBones.ArmatureDisplay).playAnimation("right_walk",0);
    // }


    
}


