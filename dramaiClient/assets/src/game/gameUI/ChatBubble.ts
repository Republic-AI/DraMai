import { _decorator, Component, Node, Label, UITransform, Sprite, Size } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('ChatBubble')
export class ChatBubble extends Component {
    @property(Label)
    label: Label = null!;

    @property(Node)
    background: Node = null!;  // 背景节点

    // 文字的边距
    private paddingX: number = 20;
    private paddingY: number = 10;
    private maxWidth: number = 490; // 聊天气泡最大宽度

    onLoad() {
        if (!this.label || !this.background) {
            console.error("Label 或 Background 未正确绑定！");
            return;
        }
    }

    /**
     * 设置聊天气泡的内容
     * @param text 需要显示的文本
     */
    setText(text: string) {
        this.label.string = text;

        // 强制更新 Label 以获取正确的尺寸
        this.scheduleOnce(() => {
            this.updateBubbleSize();
        }, 0);
    }

    /**
     * 更新气泡尺寸
     */
    private updateBubbleSize() {
        let labelTransform = this.label.node.getComponent(UITransform)!;
        let backgroundTransform = this.background.getComponent(UITransform)!;

        // 获取 Label 的当前尺寸
        let labelWidth = labelTransform.width;
        let labelHeight = labelTransform.height;

        // 限制最大宽度
        if (labelWidth > this.maxWidth) {
            this.label.overflow = Label.Overflow.RESIZE_HEIGHT; // 让 Label 高度自适应
            labelTransform.width = this.maxWidth; // 限制最大宽度
            this.label.updateRenderData(); // 重新渲染文本
        }

        // 重新获取调整后的尺寸
        labelWidth = labelTransform.width;
        labelHeight = labelTransform.height;

        // 根据 Label 的大小调整背景的尺寸
        let newSize = new Size(labelWidth + this.paddingX * 2, labelHeight + this.paddingY * 2);
        backgroundTransform.setContentSize(newSize);
    }
}
