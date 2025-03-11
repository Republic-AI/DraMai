import { NetworkCtrl } from "../../cases/network/NetworkCtrl";
import StateMachine from "../core/state/StateMachine";
import ModelManager from "../manager/ModelManager";
import ModuleManager from "../manager/ModuleManager";
import UIManager from "../manager/UIManager";
import mGameConfig from "../utils/MGameConfig";
import { Observer } from "../utils/Observer";
import SceneManager from "./scene/SceneManager";


export default class App {
  public showSend: boolean;
  public static isDebug: boolean = true;
  modMgr: ModuleManager;
  observer: Observer;
  uiMgr: UIManager;
  modelMgr: ModelManager;
  sceneManager:SceneManager;
  socket:NetworkCtrl;

  public init(): void {
    let self = this;
    this.observer = new Observer();
    this.modMgr = new ModuleManager();
    this.modelMgr = new ModelManager();
    this.uiMgr = new UIManager();
    this.sceneManager = SceneManager.instance;
    this.socket = new NetworkCtrl();
  }

 
}
let app: App;
let mduManger: ModuleManager;
let uiMgr: UIManager;
let observer: Observer;
let modelMgr: ModelManager;
let sceneManager:SceneManager;
let socket:NetworkCtrl;

export function appInit(_app: App) {
  app = _app;
  mduManger = _app.modMgr;
  uiMgr = _app.uiMgr;
  observer = _app.observer;
  window["observer"] = _app.observer;
  modelMgr = _app.modelMgr;
  window["modelMgr"] = _app.modelMgr;
  sceneManager = _app.sceneManager;
  socket = _app.socket;
  modelMgr.init();
}
export { app, mduManger, uiMgr, observer, modelMgr,sceneManager,socket };
