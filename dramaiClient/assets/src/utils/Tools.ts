import { Vec2 } from "cc";

export default class  Tools{
	public static distancePoint(src:Vec2, des:Vec2):number
	{
		return Math.sqrt((des.x - src.x) * (des.x - src.x) + (des.y -src.y) * (des.y -src.y));
	}
}