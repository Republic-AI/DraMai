import { _decorator, Camera, Canvas, Component, director, EventTouch, Node, Script, ScrollView, SpriteFrame, TiledMap, Tween, tween, UITransform, v2, v3, Vec2, view } from 'cc';
import { NpcManager } from '../../NPC/NpcManager';
import { observer } from '../App';
import { EventType } from '../../EventType';
import { changeSkinPrefab } from '../gameUI/changeSkinPrefab';
import { furnitureNode } from '../gameUI/furnitureNode';
import { sceneItemAllCfg } from '../../StaticUtils/NPCConfig';
import { secondConfirmNode } from '../gameUI/secondConfirmNode';
const { ccclass, property } = _decorator;

@ccclass('GameScene')
export class GameScene extends Component {
    @property(Camera)
    mainCamera:Camera = null;

    @property(ScrollView)
    mapView:ScrollView = null;

    @property(Node)
    mapModel:Node = null;

    @property(Node)
    cancelFollowNode:Node = null;

    @property(Node)
    secondConfimrNode:Node = null;

    _orignOrthoHeight = null;
    _cameraStatus:string =  null;
    _npcID = 10006;
    _followID = 0;
    _isAlive = false;
    _mapLayerScript  = null;
    _version = null;
    isDragging = false;
    lastMousePos = null;
    _followNpcId = 0;

    protected onLoad(): void {
        // this.node.on(Node.EventType.TOUCH_START, (event)=>{
        //     //const scroll = this.scrollNode.getComponent(ScrollView)
        //     this.cancelFollowNpc()
        // })

        // this.node.on(Node.EventType.TOUCH_END, (event: EventTouch)=>{
        //     const startPos = event.touch.getStartLocation()
        //     const prePos = event.touch.getPreviousLocation()
        //     if(Math.abs(startPos.y - prePos.y) >  Math.abs(startPos.x - prePos.x)){
        //         if(startPos.y > prePos.y){
        //             console.log("向下滑动")
        //             //向上滑动
        //             // const scroll = this.scrollNode.getComponent(ScrollView)
        //             // scroll.scrollToOffset(new Vec2(scroll.getScrollOffset().x,scroll.getScrollOffset().y - 930), 1, true)
        //         }
        //         if(startPos.y < prePos.y){ 
        //             console.log("向上滑动")
        //             // const scroll = this.scrollNode.getComponent(ScrollView)
        //             // scroll.scrollToOffset(new Vec2(scroll.getScrollOffset().x,scroll.getScrollOffset().y + 930), 1, true)
        //         }
        //     }else{
        //         if(startPos.x > prePos.x){
        //             console.log('====left')
        //             console.log("向左滑动")
        //             // if(this.canvas.scale.x === 2){
        //             //     this.canvas.setScale(1,1)
        //             // }
        //             this.changeCameraStatus()
        //         }
        //         if(startPos.x < prePos.x){
        //             console.log('====right')
        //             console.log("向右滑动")
        //             // if(this.canvas.scale.x === 1){
        //             //     this.canvas.setScale(2,2)
        //             // }
        //             this.changeCameraStatus()
        //         }
        //     }
        // })
        this._cameraStatus = "normal";
        this.mainCamera.node.getComponent(UITransform).setContentSize(view.getVisibleSize().width/2,view.getVisibleSize().height/2);
        this.mainCamera.node.getChildByName("UILayer").getComponent(UITransform).setContentSize(view.getVisibleSize().width,view.getVisibleSize().height);

        this.mapView.node.setScale(1.7,1.7)
        observer.on(EventType.TRANSPORT,this.transportToNpc,this);
        observer.on(EventType.FOLLOWNPC,this.followNpc,this);
        observer.on(EventType.POPCHANGEITEM,this.popChangeItem,this);
        observer.on(EventType.POPCHANGESKIN,this.popChangeSkin,this);

        const currentUrl = window.location.href;
        const url = new URL(currentUrl);
        // 获取查询参数
        // let codeParam = url.searchParams.get("version");
        // if(codeParam == "live"){
        //     // this.mapView.horizontal = false;
        //     // this.mapView.vertical = false;
        //     this._isAlive = true;
        //     tween(this.node).repeatForever(tween(this.node).delay(0.1).call(()=>{
        //         if(director.getScene().getComponentInChildren(NpcManager)){
        //             console.log("checkLieveStart")
        //             let npcID = director.getScene().getComponentInChildren(NpcManager).NpcID;
        //             Tween.stopAllByTarget(this.node);
        //             this.checkLieveStyle(npcID);
        //         }
        //     }).start()).start();
        // }
        // if(currentUrl.includes("morpheus")){
        //     tween(this.node).repeatForever(tween(this.node).delay(0.1).call(()=>{
        //         director.getScene().getComponentsInChildren(NpcManager).forEach(npcMgr=>{
        //             if(npcMgr.NpcID == 10013){
        //                 Tween.stopAllByTarget(this.node);
        //                 this.flollowNpcByLive(10013);
        //             }
        //         })
        //     }).start()).start();
        // }
        // if(currentUrl.includes("pippin")){
        //     tween(this.node).repeatForever(tween(this.node).delay(0.1).call(()=>{
        //         director.getScene().getComponentsInChildren(NpcManager).forEach(npcMgr=>{
        //             if(npcMgr.NpcID == 10010){
        //                 Tween.stopAllByTarget(this.node);
        //                 this.flollowNpcByLive(10010);
        //             }
        //         })
        //     }).start()).start();
        // }
        // if(currentUrl.includes("capture")){
        //     this._version = "capture";
        // }

        // if(currentUrl.includes("newcapture")){
        //     this._version = "newcapture";
        //     tween(this.node).repeatForever(tween(this.node).delay(0.1).call(()=>{
        //         director.getScene().getComponentsInChildren(NpcManager).forEach(npcMgr=>{
        //             if(npcMgr.NpcID == 10006){
        //                 Tween.stopAllByTarget(this.node);
        //                 this.flollowNpcByLive(10006);
        //             }
        //         })
        //     }).start()).start();
        // }

        //监听触摸事件（适配触控面板和移动端）
        // this.node.on(Node.EventType.TOUCH_MOVE,this.onTouchMove,this);
        //      // 监听鼠标事件（适配电脑端鼠标）
        // this.node.on(Node.EventType.MOUSE_DOWN, this.onMouseDown, this);
        // this.node.on(Node.EventType.MOUSE_MOVE, this.onMouseMove, this);
        // this.node.on(Node.EventType.MOUSE_UP, this.onMouseUp, this);
        // this.node.on(Node.EventType.MOUSE_LEAVE, this.onMouseUp, this);

        // // 禁用鼠标滚轮缩放
        // this.node.on(Node.EventType.MOUSE_WHEEL, (event) => {
        //     //event.stopPropagation();
        // });


    }

    start() {
        this.initData()
    }

    checkLieveStyle(npcID){
        this.flollowNpcByLive(npcID);
        tween(this.node).delay(30).call(()=>{
            console.log("checkLieveNpc====" + npcID);
            director.getScene().getComponentsInChildren(NpcManager).forEach((data,index)=>{
                if(Number(data.NpcID) == Number(npcID)){
                    let targetIndex = index + 1;
                    if(!director.getScene().getComponentsInChildren(NpcManager)[targetIndex]){
                        targetIndex = 0;
                    }
                    let tagetNpcID = director.getScene().getComponentsInChildren(NpcManager)[targetIndex].NpcID;
                    this.checkLieveStyle(tagetNpcID);
                }    
            })
        }).start()
    }

    update(deltaTime: number) {
        
    }

    protected onDestroy(): void {
        observer.off(EventType.TRANSPORT,this.followNpc,this);
        observer.off(EventType.FOLLOWNPC,this.followNpc,this);
        observer.off(EventType.POPCHANGEITEM,this.popChangeItem,this);
        observer.off(EventType.POPCHANGESKIN,this.popChangeSkin,this);
    }

    initData(){
        this._orignOrthoHeight = this.mainCamera.orthoHeight;
        this._npcID = 10006;
        this.mainCamera.orthoHeight = 700;
        this.finishCameraFollow()
    }

    startCameraFollow(){
        this._cameraStatus = "bigScale";
        let followNpcID = this._npcID;
        let npcNode = null;
        let npcTileLayer = null;
        this.node.getComponentsInChildren(NpcManager).forEach(npc=>{
            if(npc.NpcID == followNpcID){
                npcNode = npc.node;
                npcTileLayer = npc._tileMapLayer;
            }
        })

        tween(this.mainCamera.node).repeatForever(tween(this.mainCamera.node).delay(0.01).call(()=>{
            //this.mainCamera.orthoHeight = this._orignOrthoHeight/2;
            let npcWorldPos = npcNode.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
            let tilePos = npcTileLayer.getComponent(UITransform).convertToNodeSpaceAR(npcWorldPos);
            let visibleSize = view.getVisibleSize();
            let tiledLayerSize = npcTileLayer.getComponent(UITransform).contentSize;
            let canvasNpcPos = this.node.getComponent(UITransform).convertToNodeSpaceAR(npcWorldPos);
            if((tilePos.y * 2+  visibleSize.height / 2) > tiledLayerSize.height){
                let offsetY = tiledLayerSize.height - visibleSize.height/2;
                this.mainCamera.node.setPosition(canvasNpcPos.x,offsetY,0);
            }
            else if ((tilePos.y * 2 - visibleSize.height / 2) < -tiledLayerSize.height){
                let offsetY = tiledLayerSize.height - visibleSize.height/2;
                this.mainCamera.node.setPosition(canvasNpcPos.x,-offsetY,0);
            }
            else{
                this.mainCamera.node.setPosition(canvasNpcPos.x,canvasNpcPos.y,0);
            }
    
            if((tilePos.x * 2+  visibleSize.width / 2) > tiledLayerSize.width){
                let offsetX = tiledLayerSize.width  - visibleSize.width/2;
                this.mainCamera.node.setPosition(offsetX,this.mainCamera.node.position.y,0);
            }
            else if ((tilePos.x * 2 - visibleSize.width / 2) < -tiledLayerSize.width){
                let offsetX = tiledLayerSize.width - visibleSize.width/2;;
                this.mainCamera.node.setPosition(-offsetX,this.mainCamera.node.position.y,0);
            }
            else{
                this.mainCamera.node.setPosition(canvasNpcPos.x,this.mainCamera.node.position.y,0);
            }
        }).start()).start();
        // let npcCamera = npcNode.getComponentInChildren(Camera);
        // npcCamera.node.active = true;
        // this.mainCamera.node.active = false;
        //this.UILayer.setScale(0.5,0.5);
    }

    changeNpcID(npcID:number){
        this._npcID = npcID;
    }

    getNpcID(){
        return this._npcID;
    }

    finishCameraFollow(){
        this._cameraStatus = "normal";
        Tween.stopAllByTarget(this.mainCamera.node);
        this.mainCamera.orthoHeight = this._orignOrthoHeight;
        this.mainCamera.node.setPosition(v3(0,0,0));
        //this.UILayer.setScale(1,1);
    }

    onBtnTest1(){
        let followNpcID = 10006;
        let npcNode = null;
        this.node.getComponentsInChildren(NpcManager).forEach(npc=>{
            if(npc.NpcID == followNpcID){
                npcNode = npc.node;
            }
        })
        let npcCamera = npcNode.getComponentInChildren(Camera);
        npcCamera.node.active = true;
        this.mainCamera.node.active = false;
    }

    onBtnTest2(){
        this.node.getComponentsInChildren(Camera).forEach(camera=>{
            camera.node.active = false;
        })
        this.mainCamera.node.active = true;
    }

    changeCameraStatus(){
        if(this._cameraStatus == "normal"){
            this.startCameraFollow();
            this.node.getComponentInChildren(TiledMap).node.setScale(2,2,1);
        }
        else{
            this.finishCameraFollow();
            this.node.getComponentInChildren(TiledMap).node.setScale(1,1,1);
        }
        //this.UILayer.getComponent(UILayer).updateViewNode(this._cameraStatus);
    }

    transportToNpc(npcID){
        if(this._isAlive){
            return;
        }
        if(this._cameraStatus == "followNpc"){
            return;
        }
        let npcNode = null;
        director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
            if(npcScript.NpcID == npcID.data){
                npcNode = npcScript.node
            }
        })
        if(!npcNode){
            return;
        }
        if(!(npcNode instanceof Node)){
            return;
        }
        if(!(this.node instanceof Node)){
            return;
        }
        let npcWorldPos = npcNode.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
        let mapWorldPos = this.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));

        let offset = v3(npcWorldPos.x - mapWorldPos.x,npcWorldPos.y-mapWorldPos.y,0);
        let targetPos = v3(this.mapModel.position.x - offset.x,this.mapModel.position.y - offset.y,0);
        let maxY = this.mapModel.getComponent(UITransform).contentSize.height / 2 -  view.getVisibleSize().height/2;
        let maxX = this.mapModel.getComponent(UITransform).contentSize.width / 2 -  view.getVisibleSize().width/2;
        if(Math.abs(targetPos.x) > maxX){
            if(targetPos.x > 0){
                targetPos.x = maxX;
            }
            else{
                targetPos.x = -maxX;
            }
        }

        if(Math.abs(targetPos.y) > maxY){
            console.log("2222222222")
            if(targetPos.y > 0){
                targetPos.y = maxY;
            }
            else{
                targetPos.y = -maxY;
            }
        }
        console.log("maxX======" + maxX);
        console.log("maxY======" + maxY);
        tween(this.node).delay(0.3).call(()=>{
            this.cancelFollowNode.active = true;
            this.cancelFollowNode.getComponentInChildren(changeSkinPrefab).initData(npcID.data);
        }).start();
        //console.log("targetPos=======" + JSON.stringify(targetPos));
        // this.mapModel.setPosition(targetPos);
        // this.mapView.scrollToOffset(v2(targetPos.x,targetPos.y),0.5);
        // this.mapView.scrollToOffset(v2(10,10),0.5,true);
        // this.mapView.scrollToOffset(this._orignOffset,0.5,true);
        // this.mapView.scrollToOffset(v2(this._orignOffset.x - targetPos.x,this._orignOffset.y-targetPos.y),0.5);
        // let targetPosition = targetPos; // 目标position
        // let contentSize = this.mapModel.getComponent(UITransform).contentSize;
        // let scrollViewSize = this.mapView.node.getComponent(UITransform).contentSize;
        
        // // 水平滚动
        // let adjustedX = targetPosition.x + contentSize.width / 2 - scrollViewSize.width / 2;
        // let percentX = adjustedX / (contentSize.width - scrollViewSize.width);

        // // 垂直滚动
        // let adjustedY = targetPosition.y + contentSize.height / 2 - scrollViewSize.height / 2;
        // let percentY = 1 - (adjustedY / (contentSize.height - scrollViewSize.height));

        // // 限制百分比范围
        // percentX = Math.min(1, Math.max(0, percentX));
        // percentY = Math.min(1, Math.max(0, percentY));

        
        // // 调用scrollTo
        //this.mapView.scrollTo(v2(percentX, percentY), 0.5, true);
        //this.mapView.scrollTo(v2(anchorPercentX,anchorPercentY),0.1);
        //-640,-1412
        // this.mapView.scrollTo(v2(1, 1), 0.5, true);
        // return;
        let percentX = (maxX-targetPos.x) / (maxX * 2);
        let percentY = (maxY-targetPos.y) / (maxY * 2);
        this.mapView.scrollTo(v2(percentX, percentY), 0.5, true);
    }

    followNpc(npcID){
        Tween.stopAllByTarget(this.node);
        this.mapView.horizontal = false;
        this.mapView.vertical = false;
        this._cameraStatus = "followNpc";
        this.cancelFollowNode.active = true;
        this.cancelFollowNode.getComponentInChildren(changeSkinPrefab).initData(npcID.data);
        tween(this.node).repeatForever(tween(this.node).delay(0.01).call(()=>{
            let npcNode = null;
            director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
                if(npcScript.NpcID == npcID.data){
                    npcNode = npcScript.node
                }
            })
            if(!npcNode){
                return;
            }
            let npcWorldPos = npcNode.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
            let mapWorldPos = this.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
    
            let offset = v3(npcWorldPos.x - mapWorldPos.x,npcWorldPos.y-mapWorldPos.y,0);
            let targetPos = v3(this.mapModel.position.x - offset.x,this.mapModel.position.y - offset.y,0);
            let maxY = this.mapModel.getComponent(UITransform).contentSize.height / 2 -  view.getVisibleSize().height/2;
            let maxX = this.mapModel.getComponent(UITransform).contentSize.width / 2 -  view.getVisibleSize().width/2;
            if(Math.abs(targetPos.x) > maxX){
                if(targetPos.x > 0){
                    targetPos.x = maxX;
                }
                else{
                    targetPos.x = -maxX;
                }
            }
    
            if(Math.abs(targetPos.y) > maxY){
                if(targetPos.y > 0){
                    targetPos.y = maxY;
                }
                else{
                    targetPos.y = -maxY;
                }
            }
            this.mapModel.setPosition(targetPos);
        }).start()).start()
    }

    cancelFollowNpc(){
        this.mapView.horizontal = true;
        this.mapView.vertical = true;
        this._cameraStatus = "normal";
        Tween.stopAllByTarget(this.node);
        this.cancelFollowNode.active = false;
    }

    cancelFollowPlayer(){
        this._cameraStatus = "normal";
        Tween.stopAllByTarget(this.node);
    }

    onBtnConfirmChange(){
        this.cancelFollowNpc();
    }

    flollowNpcByLive(npcID){
        Tween.stopAllByTarget(this.node);
        // this.mapView.horizontal = false;
        // this.mapView.vertical = false;
        this._cameraStatus = "followNpc";
        this._followNpcId = npcID;
        console.log("followNpc=====" + npcID);
        tween(this.node).repeatForever(tween(this.node).delay(0.01).call(()=>{
            let npcNode = null;
            //console.log("ttttttttt======" + npcID);
            director.getScene().getComponentsInChildren(NpcManager).forEach(npcScript=>{
                if(npcScript.NpcID == npcID){
                    npcNode = npcScript.node
                }
                npcScript.showNpcName();
            })
            if(!npcNode){
                Tween.stopAllByTarget(this.node);
                //console.log("bbbbbbbbb");
                return;
            }
            let npcWorldPos = npcNode.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
            let mapWorldPos = this.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
    
            let offset = v3(npcWorldPos.x - mapWorldPos.x,npcWorldPos.y-mapWorldPos.y,0);
            let targetPos = v3(this.mapModel.position.x - offset.x,this.mapModel.position.y - offset.y,0);
            let maxY = this.mapModel.getComponent(UITransform).contentSize.height / 2 -  view.getVisibleSize().height/2;
            let maxX = this.mapModel.getComponent(UITransform).contentSize.width / 2 -  view.getVisibleSize().width/2;
            if(Math.abs(targetPos.x) > maxX){
                if(targetPos.x > 0){
                    targetPos.x = maxX;
                }
                else{
                    targetPos.x = -maxX;
                }
            }
    
            if(Math.abs(targetPos.y) > maxY){
                if(targetPos.y > 0){
                    targetPos.y = maxY;
                }
                else{
                    targetPos.y = -maxY;
                }
            }
            this.mapModel.setPosition(targetPos);
        }).start()).start()
    }

    flollowPlayer(){
        Tween.stopAllByTarget(this.node);
        // this.mapView.horizontal = false;
        // this.mapView.vertical = false;
        this._cameraStatus = "followNpc";
        tween(this.node).repeatForever(tween(this.node).delay(0.01).call(()=>{
            let npcNode = this._mapLayerScript.getPlayerNode();
            //console.log("ttttttttt======" + npcID);

            if(!npcNode){
                //console.log("bbbbbbbbb");
                return;
            }
            let npcWorldPos = npcNode.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
            let mapWorldPos = this.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
    
            let offset = v3(npcWorldPos.x - mapWorldPos.x,npcWorldPos.y-mapWorldPos.y,0);
            let targetPos = v3(this.mapModel.position.x - offset.x,this.mapModel.position.y - offset.y,0);
            let maxY = this.mapModel.getComponent(UITransform).contentSize.height / 2 -  view.getVisibleSize().height/2;
            let maxX = this.mapModel.getComponent(UITransform).contentSize.width / 2 -  view.getVisibleSize().width/2;
            if(Math.abs(targetPos.x) > maxX){
                if(targetPos.x > 0){
                    targetPos.x = maxX;
                }
                else{
                    targetPos.x = -maxX;
                }
            }
    
            if(Math.abs(targetPos.y) > maxY){
                if(targetPos.y > 0){
                    targetPos.y = maxY;
                }
                else{
                    targetPos.y = -maxY;
                }
            }
            this.mapModel.setPosition(targetPos);
        }).start()).start()
    }


    setMapScript(mapScript){
        this._mapLayerScript = mapScript;
    }

    getMapScript(){
        return this._mapLayerScript;
    }

    getVersion(){
        return this._version;
    }

    // 触控面板或移动端的拖拽逻辑
    onTouchMove(event) {
        let touches = event.getTouches();

        if (touches.length === 2) {
            // 双指拖拽逻辑
            //event.stopPropagation();
            let delta1 = touches[0].getDelta();
            let delta2 = touches[1].getDelta();

            let averageDelta = v3(
                (delta1.x + delta2.x) / 2,
                (delta1.y + delta2.y) / 2,
                0
            );

            this.mapView.content.position = this.mapView.content.position.add(averageDelta);
            console.log("双指拖拽增量====" + JSON.stringify(averageDelta));
        } else if (touches.length === 1) {
            // 单指默认拖拽（ScrollView 自带逻辑处理）
        }
    }
    
        // 鼠标按下：开始拖拽
        onMouseDown(event) {
            this.isDragging = true;
            this.lastMousePos = event.getLocation(); // 记录鼠标初始位置
        }
    
        // 鼠标移动
        onMouseMove(event) {
            if (this.isDragging) {
                let currentMousePos = {
                    x: event.getLocationX(),
                    y: event.getLocationY(),
                };
        
                // 手动计算增量（currentMousePos - lastMousePos）
                let delta = {
                    x: currentMousePos.x - this.lastMousePos.x,
                    y: currentMousePos.y - this.lastMousePos.y,
                };
        
                // 更新 ScrollView 内容的位置
                this.mapView.content.position = this.mapView.content.position.add(v3(delta.x, delta.y,0));
        
                // 更新最后鼠标位置
                this.lastMousePos = currentMousePos;
            }
        }
    
        // 鼠标抬起/离开：结束拖拽
        onMouseUp() {
            this.isDragging = false; // 停止拖拽
            this.lastMousePos = null;
        }

        getFollowNpcId(){
            return this._followNpcId;
        }

        getMyPlayerNode(){
            let npcNode = this._mapLayerScript.getPlayerNode();
            return npcNode;
        }

        transportToSceneItem(type){
            let sceneItemNode = null;
            director.getScene().getComponentsInChildren(furnitureNode).forEach(itemScript=>{
                if(itemScript.type == type){
                    sceneItemNode = itemScript.node
                }
            })
            if(!sceneItemNode){
                console.log("no scene item");
                return;
            }
        
            let npcWorldPos = sceneItemNode.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
            let mapWorldPos = this.node.getComponent(UITransform).convertToWorldSpaceAR(v3(0,0,0));
    
            let offset = v3(npcWorldPos.x - mapWorldPos.x,npcWorldPos.y-mapWorldPos.y,0);
            let targetPos = v3(this.mapModel.position.x - offset.x,this.mapModel.position.y - offset.y,0);
            let maxY = this.mapModel.getComponent(UITransform).contentSize.height / 2 -  view.getVisibleSize().height/2;
            let maxX = this.mapModel.getComponent(UITransform).contentSize.width / 2 -  view.getVisibleSize().width/2;
            if(Math.abs(targetPos.x) > maxX){
                if(targetPos.x > 0){
                    targetPos.x = maxX;
                }
                else{
                    targetPos.x = -maxX;
                }
            }
    
            if(Math.abs(targetPos.y) > maxY){
                console.log("2222222222")
                if(targetPos.y > 0){
                    targetPos.y = maxY;
                }
                else{
                    targetPos.y = -maxY;
                }
            }
            console.log("maxX======" + maxX);
            console.log("maxY======" + maxY);
            //console.log("targetPos=======" + JSON.stringify(targetPos));
            // this.mapModel.setPosition(targetPos);
            // this.mapView.scrollToOffset(v2(targetPos.x,targetPos.y),0.5);
            // this.mapView.scrollToOffset(v2(10,10),0.5,true);
            // this.mapView.scrollToOffset(this._orignOffset,0.5,true);
            // this.mapView.scrollToOffset(v2(this._orignOffset.x - targetPos.x,this._orignOffset.y-targetPos.y),0.5);
            // let targetPosition = targetPos; // 目标position
            // let contentSize = this.mapModel.getComponent(UITransform).contentSize;
            // let scrollViewSize = this.mapView.node.getComponent(UITransform).contentSize;
            
            // // 水平滚动
            // let adjustedX = targetPosition.x + contentSize.width / 2 - scrollViewSize.width / 2;
            // let percentX = adjustedX / (contentSize.width - scrollViewSize.width);
    
            // // 垂直滚动
            // let adjustedY = targetPosition.y + contentSize.height / 2 - scrollViewSize.height / 2;
            // let percentY = 1 - (adjustedY / (contentSize.height - scrollViewSize.height));
    
            // // 限制百分比范围
            // percentX = Math.min(1, Math.max(0, percentX));
            // percentY = Math.min(1, Math.max(0, percentY));
    
            
            // // 调用scrollTo
            //this.mapView.scrollTo(v2(percentX, percentY), 0.5, true);
            //this.mapView.scrollTo(v2(anchorPercentX,anchorPercentY),0.1);
            //-640,-1412
            // this.mapView.scrollTo(v2(1, 1), 0.5, true);
            // return;
            this.mapModel.setPosition(targetPos);
        }

        popChangeItem(data){
            this.cancelFollowNpc();
            this.secondConfimrNode.active = true;
            let itemType = data.data.type
            this.transportToSceneItem(itemType)
            this.secondConfimrNode.getComponent(secondConfirmNode).initData("sceneItem",data.data);
        }

        popChangeSkin(data){
            this.cancelFollowNpc();
            this.secondConfimrNode.active = true;
            this.secondConfimrNode.getComponent(secondConfirmNode).initData("changeSkin",data.data);
        }

}


