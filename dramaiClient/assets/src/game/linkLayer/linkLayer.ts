import { _decorator, Component, Node, Tween, tween, UIOpacity, v3 } from 'cc';
const { ccclass, property } = _decorator;

let linkCfg = {
    10006:{url:"https://x.com/satoshi_back",caKey:""},
    10009:{url:"https://x.com/elonmusk",caKey:""},
    10008:{url:"https://x.com/pepe",caKey:""},
    10007:{url:"https://x.com/POPCATSOLANA",caKey:""},
    10010:{url:"https://x.com/pippinlovesyou",caKey:"Dfh5DzRgSvvCFDoYc2ciTkMrbDfRKybA4SoFbPmApump"},
    10011:{url:"https://x.com/elizawakesup",caKey:""},
    10012:{url:"https://x.com/realDonaldTrump",caKey:""},
    10013:{url:"https://x.com/MorphDreamAI",caKey:"8hrZax9eVqdLB1duN2fJ3ji4FwbddPsb96uauEMroBm8"},
    10014:{url:"https://x.com/AVA_holo",caKey:"DKu9kykSfbN5LBfFXtNNDPaX35o4Fv6vJ9FKk7pZpump"},
    10015:{url:"https://x.com/luna_virtuals",caKey:""},
}
@ccclass('linkLayer')
export class linkLayer extends Component {
    @property(Node)
    copyNode:Node = null;
    protected onLoad(): void {
        this.node.setScale(v3(0, 0, 1))
        tween(this.node).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).start()
    }
    start() {

    }

    update(deltaTime: number) {
        
    }

    onBtnClose(){
        tween(this.node).to(0.17, { scale: v3(0, 0, 1), position: v3(0, 40, 0) }).call(() => {
            this.node.destroy()
        }).start()
    }

    onBtnLink(target,CustomData){
        if(linkCfg[CustomData] && linkCfg[CustomData].url){
            window.location.href = linkCfg[CustomData].url;
        }
    }

    onBtnCaKey(target,CustomData){
        if(linkCfg[CustomData] && linkCfg[CustomData].caKey){
            const textToCopy = linkCfg[CustomData].caKey;  // 要复制的字符串
            // 使用 Clipboard API 来复制文本
            navigator.clipboard.writeText(textToCopy).then(()=>{
                Tween.stopAllByTarget(this.copyNode);
                Tween.stopAllByTarget(this.copyNode.getComponent(UIOpacity));
                this.copyNode.setScale(v3(0, 0, 1))
                this.copyNode.active = true;
                this.copyNode.getComponent(UIOpacity).opacity = 255;
                tween(this.copyNode).to(0.17, { scale: v3(1, 1, 1), position: v3(0, 40, 0) }).to(0.066, { position: v3(0, 0, 0) }).delay(2).call(()=>{
                    tween(this.copyNode.getComponent(UIOpacity)).to(0.5,{opacity:0}).call(()=>{
                        this.copyNode.active = false;
                    }).start()
                }).start()
            }).catch(function(err) {
              alert("复制失败: " + err);  // 复制失败时的提示
            });
        }
    }
}


