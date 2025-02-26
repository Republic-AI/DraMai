package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.task.goods.GoodsChange;
import com.infinity.ai.domain.model.Goods;
import com.infinity.ai.domain.tables.PlayerBag;
import com.infinity.common.config.data.ItemCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.db.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

//背包管理器
@Slf4j
public class NpcBagManager {
    private NpcHolder owner;

    public NpcBagManager(NpcHolder npcHolder) {
        this.owner = npcHolder;
    }

    private PlayerBag getModel() {
        return owner.getNpcModel().get_v().getBag();
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
        log.debug("NpcBagManager addGoods,goodsId={},count={},send={},goodsSource={}", id, count, send, goodsSource);

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

        if (send) {
            send(Arrays.asList(id));
        }

        //同步NPC数据
        syncNpcItemData();
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

        log.debug("NpcBagManager addGoods,goodsList={},send={},goodsSource={}", goodsList.size(), send, goodsSource);
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

        //同步NPC数据
        syncNpcItemData();
    }

    /**
     * 更改道具的值为指定值,
     *
     * @param id    道具ID
     * @param count 道具数量
     * @param send  是否通知客户端; true 会通知
     */
    public void setGoodsValue(int id, int count, boolean send, GoodsSource goodsSource) {
        log.debug("NpcBagManager addGoods,goodsId={},count={},send={},goodsSource={}", id, count, send, goodsSource);
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

        //同步NPC数据
        syncNpcItemData();
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
        log.debug("NpcBagManager removeGoods,goodsId={},count={},send={},goodsSource={}", id, count, send, goodsSource);
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

        //同步NPC数据
        syncNpcItemData();
        return true;
    }

    public boolean removeGoods(int id, boolean send, GoodsSource goodsSource) {
        log.debug("NpcBagManager addGoods,goodsId={},send={},goodsSource={}", id, send, goodsSource);
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

        //同步NPC数据
        syncNpcItemData();
        return true;
    }

    public void send(Collection<Integer> goodsIds) {
        GoodsChange.getInstance().newNotifyGoods(owner.getNpcId(), goodsIds);
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
            goods.setPlayerId(owner.getNpcId());
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

    /**
     * 猫币支付
     *
     * @param count       支付数量
     * @param isSend      是否发送通知
     * @param goodsSource 来源
     * @return true:支付成功，false:支付失败
     */
    public Pair<Boolean, Integer> payForCatCoin(int count, boolean isSend, GoodsSource goodsSource) {
        if (count <= 0) {
            return new Pair<Boolean, Integer>(true, 0);
        }

        Goods goods = getModel().getItems().get(GoodsConsts.ITEM_MONEY_ID);
        if (goods == null) {
            return new Pair<Boolean, Integer>(false, ErrorCode.PAY_ERROR_CATCOIN_INSUFFICIENT);
        }

        synchronized (goods) {
            if (goods.getCount() < count) {
                return new Pair<Boolean, Integer>(false, ErrorCode.PAY_ERROR_CATCOIN_INSUFFICIENT);
            }

            goods.count -= count;
            if (isSend) {
                send(Arrays.asList(goods.goodsId));
            }

            return new Pair<Boolean, Integer>(true, 0);
        }
    }

    private void syncNpcItemData(){
        this.owner.getNpc().getNpcDataListener().loadItems();
    }

}
