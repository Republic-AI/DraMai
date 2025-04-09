import { _decorator, Component, Node, Sprite, Label, SpriteFrame, v3 } from 'cc';
const { ccclass, property } = _decorator;

@ccclass('storyItemPrefab')
export class storyItemPrefab extends Component {
    @property(Node)
    chooseLayout: Node = null;

    @property(Node)
    normalLayout: Node = null;

    @property(Label)
    lblContent_1: Label = null;

    @property(Label)
    lblContent_2: Label = null;

    @property(Node)
    lblChoice: Node = null;

    @property(Node)
    imgStoryWin: Node = null;

    @property(Node)
    imgStoryLose: Node = null;

    @property(Node)
    imgStoryX: Node = null;

    @property(Node)
    imgStoryDown: Node = null;

    @property(Sprite)
    winNode: Sprite = null;

    @property(Sprite)
    loseNode: Sprite = null;

    @property(SpriteFrame)
    imgWin_1: SpriteFrame = null;

    @property(SpriteFrame)
    imgWin_2: SpriteFrame = null;

    @property(SpriteFrame)
    imgLose_1: SpriteFrame = null;

    @property(SpriteFrame)
    imgLose_2: SpriteFrame = null;    
    
    @property(Node)
    thirdNode: Node = null;
    
    _roomId:number = 0;
    _storyData:any = null;
    
    start() {

    }

    update(deltaTime: number) {
        
    }

    initData(roomId:number, storyData:any,index:number){
        this._roomId = roomId;
        this._storyData = storyData;     
        //{"roomId":1,"state":0,"endTime":275660,"yesCount":0,"noCount":0,"myYesCount":0,"myNoCount":0,"content":"Should Popcat ask Pippin to go fishing?","yesContent":"Popcat will ask Pippin to go fishing.","noContent":"Popcat will not ask Pippin to go fishing."}
        let isChoose = (storyData.myYesCount > 0 || storyData.myNoCount > 0)
        if(isChoose){
            this.chooseLayout.active = true;
            this.normalLayout.active = false;

            let content = storyData.content
            this.lblContent_1.string = content;

            if(storyData.yesCount > storyData.noCount){
                this.winNode.spriteFrame = this.imgWin_1;
                this.loseNode.spriteFrame = this.imgLose_2;
            }else{
                this.winNode.spriteFrame = this.imgLose_1;
                this.loseNode.spriteFrame = this.imgWin_2;
            }
            let playerIsWin = this.isVoteMatchSystem();
            if(playerIsWin){
                this.imgStoryWin.active = true;
                this.imgStoryLose.active = false;
                this.imgStoryX.active = false;
                this.lblChoice.setPosition(v3(0,82,0))
            }else{
                this.imgStoryWin.active = false;
                this.imgStoryLose.active = true;
                this.imgStoryX.active = true;
                this.lblChoice.setPosition(v3(-295,82,0))
            }
            if(index == 0){
                this.thirdNode.active = false
            }

        }else{
            this.chooseLayout.active = false;
            this.normalLayout.active = true;
            this.lblContent_2.string = storyData.content;
            if(index == 0){
                this.normalLayout.children[1].active = false
            }
        }
    }

    /**
     * 检查玩家的投票是否与系统最终结果一致
     * @returns {boolean} true表示投票与系统结果一致，false表示不一致
     */
    isVoteMatchSystem(): boolean {
        if (!this._storyData) return false;
        
        // 判断系统最终结果是赞成还是反对
        const systemVoteYes = this._storyData.yesCount > this._storyData.noCount;
        
        // 判断玩家的投票是赞成还是反对
        const playerVoteYes = this._storyData.myYesCount > this._storyData.myNoCount;
        
        // 返回玩家投票是否与系统结果一致
        return systemVoteYes === playerVoteYes;
    }

}


