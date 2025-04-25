import { _decorator, Component, Node, director, RenderTexture, Camera, Texture2D, view, UITransform, gfx, screen } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('ScreenshotUploader')
export class ScreenshotUploader extends Component {
    @property(Camera)
    camera: Camera | null = null; // 用于截图的 Camera，必须在场景中绑定

    // 截图并上传到服务器
    async takeScreenshotAndUpload(serverUrl: string) {
        if (!this.camera) {
            console.log("Camera 未绑定！");
            return;
        }

        // 创建 RenderTexture
        const renderTexture = new RenderTexture();
        renderTexture.initialize({
            width: view.getVisibleSize().width,
            height: view.getVisibleSize().height,
        });

        this.camera.targetTexture = renderTexture;
        // 将 Camera 的输出设置为 RenderTexture
        //this.camera.targetTexture = renderTexture;
        // 创建 Texture2D 以保存截图数据
        // const texture = new Texture2D();
        // texture.reset({
        //     width: renderTexture.width,
        //     height: renderTexture.height,
        //     format: renderTexture.getPixelFormat(),
        // });

        // 从 RenderTexture 读取像素数据
        // this.camera.targetTexture.reset({
        //     width: view.getVisibleSize().width,
        //     height: view.getVisibleSize().height,
        // })
        const pixels = renderTexture.readPixels();
        if (!pixels) {
            console.log("无法读取像素数据");
            return;
        }
        const width = view.getVisibleSize().width;
        const height = view.getVisibleSize().height;
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        if (!ctx) {
            console.log("无法获取 Canvas 上下文");
            return null;
        }

        // const imageData = ctx.createImageData(width, height);
        // let rowBytes = width * 4;
        // for (let row = 0; row < height; row++) {
        //     const srow = height - 1 - row; // 需要垂直翻转
        //     const start = srow * width * 4;
        //     const end = start + rowBytes;
        //     imageData.data.set(new Uint8ClampedArray(pixels.buffer.slice(start, end)), row * rowBytes);
        // }
        const imgData = new ImageData(new Uint8ClampedArray(pixels.buffer), width, height);
        ctx.putImageData(imgData, 0, 0);
        //ctx.putImageData(imageData, 0, 0);
        // 将像素数据上传到服务器
        // const byteArray = new Uint8Array(pixels.buffer);
        // this.uploadToServer(byteArray, serverUrl);
        this.savePixelsAsImage(pixels, renderTexture.width, renderTexture.height, "test123.png");
        return;
        // 转成 Blob
        canvas.toBlob((blob) => {
        // 5. 上传截图数据
        this.uploadBlob(blob);
    }, 'image/png');
    }

    // 将字节流上传到服务器
    async uploadToServer(byteArray: Uint8Array, serverUrl: string) {
        try {
            const blob = new Blob([byteArray], { type: 'application/octet-stream' });

            const formData = new FormData();
            //formData.append('file', blob, 'screenshot.png'); // 假设服务器接受文件名为 'file'
            console.log("byteArray===" + JSON.stringify(byteArray));
            const response = await fetch(serverUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/octet-stream', // 指定内容类型为二进制流
                },
                body: blob,
            });

            if (response.ok) {
                console.log("截图上传成功！");
            } else {
                console.log("截图上传失败:", await response.text());
            }
            //const file = new File([blob], filename, { type: blob.type });
        } catch (error) {
            console.log("上传过程中发生错误:", error);
        }
    }

    uploadBlob(blob) {
        const formData = new FormData();
        formData.append('file', blob, 'screenshot.png'); // 文件名为 screenshot.png

        // 使用 XMLHttpRequest 上传
        const xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://192.168.0.100:9000/upload-image', true);
        xhr.onload = ()=>{
            if (xhr.status === 200) {
                console.log('Upload Success:', xhr.responseText);
            } else {
                console.log('Upload Failed:', xhr.responseText);
            }
            this.camera.targetTexture = null;
        };
        xhr.send(formData);
    }

    private savePixelsAsImage(pixels: Uint8Array, width: number, height: number, filename: string) {
        // 创建 Canvas
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;

        // 获取 Canvas 的 2D 上下文
        const ctx = canvas.getContext('2d');
        if (!ctx) {
            console.log("无法获取 Canvas 上下文");
            return;
        }

        // 将像素数据写入 Canvas
        const imgData = new ImageData(new Uint8ClampedArray(pixels.buffer), width, height);
        ctx.putImageData(imgData, 0, 0);

        // 将 Canvas 转换为 Base64 并下载图片
        canvas.toBlob((blob) => {
            if (!blob) {
                console.log("无法生成 Blob");
                return;
            }
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = filename || 'screenshot.png';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }, 'image/png');
    }
}
