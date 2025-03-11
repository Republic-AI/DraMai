export default class ArrUtils{
    public static  testArr = [
        {id:0,name:"张三",age:20},
        {id:1,name:"张三",age:10},
        {id:2,name:"张三",age:220},
        {id:3,name:"张三",age:20},
        {id:4,name:"张三",age:2},
    ];
    /**
     * 
     * @param arr 
     * @param attrname 
     * @param type 0:降序,1:升序
     */
    public static mSort(arr:any[],attrname:string,type:number = 1){
        console.log("sort before:",arr);
        if(type===1){
            arr.sort((b,a)=>a[attrname] - b[attrname]);
        }else if(type===0){
            arr.sort((b,a)=>b[attrname] - a[attrname]);
        }
        console.log("sort after:",arr);
    }
}