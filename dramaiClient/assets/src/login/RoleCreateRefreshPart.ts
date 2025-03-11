import { _decorator, Component, Node } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('RoleCreateRefreshPart')
export class RoleCreateRefreshPart extends Component {

    @property({type:[Node]})
    public itemarr:Node[] = [];

    start() {

    }

    

    update(deltaTime: number) {
        
    }
}

