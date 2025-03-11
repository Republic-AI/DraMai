import { Node, Rect, Sprite, Vec2, Vec3 } from "cc";
import mGameConfig from "../../utils/MGameConfig";

export default class Camera2D
{
	
	private _cameraView:Rect;
	public get cameraView():Rect
	{
		return this._cameraView;
	}
	
	private _worldMap:Node;
	
	private _ease:number = 0.0025;
	
	private _focusTarget: Node = null;
	public get focusTarget(): Node {
		return this._focusTarget;
	}
	
	public constructor(scene:Node)
	{
		this._cameraView = new Rect(0, 0, mGameConfig.DeviceW, mGameConfig.DeviceH);
		this._worldMap = scene;
		// this._worldMap.scrollRect = this._cameraView;
		//_worldMap.viewport = _cameraView;
		//_worldMap.optimizeScrollRect = true;
	}
	
// 	public lookAt():void
// 	{
		
// 	}
	
// 	public onResize():void
// 	{
// 		if(this._cameraView)
// 		{
// 			this._cameraView.width = mGameConfig.DesginW;
// 			this._cameraView.height = mGameConfig.DesginH;
// 		}
// 	}
	
	/**
	 * 镜头跟随
	 */ 
	public focus(target:Node):void
	{
		this._focusTarget = target;//target 这里的target隐藏的target,用来计算和存储移动到坐标;
		this._cameraView.x = (this._focusTarget.position.x-(mGameConfig.DeviceW>>1));
		this._cameraView.y = (this._focusTarget.position.y+(mGameConfig.DeviceH>>1));
		
	}
	
	private _pos:Vec2 = new Vec2();
	public update():void
	{
		if(this._focusTarget&&this._focusTarget.position)//计算加载tile区域的左上角point
		{
			this._pos.x = (this._focusTarget.position.x-(mGameConfig.DeviceW>>1));
			this._pos.y = (this._focusTarget.position.y+(mGameConfig.DeviceH>>1));
            let gridW = 7500;
            let gridH = 8400;
			if(this._pos.x + mGameConfig.DeviceW <= gridW && this._pos.x >= 0)//图片加载更新间隔可以长一些
			{
				// this._cameraView.x = this._pos.x;//一步到位;
				this._cameraView.x += (this._pos.x - this._cameraView.x) * 33 * this._ease;
			}
			if((this._pos.y - mGameConfig.DeviceH)*-1 <= gridH && this._pos.y <= 0)
			{
				// this._cameraView.y = this._pos.y;
				this._cameraView.y += (this._pos.y - this._cameraView.y) * 33 * this._ease;
            }
			//设置图层位置
      		let x = -this._focusTarget.position.x + mGameConfig.DeviceW/2;
      		let y = -this._focusTarget.position.y -mGameConfig.DeviceH/2;
			let vec :Vec3= this._focusTarget.parent.getPosition();
			vec.x += (x - vec.x)*33*this._ease;
			vec.y += (y - vec.y)*33*this._ease;
			this._focusTarget.parent.setPosition(vec);
		}
	}
	
}