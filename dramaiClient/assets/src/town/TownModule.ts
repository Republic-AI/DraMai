import { director } from "cc";
import ModuleBase from "../core/base/ModuleBase";
import { EventType } from "../EventType";
import { TownViewMediator } from "./TownViewMediator";

export default class TownModule extends ModuleBase {
  private mediator:TownViewMediator;
  static ins: TownModule;

  constructor(moduleId:string) {
    super(moduleId);
            let s = this;
            s.addEventListeners();
            TownModule.ins = s;
  }
  protected addEventListeners(): void {
    let s = this;
}
  public init(): void {
    let self = this;
    self.mediator = new TownViewMediator(self);
}

   /**打开创角面板 */
   public onOpen(openData: any = null): boolean {
    let self = this;
    if (self.mediator.weeked) {
        return;
    } else {
        self.mediator.open(openData);
        return true;
    }
}
 /**关闭创角面板 */
 public close(): void {
  let self = this;
  if (self.mediator) {
      self.mediator.close();
      self.mediator.weeked = false;
  }
}

  // /**
  //  *
  //  * @param moduleId
  //  */
  //  public openModu(data:any = null) {

       
  //   super.openModu(data);
  // }
  // public closeModu(moduleId: string): void {}
}
// const mainModule = new MainModule();
// export default mainModule;
