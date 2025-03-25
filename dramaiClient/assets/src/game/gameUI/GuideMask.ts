import { _decorator, Component, Graphics, UITransform, Color, Input, Vec2, Node, view } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('GuideMask')
export class GuideMask extends Component {

    private graphics: Graphics = null!;
    private screenWidth: number = 0;
    private screenHeight: number = 0;

    onLoad() {
        // 获取屏幕尺寸
        const visibleSize = view.getVisibleSize();
        this.screenWidth = visibleSize.width;
        this.screenHeight = visibleSize.height;

        const uiTrans = this.node.addComponent(UITransform);
        uiTrans.width = this.screenWidth;
        uiTrans.height = this.screenHeight;

        this.graphics = this.node.addComponent(Graphics);

        // 阻止点击穿透
        //this.node.on(Input.EventType.TOUCH_START, (e) => e.stopPropagation(), this);
    }

    /**
     * 外部调用，传入目标 Node
     */
    public updateHoleByTarget(targetNode: Node) {
        const targetUI = targetNode.getComponent(UITransform);
        const worldPos = targetNode.getWorldPosition();
    
        // 获取 Canvas
        const canvas = this.node.scene.getChildByName('Canvas')!;
        const canvasUI = canvas.getComponent(UITransform)!;
    
        // 1️⃣ 先将 worldPos 转换到 Canvas 坐标
        const canvasPos = canvasUI.convertToNodeSpaceAR(worldPos);
    
        // 2️⃣ 再将 Canvas 坐标转换到 GuideNode 的本地坐标
        const guideLocalPos = this.node.getComponent(UITransform)!.convertToNodeSpaceAR(canvasPos);
    
        console.log('Target World Pos:', worldPos);
        console.log('Canvas Local Pos:', canvasPos);
        console.log('GuideNode Local Pos:', guideLocalPos);
        console.log('Target Size:', targetUI.width, targetUI.height);
    
        // 绘制遮罩
        this.drawMaskWithRect(guideLocalPos.x, guideLocalPos.y, targetUI.width, targetUI.height);
    }
    

    drawMaskWithRect(centerX: number, centerY: number, width: number, height: number) {
        this.graphics.clear();
    
        // 全屏遮罩
        this.graphics.fillColor = new Color(0, 0, 0, 200);
        this.graphics.fillRect(-this.screenWidth / 2, -this.screenHeight / 2, this.screenWidth, this.screenHeight);
    
        // 镂空矩形（补偿坐标偏移）
        this.graphics.fillColor = new Color(0, 0, 0, 0);
        const rectX = centerX - width / 2;
        const rectY = centerY - height / 2;
    
        console.log(`镂空矩形: (${rectX}, ${rectY}), 尺寸: (${width}, ${height})`);
    
        this.graphics.fillRect(rectX, rectY, width, height);

        console.log('Screen Size:', this.screenWidth, this.screenHeight);
        console.log('最终镂空矩形:', rectX, rectY);
    }
    
}
