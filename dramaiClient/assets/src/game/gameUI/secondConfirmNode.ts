import { _decorator, Component, Label, Node } from 'cc';
import { sceneItem } from '../../../bundles/lobby/src/sceneItem';
import { network } from '../../model/RequestData';
import { observer, socket } from '../App';
import { EventType } from '../../EventType';
const { ccclass, property } = _decorator;

@ccclass('secondConfirmNode')
export class secondConfirmNode extends Component {
    @property(Label)
    lblPrice:Label = null;

    @property(Label)
    lblTime:Label = null;

    @property(Label)
    lblDes:Label = null;

    _data = null;
    _type = null;
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(type,data){
        this._data = data 
        this._type = type;
        if(type == "sceneItem"){
            this.lblPrice.string = data.price;
            this.lblTime.string = data.time;
            this.lblDes.string = data.info;
        }
        else if(type == "changeSkin"){
            this.lblPrice.string = data.price;
            this.lblTime.string = data.time;
            this.lblDes.string = data.info;
        }
    }   

    onBtnClose(){
        this.node.active = false;
        observer.post(EventType.CLOSEPOPCHANGE);
    }

    onBtnConfirm(){
        if(this._type == "sceneItem"){
            let json = new network.GetAllNPCRequest();
            json.command = 10111;
            json.type = 1;
            json["data"] = {
                furnitureId:this._data.id,
            }
            socket.sendWebSocketBinary(json);
        }
        else if(this._type == "changeSkin"){
            let json = new network.GetAllNPCRequest();
            json.command = 10108;
            json.type = 1;
            json["data"] = {
                npcId:this._data.npcId,
                dressId:this._data.id,
            }
            socket.sendWebSocketBinary(json);
        }

        this.node.active = false;
        observer.post(EventType.CLOSEPOPCHANGE);
    }
}


