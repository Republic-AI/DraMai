package com.infinity.ai.quartz.cron;

import com.infinity.ai.quartz.timer.ITaskRegister;
import com.infinity.ai.quartz.manager.CronTaskManager;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.utils.GsonUtil;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.network.MessageSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class CronTask implements Serializable {
    private static final long serialVersionUID = 6000566929673475616L;
    public static final String TASK_KEY = "task_key";
    public static final String TASK_KEY_CRON = "task_key_cron";
    private static AtomicLong IDSource = new AtomicLong(1);

    private long id;
    private long startTime;
    private long endTime;
    private long delay;
    private String cronType;
    private String nodeId;
    private String jobName;
    private Class<? extends BaseMsg> clazz;
    private String timerMsg;
    private JobDetail jobDetail;
    private Trigger trigger;
    private CronTaskManager mgr;

    public CronTask(String jobName) {
        this.id = IDSource.incrementAndGet();
        this.jobName = jobName;
        this.startTime = System.currentTimeMillis();
    }

    public CronTask(String jobName, BaseMsg timerMsg) {
        this.id = IDSource.incrementAndGet();
        this.jobName = jobName;
        this.startTime = System.currentTimeMillis();
        this.clazz = timerMsg.getClass();
        this.timerMsg = timerMsg.toString();
    }

    public CronTask(String jobName, BaseMsg timerMsg, String nodeId) {
        this.id = IDSource.incrementAndGet();
        this.jobName = jobName;
        this.startTime = System.currentTimeMillis();
        this.clazz = timerMsg.getClass();
        this.timerMsg = timerMsg.toString();
        this.nodeId = nodeId;
    }

    public CronTask(long delay, BaseMsg timerMsg, String nodeId) {
        this.id = IDSource.incrementAndGet();
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + delay;
        this.clazz = timerMsg.getClass();
        this.timerMsg = timerMsg.toString();
        this.nodeId = nodeId;
    }

    public void start() {

    }

    public void end() {
        log.debug("crontask run end,jobName={},startTime={},endTime={},delay={},msg={}", jobName, startTime, endTime, delay, timerMsg == null ? "" : timerMsg.toString());
        if (StringUtils.isNotBlank(nodeId) && timerMsg != null) {
            BaseMsg baseMsg = GsonUtil.parseJson(timerMsg, this.clazz);
            MessageSender.getInstance().sendMessage(nodeId, baseMsg);
        }

        if (StringUtils.isEmpty(this.jobName)) {
            return;
        }

        Object register = SpringContextHolder.getBean(this.jobName);
        if (register != null) {
            ((ITaskRegister) register).execute();
        }
    }

    public CronTask reset() {
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + delay;
        return this;
    }

    public void setDetail(CronTaskManager mgr, JobDetail jobDetail, Trigger trigger) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
        this.mgr = mgr;
    }
}
