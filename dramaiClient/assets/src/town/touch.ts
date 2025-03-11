import { _decorator, Component, EventTouch, Node, ScrollView, SystemEvent, Vec2, Vec3 } from 'cc';
import { GlobalConfig } from '../game/config/GlobalConfig';
const { ccclass, property } = _decorator;

@ccclass('touch')
export class touch extends Component {
    @property(Node)
    scrollNode: Node

    @property(Node)
    canvas: Node

    npcIndex: number = 0
    start() {
        this.node.on(Node.EventType.TOUCH_START, (event)=>{
            const scroll = this.scrollNode.getComponent(ScrollView)

        })

        this.node.on(Node.EventType.TOUCH_END, (event: EventTouch)=>{
            const startPos = event.touch.getStartLocation()
            const prePos = event.touch.getPreviousLocation()
            if(Math.abs(startPos.y - prePos.y) >  Math.abs(startPos.x - prePos.x)){
                if(startPos.y > prePos.y){
                    const scroll = this.scrollNode.getComponent(ScrollView)
                    scroll.scrollToOffset(new Vec2(scroll.getScrollOffset().x,scroll.getScrollOffset().y - 930), 1, true)
                }
                if(startPos.y < prePos.y){ 
                    const scroll = this.scrollNode.getComponent(ScrollView)
                    scroll.scrollToOffset(new Vec2(scroll.getScrollOffset().x,scroll.getScrollOffset().y + 930), 1, true)
                }
            }else{
                if(startPos.x > prePos.x){
                    console.log('====left')
                    if(this.canvas.scale.x === 2){
                        this.canvas.setScale(1,1)
                    }
                }
                if(startPos.x < prePos.x){
                    console.log('====right')
                    if(this.canvas.scale.x === 1){
                        this.canvas.setScale(2,2)
                    }
                }
            }
        })
    }

    scrollEvent(event, data){
        let preOffset
        let curOffset
        if(data === 12){
            const scroll = this.scrollNode.getComponent(ScrollView)
            preOffset = scroll.getScrollOffset().y
            console.log('====', scroll.getScrollOffset().y)
        }

        if(data === 9){
            const scroll = this.scrollNode.getComponent(ScrollView)
            curOffset = scroll.getScrollOffset().y
            console.log('====', scroll.getScrollOffset().y)
        }
        if(preOffset < curOffset){
            GlobalConfig.instance.currentNpcIndex = GlobalConfig.instance.currentNpcIndex + 1
            this.npcIndex = GlobalConfig.instance.currentNpcIndex
        }else{
            GlobalConfig.instance.currentNpcIndex = GlobalConfig.instance.currentNpcIndex - 1
            this.npcIndex = GlobalConfig.instance.currentNpcIndex
        }
    }

    scrollUp(){

    }

    scrollDown(){

    }

    scrollLeft(){

    }

    scrollRight(){

    }

    update(deltaTime: number) {
        
    }
}

