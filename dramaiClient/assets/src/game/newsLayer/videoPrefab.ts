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
        // 添加视频错误事件监听
        this.videoPlayer.node.on('error', this.onVideoError, this);
        // 初始化按钮状态
        //this.updateBtnState();
    }

    update(deltaTime: number) {
        // 实时更新按钮状态
        this.updateBtnState();
    }

    initData(data) {
        // 添加错误处理
        try {
            let cfgBundle = assetManager.getBundle("newsCfg");
            if (!cfgBundle) {
                // 如果 bundle 不存在，先加载 bundle
                assetManager.loadBundle("newsCfg", (err, bundle) => {
                    if (err) {
                        console.log("Failed to load newsCfg bundle:", err);
                        this.onVideoError(err);
                        return;
                    }
                    // bundle 加载成功后加载视频
                    this.loadVideoFromBundle(bundle, data);
                });
                return;
            }

            this.loadVideoFromBundle(cfgBundle, data);
        } catch (error) {
            console.log('Error loading video:', error);
            this.onVideoError(error);
        }
        AudioManager.instance.stop();
    }

    private loadVideoFromBundle(bundle, data) {
        // 检查资源路径
        const videoPath = `video/${data}`;
        console.log("Loading video from path:", videoPath);

        bundle.load(videoPath, VideoClip, (err, asset: VideoClip) => {
            if (err) {
                console.log("News video error:", err);
                console.log("Failed path:", videoPath);
                this.onVideoError(err);
                return;
            }

            if (!this.node || !this.node.parent) {
                console.warn("Node is destroyed before video loaded");
                return;
            }

            try {
                this.videoPlayer.clip = asset;
                this.videoPlayer.play();
                this.btnOperate.node.active = true;
                this.updateBtnState();
            } catch (error) {
                console.log("Error playing video:", error);
                this.onVideoError(error);
            }
        });
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

    onVideoError(event) {
        console.log('Video loading error:', event);
        if (event.message) {
            console.log('Error message:', event.message);
        }
        if (event.stack) {
            console.log('Error stack:', event.stack);
        }
        // 隐藏操作按钮
        this.btnOperate.node.active = false;
        
        // TODO: 可以在这里添加错误提示UI
        // this.showErrorUI("视频加载失败，请稍后重试");
    }
}



