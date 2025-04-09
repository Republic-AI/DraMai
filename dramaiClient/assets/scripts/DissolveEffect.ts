import { _decorator, Component, Node, Material, Sprite, CCFloat, Color, Texture2D } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('DissolveEffect')
export class DissolveEffect extends Component {
    @property(Material)
    private material: Material = null;

    @property(CCFloat)
    private dissolveTime: number = 2.0;

    @property(Color)
    private edgeColor: Color = new Color(255, 128, 0, 255);

    @property(CCFloat)
    private edgeWidth: number = 0.1;

    @property(Texture2D)
    private dissolveTexture: Texture2D = null;

    private _sprite: Sprite = null;
    private _dissolveThreshold: number = 0;
    private _isDissolving: boolean = false;
    private _elapsedTime: number = 0;

    start() {
        // 获取Sprite组件
        this._sprite = this.getComponent(Sprite);
        if (!this._sprite) {
            console.error('DissolveEffect: No Sprite component found!');
            return;
        }

        // 初始化材质
        if (this.material) {
            // 创建材质实例
            const matInstance = new Material();
            matInstance.copy(this.material);
            
            // 设置材质参数
            matInstance.setProperty('edgeColor', this.edgeColor);
            matInstance.setProperty('edgeWidth', this.edgeWidth);
            if (this.dissolveTexture) {
                matInstance.setProperty('dissolveTex', this.dissolveTexture);
            }

            // 应用材质到精灵
            this._sprite.customMaterial = matInstance;
        } else {
            console.error('DissolveEffect: Material not assigned!');
        }
    }

    update(deltaTime: number) {
        if (!this._isDissolving) return;

        this._elapsedTime += deltaTime;
        this._dissolveThreshold = this._elapsedTime / this.dissolveTime;

        // 更新材质参数
        if (this._sprite?.customMaterial) {
            this._sprite.customMaterial.setProperty('dissolveThreshold', this._dissolveThreshold);
        }

        // 检查是否完成溶解
        if (this._dissolveThreshold >= 1.0) {
            this._isDissolving = false;
            this.node.emit('dissolveComplete');
        }
    }

    /**
     * 开始溶解效果
     */
    public startDissolve() {
        this._isDissolving = true;
        this._elapsedTime = 0;
        this._dissolveThreshold = 0;

        if (this._sprite?.customMaterial) {
            this._sprite.customMaterial.setProperty('dissolveThreshold', 0);
        }
    }

    /**
     * 重置溶解效果
     */
    public resetDissolve() {
        this._isDissolving = false;
        this._elapsedTime = 0;
        this._dissolveThreshold = 0;

        if (this._sprite?.customMaterial) {
            this._sprite.customMaterial.setProperty('dissolveThreshold', 0);
        }
    }
} 