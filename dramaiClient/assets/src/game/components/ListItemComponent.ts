import { _decorator, Component, Node, Label } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('ListItemComponent')
export class ListItemComponent extends Component {

    @property(Label)
    public index_label:Label = null;
    start() {

    }

    /**
     * 更新数据
     */
    public setData(data:any = null){
        this.node.emit("setdata",data);
    }

    update(deltaTime: number) {
        
    }
}

