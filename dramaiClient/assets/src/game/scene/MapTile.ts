import { Layers, Node, resources, Sprite, SpriteFrame, Texture2D, UITransform, Vec2, Vec3 } from "cc";
import WebUtils from "../../utils/WebUtils";
import LayerEnum from "./LayerEnum";
/**
 *
 */
export default class MapTile {
  private _row: number;
  private _col: number;
  private _resUrl: string;
  private _isLoaded: boolean = false;
  private _parent: Node;
  private _disObj: Node;
  private _isLoading: boolean = false;

  public constructor(row: number, col: number, parent: Node) {
    this._row = row;
    this._col = col;
    this._parent = parent;
    let id: string = this._row + "_" + this._col;
    this._resUrl ="res/map_001/" + id ;
    this._disObj= new Node();
    this._disObj.layer = Layers.Enum.UI_2D;;//Layer 设置要准确 ui_2d;
    this._disObj.addComponent(Sprite);
    this._disObj.getComponent(UITransform).setAnchorPoint(new Vec2(0,1));
  }

  public loadTile(): void {
    if (this._isLoaded || this._isLoading) return;
    this._isLoading = true;
    // 加载 texture
    WebUtils.getResouceImg(this._resUrl,this._disObj,this.onLoadCmp.bind(this));

  }

  private onLoadCmp(err: any, spriteFrame: SpriteFrame): void {
   
    this._isLoading = false;
    this._isLoaded = true;


    this._disObj.setPosition(new Vec3(this._col * 300,-this._row * 300,0));
    this._parent.addChild(this._disObj);
    //test code ...
    // sceneManager.addToLayer(this._disObj,LayerEnum.MapLayer,300,300);
  }

  public unloadTile(): void {
    //return;
    //清除资源缓存,容器保存
    this._disObj.getComponent(Sprite).spriteFrame = null;
    //释放资源
    // if (this._isLoading) resources.release(this._resUrl);
    this._isLoaded = false;
    this._parent.removeChild(this._disObj);
    resources.release(this._resUrl+"/spriteFrame");
    // console.log("unloadTile: " + this._resUrl);
  }
}
