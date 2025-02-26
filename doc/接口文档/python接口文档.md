

## 接口文档：

### 一. 概述

1. 消息由Header + Message 两部分构成

2. 消息Header中的command决定了该消息类型，比如10100代表NPC的行为

3. Message 中content字段是该命令的具体业务数据，以下是伪代码：

   ```python
   #以10100 命令号为例
   def make_message():
       message = Message_pb2.Message()
       message.content = b"{\"actionId\": 100, \"data\": {\"npcId\": 10003,\"actionId\": 104,\"params\": {\"param1\":1}}}"
       return message.SerializeToString()
   ```

4. 以下接口协议针对的是Message中content 字段的内容，Message的content中的消息格式用JSON形式表示：

   ```json
   //python -> platform 
   {
       "npcId": 10003,  	#npcId
       "actionId": 100, 	#行为ID
       "ack": 10086,       #确认消息
       "data": {
          	"参数名称1": "参数值1",
           "参数名称2": "参数值2"
       }
   }
   ```



### 二. NPC行为:

> 行为命令编号：10100，Header中的command=10100
   ```json
    //python -> platform 
  {
     "npcId": 10003,  	#npcId
     "actionId": 100, 	#行为ID
     "ack": 10086,       #确认消息
     "data": {
        "参数名称1": "参数值1",
        "参数名称2": "参数值2"
      }
  }
   //platform -> python
   {
      "npcId": 10003,  	  #npcId
      "actionId": 100, 	  #行为ID
      "ack": 10086,          #消息确认ID
      "actionUid": 10086,    #行动的唯一id
      "status": -1           #行动状态 -1 丢弃 0：正在做，1：完成 2：准备做开没开始
   }
   ```

#### 1.耕种:#

> 耕种行为编号：100

- 入参


```json
{
    "actionId": 100, 	#行为ID
    "npcId": 10003,  	#npcId
    "data": {
        "oid": "耕种区域编号",
        "itemId": 10101001, 	#"物品ID，对应道具表：Item.xls表中的ID"
    }
}

//耕种区域,到哪个地方耕种，若目前只有一个区域，则不填；要是有多个区域，则需要指定区域编号
String oid; 
//物品ID，对应道具表：Item.xls表中的ID
Integer itemId;
//物品数量（字段作废）
Integer count;
```

- 出参


```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的NPC、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```



#### 2.采收#

> 采收行为编号：101

- 入参


```json
{
    "actionId": 101, 	#行为ID
    "npcId": 10003,  	#npcId
    "data": {
        "oid": "采收区域编号"
    }
}

//采收区域,到哪个地方采收，若目前只有一个区域，则不填；要是有多个区域，则需要指定区域编号
String oid; 
```

- 出参


```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```



#### 3.销售

> 采收行为编号：102

- 入参

```json
{
    "actionId": 102, 	#行为ID
    "npcId": 10003,  	#卖家npcId
    "data": {
    	"oid": "salerPos",		#地图对象ID，销售地方
    	"itemId": 10101001, 	#"销售的物品ID，对应道具表：Item.xls表中的ID"
    	"count": 1,				#"销售物品数量"
    	"price": 10,			#"销售价格"(分)
        "buyerNpcId": 10003		#"买家NPC ID"
    }
}

//物品ID，对应道具表：Item.xls表中的ID
Integer itemId;
//销售物品数量
Integer count;
//销售价格
Long price;
//"卖家NPC ID
Integer sellerNpcId;
```

- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的NPC、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

#### 4.购买

> 采收行为编号：103

-   入参

```json
{
    "actionId": 103, 	#行为ID
    "npcId": 10003,  	#买家npcId
    "data": {
    	"oid": "salerPos",		#地图对象ID，销售地方
    	"itemId": 10101001, 	#"销售的物品ID，对应道具表：Item.xls表中的ID"
    	"count": 1,				#"销售物品数量"
    	"price": 10,			#"销售价格(分)"
        "sellerNpcId": 10003	#"卖家NPC ID"
    }
}

//物品ID，对应道具表：Item.xls表中的ID
Integer itemId;
//销售物品数量
Integer count;
//销售价格
Long price;
//"卖家NPC ID
Integer sellerNpcId;
```

- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的NPC、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```



#### 5.做饭#

> 做饭行为编号：104

-   入参

```json
{
    "actionId": 104, 	#行为ID
    "npcId": 10003,  	#买家npcId
    "data": {
    	"oid": "做饭区域编号",	#厨房灶台位置或者坐标（X,Y)
		"items":[
            {
                "itemId": 10101001, 	#"消耗的物品ID，对应道具表：Item.xls表中的ID"
    			"count": 1,				#"消耗物品数量"
            }
        ]
    }
}

//做饭区域编号, 厨房灶台位置或者坐标（X,Y)
String oid; 
//物品ID，对应道具表：Item.xls表中的ID
Integer itemId;
//销售物品数量
Integer count;
```

- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的NPC、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

 

#### 6.吃饭#

> 吃饭行为编号：105

-   入参

```json
{
    "actionId": 105, 	#行为ID
    "npcId": 10003,  	#买家npcId
    "data": { 
    	"oid": "吃饭的地方：地图上桌子ID"
    }
}

//吃饭的地方：地图上桌子ID
String oid; 
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的NPC、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```



#### 7.睡眠#

> 睡眠行为编号：106

-   入参

```json
{
    "actionId": 106, 	#行为ID
    "npcId": 10003,  	#买家npcId
    "data": { 
    	"oid": "地图道具ID", 	#床的ID
    	"getUpTime":"2024-10-03 07:00:01"
    }
}
//地图上物品的ID：床的ID
private String oid;
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```



#### 8.起床#

> 起床行为编号：111

-   入参

```json
{
    "actionId": 111, 	#行为ID
    "npcId": 10003,  	#买家npcId
    "data": { 
    	"oid": "地图道具ID" 	#床的ID
    }
}

//床的ID
String oid;

//测试数据：
{
    "actionId": 111,
    "npcId": 10002,
    "data": {
        "oid": "farmerBed"
    }
}
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

#### 9.说话?

> 说话行为编号：110

-   入参

```json
{
    "actionId": 110, 	#行为ID
    "npcId": 10003,  	#主动说话的npcId
    "data": { 
    	"content": "说话内容", 	 	 #说话的具体内容
    	"npcId": 10002,				#被动接收聊天对象ID
    }
}
//说话的具体内容
String content;
//聊天对象ID
Long npcId;
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```



#### 10.NPC移动#

> 移动行为编号：112

-   入参

```json
{
    "actionId": 112, 	#行为ID
    "npcId": 10003,  	#买家npcId
    "data": { 
    	"oid": "地图物品ID", 	 	 #"地图物品ID"
    }
}
//地图物品对象ID
String oid;
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

#### 

#### 11.投喂#

> 移动行为编号：107
>
> 1. NPC走到“牛”前 
> 2. 投喂期间NPC头上出现气泡显示，气泡中出现投喂图标

-   入参

```json
{
    "actionId": 107, 	#行为ID
    "npcId": 10002,  	#npcId
    "data": { 
    	"oid": "牛对象ID", 	 	 #地图上牛这个对象的ID
        "items":[
            {
                "itemId": 10101001, 	#"消耗的物品ID，对应道具表：Item.xls表中的ID"
                "count": 1,				#"消耗物品数量"
            }
         ]
    }
}
//地图上牛这个对象的ID
String oid;
//物品ID，对应道具表：Item.xls表中的ID
Integer itemId;
//销售物品数量
Integer count;
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

#### 



#### 12.屠宰#

> 移动行为编号：108
>
> 1. NPC走到“牛”前 
> 2. 屠宰期间NPC头上出现气泡显示，气泡中出现屠宰图标

-   入参

```json
{
    "actionId": 108, 	#行为ID
    "npcId": 10002,  	#npcId
    "data": { 
    	"oid": "要屠宰的牛对象ID", 	 	 #地图上牛这个对象的ID
    }
}
//地图上牛这个对象的ID，要屠宰的牛对象ID
String oid;
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

#### 



#### 13.制作#

> 移动行为编号：109
>
> 1. NPC走到厨房桌子前 
> 2.  制作期间NPC头上出现气泡显示，气泡中出现制作图标

-   入参

```json
{
    "actionId": 109, 	#行为ID
    "npcId": 10005,  	#npcId
    "data": { 
    	"oid": "餐桌物品ID", 	 	 #地图上厨房桌子的ID
        "items":[
            {
                "itemId": 10101001, 	#"消耗的物品ID，对应道具表：Item.xls表中的ID"
                "count": 1,				#"消耗物品数量"
            }
         ]
    }
}
//地图上厨房桌子的ID
String oid;
//物品ID，对应道具表：Item.xls表中的ID
Integer itemId;
//销售物品数量
Integer count;
```



- 出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给python程序的其他业务数据待定
        
        #1.NPC 坐标（X,Y)
    	#2.当前行为Id
    	#3.NPC状态（待定）
    	#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
    	#5.行为结束同步数据（或者碰到NPC同步）
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;
```

#### 

| 投喂 |
| ---- |
| 屠宰 |
| 制作 |



### 三. 数据同步:

> 以下是JAVA 同步给python程序的数据定义
>
> 数据同步命令：10101

```mysql
需要从后端传回AI的信息：
npc所处地点，包括地名和坐标
npc自己的状态
npc目前正在进行的动作
目前的时间
目前所有物，金钱，物品
周围X范围内的人物（包括人物坐标，动作状态，是否在对话，跟谁对话，对话内容，有什么东西在售卖）， 
周边地点（例如杂货商）以及地址
周边物品（包括物品坐标，状态，例如 炉灶：火燃烧或空闲，和可用动作，例如:关火或开火）
```

-   入参

```json
#1.NPC 坐标（X,Y)
#2.当前行为Id
#3.NPC状态（待定）
#4.周围的人、周围的物品（周围物品名称/ID、状态）、拥有的物品名称和数量
#5.行为结束同步数据（或者碰到NPC同步）
一、世界数据
	1.当前时间
二、NPC数据:
    1.info：NPC基础数据：
        NPC ID	
        NPC名字	
        first_name	
        last_name	
        NPC类型	
        模型ID	
        发型	
        top	
        bottoms
		#所处地名：addr
        NPC位置X	
        NPC位置Y	
        age	
        height	
        weight	
        body_style	
        innate	
        learned	
        lifestyle	
        living_area	property	
        #daily_plan_req
    2.items：Npc拥有的道具数据
        物品名称：itemName
        物品ID：itemId
        物品数量：count
	3.action：Npc行为数据：
		行为名称：actionName
		行为ID：actionId
	4.mapData：地图上属于Npc的物品属性：
		地图物品名称：objName,
		物品ID:oid
		物品类型:type
		物品坐标X:X
		物品坐标Y:Y
	5.surroundings：NPC周围物品
		地图物品名称：objName,
		物品ID:oid
		物品类型:type
		物品坐标X:X
		物品坐标Y:Y
	6.talk：NPC当前对话内容：
		发送者NPCID
		对话时间
		说话内容
        接收者NPCID

//以下为游戏同步给AI这边的数据协议，格式如下：
{
    "requestId":11111,
    "command": 10101,
    "data": {
        "world": {
            //当前游戏时间戳，单位：毫秒
            "time": 1722333837000
        },
        "mapObj": [//地图上物品信息
            {
                "objName": "地图物品名称",
                "oid": "物品ID",
                "type": "物品类型",
                "X": 11, //物品坐标X
                "Y": 11, //物品坐标Y
                "status": "燃烧",
                "availableActions": [ //可用动作
                    "关火",
                    "开火"
                ]
            }
        ],
        "npcs": [
            {
                "npcId": 10003, //NPC ID
                "status": "闲置", //NPC 状态
                "data": {
                    "info": {
                        "name": "NPC名字",
                        "first_name": "NPC名字",
                        "last_name": "NPC名字",
                        "type": "NPC类型",
                        "X": 11, 
                        "Y": 22,
                        "age": 20,
                        "height": 170,
                        "weight": 68,
                        "body_style": "body_style",
                        "innate": "innate",
                        "learned": "learned",
                        "lifestyle": "lifestyle",
                        "living_area": "living_area",
                        "property": "property"
                    }
                },
                "selling": [ //售卖物品 (如果有)
                    {
                        "name": "物品名称",
                        "itemId": 111111,
                        "count": 1
                    }
                ],
                "items": [   //拥有的物品
                    {
                        "name": "物品名称",
                        "itemId": 111111,
                        "count": 1
                    }
                ],
                "action": {  //当前正在做的行为
                    "actionName": "行为名称",
                    "actionId": 100
                },
                "mapData": { //NPC自己的物品信息
                    "objName": "地图物品名称",
                    "oid": "物品ID",
                    "type": "物品类型",
                    "X": 11, //物品坐标X
                    "Y": 11, //物品坐标Y
                },
                "surroundings": { //周边信息
                    "locations": [ //周边的地理位置信息 （作废）
                        {
                            "name": "杂货店",
                            "address": "坐标地址"
                        }
                    ],
                    "people": [ //周边人
                        {
                            "npcId": 10002,
                            "status": "状态"
                        }
                    ],
                    "items": [
                        {  //周边物品信息
                            "objName": "地图物品名称",
                            "oid": "物品ID",
                            "type": "物品类型"
                        }
                    ]          
                },
                "talk":{   //对话信息，多人对话
                    //"isTalking": false, //是否在对话中
                    "talkingTo": [123123],//对话对象
                    "contents": [
                        {  //对话内容
                            "sender": "发送者NPCID",
                            "time": 123123123,
                            "content": "说话内容",
                            "target": "接收者NPCID"
                    	}
                     ]
                 }
            }
        ]
    }
}





=================================================================================================
{ //以下为一个案例，具体根据游戏里面数据定
    "command": 10101,
    "data": {
        "location": {
            "name": "地点名称",    // 地点名称
            "coordinates": {
                "x": 100,          // x坐标
                "y": 200           // y坐标
            }
        },
        "npcStatus": {
            "health": 100,         // npc健康状态
            "energy": 80,          // npc能量状态
            "mood": "happy"        // npc情绪状态
        },
        "currentAction": {
            "actionId": 100,       // 当前动作ID
            "actionName": "耕种"  // 当前动作名称
        },
        "currentTime": "2024-07-17T12:34:56Z", // 当前时间 (ISO 8601 格式)？
        "inventory": {
            "money": 1000,         // 金钱数量
            "items": [
                {
                    "itemId": 10101001,   // 物品ID
                    "itemName": "锄头",   // 物品名称
                    "quantity": 1         // 物品数量
                }
            ]
        },
        "surroundings": {
            "people": [
                {
                    "npcId": 10002,       // 周围人物ID
                    "name": "NPC名称",    // 周围人物名称
                    "coordinates": {
                        "x": 110,          // 人物x坐标
                        "y": 210           // 人物y坐标
                    },
                    "status": "闲置",      // 人物状态
                    "isTalking": false,    // 是否在对话
                    "talkingTo": null,     // 对话对象ID (如果有)
                    "dialogue": null,      // 对话内容 (如果有)
                    "selling": null        // 售卖物品 (如果有)
                }
            ],
            "locations": [
                {
                    "name": "杂货店",      // 地点名称
                    "address": "坐标地址"  // 地点地址
                }
            ],
            "items": [
                {
                    "itemId": 10101002,   // 物品ID
                    "coordinates": {
                        "x": 120,          // 物品x坐标
                        "y": 220           // 物品y坐标
                    },
                    "status": "燃烧",      // 物品状态
                    "availableActions": [  // 可用动作
                        "关火",
                        "开火"
                    ]
                }
            ]
        }
    }
}

```

-   出参

```json
{
    "command": 10100,
    "code": 0,
    "message":"",
    "data": {
        #返回给java程序的其他业务数据待定
    }
}

//错误码,0：成功,其他失败
protected int code = 0;
//错误信息，成功为空，异常为非空
protected String message;



```



### 四. 查询数据:

> Python程序启动后，主动查询所有的NPC数据
>
> From: Python
>
> Target: Java
>
> 数据同步命令：10102

- 入参

```json
{
  "command": 10102,
  "data": {}
}
```

- 出参

```json
{
    "requestId":11111,
    "command": 10101,
    "data": {
        "world": {
            //当前游戏时间戳，单位：毫秒
            "time": 1722333837000
        },
        "mapObj": [//地图上物品信息
            {
                "objName": "地图物品名称",
                "oid": "物品ID",
                "type": "物品类型",
                "X": 11, //物品坐标X
                "Y": 11, //物品坐标Y
                "status": "燃烧",
                "availableActions": [ //可用动作
                    "关火",
                    "开火"
                ]
            }
        ],
        "npcs": [
            {
                "npcId": 10003, //NPC ID
                "status": "闲置", //NPC 状态
                "data": {
                    "info": {
                        "name": "NPC名字",
                        "first_name": "NPC名字",
                        "last_name": "NPC名字",
                        "type": "NPC类型",
                        "X": 11, 
                        "Y": 22,
                        "age": 20,
                        "height": 170,
                        "weight": 68,
                        "body_style": "body_style",
                        "innate": "innate",
                        "learned": "learned",
                        "lifestyle": "lifestyle",
                        "living_area": "living_area",
                        "property": "property"
                    }
                },
                "selling": [ //售卖物品 (如果有)
                    {
                        "name": "物品名称",
                        "itemId": 111111,
                        "count": 1
                    }
                ],
                "items": [   //拥有的物品
                    {
                        "name": "物品名称",
                        "itemId": 111111,
                        "count": 1
                    }
                ],
                "action": {  //当前正在做的行为
                    "actionName": "行为名称",
                    "actionId": 100
                },
                "mapData": { //NPC自己的物品信息
                    "objName": "地图物品名称",
                    "oid": "物品ID",
                    "type": "物品类型",
                    "X": 11, //物品坐标X
                    "Y": 11, //物品坐标Y
                },
                "surroundings": { //周边信息
                    "locations": [ //周边的地理位置信息 （作废）
                        {
                            "name": "杂货店",
                            "address": "坐标地址"
                        }
                    ],
                    "people": [ //周边人
                        {
                            "npcId": 10002,
                            "status": "状态"
                        }
                    ],
                    "items": [
                        {  //周边物品信息
                            "objName": "地图物品名称",
                            "oid": "物品ID",
                            "type": "物品类型"
                        }
                    ]          
                },
                "talk":{   //对话信息，多人对话
                    "isTalking": false, //是否在对话中
                    "talkingTo": [123123],//对话对象
                    "contents": [
                        {  //对话内容
                            "sender": "发送者NPCID",
                            "time": 123123123,
                            "content": "说话内容",
                            "target": "接收者NPCID"
                    	}
                     ]
                 }
            }
        ]
    }
}
```

####  
