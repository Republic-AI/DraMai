import { ViewMediatorBase } from "../core/base/ViewMediatorBase";
import { EventType } from "../EventType";
import  MainModule  from "./TownModule";

export class TownViewMediator extends ViewMediatorBase {
    public _defaultView:string = "gameScene";
  constructor(module: MainModule) {
    super(module);
  }
  public ons(): void {}
  public off(): void {
    let s = this;
  }
  public onWeekup(openData: any): void {
    let s = this;   
    if(!openData){
        openData = {
               view:s._defaultView
           }
       }else{
        //  if(openData.open){//example
        //    if(openData.open == "task"){
        //      setTimeout(() => {
        //        observer.post(EventType.OPEN_RENWUPANELFROM,{from:"fightview"});
        //      }, 1000);
        //    }
        //  }
         if(!openData.view){
            openData.view = s._defaultView;
         }
       }
       super.onWeekup(openData);
  }
  /**面板关闭 */
  public close(): void {
    let s = this;
  }
}
