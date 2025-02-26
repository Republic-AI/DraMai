package com.infinity.ai.platform.web.controller;

import com.infinity.ai.platform.application.Config;
import com.infinity.common.base.common.Response;
import com.infinity.common.msg.common.RefreshMsg;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeManager;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {

    /**
     * 刷新配置
     *
     * @param server all:所有服务器，P:所有platform，C：所有chatgpt, Q:quartz, G:gateway
     * @return
     */
    @GetMapping("/config/refresh/{server}")
    private Response refreshConfig(@PathVariable("server") String server) {
        log.debug("start refresh game config ......,server={}", server);
        if (StringUtils.isEmpty(server)) {
            return Response.createError("refresh fail, server can't null");
        }

        //server all:所有服务器，P:所有platform，C：所有chatgpt, Q:quartz, G:gateway
        if (Config.getInstance().getNodeId().equals(server) || "DD".equals(server.toUpperCase())) {
            log.info("refresh {} config", server);
            Config.getInstance().reload();
        } else if ("ALL".equals(server.toUpperCase())) {
            log.info("refresh {} config", server);
            Config.getInstance().reload();
            //通知其他服务器
            MessageSender.getInstance().broadcastMessageToAllService(RefreshMsg.builder().build());
        } else if (server.trim().length() == 1) {
            log.info("refresh {} config", server);
            MessageSender.getInstance().sendMessage(server.toUpperCase().charAt(0), RefreshMsg.builder().build());
        } else {
            //指定服务器ID刷新
            Map<String, INode> allServices = NodeManager.getInstance().getAllNodes();
            INode iNode = allServices.get(server);
            if (iNode != null) {
                log.info("refresh {} config", server);
                MessageSender.getInstance().sendMessage(server, RefreshMsg.builder().build());
            }
        }

        log.debug("refresh game config success......");
        return Response.createSuccess("操作成功");
    }

    /*@GetMapping("/player/{playerId}")
    public Response<VPlayer> player(@PathVariable("playerId") long playerId) {
        VPlayer v = DBManager.get(PPlayer.class, playerId).get_v();
        Map<Integer, PlayerTaskEntity> taskMap = v.getTask().getTaskMap();
        taskMap.values().forEach(d->{
            d.setStatus(2);
        });

        return Response.createSuccess(v);
    }*/


    /*@GetMapping("/quartz/{playerId}")
    public Response<VPlayer> pushQuartz(@PathVariable("playerId") long playerId) {
        VPlayer v = DBManager.get(PPlayer.class, playerId).get_v();
        if (v != null) {
            SubmitExpridedMessage msg = new SubmitExpridedMessage();
            msg.setPlayerId(playerId);
            msg.setNodeId("app.z1.s1.P1");
            msg.setDelay(10000);


            IChannel channel = QueueSelector.getInstance().loadBalance(msg.getPlayerId(), NodeConstant.kQuartzService);
            if (channel == null) {
                log.error("send message to chatgpt services fail, node is null; msg={}", msg.toString());
                return Response.createError();
            }

            if (channel != null && msg != null) {
                HeaderOuterClass.Header header = makeHeader(msg.getCommand(), msg.getCode());
                msg.setStart(System.currentTimeMillis());
                ByteBuffer byteBuffer = buildPacketBuffer(header.toByteArray(), makeMessage(msg));
                log.info("send message to quartz node");
                channel.write(byteBuffer);
            }
        }

        return Response.createSuccess();
    }

    protected HeaderOuterClass.Header makeHeader(final int command, final int errorCode) {
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(command);
        headerBuilder.setSource("app.z1.s1.P1");
        headerBuilder.setDestination("app.z1.s1.Q1");
        headerBuilder.setRtype(ProtocolCommon.kRequestTag);
        headerBuilder.setCode(errorCode);
        headerBuilder.setCmd(command);
        return headerBuilder.build();
    }

    public static ByteBuffer buildPacketBuffer(final byte[] headerData, final byte[] responseData) {
        int packetLength = 8 + headerData.length + responseData.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(packetLength);
        byteBuffer.putInt(headerData.length);
        byteBuffer.put(headerData);
        byteBuffer.putInt(responseData.length);
        byteBuffer.put(responseData);
        byteBuffer.flip();
        return byteBuffer;
    }

    private byte[] makeMessage(BaseMsg message) {
        return MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(message.toString().getBytes()))
                .build().toByteArray();
    }*/
}
