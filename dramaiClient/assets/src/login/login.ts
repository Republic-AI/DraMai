import { _decorator, Component, Node, director, Label, view, resources, ProgressBar, EditBox, Sprite, randomRangeInt, SpriteFrame, tween, v3, UITransform, assetManager, sys, SceneAsset, game } from "cc";
const { ccclass, property } = _decorator;
import { tizhuanMgr } from "../../src/manager/TiaozhuanManager";
import { showMsg2 } from "../../src/core/message/MessageManager";
import App, { appInit, mduManger, modelMgr, observer, socket } from "../../src/game/App";
import mGameConfig from "../../src/utils/MGameConfig";
import WebUtils from "../../src/utils/WebUtils";
import { GlobalConfig } from "../../src/game/config/GlobalConfig";
import { network } from "../../src/model/RequestData";
import { EventType } from "../../src/EventType";
import Log from '../../src/utils/LogUtils'
import { NPCPartBaseDataMap } from "../NPC/NPCControl";
import { RolePartIcon } from "../StaticUtils/NPCConfig";
import loginModel from "../model/loginModel";
import { alert_cb_status } from "../common/alertPrefab";
export let Axios;
export let NeteaseGame;
export let yyygame;
const treePos = -480
const treeAdapt = [
  2.75,
  2.75,
  2.5,
  2.5,
  2,
  2,
]

const rabbit_x = [
  0,
  -66,
  -133,
  -200,
]

export const MAX_LENGTH = 1

const cloudPos = -620
const cloudAdapt = [
  1.5,
  1,
  1.75,
  0.5
]

const BgStartPos = [
  { x: 35, y: -225 },
  { x: -956, y: -225 },
  { x: -1945, y: -225 },
  { x: -2940, y: -225 },
  { x: 35, y: 330 },
  { x: -956, y: 330 },
  { x: -1945, y: 330 },
  { x: -2940, y: 330 },
]

const CowBabyPos = [
  { x: 0, y: 0 },
  { x: -25, y: 0 },
  { x: -54, y: 0 },
  { x: -80, y: 0 },
  { x: -80, y: 31 },
  { x: -54, y: 31 },
  { x: -25, y: 31 },
  { x: 0, y: 31 },
]

const CowBlackPos = [
  { x: 0, y: 0 },
  { x: -50, y: 0 },
  { x: -100, y: 0 },
  { x: -150, y: 0 },
  { x: -150, y: -62 },
  { x: -100, y: -62 },
  { x: -50, y: -62 },
  { x: 0, y: -62 },
]

const CowPos = [
  0,
  -24,
  -47,
  -72
]

/**
 * 1.登录验证/登录逻辑
 * 2.登录页/loading页
 * 3.活动未开启/开启/结束状态处理
 * 4.游戏首接口home 接口
 * 5.微信qq 分享文案
 */

let version = "v0.3.2"
const TAG = 'Index'
@ccclass("Index")
export default class Index extends Component {

  @property(Node)
  public loading: Node;

  @property(SpriteFrame)
  animationFrameArr: SpriteFrame[] = [];

  @property(Node)
  imgCloudArr: Node[] = [];

  @property(Node)
  imgPlanetArr: Node[] = [];

  @property(Node)
  imgGrassArr: Node[] = [];

  @property(Node)
  public npcNode: Node;

  @property(Node)
  public lblText: Node;

  //人物选择的部分Icon信息
  currentIndex: 0
  currentPartName: string = 'hair'
  frameTime15 = 0

  frameTime10 = 0

  starIndex: number = 0 //星星闪动的index
  cowIndex: number = 0 //奶牛的index

  cowBabyIndex: number = 0 //小牛的index
  cowBlackIndex: number = 0 //走路的奶牛的index
  npcBasePartInfo: NPCPartBaseDataMap = {
    body: 0,
    hair: {
      sexy: 'man',
      index: 0
    },
    pants: 0,
    shirt: 0
  }
  rabbit_index: number = 0;
  addFrameRabbit: number = 0;
  _frameIndex: number = 0;
  _timeDur:number = 0;
  _isLogin = false;

  protected onLoad(): void {

  }
  constructor() {
    super();
  }
  async start() {
    //this.loginview.active = true;
    let s = this;
    const currentUrl = window.location.href;
    // 创建 URL 对象
    // const url = new URL(currentUrl);
    // if(sys.isMobile && currentUrl.includes("satoshi-ai.live")){
    //   if(currentUrl.includes("aitown_pc")){
    //     let newUrl = currentUrl.replace("aitown_pc","");
    //     window.location.replace(newUrl);
    //   }
    // }
    // else if(currentUrl.includes("satoshi-ai.live")){
    //   if(!currentUrl.includes("aitown_pc")){
    //     let newUrl = currentUrl.replace("satoshi-ai.live","satoshi-ai.live/aitown_pc/");
    //     window.location.replace(newUrl);
    //   }
    // }
    tizhuanMgr.hideloading();
    mGameConfig.DeviceW = view.getVisibleSize().width;
    mGameConfig.DeviceH = view.getVisibleSize().height;
    mGameConfig.DesginW = view.getVisibleSize().width;
    mGameConfig.DesginH = view.getVisibleSize().height;
    await this.initData();
    // 获取查询参数
    await this.goLogin()

    //控制台调试
    // let script = document.createElement("script");
    // script.src = "https://s.url.cn/qqun/qun/qqweb/m/qun/confession/js/vconsole.min.js";
    // document.body.appendChild(script);
  }

  protected update(dt: number): void {
    this._timeDur = this._timeDur + dt;
    if(this._timeDur > 0.5){
      if(this.lblText.getComponent(Label).string == "Loading"){
        this.lblText.getComponent(Label).string = "Loading."
      }
      else if(this.lblText.getComponent(Label).string == "Loading."){
        this.lblText.getComponent(Label).string = "Loading.."
      }
      else if(this.lblText.getComponent(Label).string == "Loading.."){
        this.lblText.getComponent(Label).string = "Loading..."
      }
      else if(this.lblText.getComponent(Label).string == "Loading..."){
        this.lblText.getComponent(Label).string = "Loading"
      }
      this._timeDur = 0;
    }
  }

  async initData() {
    this.setAnimation()
    this.imgCloudArr.forEach(cloudNode=>{
      tween(cloudNode).repeatForever(tween(cloudNode).by(0.2,{position:v3(-5,0,0)}).call(()=>{
        if(cloudNode.position.x <= -300){
          cloudNode.setPosition(v3(300+cloudNode.getComponent(UITransform).contentSize.width,cloudNode.position.y,0));
        }
      }).start()).start()
    })
    this.imgPlanetArr.forEach(planet=>{
      tween(planet).repeatForever(tween(planet).by(0.15,{position:v3(-5,0,0)}).call(()=>{
        if(planet.position.x <= -300){
          planet.setPosition(v3(300+planet.getComponent(UITransform).contentSize.width,planet.position.y,0));
        }
      }).start()).start()
    })
    this.imgGrassArr.forEach(grass=>{
      tween(grass).repeatForever(tween(grass).by(0.02,{position:v3(-5,0,0)}).call(()=>{
        if(grass.position.x <= -300){
          grass.setPosition(v3(300+grass.getComponent(UITransform).contentSize.width,grass.position.y,0));
        }
      }).start()).start()
    })
    //加载初始界面必要bundle配置
    await assetManager.loadBundle("npcAnimation");
    await assetManager.loadBundle("headFrame");
    await assetManager.loadBundle("newsCfg");
    await assetManager.loadBundle("voteLayer");
    await assetManager.loadBundle("createPlayer");
    await assetManager.loadBundle("chatRecord");
    await assetManager.loadBundle("linkLayer");
    await assetManager.loadBundle("lobby",(err,bundle)=>{
      if(err){
        console.log("loadError")
      }
      else{
        console.log("loadLobbySuccessFul1111");
      }
    });
    await assetManager.loadBundle("game_map1");
    await assetManager.loadBundle("game_map2");
    await assetManager.loadBundle("game_map3");
    await assetManager.loadBundle("game_map4");
  }

  setAnimation() {
    this._frameIndex = 0;
    this.npcNode.getComponent(Sprite).spriteFrame = this.animationFrameArr[this._frameIndex];
    if (this.animationFrameArr.length > 0) {
      tween(this.npcNode).repeatForever(tween(this.npcNode).delay(0.12).call(() => {
        if (this._frameIndex < this.animationFrameArr.length - 1) {
          this._frameIndex++;
        }
        else {
          this._frameIndex = 0
        }
        this.npcNode.getComponent(Sprite).spriteFrame = this.animationFrameArr[this._frameIndex];
      }).start()).start();
    }
  }



  public async getlogin() {
    //游客登陆
    let json = new network.LoginRequest();
    json.requestId = 0;
    json.type = 1;
    json.command = 10000;
    
    let avatarId = Number(localStorage.getItem("avatarId"));
    if(avatarId){
        json.data.avatar = avatarId;
    }
    else{
        avatarId = Math.floor(Math.random() * 160) + 1
        localStorage.setItem("avatarId",avatarId.toString());
    }
    json.data.avatar = Number(avatarId);
    json.data.clientOs = "";
    json.data.loginType = 0;
    let idString
    if (localStorage.getItem('visitor')) {
      idString = localStorage.getItem('visitor')
    } else {
      idString = Date.now().toString();
      localStorage.setItem('visitor', idString)
    }
    if(localStorage.getItem("walletID")){
      //钱包地址登录
      idString = localStorage.getItem("walletID")
      json.data.loginType = 1;
    }
    GlobalConfig.instance.userId = GlobalConfig.instance.nickName = GlobalConfig.instance.playername = idString
    json.data.name = GlobalConfig.instance.playername;
    json.data.nickName = GlobalConfig.instance.nickName;
    json.data.userId = GlobalConfig.instance.userId;
    json.data.password = "123";
    json.data.timeZone = 0;
    socket.sendWebSocketBinary(json);
    //this.loading.active = true
  }
  public onDestroy() {
    if(this._isLogin){
      observer.off(EventType.SOCKET_ONOPEN, this.getlogin, this);
      observer.off(EventType.SOCKET_ONMESSAGE, this.hasLogin, this);
    }
  }


  public hasLogin(da: any) {
    let data: network.InetwarkResponseData = da.data;
    if (data && data.command == 10000 && data.code == 0) {
      //loginModel.character = data.player.character 
      GlobalConfig.instance.hasLogin = true;
      GlobalConfig.instance.LoginData = data as network.LoginResponse;
      GlobalConfig.instance.loginType  = da.data.data.loginType;
      GlobalConfig.instance.address = da.data.data.address;
      GlobalConfig.instance.nickName = da.data.data.nickName;
      console.log("loginType====" + GlobalConfig.instance.loginType);
      //显示loading页
      //this.loginview.active = false;
      //this.loadingview.active = true;
      //预加载场景并获得加载进度
      let lobbyBundle = assetManager.getBundle("lobby")
      if(lobbyBundle){
        console.log("go to lobby")
        lobbyBundle.loadScene("lobbyScene",(err,scene:SceneAsset)=>{
          if(err){
            console.log("loadScene error" + err)
            return;
          }
          else{
            director.runScene(scene);
          }

        });
      }
      else{
        console.log("no lobby bundle");
        assetManager.loadBundle("lobby",(err,bundle)=>{
          if(err){
            console.log("loadError")
          }
          else{
            console.log("loadLobbySuccessFul22222");
            bundle.loadScene("lobbyScene",(err,scene:SceneAsset)=>{
              if(err){
                console.log("loadScene error" + err)
                return;
              }
              else{
                director.runScene(scene);
              }
    
            });
          }
        });
      }
      // director.preloadScene('gameScene', (completedCount, totalCount, item) => {
      //   //可以把进度数据打出来
      //   //this.prg?.progress && (this.prg.progress = completedCount / totalCount);

      // }, () => {
      //   this.scheduleOnce(() => {
      //     Log.log(TAG, 'Enter Game HomePage')

      //     tizhuanMgr.checkEntrance();
      //   }, 0);
      // });
    }
  }



  public gotoHome() {

    return;
  }
  /**
   * 按钮登录点击，
   * @returns 
   */
  public async goLogin() {
    this.loading.active = true
    if (GlobalConfig.instance.hasInitGame == false) {
      // const debounceThings = WebUtils.debounce(()=>{
      let app = new App();
      app.init();
      appInit(app);
      Log.log(TAG, 'main init start ...')
      mduManger.start();
      modelMgr.configModel.loadResDir();
      //director.addPersistRootNode(this.worldNode);
      observer.on(EventType.SOCKET_ONOPEN, this.getlogin, this);
      observer.on(EventType.SOCKET_ONMESSAGE, this.hasLogin, this);
      this._isLogin = true;
      // }, 2)
      // debounceThings()
    }
    this.loading.getChildByName("lblVersion").getComponent(Label).string = version;
  }

  setCurrentPart(data, event) {
    if (this.currentIndex + data > MAX_LENGTH || this.currentIndex + data < 0) {
      return
    }
    this.currentIndex = this.currentIndex + data
  }



}
