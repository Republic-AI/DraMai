import { _decorator, Component, Node, director, Label } from 'cc';
import { modelMgr } from '../App';
const { ccclass, property } = _decorator;

@ccclass('LoadingController')
export class LoadingController extends Component {
    start() {
        this._templetDic = {};
    }

    update(deltaTime: number) {

    }

    static _ins: LoadingController;
    private _templetDic: object;
    private time ;

    public static get ins(): LoadingController {
        let self = this;
        if (self._ins == null) {
            self._ins = new LoadingController();
        }
        return self._ins;
    }


    showLoading(timeoutCall = null) {
        let scene = director.getScene().getChildByName("Canvas");
        if (scene && scene.getChildByName('loadingNode')) {
            let loadingnode = scene.getChildByName('loadingNode');
            let txt = loadingnode.getChildByName("Label");
            if (txt) {
                txt.getComponent(Label).string = modelMgr.configModel.getStrById(10015);
            }
            loadingnode.setSiblingIndex(999);

            scene.getChildByName('loadingNode').active = true;
            clearTimeout(this.time);
            this.time = setTimeout(() => {
                timeoutCall && timeoutCall();
                this.hideLoading();
            }, 10000);
        }
    }

    hideLoading() {
        let scene = director.getScene().getChildByName("Canvas");
        if (scene && scene.getChildByName('loadingNode')) {
            scene.getChildByName('loadingNode').active = false;
            clearTimeout(this.time);
        }
    }
}

