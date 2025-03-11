import { director } from "cc";
import ModuleBase from "../../core/base/ModuleBase";
import { EventType } from "../../EventType";
import { JishiViewMediator } from "./JishiViewMediator";

export default class JishiModule extends ModuleBase {
  private mediator:JishiViewMediator;
  static ins: JishiModule;

  constructor(moduleId:string) {
    super(moduleId);
            let s = this;
            s.addEventListeners();
            JishiModule.ins = s;
  }
  protected addEventListeners(): void {
    let s = this;
}
  public init(): void {
    let self = this;
    self.mediator = new JishiViewMediator(self);
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
  
}