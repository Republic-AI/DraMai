import { director, Scene } from "cc";
import { mduManger } from "../../game/App";

export default class ModuleBase {
  public moduleId: any;
  constructor(moduleId: any) {
    this.moduleId = moduleId;
  }
  /**每次模块打开完成时调用 */
  protected onOpen(openData: any): void {
    //throw Error('onOpen方法需子类重写');
  }
  /**
   * 打开模块(该方法不允许被子类重写)
   * 返回值：是否成功打开
   */
  public readonly open = function (
    openData: any = null,
    mgr = false
  ): boolean {
    let self = this;
    if (!mgr) {
      mduManger.openModu(self.moduleId, openData);
      return false;
    }

    if (!self._inited) {
      self.init();
      self._inited = true;
    }
    self.onOpen(openData);
    return true;
  };
  public closeModu(moduleId: string): void {}
}
