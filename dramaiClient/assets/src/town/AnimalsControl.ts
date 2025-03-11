import { _decorator, Component, Node, randomRange, randomRangeInt } from "cc";
import { PromiseUtils } from "../StaticUtils/PromiseUtils";
const { ccclass, property } = _decorator;
const offsetX = -70.5;
const offsetY = 70;
@ccclass("AnimalsControl")
export class AnimalsControl extends Component {
  @property(Node)
  public animal: Node = null;

  @property(Node)
  public animalFrame: Node = null;

  @property(Number)
  public maxX: Number = 0;

  @property(Number)
  public minX: Number = 0;

  @property(Number)
  public maxY: Number = 0;

  @property(Number)
  public minY: Number = 0;

  @property(Number)
  public upIndex: Number = 0;

  @property(Number)
  public downIndex: Number = 0;

  @property(Number)
  public leftIndex: Number = 0;

  @property(Number)
  public rightIndex: Number = 0;

  frameTime: number = 0;

  async start() {
    await PromiseUtils.wait(250);
    await this.frameSprite(0, 0, -10);
  }

  async frameSprite(
    indexY: number,
    moveOffsetX: number = 0,
    moveOffsetY: number = 0
  ) {
    for (const index of [0, 1, 2, 3]) {
      await this.setFrameMove(
        this.animalFrame,
        index,
        indexY,
        moveOffsetX,
        moveOffsetY
      );
    }
    await this.randomAction();
  }

  async setFrameMove(
    node: Node,
    indexX: number,
    indexY: number,
    moveOffsetX: number = 0,
    moveOffsetY: number = 0
  ) {
    this.judgeRange();
    this.animalFrame.setPosition(indexX * offsetX, offsetY * indexY);
    this.animal.setPosition(
      this.animal.getPosition().x + moveOffsetX,
      this.animal.getPosition().y + moveOffsetY
    );
    await PromiseUtils.wait(250);
  }

  async randomAction() {
    const type = randomRangeInt(0, 5);
    switch (type) {
      case 0:
        await this.frameSprite(Number(this.upIndex), 0, 10);
        break;
      case 1:
        await this.frameSprite(Number(this.downIndex), 0, -10);
        break;
      case 2:
        await this.frameSprite(Number(this.leftIndex), -10, 0);
        break;
      case 3:
        await this.frameSprite(Number(this.rightIndex), 10, 0);
        break;
      case 4:
        await this.frameSprite(4, 0, 0);
        break;

      default:
        break;
    }
  }

  judgeRange() {
    const pos = this.animal.getPosition();
    if (pos.x >= Number(this.maxX)) {
      this.animal.setPosition(Number(this.maxX), pos.y);
    }
    if (pos.y >= Number(this.maxY)) {
      this.animal.setPosition(pos.x, Number(this.maxY));
    }
    if (pos.x <= Number(this.minX)) {
      this.animal.setPosition(Number(this.minX), pos.y);
    }
    if (pos.y <= Number(this.minY)) {
      this.animal.setPosition(pos.x, Number(this.minY));
    }
  }

  update(deltaTime: number) {}
}
