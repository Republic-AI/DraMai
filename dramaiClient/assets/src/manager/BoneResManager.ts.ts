
export default class BoneResManager {
    static _ins: BoneResManager;
    private _templetDic: object;

    constructor() {
        this._templetDic = {};
    }
    public static get ins(): BoneResManager {
        let self = this;
        if (self._ins == null) {
            self._ins = new BoneResManager();
            let agent = navigator.userAgent;
            // if (agent.indexOf('iPhone') == -1&&agent.indexOf('AWM-A0') == -1) {
            //     Laya.timer.loop(1000, self, self._ins.excGC);
            // }
            console.log('navigator.userAgent :', agent);
        }
        return self._ins;
    }
    
    // public preloadRes(arr:string[],func:Laya.Handler = null){
    //     let temparr:any[] = [];
    //     for(let i = 0;i<arr.length;i++){
    //         let png = arr[i].replace(".sk",".png");
    //         temparr.push({url:arr[i],type:Laya.Loader.BUFFER});
    //         temparr.push({url:png,type:Laya.Loader.IMAGE});
    //         if(Laya.loader.getRes(arr[i])){
    //            // Laya.loader.clearRes(arr[i]);
               
    //             console.log("has load:"+arr[i]);
    //         }
    //         if(Laya.loader.getRes(png)){
    //            // Laya.loader.clearRes(png);
               
    //             console.log("has load:"+png);
    //         }
    //     }
    //     Laya.loader.load(temparr, func,Laya.Handler.create(this,(progress,...args)=>{
    //         console.log('加载进度: ' + progress);
    //     }));
    //     Laya.loader.on(Laya.Event.ERROR, this, (da) => {
           
    //         console.log('加载错误1: ' + da);
    //     });
    // }
    // /**
    //  * 超尺寸龙骨资源加载
    //  * @param arr 
    //  * @param func 
    //  */
    // public preloadRes2(arr:string[],func:Laya.Handler = null){
    //     let temparr:any[] = [];
    //     console.log(arr,'arrpreloadRes2');
    //     for(let i = 0;i<arr.length;i++){
    //         if(arr[i].indexOf(".sk")!=-1){
    //             temparr.push({url:arr[i],type:Laya.Loader.BUFFER});
    //         }else if(arr[i].indexOf(".png")!=-1){
    //             temparr.push({url:arr[i],type:Laya.Loader.IMAGE});
    //         }
           
    //         if(Laya.loader.getRes(arr[i])){
    //            // Laya.loader.clearRes(arr[i]);
    //             console.log("has load:"+arr[i]);
    //         }
    //     }
    //     console.log(temparr,'temparr');
    //     Laya.loader.load(temparr, func);
    //     Laya.loader.on(Laya.Event.ERROR, this, (da) => {
    //         console.log('加载错误2: ' + da);
    //     });
    // }
    // public hasTemp(url: string): boolean {
    //     if (this._templetDic[url]) {
    //         return true;
    //     }
    //     return false;
    // }
    // public static _frominit: boolean;
    // public getSkelet(url: string, parseComplete: Laya.Handler) : Laya.Skeleton{
    //     if (!this._templetDic[url]) {
    //         let mFactory = new Laya.Templet();
    //         let obj = {
    //             url: url,
    //             templet: mFactory,
    //             gettime: Laya.timer.currTimer,
    //         };
    //         this._templetDic[url] = obj;
    //         mFactory.on(Laya.Event.COMPLETE, this, this._parseComplete, [url, parseComplete]);
    //         console.log('getSkelet', url);
    //         mFactory.on(Laya.Event.ERROR, this, this.onError, [url]);
    //         mFactory.loadAni(obj.url);
    //     } else {
    //         this._templetDic[url].gettime = Laya.timer.currTimer;
    //         let ske;
    //         try{
    //             ske = this._createSke(url);
    //         }catch(e){
    //             this._templetDic[url] = null;
    //             this.getSkelet(url,parseComplete)
    //         }
    //         return ske;
    //     }
    // }
    // private onError(url, evt): void {
    //     console.log('error', evt,url);
    //     if (BoneResManager._frominit) {
    //         let bus = window['bus'];
    //         bus.emit(bus.busName.game.resourceLoadFail);
    //         this._templetDic[url] = {};
    //     }else{
    //         showMsg2("网络异常，请检查网络设置");
    //     }
    // }
    // private _createSke(url): Laya.Skeleton {
    //     let s = this;
    //     if (!this._templetDic[url]) {
           
    //         for (let key in s._templetDic) {
    //             if (this._templetDic[key] && this._templetDic[key].templet) {
    //                 url = key;
    //                 break;
    //             }
    //         }
    //     }
    //     if(!this._templetDic[url])return null;
    //     let templ: Laya.Templet = this._templetDic[url].templet;
    //     let ske: Laya.Skeleton;
    //     if (templ) {
    //         ske = templ.buildArmature();
    //     }
    //     return ske;
    // }
    // private _parseComplete(url: string, parseComplete: Laya.Handler) {
    //     console.log('_parseComplete:', url, typeof parseComplete);
    //     let ske = this._createSke(url);
    //     if (parseComplete) {
    //         parseComplete.runWith(ske);
    //     }
    // }
    // public excGC() {
    //     let s = this;
    //     for (let key in s._templetDic) {
    //         let lasttime = s._templetDic[key].gettime;
    //         if (Laya.timer.currTimer - lasttime > 180000) {
    //             let templ: Laya.Templet = this._templetDic[key].templet;

    //             if (templ) {
                   
    //                 templ.off(Laya.Event.COMPLETE, this, this._parseComplete);
    //                 templ.destroy(); //释放动画模板类下的纹理数据
    //                 this._templetDic[key] = null;
    //             }
    //         }
    //     }
    // }
    // public clearBone(url) {
    //     let s = this;
    //     s._templetDic[url] = null;
    //     // Laya.Resource.destroyUnusedResources();
    // }
    // public destroy() {
    //     let s = this;
    //     for (let key in s._templetDic) {
    //         if (!this._templetDic[key]) continue;
    //         let lasttime = s._templetDic[key].gettime;
    //         let templ: Laya.Templet = this._templetDic[key].templet;
    //         if (templ) {
    //             templ.off(Laya.Event.COMPLETE, this, this._parseComplete);
    //             templ.destroy();
    //             this._templetDic[key] = null;
    //             templ = null;
    //         }
    //     }
    //     this._templetDic = {};
       
    // }
}
