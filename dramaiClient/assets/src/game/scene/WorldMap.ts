import { Layers, Node,Rect,Sprite, Vec2 } from "cc";
import ObjDictionary from "../../core/dataStructue/ObjDictionary";
import mGameConfig from "../../utils/MGameConfig";
import { sceneManager } from "../App";
import LayerEnum from "./LayerEnum";
import MapGridVO from "./MapGridVO";
import MapTile from "./MapTile";
import SceneManager from "./SceneManager";

export default class WorldMap
{
	
	private _container:Node;
	public get container():Node
	{
		return this._container;
	}
 
	
	private _gridVO:MapGridVO = new MapGridVO();
	public get gridVO():MapGridVO
	{
		return this._gridVO;
	}
	
	private _gridTiles:Array<Array<MapTile>>;
	public get gridTiles():Array<Array<MapTile>>
	{
		return this._gridTiles;
	}
	
	/**
	 * 已经加载的Tile 
	 */		
	private _loadedTiles:ObjDictionary = new ObjDictionary();

	public constructor()
	{
		this._container = new Node();
        this._container.layer = Layers.Enum.UI_2D;;
        this.init();
	}
	
	public init():void
	{
		//let bg:Sprite = new Sprite();
		//bg.loadImage("res/bg3.jpg");
		//_container.addChild(bg);
		this.createTiles();
	}
	public reset(){
		WorldMap._instance = null;
	}
	
	private createTiles():void
	{
		this._gridTiles = new Array<Array<MapTile>>();
		for(let col:number=0; col<this._gridVO.col; col++)
		{
			this._gridTiles[col] = new Array<MapTile>();
			for(let row:number=0; row<this._gridVO.row; row++)			
			{
				this._gridTiles[col][row] = new MapTile(row, col, this._container);
			}
		}
	}
	

	public update():void
	{
		if(!SceneManager.instance.camera||!this._container.parent)return;
		let camView:Rect = SceneManager.instance.camera.cameraView; 
		let tiles:ObjDictionary = this.getNeedLoadTiles(camView.x, camView.y);
        // console.log(JSON.stringify(tiles.container)+"=========================================");
		this.loadTiles(tiles);
        let vec = SceneManager.instance.camera.focusTarget.parent.position;//actor_layer
        this._container.parent.setPosition(vec );//map_layer
		sceneManager.getLayer(LayerEnum.DebugLayer).setPosition(vec);

	}
	
	public loadTiles(tiles:ObjDictionary):void
	{
		if(tiles && tiles.length > 0)
		{
			let col:number, row:number;
			let arr:Array<string>, key:string;
			let strKey:string;
			//先卸载  暂不卸载,在动态加载图片显示时,cocos 会非常卡
			// for(key in this._loadedTiles.container)
			// {
			// 	if(!tiles.containsKey(key))
			// 	{
			// 		arr = key.split("_");
			// 		col = parseInt(arr[0]);
			// 		row = parseInt(arr[1]);
			// 		this._gridTiles[col][row].unloadTile();
			// 		strKey = col + "_" + row;
			// 		this._loadedTiles.remove(strKey);
			// 		console.log("loadtiles remove::",strKey);
			// 	}
			// }
			//再加载
			for(key in tiles.container)
			{
				if(!this._loadedTiles.containsKey(key))
				{
					arr = key.split("_");
					col = parseInt(arr[0]);
					row = parseInt(arr[1]);
					if(col < this._gridVO.col && row < this._gridVO.row)
					{
						if(this._gridTiles[col]&&this._gridTiles[col][row])
						{
							this._gridTiles[col][row].loadTile();
							strKey = col + "_" + row;
							this._loadedTiles.add(strKey, strKey);
							console.log("loadtiles add::",strKey);
						}
					}
				}
			}
		}
	}
	
	public getNeedLoadTiles(camX:number, camY:number):ObjDictionary
	{
		let cellW:number = this.gridVO.cellW;
		let cellH:number = this.gridVO.cellH;
		let rect:Rect = new Rect(camX-cellW, camY+cellH, mGameConfig.DeviceW + cellW, (mGameConfig.DeviceH + cellH));
		let p1:Vec2 = this.scenePosToGrid(rect.x, rect.y);
		let p2:Vec2 = this.scenePosToGrid(rect.x+rect.width,rect.y- rect.height);
		let res:ObjDictionary = new ObjDictionary();
		let key:string;
		for(let i:number=p1.x; i<=p2.x; i++)
		{
			for(let j:number=p1.y; j<=p2.y; j++)
			{
				key = i + "_" + j;
				res.add(key, key);
			}
		}
		return res;
	}
	
	public getNeedLoadTiles1(camX:number, camY:number):ObjDictionary
	{
		let cellW:number = this.gridVO.cellW;
		let cellH:number = this.gridVO.cellH;
		//let rect:Rectangle = new Rectangle(camX-cellW*0.5, camY-cellH*0.5, mGameConfig.DeviceW + cellW, mGameConfig.DeviceH + cellH);
		let rect:Rect = new Rect(camX-cellW, camY-cellH, mGameConfig.DeviceW + cellW*2, mGameConfig.DeviceH + cellH*2);
		let p1:Vec2 = this.scenePosToGrid(rect.x, rect.y);
		let p2:Vec2 = this.scenePosToGrid(rect.width, rect.height);
		
		let res:ObjDictionary = new ObjDictionary();
		let centerP:Vec2;
		let key:String;
		for(let i:number=p1.x; i<=p2.x; i++)
		{
			for(let j:number=p1.y; j<=p2.y; j++)
			{
				centerP = this.gridToScenePos(i,j);
				if(rect.contains(centerP))
				{
					key = i + "_" + j;
					res.add(key, key);
				}
			}
		}
		return res;
	}
	
	public scenePosToGrid(x:number, y:number):Vec2
	{
		let p:Vec2 = new Vec2();
		p.x = parseInt(x / this._gridVO.cellW + "");     //列
		p.y = parseInt(y / this._gridVO.cellH*-1 + "");     //行
		return p;
	}
	
	public gridToScenePos(x:number, y:number):Vec2
	{
		let p:Vec2 = new Vec2();
		p.x = (x +0.5) *  this._gridVO.cellW;     //列
		p.y = -(y + 0.5) * this._gridVO.cellH;     //行
		return p;
	}
	
	private static _instance:WorldMap
	public static get instance():WorldMap
	{
		if(!this._instance){ this._instance = new WorldMap(); }
		return this._instance;
	}
	
}