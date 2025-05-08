import { EventType } from "../EventType";
import BoswerUtils from "../utils/BoswerUtils";
import { getIslogin } from "./RequestManager";
import { director } from 'cc';
import { ModuleID } from "../game/config/ModuleID";
import { mduManger, observer } from "../game/App";
import Log from '../../../assets/src/utils/LogUtils'

const TAG = 'TiaozhuanMananger'
/**
 * 游戏内外页面跳转
 * 本地存储跳转
 * href 链接跳转
 * webappear执行
 */
export default class TiaozhuanMananger {
  private _from: string = "";
  private _hasInit: [] = [];
  private _gameUrl: { [name: string]: any[] }[];
  public static TYPE_LOCALSTORGE: string = "localstroge";
  public static TYPE_URL: string = "fromurl";
  public static TYPE_GAME: string = "games";
  public initGroupRes: Array<any>;
  public visit: any;
  private h5utils;
  constructor() {
    this._gameUrl = [];
    this.initGroupRes = [];
    //添加预加载资源逻辑

    console.log(TAG,BoswerUtils.getQueryVariable("token"), "token");
    this.visit = BoswerUtils.getQueryVariable("token");
  }

  public static KEY_OTPAGETYPE: string = "cocos_game";
  /**
   *获取存储到本地的页面名称
   1.临时存储
   2.url链接跳转
   */
  public static getTentrance(): string {
    let storage = BoswerUtils.getLocalStorage(
      TiaozhuanMananger.KEY_OTPAGETYPE,
      "string"
    );
    if (storage) {
      return storage;
    }
    let urlparam = BoswerUtils.getParam()[TiaozhuanMananger.KEY_OTPAGETYPE];
    if (urlparam) {
      return urlparam;
    }
    return "";
  }

  public ons() {
    observer.on(EventType.LOGIN_COMPELETED, this.afterLogin, this);
  }
  public get from() {
    return this._from;
  }

  /**
   * 通用协议及显示处理
   */
  public defaultRequest(): Promise<void> {
    return new Promise((resolve, reject) => {
      getIslogin().then((res) => {
        res["data"];
        console.log(TAG,"defaultRequest:isLogin::", res["data"].isLogin);
        resolve();
      }).catch((err) => {

      });

      //判断是否登录
    });
  }

  /**
   * 入口::
   * 获取来源
   * 优先级：localstorage-->url-->game
   * BoswerUtils.KEY_OTPAGETYPE:游戏入口类型key
   */
  public checkEntrance() {
    let storage = BoswerUtils.getLocalStorage(
      TiaozhuanMananger.KEY_OTPAGETYPE,
      "string"
    );
    if (storage) {
      this.goto(TiaozhuanMananger.TYPE_LOCALSTORGE, TiaozhuanMananger.KEY_OTPAGETYPE);
      return;
    }
    let urlparam = BoswerUtils.getParam()[TiaozhuanMananger.KEY_OTPAGETYPE];
    if (urlparam) {
      this.goto(TiaozhuanMananger.TYPE_URL, TiaozhuanMananger.KEY_OTPAGETYPE);
      return;
    }
    this.defalutstart();
  }
  /**
   * 默认打开模块
   */
  public defalutstart() {
    console.log(TAG,"in defalut start.")
    //验证登录,成功后,打开默认界面
    // this.afterLogin();
    // mainModule.openModu();
    mduManger.openModu(ModuleID.TOWN);
  }
  /**
   * 
   * @param type 跳转来源
   * @param key 跳转页面
   */
  public goto(type: string, key: string) {
    let s = this;
    switch (type) {
      case TiaozhuanMananger.TYPE_LOCALSTORGE:
        let storage = BoswerUtils.getLocalStorage(key, "string");
        this.openmodule(storage);
        break;
      case TiaozhuanMananger.TYPE_URL:
        let urlparam = BoswerUtils.getParam()[key];
        this.openmodule(urlparam);
        break;
      case TiaozhuanMananger.TYPE_GAME:
        //跳转到其他游戏页面,暂停本游戏
        // Laya.stage.on(Laya.Event.VISIBILITY_CHANGE, s, s._visibilityChange);
        break;
    }
  }

  /**
   * 根据key 打开指定页面
   * @param storage
   */
  public openmodule(storage: string) {
    switch (storage) {
      case "sign": //范例
        observer.post("opensign");
        break;
      case "decorate": //范例2
        director.loadScene("decorate");
        break;
    }
  }
  /**
   *登录执行后返回操作
   */
  afterLogin(): Promise<void> {
    return new Promise((resolve, reject) => {
      getIslogin().then((res) => {
        //判断是否登录
        try {

          resolve();
        } catch (err) {
          reject();
          if (err) console.log(TAG,err.toString());
        }

      });
    })

  }


  //====================================
  /**
   * 跳转
   * @param data 
   */
  async openSchema(url) {

  }
  /**
   * 返回
   * @param data 
   */
  async goBack() {

  }
  /**
   * webview 激活
   */

  public onWebviewAppear(callback) {
    if (typeof callback === 'function') {
      if (window["onWebAppearanceQuee"].indexOf(callback) != -1) {
        return;
      }
      window["onWebAppearanceQuee"].push(callback);
    }
  }
  /**
   * webview 隐藏
   * @param cb 
   */
  public onWebviewDisappear(cb) {
    window["onWebviewAppear"] = cb;
  }

  /**
   * 隐藏app loading
   */
  public hideloading() {

  }

}
export const tizhuanMgr: TiaozhuanMananger = new TiaozhuanMananger();