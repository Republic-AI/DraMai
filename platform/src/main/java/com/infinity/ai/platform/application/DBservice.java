package com.infinity.ai.platform.application;

import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.utils.LoggerHelper;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.db.cache.TtlStrategy;
import com.infinity.db.db.DBManager;
import com.infinity.db.db.DBoptType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;

import java.io.*;

@Slf4j
public class DBservice {
    public static int machineId = -1;

    private static int getMachineId(String fid) {
        int id = -1;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fid));
            id = Integer.parseInt(in.readLine());
            in.close();
            log.info("load machineId: {}", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    private static void saveMachineId(String fid, int id) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fid));
            out.write(String.valueOf(id));
            out.close();
            log.info("save machineId: {}", id);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static void init(ApplicationContext ac) {
        File fdir = new File("machine-id");
        if (!fdir.exists()) {
            fdir.mkdir();
        }

        String fid = fdir.getPath() + "/platform";
        machineId = getMachineId(fid);
        if (machineId == -1) {
            RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
            var gen = redissonClient.getAtomicLong(RedisKeyEnum.MACHINE_ID.getKey());
            machineId = (int) gen.addAndGet(1);
            saveMachineId(fid, machineId);
        }

        DBManager.init(ac, machineId, 2, LoggerHelper.isRelease(), DBoptType.MYSQL, new TtlStrategy());
    }
}
