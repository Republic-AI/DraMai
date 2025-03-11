import { director } from "cc";
import ModuleBase from "../core/base/ModuleBase";
import { ModuleID } from "../game/config/ModuleID";
import JishiModule from "../module/jishi/JishiModule";
import TownModule from "../town/TownModule";

export default class ModuleManager {
  private _moduDic = {};
  /**
   * module打开状态
   */
  private _moduOnOff = {
  };
  currentMduId: string;



  public start(): void {
    let self = this;
    self.initModule();
  }
  public initModule(): void {
    let self = this;
    let moduDic = self._moduDic;
    moduDic[ModuleID.JISHI] = new JishiModule(ModuleID.JISHI); //集市界面
    moduDic[ModuleID.TOWN] = new TownModule(ModuleID.TOWN); //集市界面
  }

  /**
   *
   * @param moduleId
   */
  /** 打开模块
   *
   */
  public openModu(moduleId: any, modOpenData: any = null): void {
    let self = this;

    if (moduleId) moduleId = (moduleId);

    let modu: ModuleBase = this._moduDic[moduleId];
    if (!modu) return;
    if (this.currentMduId&&this.currentMduId!=moduleId) {
      this.closeModu(this.currentMduId);
    }
    self._moduOnOff[moduleId] = modu.open(modOpenData, true);
  }
  public closeModu(moduleId: any): void {
    let self = this;
    let modu = this._moduDic[moduleId];
    if (!modu) return;
    modu.close();
    if (!self._moduOnOff[moduleId]) return;
    self._moduOnOff[moduleId] = false;
  }
}
// const mduManger = new ModuleManager();
// export default mduManger;
