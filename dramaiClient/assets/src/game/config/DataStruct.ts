import { ActionType } from "../../model/StaticTextConfig";

export type chatcell = {
    "from": number,
    "to": number,
    "type": number,
    "content": string,
    "time": number
};
export type NPCServerD = {
    "id": number,
    "name": string,
    "type": number,
    "model": number,
    "career": string,
    "keyword": string,
    "hair": number,
    "top": number,
    "bottoms": number,
    "speed": number,
    "x": number,
    "y": number,
    "requestData":any,
    "items":any
}
export type catCfgD = {
    cat_characters: any;
    "probability": number,
    "breed": string,
    "cv_factor": number,
    "mbti": string,
    "id": number,
    "name": string,
    "avatar": string,
    "tags": string,
    "cv": number,
    "earningRate"?: number,
    "iq"?: number,
    "rate"?: number,
    "career"?: string,
}

export type catInfo = {
    
        "id": number,
        "name": string,
        "userName": string,
        "cv": number,
        "birthday": number,
        "workFlag": number,
        "workTime": number,
        "workFinish": number,
        "iq": number,
        "rate": number
        "career"?: string,
}
/**
 * // 状态：0:初始化,1:任务进行中,2:任务完成,3:领取完成#
 */
export type taskcell = {
    "playerId": 0,
    "taskId": number,
    "status": number,
    "value1": 0,
    "unlock": 0
}

export type WorkResult = {
    "requestId": 0,
    "type": 1,
    "command": 10030,
    "code": 0,
    "data": workDataType[]
}

interface workDataType {
    "id": string,
    "story": string,
    "img": string,
    "score": number,
    "earning": number,
    "status": number,
    "progress": number
}

export type itemServerD = {
    "playerId": number,
    "goodsId": number,
    "goodsType": number,
    "count": number,
    "maxLimit": number
}

export interface CatActionInfo {
    type: ActionType,
    text: string,
    duringTime: number,
    data?: any 
    toyId?: number
    //剩下字段待定
}

export enum WorkState {
    WORKING,
    WORK_DONE_REWARDING,
    WORK_DONE_REWARD_DONE
}