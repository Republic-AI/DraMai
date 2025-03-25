import { _decorator, Component, Node, UITransform } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('BgFixByContent')
export class BgFixByContent extends Component {
    @property(UITransform)
    heightContent:UITransform[] = [];

    @property(UITransform)
    widthContent:UITransform[] = [];

    @property(Number)
    offsetHeight:Number = 0

    @property(Number)
    offsetWidth:Number = 0

    @property(Number)
    maxWidth:Number = 0
    start() {

    }

    update(deltaTime: number) {
        let targetHeight = 0;
        this.heightContent.forEach(content=>{
            targetHeight = content.contentSize.height + targetHeight;
        })
        let targetWidth = 0
        this.widthContent.forEach(content=>{
            let contetnSizeLimit = content.contentSize.width > 525 ? 525 : content.contentSize.width
            targetWidth = contetnSizeLimit + targetWidth;
        })
        this.node.getComponent(UITransform).setContentSize(Number(targetWidth) + Number(this.offsetWidth),targetHeight + Number(this.offsetHeight));

        let parentHeight = targetHeight + Number(this.offsetHeight)
        let parentSize = this.node.parent.getComponent(UITransform).contentSize;
        this.node.parent.getComponent(UITransform).setContentSize(parentSize.width,parentHeight);
    }
}


