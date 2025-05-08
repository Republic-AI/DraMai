import { _decorator, Component, Label, Node } from 'cc';
import { ListViewCtrl2 } from '../game/components/listViewCtrl2';
import { modelMgr, observer } from '../game/App';
import { EventType } from '../EventType';
import { LoadingController } from '../game/components/LoadingController';
import Log from '../../src/utils/LogUtils'
import { NPCPartDisplayInfo } from '../StaticUtils/NPCConfig';
import loginModel from '../model/loginModel';
const { ccclass, property } = _decorator;

const TAG = 'rolePartSelectTool'

export const rolePartIndex = [
    'body',
    'hair',
    'shirt',
    'pants'
]

@ccclass('rolePartSelectTool')
export class rolePartSelectTool extends Component {
    @property(Node)
    pre_listView: Node = null;

    _data: any

    start() {
        LoadingController.ins.hideLoading();
        // modelMgr.mainModel.checkcatReq();
        //observer.on(EventType.SOCKET_SHOP_ITEM_LIST, this.updateEnjoyToolsHandler, this);
        observer.on(EventType.UPDATE_ROLE_PLAY_INDEX, this.setDataStoreId, this);
        // observer.on(EventType.SOCKET_SHOP_ITEM_LIST, (data)=>{
        //     console.log(TAG, data)
        // }, this);
        //modelMgr.mainModel.getShopList()
        //this.updateEnjoyToolsHandler();
        this.splitHair()
        this.updateEnjoyToolsHandler(this._data)
        loginModel.currentType = loginModel.hair
        loginModel.currentName = 'hair'
    }

    protected onDestroy(): void {
        //observer.off(EventType.SOCKET_SHOP_ITEM_LIST, this.updateEnjoyToolsHandler, this);
        observer.off(EventType.UPDATE_ROLE_PLAY_INDEX, this.setDataStoreId, this);
    }

    splitHair(){
    //     let roleInfoArray = []
    //     const iconHair = [...NPCPartDisplayInfo['hair']['icon']['man'], ...NPCPartDisplayInfo['hair']['icon']['woman']]
    //     const frameHair = [...NPCPartDisplayInfo['hair']['frame']['man'], ...NPCPartDisplayInfo['hair']['frame']['woman']]
    //     iconHair.forEach((element, index)=>{
    //         roleInfoArray.push(
    //             {
    //                 icon:iconHair[index],
    //                 frame: frameHair[index],
    //             }
    //         )
    //    })
       this._data = NPCPartDisplayInfo.hair

    }

    setDataStoreId(partIndex: any){
        let roleInfoArray = []
        loginModel.currentType = loginModel[rolePartIndex[partIndex.data]]
        loginModel.currentName = rolePartIndex[partIndex.data]
        if(partIndex.data === 1){
            this.splitHair()
        }else{

        //    NPCPartDisplayInfo[rolePartIndex[partIndex.data]].icon.forEach((element, index)=>{
        //         roleInfoArray.push(
        //             {
        //                 icon:NPCPartDisplayInfo[rolePartIndex[partIndex.data]].icon[index],
        //                 frame: NPCPartDisplayInfo[rolePartIndex[partIndex.data]].frame[index],
        //             }
        //         )
        //    })
           this._data = NPCPartDisplayInfo[rolePartIndex[partIndex.data]]
        }
        
        this.updateEnjoyToolsHandler(this._data)
    }

    private updateEnjoyToolsHandler(data){
        this._data = data
        let current_storeId_arr = [];
        // (this._data.data.data as CatEnjoyToolsData[]).forEach(element => {
        //     if(element.storeId === this.current_storeId){
        //         current_storeId_arr.push(element)
        //     }
        // });
        console.log(TAG, this._data)
        this.showZhongziList(this._data);
    }
    protected onEnable(): void {
        //排序逻辑
        // let arr: any[] = modelMgr.configModel.commonConfigJson.Cat;
        // this.showZhongziList(arr);
    }
    /**
* 
*/
    public showZhongziList(arr) {
        // if(!arr.length){
        //     this.nullTxt.node.active = true
        // }else{
        //     this.nullTxt.node.active = false
        // }
        let len = Math.ceil(arr.length / 3);
        let grpArr = [];
        for (let i = 0; i < len; i++) {
            let temparr = [];
            for (let j = 0; j < 3; j++) {
                arr[(i) * 3 + j] && temparr.push(arr[(i) * 3 + j]);
            }
            grpArr.push(temparr);
        }
        if (this.pre_listView) {
            let ctrl = this.pre_listView.getChildByName("listctrl");
            let spawnCount = grpArr.length;
            ctrl.getComponent(ListViewCtrl2).initData(spawnCount, grpArr);
        }
    }

    update(deltaTime: number) {

    }
}


