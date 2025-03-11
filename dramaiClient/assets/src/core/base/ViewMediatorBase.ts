import { Component, director, Scene } from "cc";
import { Observer } from "../../utils/Observer";
import ModuleBase from "./ModuleBase";
import { mduManger } from "../../game/App";

export class ViewMediatorBase extends Observer {
  protected modu: ModuleBase;
  public tabBar: any;
  private _openData;
  public _defaultView:string = "";

  constructor(modu: ModuleBase) {
    super();
    this.modu = modu;
  }
  /**
   * 执行mediator入口
   * @param openData 
   */
  public open(openData?: any): void {
    let self = this;
    //是否需要预先load 场景资源
    self.onOpen(openData);
  }
/**
 * 记录此mediator是否处于活动状态
 * 若处于活动状态,则后台继续执行其业务逻辑
 */
  public weeked = false;
  /**
   * .scene or .json 皮肤 加载完毕，
   * @param openData
   */
  private onOpen(openData: any = null): void {
    let self = this;
    self.weeked = true;
    self.ons();
    self.onWeekup(openData);
  }

  /**
   * 每次med打开完成时调用
   * 处理模块及其场景打开前执行的逻辑
   */
  public onWeekup(openData: any = null): void {
    //   console.log("打开view:"+openData.view);
    //   /**
    //    * 调用cocos 场景打开的逻辑,
    //    * 成功打开后直接替换上一场景,展示到舞台上
    //    */
    // let load = director.loadScene(openData.view,(error:Error,scene:Scene)=>{
    //       console.log(error,scene);
    //   });
    //   if(load){
    //     console.log(this._defaultView+"sss");
    //     mduManger.currentMduId = this.modu.moduleId;
    //   }
  }
    /**面板关闭 */
    public close(): void {
      let s = this;
    }

  /**每次med关闭所有界面 */
  public sleep(): void {
    let self = this;
    self.weeked = false;
    self.offs();
  }

  /** 销毁 */
  public dispose(): void {
    let self = this;
    this.sleep();
  }

  /** 开启侦听 */
  protected ons(): void {}

  /** 关闭侦听 */
  protected offs(): void {}
}
