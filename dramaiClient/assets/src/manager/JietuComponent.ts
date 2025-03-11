import { _decorator, Component, Node, Camera, RenderTexture, director, gfx, SpriteFrame, ImageAsset, Sprite, Texture2D, EventTouch, Vec3, Canvas } from 'cc';
import Log from '../../../assets/src/utils/LogUtils'
import { GameScene } from '../game/scene/GameScene';

const { ccclass, property } = _decorator;

const postCardSize = { width: 320, height: 700 }
const windowSize = { width: 320, height: 700 }
export const upLoadPostcard = 'upLoadPostcard'

export function base64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
        const base64 = (base64String + padding)
                    .replace(/\-/g, '+')
                    .replace(/_/g, '/');

        const rawData = window.atob(base64);
        const outputArray = new Uint8Array(rawData.length);

        for (let i = 0; i < rawData.length; ++i) {
            outputArray[i] = rawData.charCodeAt(i);
        }
        return outputArray;
}

const TAG = 'JietuComponent'
/**
 * 
 */
@ccclass('JietuComponent')
export class JietuComponent extends Component {
    @property(Camera)
    camera: Camera = null;
    @property(Canvas)
    canvas: Canvas = null;
    @property(Sprite)
    base64HeadSpr: Sprite = null;


    base64Head;
    _content = null;
    _npcID = null;
    protected _renderTex: RenderTexture = new RenderTexture();
    protected _recorder: MediaRecorder | null = null;
    protected _recordedChunks: Blob[] = [];
    private _canvas: HTMLCanvasElement;
    private _ctx: CanvasRenderingContext2D;
    start() {
        this._renderTex.reset({ width: 640, height: 640 });
        this._canvas = document.createElement('canvas');
        this._canvas.width = 640;
        this._canvas.height = 640;
        this._ctx = this._canvas.getContext('2d');
        
        // this.base64HeadSpr.node.on(Node.EventType.TOUCH_START, this.touchStartHandler, this);
        // this.base64HeadSpr.node.on(Node.EventType.TOUCH_MOVE, this.touchMOveHandler, this);
        // this.base64HeadSpr.node.on(Node.EventType.TOUCH_END, this.touchendHandler, this);
    }

    update(deltaTime: number) {

    }
    /** */
    clickCaptureHandler() {
        // console.log("123123123");
        // this.capture(windowSize, "chatLogImage");
    }

    /**生成明信片分享到TG */
    createPostcard(){
        // this.capture(postCardSize, 'postcard');
    }

    /**
     * 将camera 绘制的内容渲染到 指定的 rendertexture,对应的canvas内容将失效,不再更新新的数据;
     * @returns 
     */
    capture(content,npcID = null) {
        (window as any).global = window
        let camera = this.camera;
        camera.targetTexture = this._renderTex;
        this._content = content
        this._npcID = npcID;
        this.scheduleOnce(() => {
            //this._renderTex.reset(size)
            this.base64Head = this.copyRenderTex(this._renderTex,content);
            camera.targetTexture = null;
            //this.downloadBase64AsImage(this.base64Head, `test_temp.png`);
            // const generateUniqueId = `id_${Date.now().toString(36)}`;
            // let temp = this.base64Head.split("data:image/jpeg;base64,")[1];

            //this.node.emit(message,"data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAoCAYAAAD6xArmAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAJDSURBVHgB7VdNaxNBGH5md0sJ2mpLNUlT6qEq1KpEhGpJPVSaglUPbcAcRKxSvHgSPBXBX6BnUfCmEBDFqycPildBFPET+pE2pR9pWhLytduZKZvukm1mpjSXkgeWeWfmnWfeeebdN1lSnP9ioQ7QUCc0iBvEisRff/xGcyiCpuAAfxLvPkAWRPSCMPIL0QmYpglN05Cf+wwZCKUInzqBxw8nuc3I+wbjkIGUxlMP7qDT38HtX3+n+Sn2hJjh/eunFfvmvUdCf2liJonP18ztP/9nhf5K6Xa29zhvmdYiOZSIT/f2VOxnL9/U9CWq9ZjlM8PJnm58/5TY0W+f1wrnhbUdasGeETsvbHQ4UtNX6fKOha8jmVriNl1X01cp4vnFZd4eFsigRDx4dRKWtXW42LUhob+UFOzS+odvc2L2Wmf+fRQtkYs4On6/Eu2LJ1MyS8TErP6m19a5PRqNID42AhkYtSaZBJcuhvnTHQrwuiwLUq//bkbq28+tHejvmUXLIdOSEFLRlM/RvhO2j6+9DQcCHdB1vYqYvB27TBnYwp0Cd85t2yyQluBRnJu4gdaQvzriUr6A3YBtkVvNoFzIe84b4Vvj2z2TRqO5j+0ac9hE03HQf4RGG/Am1rs6oekEZtlytS5uxxztYXU5jQ2agu25LFrpiO5FnHj+CiowyyZy2SxvjSYDsbtx9J0/U028ns5AFXbGFAtFLMwkvYlHYldc6WWnFuvbaeUcLxVLWEymsLaShr8riP6hAc/NSePjxsYmos7ZM/x85v8AAAAASUVORK5CYII=");

           
        }, 1)
    }



    /**
     * 将rendertexture 中的内容复制出来
     * @param renderTex 
     * @returns 
     */
    copyRenderTex(renderTex: RenderTexture,content): string {

        let arrayBuffer = new ArrayBuffer(renderTex.width * renderTex.height * 4);
        let region = new gfx.BufferTextureCopy();
        region.texOffset.x = 0;
        region.texOffset.y = 0;
        region.texExtent.width = renderTex.width;
        region.texExtent.height = renderTex.height;
        region.texExtent.width = 640;
        region.texExtent.height = 640;
        let dataview = new Uint8Array(arrayBuffer, 0, arrayBuffer.byteLength);
        // console.log(dataview);[0,0,0,0,0,0,0,0,0,0,0,,,]
        //获取到纹理数据
        director.root.device.copyTextureToBuffers(renderTex.getGFXTexture(), [dataview], [region]);
        // director.root.device.copyFramebufferToBuffer(renderTex.window.framebuffer, arrayBuffer, [region]);
        // console.log(dataview);[0,0,0,255,0,0,0,255,0,0,0,255,0,0,0,255,0,0,0,255,0,0,0,255,0,0,0,255,,,,]

        return this.toB64(dataview.buffer,content);
    }

    /**
     * 通过htmlcanvas ,将纹理缓冲绘制到canvas,
     * 将canvas 上图片转换为base64 数据;
     * @param arrayBuffer 
     * @returns 
     */
    toB64(arrayBuffer: ArrayBuffer,content): string {
        let canvas = document.createElement('canvas');
        let width = canvas.width = Math.floor(640);
        let height = canvas.height = Math.floor(640);
        let ctx = canvas.getContext('2d');
        let imageU8Data = new Uint8Array(arrayBuffer);
        let rowBytes = width * 4;
        let rowBytesh = height * 4;
        for (let row = 0; row < 640; row++) {
            let sRow = height - 1 - row;
            let imageData = ctx.createImageData(width, 1);
            let start = sRow * rowBytes;
            for (let i = 0; i < rowBytes; i++) {
                imageData.data[i] = imageU8Data[start + i];
            }
            ctx.putImageData(imageData, 0, row);
        }
        var base64 = canvas.toDataURL("image/png", 0.1); //压缩语句
        canvas.toBlob((blob) => {
            // 5. 上传截图数据
            this.uploadBlob(blob);
        }, 'image/png');
        // try {
        //     localStorage.setItem('base64Head', base64)
        // } catch (error) {

        // }
        // console.log('base64', base64)
        return base64;

    }


    public copybase64toSprite(evt) {
        let img = new Image();
        let texture = new Texture2D();
        img.src = this.base64Head;
        img.onload = () => {
            texture.image = new ImageAsset(img);
            const spriteFrame = new SpriteFrame();
            spriteFrame.texture = texture;
            this.base64HeadSpr.getComponent(Sprite).spriteFrame = spriteFrame;
            this.scheduleOnce(() => {
                Log.log(TAG,this.base64HeadSpr)
            },)
        }
    }
    public canmove: boolean = false;
    public touchStartHandler(Tevt) {
        this.canmove = true;
    }

    public touchMOveHandler(Tevt: EventTouch) {
        let loca = Tevt.getLocation();
        if (this.canmove) {
            // this.base64HeadSpr.node.position = new Vec3(loca.x,loca.y);
            this.base64HeadSpr.node.worldPosition = new Vec3(loca.x, loca.y);
        }
    }
    touchendHandler(evt) {
        this.canmove = false;
    }

    downloadBase64AsImage(base64: string, filename: string) {
        // 去掉 Base64 前缀
        const base64Data = base64.replace(/^data:image\/(png|jpeg);base64,/, '');
        const byteCharacters = atob(base64Data); // 解码 Base64 数据
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
    
        // 创建 Blob 对象
        const blob = new Blob([byteArray], { type: 'image/png' });
    
        // 创建一个 <a> 标签，模拟点击下载
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = filename || 'screenshot.png';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    uploadBlob(blob) {
        const formData = new FormData();
        formData.append('file', blob, 'screenshot.png'); // 文件名为 screenshot.png
        formData.append("content",this._content);
        // 使用 XMLHttpRequest 上传
        const xhr = new XMLHttpRequest();
        xhr.open('POST', 'https://memerepublic.ai/npc/upload-image', true);
        xhr.onload = ()=>{
            if (xhr.status === 200) {
                console.log('Upload Success:', xhr.responseText);
            } else {
                console.error('Upload Failed:', xhr.responseText);
            }
            this.camera.targetTexture = null;
        };
        xhr.send(formData);
    }

    startRecording(content:string,npcID=null) {
        this._content = content;
        this._npcID =npcID;
        this.camera.targetTexture = this._renderTex;
        const stream = this._canvas.captureStream(60); // 30 FPS
        this._recorder = new MediaRecorder(stream, { mimeType: 'video/mp4; codecs="avc1.42E01E,mp4a.40.2"' });
        // this.scheduleOnce(() => {
        //     this.camera.targetTexture = null; // 渲染完成后重置
        // }, 0.1);
        // const canvas = document.createElement('canvas');
        // canvas.width = 320; // 设置宽度
        // canvas.height = 320; // 设置高度
        // const region = new gfx.BufferTextureCopy();
        // region.texOffset.x = 0;
        // region.texOffset.y = 0;
        // region.texExtent.width = renderTex.width;
        // region.texExtent.height = renderTex.height;
                // 录制视频数据
        this._recordedChunks = [];
        this._recorder.ondataavailable = (event) => {
            if (event.data.size > 0) {
                this._recordedChunks.push(event.data);
            }
        };
        // 定时将 RenderTexture 数据绘制到 Canvas 上
        const drawFrame = () => {
            try {
                const region = new gfx.BufferTextureCopy();
                region.texOffset.x = 0;
                region.texOffset.y = 0;
                region.texExtent.width = this._renderTex.width;
                region.texExtent.height = this._renderTex.height;

                const imageData = new Uint8Array(this._renderTex.width * this._renderTex.height * 4);
                director.root.device.copyTextureToBuffers(
                    this._renderTex.getGFXTexture(),
                    [imageData],
                    [region]
                );

                // // 将 RenderTexture 数据绘制到 Canvas 上
                // const imgData = new ImageData(
                //     new Uint8ClampedArray(imageData.buffer),
                //     this._renderTex.width,
                //     this._renderTex.height
                // );
                // const ctx = this._canvas.getContext('2d');

                // // 翻转 Y 轴
                // ctx.clearRect(0, 0, this._canvas.width, this._canvas.height);
                // ctx.save();
                // ctx.translate(0, this._canvas.height); // 将坐标原点移到底部
                // ctx.scale(1, -1); // Y 轴翻转
                // ctx.putImageData(imgData, 0, 0); // 正确方向绘制

                const ctx = this._canvas.getContext('2d');
                const flippedImageData = new Uint8Array(this._renderTex.width * this._renderTex.height * 4);
                for (let y = 0; y < this._renderTex.height; y++) {
                    const row = new Uint8Array(
                        imageData.buffer,
                        y * this._renderTex.width * 4,
                        this._renderTex.width * 4
                    );
                    const flippedRow = flippedImageData.subarray(
                        (this._renderTex.height - y - 1) * this._renderTex.width * 4,
                        (this._renderTex.height - y) * this._renderTex.width * 4
                    );
                    flippedRow.set(row);
                }

                // 绘制翻转后的数据到 Canvas
                const imgData = new ImageData(
                    new Uint8ClampedArray(flippedImageData.buffer),
                    this._renderTex.width,
                    this._renderTex.height
                );
                ctx.putImageData(imgData, 0, 0);

                if (this._recorder && this._recorder.state === 'recording') {
                    requestAnimationFrame(drawFrame); // 每帧调用
                }
            } catch (error) {
                console.error('Error during frame drawing:', error);
            }
        };
        // 开始录制
        this._recorder.start();
        drawFrame();

        console.log('Recording started');
    }

    stopRecording() {
        if (!this._recorder || this._recorder.state != 'recording') return;
        //this.camera.targetTexture = null; // 渲染完成后重置
        this._recorder.onstop = () => {
            const blob = new Blob(this._recordedChunks, { type: 'video/mp4' });
            // 保存到本地
            //this.downloadVideo(blob, 'recorded_video.mp4');

            // 上传到服务器
            this.uploadVideoBlob(blob);
        };

        // 停止录制
        this._recorder.stop();
        this.camera.targetTexture = null;
        console.log('Recording stopped');
    }

        /**
     * 下载视频到本地
     * @param blob 视频 Blob 对象
     * @param filename 文件名
     */
    downloadVideo(blob: Blob, filename: string) {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);

        console.log('Video saved locally as', filename);
    }

        /**
     * 上传视频到服务器
     * @param blob 视频 Blob 对象
     */
    uploadVideoBlob(blob: Blob) {
        const formData = new FormData();
        formData.append('file', blob, 'screenshotVideo.mp4'); // 文件名为 screenshot.png
        formData.append("content",this._content);
        if(this._npcID){
            formData.append("npcId",this._npcID);
        }
        // 使用 XMLHttpRequest 上传
        const xhr = new XMLHttpRequest();
        if(director.getScene().getComponentInChildren(GameScene).getVersion() == "capture"){
            xhr.open('POST', 'https://memerepublic.ai/npc/upload-image', true);
        }
        else if(director.getScene().getComponentInChildren(GameScene).getVersion() == "newcapture"){
            xhr.open('POST', 'https://memerepublic.ai/npc/upload-image-permanent', true);
        }
        xhr.onload = ()=>{
            if (xhr.status === 200) {
                console.log('Upload Success3333:', xhr.responseText);
            } else {
                console.error('Upload Failed:', xhr.responseText);
            }
            //this.camera.targetTexture = null;
        };
        xhr.send(formData);
    }

}


