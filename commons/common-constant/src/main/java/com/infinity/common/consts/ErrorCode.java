package com.infinity.common.consts;

public final class ErrorCode
{
    public final static int SystemError = 40001;
    public final static String SystemErrorMessage = "operation failed";


    public final static int PlayerNotOnlineError = 10001;
    public final static String PlayerNotOnlieErrorMessage = "Illegal user, please log in and request.";

    //没有可以领取的奖励
    public final static int kDrawAwardEmptyError = 11006;
    //领取任务奖励失败
    public final static int kDrawTaskAwardError = 11007;
    //领取的任务为空
    public final static int kDrawTaskNullError = 11008;
    //领取的任务状态错误
    public final static int kDrawTaskStatusError = 11009;
    //任务为空
    public final static int kDrawTaskNotExistError = 11010;
    //任务未解锁
    public final static int kDrawTaskNotUnlockError = 11011;
    //未满足领取成就奖励条件
    public final static int kAchievementError = 11012;
    public final static int kLoginVerifyError = 10019;
    public final static int PlayerStatusError = 10020;
    public final static int SHOP_ERROR_NOACTIVE = 10021;
    public final static int PAY_ERROR_CATCOIN_INSUFFICIENT = 10022;
    public final static int CAT_NOT_EXISTS = 10023;
    public final static int PLAYER_AP_PHYSICAL = 10024;
    public final static int GOODS_NOT_ENOUGH = 10025;
    public final static int WORK_CAT_ENOUGH = 10026;
    public final static int WORK_ITEM_ENOUGH = 10027;
    public final static int WORK_CLAIM_NULL = 10028;
    public final static int WORK_FAIL_ERROR = 10029;

    public final static int PlayerNpcExistError = 10030;
    public final static String PlayerNpcExistErrorMessage = "The npc already exists";

    public final static int PositionNotWalkableError = 10031;
    public final static String PositionNotWalkableErrorMessage = "The position is not walkable";

    public final static int NftTransferFailedError = 10032;
    public final static String NftTransferFailedErrorMessage = "NFT transfer failed, please try again";


    public final static int CharacterCreationLimitError = 10032;
    public final static String CharacterCreationLimitErrorMessage = "The character creation limit has been reached";


    public final static String PlayerStatusErrorMessage = "The account has been banned.";
    public final static String kDrawTaskNullMessage = "Task does not exist";
    public final static String kDrawTaskStatusMessage = "Task status error";
    public final static String kDrawTaskNotExistErrorMessage = "There are currently no rewards to receive";
    public final static String kDrawTaskNotUnlockMessage = "The handler not unlocked";

    public final static String kLoginVerifyMesage = "Login verification failed";
    public final static String CAT_NOT_EXISTS_MSG = "Cats do not exist";
    public final static String PLAYER_AP_PHYSICAL_MSG = "Lack of physical strength";
    public final static String GOODS_NOT_ENOUGH_MSG = "Goods not enough";
    public final static String WORK_CAT_ENOUGH_MSG = "No cat selected";
    public final static String WORK_ITEM_ENOUGH_MSG = "No item selected";
    public final static String WORK_CLAIM_NULL_MSG = "Work do not exist or Haved claim";
    public final static String WORK_FAIL_ERROR_MSG = "Work fail,System error";
}
