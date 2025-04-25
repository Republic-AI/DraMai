type redpoint = {
    key: string,
    rednode: Node,
    childkey?: string[]
}
import { _decorator, Component, Node } from 'cc';
import { network } from '../model/RequestData';
import { observer } from '../game/App';
export default class RedManager {


    static _instance: RedManager;
    static KEY = {
        renwutabred: "renwutabred",
        renwutabred_main: "renwutabred_main",
    }
    static get instance() {
        if (this._instance) {
            return this._instance;
        }
        this._instance = new RedManager();
        return this._instance;
    }
    public keystate = {};
    public redpoint: any = {};
    registRedPoint(key: string, rednode: Node, childkey: string[] = null) {
        this.redpoint[key] = rednode;
    }
    changeState(key: string, state: boolean) {
        let node = this.redpoint[key];
        if (node) {
            node.active = state;
        }
        // if(this.redpoint[key].childkey){
        //     for (let index = 0; index < this.redpoint[key].childkey.length; index++) {
        //         const element = this.redpoint[key].childkey[index];
        //         if(element == key){
        //             node.active = state;
        //             break;
        //         }
        //     }
        // }

    }
    setkeyState(arg0: string, tasklist: any) {
        let state: boolean = false;
        if (arg0 == RedManager.KEY.renwutabred) {//任务列表数据刷新
            let da = (tasklist as network.taskListResponse)?.data;
            if (da && Array.isArray(da)) {
                for (let index = 0; index < da.length; index++) {
                    const element = da[index];
                    if (element?.status == 2) {
                        state = true;
                        break;
                    }
                }
            }
            this.keystate[RedManager.KEY.renwutabred_main] = state;
        }
        this.keystate[arg0] = state;
        observer.post("keychange", arg0);
    }
    getKeyState(key: string): boolean {
        return this.keystate[key];
    }
}