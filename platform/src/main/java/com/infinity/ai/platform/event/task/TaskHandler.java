package com.infinity.ai.platform.event.task;


import com.infinity.common.base.common.Response;

/***
 *任务处理器
 */
public interface TaskHandler {

    /***
     * 执行任务
     * @param playerTask 任务
     * @return true:完成，false:任务未完成
     */
    boolean execute(PlayerTask playerTask);

    /***
     * 奖励领取
     * @param playerTask 任务
     * @return true：领取成功，false:领取失败
     */
    Response<ReceiveResponse> receive(PlayerTask playerTask);
}
