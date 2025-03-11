import { _decorator, Component, director, dragonBones, Node, v2 } from 'cc';
import { GameScene } from '../game/scene/GameScene';
import { PlayerManager } from '../NPC/PlayerManager';
import { network } from '../model/RequestData';
import { socket } from '../game/App';
import { GlobalConfig } from '../game/config/GlobalConfig';
import { UILayer } from '../game/gameUI/UILayer';
import WebUtils from '../utils/WebUtils';
import { alert_cb_status } from '../common/alertPrefab';
const { ccclass, property } = _decorator;
const canTouchTile = {
    "2":[v2(40,19),v2(40,20),v2(40,21),v2(39,21),v2(38,21)
        ],
    "0":[v2(58,98),v2(58,99),v2(59,99)
        ,v2(56,98),v2(57,98)
        ,v2(56,99),v2(57,99)
        ,v2(56,100),v2(57,100),v2(58,100),v2(59,100)
        ],
    "6":[v2(57,59),v2(58,59),v2(59,59),v2(60,59),v2(60,60),v2(60,61),v2(60,62),v2(60,63)
        ,v2(59,63),v2(58,63),v2(57,63),v2(57,62),v2(57,61),v2(57,60)
        ],
    "4":[v2(43,63),v2(44,63),v2(45,63)
        ,v2(42,64),v2(42,65),v2(42,66)
        ,v2(46,64),v2(46,65),v2(46,66)
        ],
    "5":[v2(30,109),v2(31,109),v2(32,109),v2(33,109),v2(34,109)
        ,v2(30,110),v2(31,110),v2(32,110),v2(33,110)
        ],
    "3":[v2(17,115),v2(18,115),v2(19,115)
        ,v2(17,116),v2(18,116),v2(19,116)
        ,v2(14,114),v2(15,114),v2(16,114)
        ,v2(14,115),v2(15,115),v2(16,115)
        ,v2(14,116),v2(15,116),v2(16,116)
        ],
    "1":[v2(34,160),v2(35,160),v2(36,160),v2(37,160)
        ,v2(34,161),v2(35,161),v2(36,161),v2(37,161)
        ],
    "7":[v2(8,49),v2(8,50),v2(8,51)
        ,v2(9,51),v2(9,51),v2(10,51),v2(11,51)
        ,v2(7,49),v2(7,50),v2(7,51),
        ],
}

@ccclass('nftPrefab')
export class nftPrefab extends Component {
    @property
    nftId:number = 0;
    protected onLoad(): void {
        const armatureDisplay = this.node.getComponent(dragonBones.ArmatureDisplay);

        // 监听动画播放完成事件
        armatureDisplay.addEventListener(dragonBones.EventObject.LOOP_COMPLETE, this.onAnimationComplete, this);
    }

    _status = null;
    start() {

    }

    update(deltaTime: number) {
        this.node.getChildByName("getNftNode").active = this._status == 2 ? true : false;
    }

    setNftIdle(){
        this.node.getComponent(dragonBones.ArmatureDisplay).playAnimation("standby",0);
    }

    setNftClick(){
        this.node.getComponent(dragonBones.ArmatureDisplay).playAnimation("click",0);
    }

    setNftCanGet(){
        this.node.getComponent(dragonBones.ArmatureDisplay).playAnimation("standbyafter",0);
    }

    setNftCD(){
        this.node.getComponent(dragonBones.ArmatureDisplay).playAnimation("cd",0);
    }

    setStatus(status){
        switch(status.state){
            case 1:
                this.setNftIdle();
                break;
            case 2:
                if(this._status == 1){
                    this.setNftClick();
                }
                break;
            case 3:
                if(this._status == 2 && status.userNo == GlobalConfig.instance.LoginData.data.player.playerId ){
                    director.getScene().getComponentInChildren(UILayer).showNftNode(this.nftId);
                }
                this.setNftCD();
                break;
        }
        this._status = status.state;
    }

    // 动画播放完成时的回调
    onAnimationComplete (event) {
        // 在这里处理动画完成后的逻辑
        if(this.node.getComponent(dragonBones.ArmatureDisplay).animationName == "click"){
            this.setNftCanGet();
        }
    }

    onClickNft(){
        let isHaveMyPlayer = director.getScene().getComponentInChildren(GameScene).getMyPlayerNode();
        if(!isHaveMyPlayer){
            return;
        }
        let myPlayerPos = isHaveMyPlayer.getComponent(PlayerManager)._curTile;
        let canClick = false
        canTouchTile[this.nftId].forEach(tile=>{
            if(tile.x == myPlayerPos.x && (myPlayerPos.y + 1) == tile.y){
                canClick = true;
            }
        })
        if(!canClick){
            console.log("不在触摸范围内")
            return;
        }
        if(this._status == 1){
            this._status = 2;
            this.setNftClick();
            let json2 = new network.GetAllNPCRequest();
            json2.command = 10027;
            json2.type = 1;
            json2["data"] = {
                id:this.nftId,
                op:1,
                address:""
            }
            socket.sendWebSocketBinary(json2);
            console.log("请求产出===" + this.nftId);
        }
    }

    async onGetNft(){
        let isHaveMyPlayer = director.getScene().getComponentInChildren(GameScene).getMyPlayerNode();
        if(!isHaveMyPlayer){
            return;
        }
        let myPlayerPos = isHaveMyPlayer.getComponent(PlayerManager)._curTile;
        let canClick = false
        canTouchTile[this.nftId].forEach(tile=>{
            if(tile.x == myPlayerPos.x && (myPlayerPos.y + 1) == tile.y){
                canClick = true;
            }
        })
        if(!canClick){
            console.log("不在触摸范围内")
            return;
        }
        if(this._status == 2){
            let address = "123";
            console.log("type 111====" + typeof(address));
            // if (window.solana && window.solana.isPhantom){
            //     const wallet =  await window.solana.connect();
            //     address = wallet.publicKey.toString();
            //     console.log("address=====" + address);
            //     console.log("type copy=====" + typeof(address));
            // }
            // else{
            //     let str = "Phantom wallet not detected, click OK to proceed with installation";
            //     WebUtils.showAlert(str,alert_cb_status.both,()=>{
            //         let downLoadUrl = "https://phantom.app/download"
            //         window.location.href = downLoadUrl
            //     });
            //     console.error('未检测到 Phantom 钱包');
            //     return;
            // }
            let json2 = new network.GetAllNPCRequest();
            json2.command = 10027;
            json2.type = 1;
            json2["data"] = {
                id:this.nftId,
                op:2,
                address:address
            }
            socket.sendWebSocketBinary(json2);
            console.log("请求拾取===" + this.nftId);
        }
    }
}


