export interface DataEvents {
  items: any;
  bid:number,	
  actionId: number;
  npcId: number;
  users: any;
  params?: {
    path:[];
    npcId: any;
    price: any;
    count?: any;
    oid?: string;

    items?: {
        itemId: number,
        count: number
    }[];
    itemId?: number
    content:string
    chatData?:{
      barrage:number,
      type:number,
      context:string,
      npcId:number,
      sender:string,
      receiver:string,
      rName:string
    }
  };
}

export interface DataFarmEvent {
    bid:number,	
    actionId: number,
    npcId: number,
    isFinish:number,
    x:number,
    y:number,
    objId:string,
    state:number,
    users: any,
    params: {
        oid: string,
        itemId:10101001
    }
}
