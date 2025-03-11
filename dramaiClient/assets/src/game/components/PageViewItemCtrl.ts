import { _decorator, Component, Node } from 'cc';
import { forEach } from 'lodash-es';
const { ccclass, property } = _decorator;

@ccclass('PageViewItemCtrl')
export class PageViewItemCtrl extends Component {
    @property({type:[Node]})
    public itemarr:Node[] = [];
    start() {

    }


    public updateData(da:any[]){
        
        for(let i = 0;i<this.itemarr.length;i++){
            if(da[i]){
                this.itemarr[i].active = true;
            }else{
                this.itemarr[i].active = false;
            }
            this.itemarr[i].emit("update_data",da[i]);
            // this.itemarr[i].getComponent(GoumaiZZItem).updateData(da[i]);
        }
    }
    update(deltaTime: number) {
        
    }
    public onDestroy(){
        forEach( this.itemarr,function(value){
            value.destroy();
        })
    }
}

