const appBuild = {
    ios519: 6220,
    couponIosBuild: 6500,
    couponAndroidBuild: 519010,
    merchantIosBuild: 6510,
    merchantAndroidBuild: 5200020,
    ios522: 6520,
    android522: 5219000,
    ios523: 6570,
    android523: 5220010,
    ios524: 6620,
    android524: 5240000,
    ios525: 6680,
    android525: 5250000,
    ios526: 6710,
    android526: 5250009,
    ios529: 6831,
    android529: 5285001,
    ios548: 8821,
    android548: 5480000,
    ios549: 8930,
    android549: 5490000,
    ios550: 8931,
    android550: 5500000
};
const u = navigator.userAgent;
const isiOS = /iPad|iPhone|iPod/i.test(u);
const isAndroid = !isiOS;
const isBiliApp = /BiliApp/i.test(u);
function getAppVersion() {
    const agent = navigator.userAgent.toLowerCase();
    const regStr = /biliapp\/[\d.]+/gi;
    return isBiliApp ? (`${agent.match(regStr)}`).replace(/[^0-9.]/ig, '') : 0;
}
const appVersion = Number(getAppVersion());
const isiOSBiliBlue = appVersion >= 7000 && appVersion < 8000;
const isBeforeIos519 = isiOS && isBiliApp && appVersion < 6220;
const isH5Pay = (isiOS && isBiliApp && Number(getAppVersion()) < appBuild.ios522)
const deviceType = isiOS ? 'ios' : 'android';
const isWeiXin = /MicroMessenger/i.test(u) && u.toLowerCase().indexOf('wxwork') === -1;
const isQQ = u.match(/\sQQ/i) !== null;
const isWeibo = u.indexOf('Weibo') > -1;
const isMissevanWebview = false; // 猫耳渠道
const pathname = ""; //window.location.pathname;
const isChannelComic =  pathname.indexOf('/channelcomic') > -1 || pathname.indexOf('/comic') > -1;
const isChannel = isMissevanWebview ||  isChannelComic;
// 是否sdk容器
const isBiliMallSdk = /mallSdkVersion/i.test(u);
const isSupportNA = isBiliApp||isBiliMallSdk;
// 获取外渠使用的SDK版本号
function getMallSdkVersion(){
    const agent = navigator.userAgent.toLowerCase();
    const regStr = /mallSdkVersion\/([0-9]+)/i;
    const m = agent.match(regStr);
    if(isBiliMallSdk && m && m[1]){
        return m[1];
    }
    return isBiliMallSdk ? (`${agent.match(regStr)}`) : 0;
}
const mallSdkVersion = getMallSdkVersion();
const isH5 = true;
const isMini = false;

const device = {
    isAlipay: false,
    isWeiXin,
    isQQ,
    isWeibo,
    isBiliMini: false,
    isBiliApp,
    isiOSBiliBlue,
    isiOS,
    isBeforeIos519,
    isH5Pay,
    isAndroid,
    appVersion,
    appBuild,
    isMissevanWebview,
    platform: deviceType,
    isChannelComic,
    isBiliMallSdk,
    mallSdkVersion,
    isChannel,
    isSupportNA,
    isH5,
    isMini,
    version: {
        is(version) {
            return isBiliApp && appVersion === appBuild[deviceType + version];
        },
        gt(version) {
            return isBiliApp && appVersion > appBuild[deviceType + version];
        },
        gte(version) {
            return isBiliApp && appVersion >= appBuild[deviceType + version];
        },
        lt(version) {
            return isBiliApp && appVersion < appBuild[deviceType + version];
        },
        lte(version) {
            return isBiliApp && appVersion <= appBuild[deviceType + version];
        }
    }
};

export default device;
