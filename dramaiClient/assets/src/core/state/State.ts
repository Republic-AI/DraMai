import StateMachine from "./StateMachine";

export default class State
{
	
	protected _owner:Object;
	
	public get owner():Object
	{
		return this._owner;
	}
	
	public constructor(owner:Object)
	{
		this._owner = owner;
	}
	
	
	/**
	 * 进入状态 
	 */		
	public onEnter(obj:Object=null):void
	{
		
	}
	
	/**
	 * 再次进入状态 
	 * @param obj
	 * @return 
	 * 
	 */		
	public onReEnter(obj:Object=null):void
	{
		
	}
	
	/**
	 * 状态更新 
	 * @return 
	 * 
	 */		
	public onUpdate():void
	{
		
	}
	
	/**
	 * 离开状态结束 
	 * @param string
	 * @return 
	 * 
	 */		
	public onLeave(preKey:string):void
	{
		
	}
	
	/**
	 * 返回状态ID 
	 * @return 
	 * 
	 */		
	public getStateKey():string
	{
		return StateMachine.InvalidState;	
	}
	
	
}