import { Component, Node, Sprite, Vec3 } from "cc";
import ObjDictionary from "../../core/dataStructue/ObjDictionary";
import mGameConfig from "../../utils/MGameConfig";
import Camera2D from "./Camera2D";
import LayerEnum from "./LayerEnum";

export default class SceneManager
{
	
	private _camera:Camera2D;
	public get camera():Camera2D
	{
		return this._camera;
	}
	
	
	private _scene:Node;
	public get scene():Node
	{
		return this._scene;
	}
	

	private _layer_map:Node;
	
	private _layer_actor:Node;
	
	private _layer_effect:Node;
	
	private _layer_tip:Node;
	
	private _layer_debug:Node;
	
	private _layerDic:ObjDictionary;
	
	public constructor()
	{

	}
	
	public init():void
	{
		// this.init2dScene();
		//Laya.stage.scrollRect = new Rectangle(0, 0, mGameConfig.DeviceW, mGameConfig.DeviceH); 
	}
	
	public init2dScene(scene:Node):void
	{
		this._scene = scene;
		this._layer_map = this._scene.getChildByPath("_layer_map");
		this._layer_actor =  this._scene.getChildByPath("_layer_actor");
		this._layer_effect =  this._scene.getChildByPath("_layer_effect");
		this._layer_debug =  this._scene.getChildByPath("_layer_debug");
		// this._layer_tip =  this._scene.getChildByPath("_layer_tip");
        
		this._layer_debug.active = mGameConfig.IsDebug;
		this._layerDic = new ObjDictionary(Node);
		this._layerDic.add(LayerEnum.MapLayer, this._layer_map);
		this._layerDic.add(LayerEnum.ActorLayer, this._layer_actor);
		this._layerDic.add(LayerEnum.EffectLayer, this._layer_effect);
		this._layerDic.add(LayerEnum.DebugLayer, this._layer_debug);
		// this._layerDic.add(LayerEnum.TipLayer, this._layer_tip);
		
		this._camera = new Camera2D(this._scene);
	}
	
	
	

	// public showAutoPlayerAni():void
	// {
	// 	let ani:Laya.Animation = new Laya.Animation();
	// 	ani.loadImages(["ui_main/ui_auto_01.png","ui_main/ui_auto_02.png","ui_main/ui_auto_03.png","ui_main/ui_auto_04.png"]);
	// 	ani.pos((mGameConfig.DeviceW>>1)-100, (mGameConfig.DeviceH>>1) + 200);
	// 	Laya.stage.addChild(ani);
	// 	ani.interval = 250;
	// 	ani.play();
	// }
	
	public addToLayer(spr:Node, layer:String=LayerEnum.MapLayer, posX:number=0, posY:number=0):void
	{
		let layerSpr:Node = this._layerDic.get(layer) as Node;
		if(layerSpr)
		{
			layerSpr.addChild(spr);
            spr.setPosition(new Vec3(posX,posY,0));
		}
		else
		{
			console.log("can not find layer " + layer);				
		}
	}
	
	public getLayer(layer:string):Node
	{
		return  this._layerDic.get(layer);
	}
	

	// public getGlobalScreenPos():Point
	// {
	// 	if(this._scene)
	// 		return this._scene.localToGlobal(new Point(this._scene.mouseX, this._scene.mouseY));
	// 	else
	// 		return new Point();
	// }
	
	// public getMousePos():Point
	// {
	// 	if(this._scene)
	// 		return new Point(this._scene.mouseX, this._scene.mouseY);
	// 	else
	// 		return new Point();
	// }
	
	public update():void
	{
		if(this._camera)
			this._camera.update();
	}
	
	private static _instance:SceneManager
	public static get instance():SceneManager
	{
		if(!this._instance){ this._instance = new SceneManager(); }
		return this._instance;
	}
	
}