import { _decorator, Component, Label, Asset, assert, loader, sys, assetManager, director } from 'cc';
const { ccclass, property } = _decorator;
import { Player, encodePlayer } from '../../src/proto';
import ReconnectingWebSocket from 'reconnecting-websocket';
import { network } from '../../src/model/RequestData';
import { modelMgr, observer, socket } from '../../src/game/App';
import { EventType } from '../../src/EventType';
import { showMsg2 } from '../../src/core/message/MessageManager';
import Log from '../../../assets/src/utils/LogUtils'
import { GlobalConfig } from '../../src/game/config/GlobalConfig';
import AxiosManager from '../../src/manager/AxiosManager';
// imported from socket-io.js
declare var io: any;
const TAG = 'NetworkCtrl'
@ccclass('NetworkCtrl')
export class NetworkCtrl {


    private _reconnectCount = 0;

    private _wsiSendBinary: ReconnectingWebSocket | null = null;
    private _sioClient: any = null;

    private tag: string = '';
    private _url: string = "";

    private heartbeatInterval: number = 75000;
    private heartbeatTimer: any;

    // use this for initialization
    constructor() {
        const currentURL = window.location.href;
        if(currentURL.includes("storyModel")){
            return;
        }
        if (currentURL.indexOf("localhost") != -1||currentURL.indexOf("192.168") != -1) {
            //this._url = "ws://192.168.1.100:8686";//本地
            //this._url = "wss://13.214.33.171:8989"; //windows
            //this._url = "wss://www.infinitytest.cc:8989"
            //this._url = "ws://44.200.143.84:8989"; //linux
            //this._url = "wss://aitown.infinitytest.cc/api/ws"
            //this._url = "ws://192.168.0.100:8686"; //张航本地
            //this._url = "ws://54.159.82.155:8686"
            //this._url = "wss://aitown.infinitytest.cc/api/ws"
            this._url = "wss://dramai.world/api/ws"
            //this._url = "wss://memerepublic.ai/api/ws"
            GlobalConfig.instance.isDebug = true;
            GlobalConfig.instance.testTools = true
            
        } else {
            //this._url = "wss://www.infinitytest.cc:8989"
            //this._url = "wss://cat.infinityg.ai/api/ws"
            //this._url = "wss://cat.infinitytest.cc/api/ws"
            //this._url = "ws://192.168.0.102:8686"; //张航本地
            // this._url = "wss://aitown.infinitytest.cc/api/ws"//测试服
            // GlobalConfig.instance.isDebug = false;
            //this._url = "wss://satoshi-ai.live/api/ws"
            // this._url = "ws://192.168.0.100:8686"; //张航本地
            //GlobalConfig.instance.isDebug = false;
            this._url = "wss://dramai.world/api/ws"
            GlobalConfig.instance.isDebug = true;
            GlobalConfig.instance.testTools = false
        }
        this._wsiSendBinary = null;
        Log.log(TAG, "socket waiting...")

        this.prepareWebSocket();
        this.startHeartbeat()
    }

    private startHeartbeat(): void {
        // if (this.heartbeatTimer) {
        //   clearInterval(this.heartbeatTimer);
        // }
    
        this.heartbeatTimer = setInterval(() => {
          Log.log(TAG, 'send heart pin')
          this.sendWebSocketBinary({
            "requestId": 111110,
            "command": 99996,
            "msg":"1"
          }); // 假设服务器用 'pong' 响应心跳
        }, this.heartbeatInterval);
    }

    onDestroy() {
        let wsiSendBinary = this._wsiSendBinary;
        if (wsiSendBinary) {
            wsiSendBinary.onopen = null;
            wsiSendBinary.onmessage = null;
            wsiSendBinary.onerror = null;
            wsiSendBinary.onclose = null;
            wsiSendBinary.close();
        }

    }



    prepareWebSocket() {
        const self = this;
        let websocketLabel = "";
        let respLabel = "";
        let url = this._url;
        // if (assetManager.cacheManager) {
        //     url = assetManager.cacheManager.getCache(url) || assetManager.cacheManager.getTemp(url) || url;
        // }
        // We should pass the cacert to libwebsockets used in native platform, otherwise the wss connection would be closed.
        // @ts-ignore
        this._wsiSendBinary = new ReconnectingWebSocket(url, [], {
            maxReconnectionDelay: 10000,
            minReconnectionDelay: 1000 + Math.random() * 4000,
            reconnectionDelayGrowFactor: 1.3,
            minUptime: 5000,
            connectionTimeout: 4000,
            maxRetries: Infinity,
            maxEnqueuedMessages: Infinity,
            startClosed: false,
            debug: false,
        });
        this._wsiSendBinary.retryCount
        let _wsiSend = this._wsiSendBinary
        let redirectCode = 
        // this._wsiSendBinary = new WebSocket('wss://echo.websocket.events', [], url);
        this._wsiSendBinary.binaryType = 'arraybuffer';
        this._wsiSendBinary.onopen = function (evt) {
            respLabel = 'Opened!';
            websocketLabel = 'WebSocket: onopen'
            Log.log(TAG, websocketLabel)
            if(director.getScene().name != 'login'){
                let json = new network.LoginRequest();
                json.requestId = 0;
                json.type = 1;
                json.command = 10000;
                json.data.avatar = "";
                json.data.clientOs = "";
                json.data.loginType = 1;
                json.data.name = GlobalConfig.instance.playername;
                json.data.nickName = GlobalConfig.instance.nickName;
                json.data.userId = GlobalConfig.instance.userId;
                json.data.password = "123";
                json.data.timeZone = 0;
                socket.sendWebSocketBinary(json);
            } 
            observer.post(EventType.SOCKET_ONOPEN);
            GlobalConfig.instance.hasInitGame = true
        };

        this._wsiSendBinary.onmessage = this.onmessage;

        this._wsiSendBinary.onerror = function (evt) {
            if(this._wsiSendBinary){
                this._wsiSendBinary.reconnect()
            }

            websocketLabel = 'WebSocket: onerror'
            respLabel = 'Error!';
        };

        this._wsiSendBinary.onclose = function (evt) {
            // _wsiSend = this._wsiSendBinary = new ReconnectingWebSocket(url, [], {
            //     maxReconnectionDelay: 10000,
            //     minReconnectionDelay: 1000 + Math.random() * 4000,
            //     reconnectionDelayGrowFactor: 1.3,
            //     minUptime: 5000,
            //     connectionTimeout: 4000,
            //     maxRetries: Infinity,
            //     maxEnqueuedMessages: Infinity,
            //     startClosed: false,
            //     debug: false,
            // });
            // _wsiSend.onmessage =this.onmessage;

            // websocketLabel = 'WebSocket: onclose'
            // // After close, it's no longer possible to use it again,
            // // if you want to send another request, you need to create a new websocket instance
            // //self._wsiSendBinary = null;
            // respLabel = 'Close!';
            

            //this._wsiSendBinary.reconnect()
        };

    }
    /**
     * 消息响应
     * @param evt 
     */
    private onmessage(evt) {
        // const binary = new Uint8Array(evt.data);
        let binaryStr = 'response bin msg: ';

        // let str = '0x';
        // const hexMap = '0123456789ABCDEF'.split('');
        // assert(hexMap.length == 16);


        // for (let i = 0; i < binary.length; i++) {
        //     str += hexMap[binary[i] >> 4];
        //     str += hexMap[binary[i] & 0x0F];
        // }
        var c: any = evt.data;
        binaryStr += c;
        Log.log(TAG, binaryStr)
        // c = c.replace(/\\/g, "");
        c = JSON.parse(c);
        if(c){
            if(c.code == 10019){
                showMsg2(c.message);
            }else if (c.code == 99998) {

            }
        }
        observer.post(EventType.SOCKET_ONMESSAGE, c);
    };
    /**
     * 发送消息
     * @returns 
     */
    sendWebSocketBinary(req: Object) {
        let websocketLabel = "";
        if (!this._wsiSendBinary) { return; }
        if (this._wsiSendBinary.readyState === WebSocket.OPEN) {
            websocketLabel = 'WebSocket: sendbinary';
            req["requestId"] = Date.now() + Math.floor(Math.random() * 100);

            let buf = JSON.stringify(req);
            Log.log(TAG, 'websocket::', buf)
            //--------
            let arrData = new Uint16Array(buf.length);
            for (let i = 0; i < buf.length; i++) {
                arrData[i] = buf.charCodeAt(i);
            }

            // this._wsiSendBinary.send(arrData.buffer);
            //------------
            // let p: Player = { name: "hello", score: 10 };
            // this._wsiSendBinary.send(encodePlayer(p));
            this._wsiSendBinary.send(buf);
        }
        else {
            let warningStr = 'send binary websocket instance wasn\'t ready...';
            websocketLabel = 'WebSocket: not ready';
            Log.log(TAG, websocketLabel)
            // this.scheduleOnce(() => {
            //     this.sendWebSocketBinary();
            // }, 1);
        }
    }

}
