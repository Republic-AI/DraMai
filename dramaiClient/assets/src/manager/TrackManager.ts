import { modelMgr } from "../game/App";
import { GlobalConfig } from "../game/config/GlobalConfig";
import AxiosManager from "./AxiosManager";

export default class TrackManager {
    /**
     * 1.接入文档文档请参见，https://docs.popo.netease.com/ofedit/096b65ea9fd44cc4a46c23b5a311f3a7； 
        2.相关参数：
        token所需app：garden
        测试服game_key：54utzeoo  查询game_name：GARDEN_TEST
        正式服game_key：oer34yks  查询game_name：GARDEN 
     * 
     * 
     */

    private BaseUrl = true ? 'https://wtgame-api-live.baochuangame.com:10443/' : 'https://apitest2.baochuangames.com/';
    private navigator = window.navigator;
    private isAndroid = /(Android)/i.test(navigator.userAgent);
    private isIos = /(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent);
    private isApp = /(NeteaseMusic)/i.test(navigator.userAgent);
    private BCAPPID: string = "CloudFlower";
    private device = {
        brand: 'gamesdk',
        channel: 'gamesdk',
        deviceId: 0,
        deviceType: -1,//-2:机器人 -1:未知 0:iOS 1:安卓 2:PC
        appVer: 0,
        os: 0,
        osVer: 0,
        model: 0,
        clientIp: 0,
    };
    private track_base_info: any = {
    };

    private getdeviceType() {
        let num: number = -1;
        if (this.isIos) {
            num = 0;
        } else if (this.isAndroid) {
            num = 1;
        }
        return num;
    }
    /**
     * 登录成功,并获取userinfo后 init
     */
    public init(trackParams = {}) {
        if (AxiosManager.BaseUrl == "https://flower.qa.web.netease.com") {
            this.BaseUrl = 'https://apitest2.baochuangames.com/';
        } else {
            this.BaseUrl = 'https://wtgame-api-live.baochuangame.com:10443/';
        } 

        this.device.deviceType = this.getdeviceType();
        this.UrlSource = this.getParameter('source') || this.getParameter('source_channel');
        this.track_base_info = {
            platform: this.device.deviceType,
            playerId: modelMgr.mainModel.userinfo.uid,
            playerName: "",
            app: this.BCAPPID,
        };


        try {
            
        } catch (e) {
            console.warn('log exception:', e);
        }
        this.track({ //example ...
            eventId: 100024, // 事件点击
            clickName: '页面曝光',
            clickType: '主页',
            sourceChannel: this.UrlSource,
            subChannel: 0,
        });
    }
    track(params) {
        const data = {
            ...this.track_base_info,
            eventValue: {
                ...params,
                playerId: this.track_base_info.playerId,
                playerName: this.track_base_info.playerName,
            },
        };
        try {
            AxiosManager.instance.post(this.BaseUrl + 'oauth2/anchor/bi/report', data).catch((err) => {
                console.log("track err::", err);
            });
        } catch (err) {
            console.log(err);
        }
    };
    clickTrack(clickName, clickType, sourceChannel) {
        const params = {
            eventId: 100024,
            clickName,
            clickType,
            sourceChannel: this.UrlSource,
            subChannel: 0,
        };
        return this.track(params);
    }
    public UrlSource = "";
    private encode(str) {
        // first we use encodeURIComponent to get percent-encoded UTF-8,
        // then we convert the percent encodings into raw bytes which
        // can be fed into btoa.
        return btoa(
            encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function toSolidBytes(match, p1) {
                return String.fromCharCode(Number('0x' + p1));
            })
        );
    }
    private getParameter(param) {
        let re = new RegExp(param + '=([^&]*)', 'i');
        let a = re.exec(document.location.search);
        if (a == null) return null;
        return a[1];
    }

}
export const trackMgr: TrackManager = new TrackManager();