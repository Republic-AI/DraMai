package com.infinity.ai.telegram.application;

import com.infinity.common.base.thread.MultiParam;
import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1000)
@Component

public class EventRunner implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(EventRunner.class.getClass());

    @Override
    public void run(String... args) throws Exception {
        logger.info("启动系统线程");
        Threads.regist(new MultiParam<NameParam>().append(new NameParam(ThreadConst.QUEUE_LOGIC, 1)));
    }

}
