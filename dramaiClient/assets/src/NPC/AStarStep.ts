import { _decorator, Component, Vec2, Node } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('AStarStep')
export class AStarStep extends Component {
    @property(Vec2)
    public position: Vec2 = new Vec2();

    @property
    public g: number = 0;

    @property
    public h: number = 0;


    @property
    public f: number = 0;

    @property({
        type: Number
    })

    @property(AStarStep) // 假设 AStarStep 也被导出为一个组件类型
    public last: AStarStep = null;

    constructor(...args: any[]) {
        super();
        if (args.length > 0 && args[0] instanceof Vec2) {
            this.position = args[0];
        }
    }

    public equalTo(other: AStarStep): boolean {
        return other instanceof AStarStep && this.position.equals(other.position);
    }

    public toString(): string {
        return `(position: ${this.position}, g: ${this.g}, h: ${this.h}, f: ${this.f})`;
    }
}