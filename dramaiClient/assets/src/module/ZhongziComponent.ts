import { _decorator, Component, Node } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('ZhongziComponent')
export class ZhongziComponent extends Component {
    @property({type:[Node]})
    public itemarr:Node[] = [];
    _data: any;
    start() {
        this.node.on("setdata",this.setData,this);
    }


    public setData(data: any) {
        if(!data){
            return
        }
        this._data = data;
        for(let i = 0;i<this.itemarr.length;i++){
            if(data[i]){
                this.itemarr[i].active = true;
            }else{
                this.itemarr[i].active = false;
            }
            this.itemarr[i].emit("update_data",data[i]);
            // this.itemarr[i].getComponent(GoumaiZZItem).updateData(da[i]);
        }
    }
    update(deltaTime: number) {
        
    }

    onDestroy(){
        this.node.off("setdata");

    }
}

