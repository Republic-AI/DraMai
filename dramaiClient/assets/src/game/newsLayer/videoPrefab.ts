import { _decorator, assetManager, Component, Node, Sprite, SpriteFrame, VideoPlayer, VideoClip } from 'cc';
import { AudioManager } from '../../manager/AudioManager';
const { ccclass, property } = _decorator;

@ccclass('videoPrefab')
export class videoPrefab extends Component {
    @property(VideoPlayer)
    videoPlayer:VideoPlayer = null;

    @property(Sprite)
    btnOperate:Sprite = null;

    @property(SpriteFrame)
    btnOnSpriteFrame: SpriteFrame = null;

    @property(SpriteFrame)
    btnOffSpriteFrame: SpriteFrame = null;

    start() {
        // 添加按钮点击事件监听
        this.btnOperate.node.active = false
        this.btnOperate.node.on(Node.EventType.TOUCH_END, this.onBtnOperateClick, this);
        // 初始化按钮状态
        //this.updateBtnState();
    }

    update(deltaTime: number) {
        // 实时更新按钮状态
        this.updateBtnState();
    }

    initData(data){
        let cfgBundle = assetManager.getBundle("newsCfg");
        cfgBundle.load(`video/${data}`, (err, asset: VideoClip) => {
            if(err){
                console.log("news video error: " + err);
                return;
            }
            // 设置视频资源并播放
            if(!this.node || !this.node.parent){
                return;
            }
            this.videoPlayer.clip = asset;
            this.videoPlayer.play();
            // 显示操作按钮
            this.btnOperate.node.active = true;
            this.updateBtnState();
        });
        AudioManager.instance.stop();
    }

    onBtnOperateClick() {
        if (this.videoPlayer.isPlaying) {
            this.videoPlayer.pause();
        } else {
            this.videoPlayer.play();
        }
        this.updateBtnState();
    }

    updateBtnState() {
        // 根据视频播放状态更新按钮图片
        this.btnOperate.spriteFrame = this.videoPlayer.isPlaying ? this.btnOffSpriteFrame : this.btnOnSpriteFrame;
    }

    onBtnClose(){
        this.node.destroy();
    }

    protected onDestroy(): void {
        AudioManager.instance.playMusic(true);
    }
}



