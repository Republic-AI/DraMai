import { _decorator, Camera, Component, EventKeyboard, input, Input, KeyCode, Node } from 'cc';
import { NPCControl, originRootX } from './NPCControl';
import _ from 'lodash';
const { ccclass, property } = _decorator;

@ccclass('PlayerControl')
export class PlayerControl extends Component {

    @property(Node)
    public player!: Node;

    @property(Node)
    public createRoleInfoNode!: Node;

    start() {
        input.on(Input.EventType.KEY_DOWN, (event: EventKeyboard)=>{
            if(this.createRoleInfoNode.active || !_.includes([KeyCode.KEY_A, KeyCode.KEY_W, KeyCode.KEY_S, KeyCode.KEY_D], event.keyCode)){
                return
            }
            this.player.getComponent(NPCControl).setNPCMoveDirect(event.keyCode)
            this.player.getComponent(NPCControl).keyDown = true
            this.player.getComponent(NPCControl).currentDirectKey = event.keyCode
        })
        input.on(Input.EventType.KEY_UP, (event: EventKeyboard)=>{
            if(this.createRoleInfoNode.active){
                return
            }
            this.player.getComponent(NPCControl).keyDown = false
            this.player.getComponent(NPCControl).npcRoot.setPosition(originRootX ,this.player.getComponent(NPCControl).npcRoot.getPosition().y)
            this.player.getComponent(NPCControl).stepIndex = 0
        })
        
    }

    // update(deltaTime: number) {
    //     this.createRoleInfoNode.setPosition(this.player.getPosition().x, this.player.getPosition().y)
    // }
}

