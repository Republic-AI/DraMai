import { _decorator, Component, Node, Label, EditBox } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('SpinBoxComponent')
export class SpinBoxComponent extends Component {
    @property(EditBox)
    textLabel: EditBox = null;//输入文本框
    @property(Number)
    min: number = 0;//最小值
    @property(Number)
    max: number = 0;//最大值
    temp: string;

    start() {

    }

    onLoad() {
        this.textLabel.node.active = true;
        this.textLabel.string = String(this.min);
    }

    update(deltaTime: number) {

    }
    up(evt ,num) {
        // 调高数字
        let temp = (Number(this.textLabel.string) + Number(num));
        if (temp > this.max) {
            temp = this.max;
        }
        
        this.textLabel.string = String(temp);
        this.node.emit("update_money",this.textLabel.string);
    }
    down(evt ,num) {
        // 调低数字
        let temp = (Number(this.textLabel.string) - Number(num));
        if (temp < this.min) {
            temp = this.min;
        }
        this.textLabel.string = String(temp);
        this.node.emit("update_money",this.textLabel.string);
    }



    inputBegin() {
        // 输入开始
        this.temp = this.textLabel.string;
    };

    inputDone() {
        // 输入结束
        if (isNaN(Number(this.textLabel.string))) {
            this.textLabel.string = this.temp;                     // 如果输入非数字则数字不变
        }
        else if (Number(this.textLabel.string) > this.max) {       // 输入数字不能超过最大值
            this.textLabel.string = String(this.max);
        }
        else if (Number(this.textLabel.string) < this.min) {       // 输入数字不能小于最小值
            this.textLabel.string = String(this.min);
        }
    }
}

