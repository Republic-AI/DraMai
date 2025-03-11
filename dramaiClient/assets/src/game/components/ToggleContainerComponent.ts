import { _decorator, Component, Node, Toggle } from 'cc';
import { observer } from '../App';
const { ccclass, property } = _decorator;

@ccclass('ToggleContainerComponent')
export class ToggleContainerComponent extends Component {
    start() {

    }

    update(deltaTime: number) {

    }
    onToggleContainerClick(toggle: Toggle) {
        console.log(`触发了 ToggleContainer 事件，点了${toggle.node.name}的 Toggle`);
        observer.post("toggle", {type:toggle.node.name});
    }
}

