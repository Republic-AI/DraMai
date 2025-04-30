import { _decorator, Component, Node } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('webEventManager')
export class webEventManager extends Component {
    protected onLoad(): void {
        
    }
    start() {
        // console.log("webEventManager start");
        // window.addEventListener('message', (event) => {
        //     // 安全检查：确认消息来源
        //     if (event.origin !== window.location.origin) {
        //       console.warn(' sam Test Received message from unknown origin:', event.origin);
        //       return;
        //     }
            
        //     // 处理消息
        //     const message = event.data;
        //     console.log('sam Test Received message from parent:' + JSON.stringify(message));
            
        //     // switch (message.type) {
        //     //   case 'INIT_SCENE':
        //     //     // 初始化场景
        //     //     const sceneId = message.data.sceneId;
        //     //     initGameScene(sceneId);
        //     //     break;
                
        //     //   case 'TEST_ACTION':
        //     //     // 执行测试动作
        //     //     performAction(message.data.action);
        //     //     break;
                
        //     //   // 更多消息类型处理...
        //     // }
        //   });
          
        //   // 游戏加载完成后通知父窗口
        //   function notifyGameLoaded() {
        //     window.parent.postMessage({ type: 'GAME_LOADED' }, window.location.origin);
        //   }
          
        //   // 发送游戏事件到父窗口
        //   function sendGameEvent(eventData) {
        //     window.parent.postMessage({ 
        //       type: 'GAME_EVENT', 
        //       data: eventData 
        //     }, window.location.origin);
        //   }
          
        //   // 游戏初始化后调用
        //   notifyGameLoaded();
          
        //   // 游戏中某些事件发生时调用
        //   // 例如：玩家进入新区域
        //   sendGameEvent({ 
        //     eventName: 'PLAYER_ENTER_ZONE', 
        //     zoneName: 'forest', 
        //     timestamp: Date.now() 
        //   });

    }

    update(deltaTime: number) {
        
    }
}


