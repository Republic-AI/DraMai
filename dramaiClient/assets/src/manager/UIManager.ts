import { director, instantiate, Node, Prefab, resources, size } from "cc";
import { showMsg2 } from "../core/message/MessageManager";
import { LoadingController } from "../game/components/LoadingController";
import Log from '../../../assets/src/utils/LogUtils'

const TAG = 'UIManager'
export default class UIManager {
  /**
   * 当前显示节点;
   */
  private _curPar: Node;

  /**
   *
   * @param ctrl
   * @param par
   * @param isOnResize
   * @param isEff
   */
  public popToParent(
    child: string,
    par: Node = null,
  ): void {
    let s = this;
    if(!par){
        let scen = director.getScene().getChildByName("Canvas");
        console.log(TAG,"uimanager::poptoparent",scen);
        par = scen as unknown as Node;
    }
    s._curPar = par;
    let name;
    if(child.includes("/")){
        let strarr = child.split("/");
        name =strarr[strarr.length-1];
    }else{
        name = child;
    }
    if(s._curPar.getChildByName(name)){
        console.log(TAG,"已经添加child:"+name);
        return;
    }
    // s._curPar.addChild(child);
    if(s._isloadingArr.indexOf(name)!=-1){
        console.log(TAG,"正在加载"+name);
        return;
    }else{
        s._isloadingArr.push(name);
    }
    resources.load(child, Prefab, (err:Error,data:Prefab)=>{
        let ind = s._isloadingArr.indexOf(name);
        s._isloadingArr.splice(ind,1);
        if(!err&&data){
            let n = instantiate(data);
            if(child.includes("/")){
                let strarr = child.split("/");
                n.name =strarr[strarr.length-1];
            }else{
                n.name = child;
            }
          try {
              s._curPar.addChild(n);
              
          } catch (error) {
    
          }
        }else{
            console.log(TAG,"预制体资源加载失败::"+err);
            showMsg2("Load resources failed!");
        }
    });
  }
  private _isloadingArr = [];

  public removeParent(child: string): void {
    let s = this;
    if(!s._curPar){
        return;
    }
    let name;
    if(child.includes("/")){
        let strarr = child.split("/");
        name =strarr[strarr.length-1];
    }else{
        name = child;
    }
    let c = s._curPar.getChildByName(name);
    if(c){
        c.name = "";
        c.destroy();
    }else{
        console.log(TAG,"不存在弹窗"+child);
    }
  }
  /**
   * @param ctrl 显示对象
   * @param layerIdx 层级
   * @param isShapeClose 是否点击空白关闭
   * @param isShowShape 是否有遮罩
   * @param fun 点击空白关闭后回调
   * @param isEff:是否缩放动画
   */
  public popParentShape(
    ctrl: Node,
    layerIdx: Node,
    isShapeClose: boolean = true,
    isShowShape: boolean = true,
    fun?: Function,
    isEff: boolean = false
  ): void {}
  public removeParentShape(ctrl: Node, isEff: boolean = false): void {}
}
// const uiMgr:UIManager = new UIManager();
// export default uiMgr;
