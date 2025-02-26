package com.infinity.ai.platform.manager;

import com.infinity.ai.domain.model.PlayerSignEntity;
import com.infinity.ai.domain.model.PlayerSignRecordEntity;
import com.infinity.ai.domain.tables.PlayerSign;
import com.infinity.ai.platform.event.ActionService;
import com.infinity.ai.platform.event.ActionTypeEnum;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.platform.player.SignRequest;
import com.infinity.common.msg.platform.player.SignResponse;
import com.infinity.common.utils.DateUtil;
import com.infinity.db.util.Pair;
import com.infinity.network.MessageSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class SignManager {

    //数据每日5点更新
    private static final String SIGN_TIME = " 00:00:01";
    private static final long TIME = (1000 * 60 * 60 * 24);
    private Player owner;

    public SignManager(Player owner) {
        this.owner = owner;
    }

    private boolean isSameYear(Date d) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d);
        Calendar c2 = Calendar.getInstance();
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    public PlayerSign getSign() {
        return this.owner.getPlayerModel().get_v().getSign();
    }

    public PlayerSignEntity getSign(int month) {
        PlayerSignEntity entity = getSign().getSigns().get(month);
        /*if (entity != null && !isSameYear(new Date(entity.getLastSignTime()))) {//重置当月数据
            getSign().getSigns().remove(month);
            getSign().getRecords().clear();
            entity = null;
        }*/

        return entity;
    }

    //当天是否已签到
    public boolean todayHasSign() {
        //签到月份(暂时用不到该字段，作0处理
        int month = 0; //getMonth(now);
        //签到记录，每月一条
        PlayerSignEntity entity = getSign(month);
        //当天是否重复签到
        return (entity != null && isRepeatSign(new Date(), new Date(entity.getLastSignTime())));
    }

    /**
     * 当天已签到或者签到满3次不可以再签到
     *
     * @return
     */
    public boolean isCanSign() {
        //签到月份(暂时用不到该字段，作0处理
        int month = 0; //getMonth(now);
        //签到记录，每月一条
        PlayerSignEntity entity = getSign(month);
        if (entity == null) {
            return true;
        }

        if ((entity != null && isRepeatSign(new Date(), new Date(entity.getLastSignTime())))) {
            return false;
        }

        /*SysParamCfg sysParamCfg = GameConfigManager.getInstance().getSysParamCfgManager().get(SysParamsConsts.SIGN_DAYS);
        int defaultSign = (sysParamCfg != null && !"".equalsIgnoreCase(sysParamCfg.getValue())) ?
                Integer.parseInt(sysParamCfg.getValue()) : SIGN_CONTINUE_DAYS;
        return entity.getContinuousDays() < defaultSign;*/

        return true;
    }

    /***
     * 签到
     * @return 大于0 成功
     */
    public int sign(SignRequest msg) {
        //签到时间
        Date now = new Date();
        //签到月份(暂时用不到该字段，作0处理
        int month = 0; //getMonth(now);
        //签到记录，每月一条
        PlayerSignEntity entity = getSign(month);
        //当天是否重复签到
        if (entity != null && isRepeatSign(now, new Date(entity.getLastSignTime()))) {
            return -1;
        }

        int totalDays = (entity == null) ? 1 : entity.getTotalDays() + 1;
        if (entity == null) {
            entity = buildPlayerSignEntity(month);
            getSign().getSigns().put(month, entity);
        } else {
            entity.setTotalDays(totalDays);
            //连续签到天数
            entity.setContinuousDays(getContinuousDays(entity.getContinuousDays(), now, entity.getLastSignTime()));
            entity.setLastSignTime(now.getTime());
        }

        PlayerSignRecordEntity recordEntity = buildPlayerSignRecordEntity(month);
        recordEntity.setSignTime(now.getTime());
        //recordEntity.setSignReward(String.valueOf(signCfg.getId()));
        recordEntity.setSignReward("0");
        recordEntity.setSignDays(totalDays);
        getSign().getRecords().put(recordEntity.getId(), recordEntity);

        Pair<Integer, Integer> result = new Pair<>(0, 0);
        sendMessage(buildResponse(owner.getPlayerID(), result, msg));
        //签到事件
        ActionService.push(owner.getPlayerID(), ActionTypeEnum.SIGN);
        return 1;
    }

    private void sendMessage(BaseMsg baseMsg) {
        GameUser gameUser = GameUserMgr.getGameUser(baseMsg.getPlayerId());
        if (gameUser != null)
            MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), baseMsg);
    }

    //查询猫数据
    private BaseMsg buildResponse(long playerId, Pair<Integer, Integer> result, SignRequest msg) {
        SignResponse response = new SignResponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        response.setPlayerId(playerId);

        int catId = result.first;
        int money = result.second;

        //签到3次获得的道具类型：0: 无, 1:猫，2:积分
        int itemType = catId > 0 ? 1 : (money > 0 ? 2 : 0);
        int itemValue = catId > 0 ? catId : (money > 0 ? money : 0);

        SignResponse.ResponseData data = new SignResponse.ResponseData();
        data.setSign(isCanSign() ? 1 : 0);
        data.setItemType(itemType);
        data.setItemValue(itemValue);
        response.setData(data);
        return response;
    }


    /*private void notifyPlayer(Long playerId, int month) {
        int count = getSign().getRecords().size();
        ITask mailTask = new NotifyPlayerSignTask(playerId, count);
        mailTask.init();
        ManagerService.getTaskManager().add(mailTask);
    }*/

    /***
     * 签到奖励查询
     * @return
     */
    public Collection<PlayerSignRecordEntity> findList() {
        int month = getMonth(new Date());
        getSign(month);
        return getSign().getRecords().values();

    }

    public int getContinuousDays(int continuousDays, Date now, long lastSignTime) {
        //连续两天签到日期相差小于48小时则认为是连续签到
        boolean isContinue = Math.abs(now.getTime() - lastSignTime) <= 48 * 60 * 60 * 1000;
        return isContinue ? (continuousDays + 1) : 1;
    }

    public boolean isRepeatSign(Date now, Date lastSignTime) {
        Date midle = getMidleTime();//当天5点
        long m = midle.getTime();
        long l = lastSignTime.getTime();
        long current = System.currentTimeMillis();
        //同一天
        if (DateUtil.isSameDay(now, lastSignTime)) {
            return ((current >= m && l >= m) || (current <= m && l <= m));
        } else {
            Date preMidle = getPreMidleTime();//前一天5点
            long p = preMidle.getTime();
            //上次签到在前一天5点到今天5点之间，且当前时间也在前一天5点与今天5点之间则是重复签到
            return (current >= p && current < m && l >= p && l < m);
        }
    }

    public Date getMidleTime() {
        try {
            LocalDateTime nowTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 1));
            return Date.from(nowTime.atZone(ZoneId.systemDefault()).toInstant());
            //return SDF.parse(FORMAT.format(new Date()) + SIGN_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getPreMidleTime() {
        try {
            LocalDateTime nowTime = LocalDateTime.of(LocalDate.now().plusDays(-1L), LocalTime.of(0, 0, 1));
            return Date.from(nowTime.atZone(ZoneId.systemDefault()).toInstant());
            //return SDF.parse(LocalDate.now().plusDays(-1L).toString() + SIGN_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getMonth(Date now) {
        Calendar instance = Calendar.getInstance();
        //当天日
        int day = instance.get(Calendar.DAY_OF_MONTH);
        //当前月份: 从0开始
        int month = instance.get(Calendar.MONTH) + 1;
        //如果当天是1号，且时间是5点(SIGN_TIME)之前签到，则还是继续上个月签到
        if (day == 1 && isBeforeMidle(now)) {
            month = ((month - 1) == 0) ? 12 : (month - 1);
        }
        return month;
    }

    private boolean isBeforeMidle(Date now) {
        Date midleTime = getMidleTime();
        return now.before(midleTime);
    }

    public long getTime() {
        return TIME;
    }

    public Date getNextSignMidle(Date loginTime) {
        try {
            Date midleTime = getMidleTime();
            //当天5点之前登录，则下次自动签到时间为当天5点，四点之后登录，则下次自动签到时间为第二天4天
            if (loginTime.before(midleTime)) {
                return midleTime;
            }

            LocalDateTime nowTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0, 1));
            return Date.from(nowTime.atZone(ZoneId.systemDefault()).toInstant());
            //return SDF.parse(LocalDate.now().plusDays(1).toString() + SIGN_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PlayerSignEntity buildPlayerSignEntity(int month) {
        PlayerSignEntity entity = new PlayerSignEntity();
        entity.setTotalDays(1);
        entity.setContinuousDays(1);
        entity.setSignMonth(month);
        entity.setLastSignTime(System.currentTimeMillis());
        entity.setSignNums(0);
        return entity;
    }

    private PlayerSignRecordEntity buildPlayerSignRecordEntity(int month) {
        Calendar c = Calendar.getInstance();
        PlayerSignRecordEntity entity = new PlayerSignRecordEntity();
        entity.setId(c.get(Calendar.DAY_OF_MONTH));
        entity.setSignMonth(month);
        entity.setSignType(1);
        entity.setStatus(1);
        return entity;
    }
}
