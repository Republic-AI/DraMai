package com.infinity.ai.platform.manager;

import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.domain.tables.PlayerBag;
import com.infinity.ai.platform.task.goods.GoodsChange;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//背包管理器
@Slf4j
public class BagManager {
    private Player owner;
    private final Lock locker = new ReentrantLock();

    public BagManager(Player player) {
        this.owner = player;
    }

    private PlayerBag getModel() {
        return owner.getPlayerModel().get_v().getBag();
    }

    /**
     * 增加道具,
     *
     * @param id
     * @param count
     * @param send  是否通知客户端; true 会通知
     */
    public void addGoods(int id, int count, boolean send, GoodsSource goodsSource) {
        addGoods(id, count, send, true, goodsSource);
    }

    public void addGoods(int id, int count, boolean send, boolean isProccess, GoodsSource goodsSource) {
        log.debug("BagManager addGoods,goodsId={},count={},send={},goodsSource={}", id, count, send, goodsSource);

        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(id);
        if (cfg == null) {
            log.error("item not exists,id={}", id);
            return;
        }

        Map<Integer, Goods> items = getModel().getItems();
        Goods absent = items.computeIfAbsent(id, k -> buildGoods(id, 0));
        absent.count += count;
        if (absent.count < 0) {
            absent.count = 0;
        }

        // 如果后处理 没有对 数据做变动,且需要 发送
        send(Arrays.asList(id));
    }

    /**
     * 添加物品
     *
     * @param goodsList
     * @param send
     */
    public void addGoods(Collection<Goods> goodsList, boolean send, GoodsSource goodsSource) {
        if (goodsList == null || goodsList.size() == 0) {
            return;
        }

        log.debug("BagManager addGoods,goodsList={},send={},goodsSource={}", goodsList.size(), send, goodsSource);
        Set<Integer> set = null;
        if (send) {
            set = new HashSet<>();
        }

        for (Goods goods : goodsList) {
            addGoods(goods.getGoodsId(), goods.getCount(), false, true, goodsSource);
            if (send) {
                set.add(goods.getGoodsId());
            }
        }

        if (send && set.size() > 0) {
            send(set);
        }
    }

    /**
     * 更改道具的值为指定值,
     *
     * @param id    道具ID
     * @param count 道具数量
     * @param send  是否通知客户端; true 会通知
     */
    public void setGoodsValue(int id, int count, boolean send, GoodsSource goodsSource) {
        log.debug("BagManager addGoods,goodsId={},count={},send={},goodsSource={}", id, count, send, goodsSource);
        if (count <= 0) {
            return;
        }

        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(id);
        if (cfg == null) {
            log.error("item not exists,id={}", id);
            return;
        }

        Map<Integer, Goods> items = getModel().getItems();
        Goods absent = items.computeIfAbsent(id, k -> buildGoods(id, 0));
        absent.count = count;

        if (send) {
            send(Arrays.asList(id));
        }
    }

    /**
     * 是否拥有 对应的道具
     *
     * @param id
     * @param count
     * @return
     */
    public boolean hasGoods(int id, int count) {
        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(id);
        if (cfg == null) {
            getModel().getItems().remove(id);
            return false;
        }

        Map<Integer, Goods> goodsMap = getModel().getItems();
        if (goodsMap.containsKey(id)) {
            return goodsMap.get(id).getCount() >= count;
        }
        return false;
    }

    /**
     * 移除一个道具,
     *
     * @param id
     * @param count
     * @param send  是否通知客户端; true 会通知
     * @return 数量不够返回false;
     */
    public boolean removeGoods(int id, int count, boolean send, GoodsSource goodsSource) {
        log.debug("BagManager removeGoods,goodsId={},count={},send={},goodsSource={}", id, count, send, goodsSource);
        if (count <= 0) {
            return false;
        }

        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(id);
        if (cfg == null) {
            return false;
        }

        Map<Integer, Goods> goodsMap = getModel().getItems();
        Goods goods = goodsMap.get(id);
        if (goods == null || goods.getCount() < count) {
            return false;
        }
        goods.count -= count;
        if (send) {
            send(Arrays.asList(id));
        }
        return true;
    }

    public boolean removeGoods(int id, boolean send, GoodsSource goodsSource) {
        log.debug("BagManager addGoods,goodsId={},send={},goodsSource={}", id, send, goodsSource);
        ItemCfg cfg = GameConfigManager.getInstance().getItemBaseDataManager().getItemConfigWithID(id);
        if (cfg == null) {
            return false;
        }

        Goods goods = getModel().getItems().get(id);
        if (goods == null) {
            return false;
        }

        goods.count = 0;
        if (send) {
            send(Arrays.asList(id));
        }

        return true;
    }

    public void send(Collection<Integer> goodsIds) {
        GoodsChange.getInstance().newNotifyGoods(owner.getPlayerID(), goodsIds);
    }

    /**
     * 获取一个物品
     *
     * @param id
     * @return
     */
    public Goods getGoodsDefault(int id) {
        Goods goods = getModel().getItems().get(id);
        if (goods == null) {
            goods = new Goods();
            goods.setGoodsId(id);
            goods.setCount(0);
            goods.setPlayerId(owner.getPlayerID());
            return goods;
        }
        return goods;
    }

    /**
     * 获取一个物品的数量,如果不存在 则返回0
     *
     * @return
     */
    public int getGoodsValue(int id) {
        Goods goods = getModel().getItems().get(id);
        if (goods == null) {
            return 0;
        }
        return goods.getCount();
    }

    /**
     * 获取一组物品
     *
     * @param goodsIds
     * @return
     */
    public List<Goods> getGoodsList(Collection<Integer> goodsIds) {
        List<Goods> result = new ArrayList<>();
        if (goodsIds == null || goodsIds.size() == 0) {
            return result;
        }

        Map<Integer, Goods> goodsMap = getModel().getItems();
        for (Integer id : goodsIds) {
            Goods goods = goodsMap.get(id);
            if (goods != null) {
                result.add(goods);
            }
        }

        return result;
    }

    public static Goods buildGoods(int id, int count) {
        Goods goods = new Goods();
        goods.setGoodsId(id);
        goods.setCount(count);
        return goods;
    }

    public List<Goods> getAll() {
        return new ArrayList<>(getModel().getItems().values());
    }

    //重置体力
    public void resetAP(boolean isSend) {
        setGoodsValue(GoodsConsts.ITEM_AP_ID, 100, isSend, GoodsSource.RESET);
    }

}
