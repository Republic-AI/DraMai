import { _decorator, Component, Node, Sprite, resources, SpriteFrame } from 'cc';
import { observer } from '../App';
import { EventType } from '../../EventType';
const { ccclass, property } = _decorator;

@ccclass('npcHeadPrefab')
export class npcHeadPrefab extends Component {
    @property(Sprite)
    headSprite: Sprite = null;

    public _npcId: string = null;

    private _isValid: boolean = true;

    start() {
    }

    /**
     * 初始化NPC头像
     * @param npcId NPC的ID
     */
    public initData(npcId: string) {
        this._npcId = npcId;
        this.loadHeadImage();
    }

    /**
     * 加载NPC头像图片
     */
    private loadHeadImage() {
        if (!this._npcId) return;

        const headPath = `gameUI/image/headDir_${this._npcId}`;
        resources.load(headPath + "/spriteFrame", SpriteFrame, (err, spriteFrame) => {
            if (!this._isValid) return;
            if (err) {
                console.log(`Failed to load NPC head image: ${headPath}`, err);
                return;
            }
            
            if (this.headSprite && spriteFrame) {
                this.headSprite.spriteFrame = spriteFrame;
            }
        });
    }

    update(deltaTime: number) {
    }

    onBtnClick(){
        observer.post(EventType.CHOOSENPCHEAD, this._npcId);
    }

    protected onDestroy(): void {
        this._isValid = false;
    }
}


