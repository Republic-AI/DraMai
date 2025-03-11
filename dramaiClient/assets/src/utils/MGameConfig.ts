export default class mGameConfig
{
    public static readonly FrameLoopDelay:number = 33;
    public static DesginH:number = 750;
	public static readonly IsDebug:boolean = true;
	public static readonly ModelPath:string = "res/models/";
	
	public static readonly SoundPath:string = "res/sound/";
	
	public static readonly EffectPath:string = "res/effects/";
	
	//渲染尺寸
	public static DeviceW:number = 750;
	public static DeviceH:number = 1334;
	
	//以1136*640作为设计尺寸
	public static DesginW:number = 1334;
	public static OrthographicVerticalSize:number = 10;
	public static UnitPerPixel:number = 0.015625;    //1/64
    static actorLayer: import("cc").Node;
}