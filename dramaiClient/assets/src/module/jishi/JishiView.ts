import {
    _decorator,
    Component,
    AudioSource,
    assert,
  } from "cc";
import { mduManger } from "../../game/App";
import { ModuleID } from "../../game/config/ModuleID";
  import { AudioManager } from "../../manager/AudioManager";
  const { ccclass, property } = _decorator;
  
  @ccclass("JishiView")
  export class JishiView extends Component {

    start() {
  
    }
    onLoad() {

    }
  
  
    onDisable() {
  
    }

    public openmainclick(){
      mduManger.openModu(ModuleID.MAIN);
    }
  
  }
  