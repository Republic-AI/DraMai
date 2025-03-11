
export default class ObjDictionary
{
	private _strongType:any;     //要求存储对象为强类型
	
	private _container:Object;
	public get container():Object
	{
		return this._container;
	}
	
	private _length:number = 0;
	public get length():number
	{
		return this._length;
	}
	
	public constructor(strongType:any = null)
	{
		this._strongType = strongType;
		this._container = new Object();
	}
	
	/**
	 * 添加元素 
	 * @param key
	 * @param value
	 * 
	 */		
	public add(key:any, value:any):void 
	{
		if(this._strongType && !(value instanceof this._strongType))
		{
			throw new Error("Obj Dictionary [add] Type Check Error, Need" + this._strongType);
			//return;
		}
		//如果是新添加才增加length
		if(!this.containsKey(key))
			this._length++;
		this._container[key] = value;
	}
	
	/**
	 * 根据键值获取对象 
	 * @param key
	 * @return 
	 * 
	 */		
	public get(key:any):any
	{
		return this._container[key];
	}
	
	/**
	 * 重新设置 
	 * @param key
	 * @param value
	 * 
	 */		
	public reset(key:any, value:any):void
	{
		if(this._strongType && !(value instanceof this._strongType))
		{
			throw new Error("Obj Dictionary [add] Type Check Error, Need" + this._strongType);
		}
		if(this.containsKey(key))
		{
			this._container[key] = value;
		}
		else
		{
			console.log("ObjDictionary: warning you reset a not exist key");
		}
	}
	
	/**
	 * 是否包含键 
	 * @param key
	 * @return 
	 * 
	 */		
	public containsKey(key:any):boolean
	{
		return this._container.hasOwnProperty(key);
	}
	
	/**
	 * 移除键 
	 * @param key
	 * 
	 */		
	public remove(key:any):void
	{
		if(this._container.hasOwnProperty(key))
		{
			this._container[key] = null;
			delete this._container[key];
			this._length--;
		}
	}
	
	/**
	 *清除操作 
	 * 
	 */		
	public clear():void
	{
		this._length = 0;
		this._container = null;
		this._container = new Object();
	}
	
}