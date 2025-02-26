package com.infinity.ai.platform.map.object;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ObjectStatus {

    public enum MapObjType {
        //农田
        Farmland(1, "farmland"),
        Cook(2, "cook"),
        Table(3, "table"),
        Bed(4, "bed"),
        Butcher(5, "butcher"),
        Breeding(6, "breeding");

        public final int code;
        public final String name;

        MapObjType(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static Optional<MapObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    public static enum DefaultState {
        //空闲
        DEFAULT(0);

        public final int code;

        DefaultState(int code) {
            this.code = code;
        }
    }

    //农田状态枚举: 空闲/幼苗/果物
    public enum FarmingObjType {
        //空闲
        FREE(0),
        //种植(蔬菜/玉米/小麦)
        SEEDLING(1),
        //成熟了，待收割
        FRUIT(2);

        public final int code;

        FarmingObjType(int code) {
            this.code = code;
        }

        public static Optional<FarmingObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    //灶台状态枚举：空闲/使用
    public static enum StoveObjType {
        //空闲
        FREE(0),
        //做饭中
        USE(1);

        public final int code;

        StoveObjType(int code) {
            this.code = code;
        }

        public static Optional<StoveObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    //餐桌状态枚举
    public static enum DiningTableObjType {
        //空闲
        FREE(0),
        //吃饭中
        USE(1);

        public final int code;

        DiningTableObjType(int code) {
            this.code = code;
        }

        public static Optional<DiningTableObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    //床状态枚举
    public static enum BedObjType {
        //空闲
        FREE(0),
        //睡觉中
        USE(1);

        public final int code;

        BedObjType(int code) {
            this.code = code;
        }

        public static Optional<BedObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    //厨房状态枚举
    public enum KitchenObjType {
        //空闲
        FREE(0),
        //做饭中
        USE(1);

        public final int code;

        KitchenObjType(int code) {
            this.code = code;
        }

        public static Optional<KitchenObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    //养殖户的宰羊状态枚举：饥/饱、未屠宰/已屠宰
    public enum ButcherObjType {
        //未屠宰
        UNSLAUGHTERED(0),
        //已屠宰
        slaughter(1);

        public final int code;

        ButcherObjType(int code) {
            this.code = code;
        }

        public static Optional<ButcherObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    //养殖户的宰羊状态枚举
    public enum BreedingObjType {
        //空闲
        FREE(0),
        //饥
        Hunger(1),
        //饱
        Full(2),
        ;

        public final int code;

        BreedingObjType(int code) {
            this.code = code;
        }

        public static Optional<BreedingObjType> getByCode(int code) {
            return Arrays.stream(values()).filter(o -> Objects.equals(o.code, code)).findFirst();
        }
    }

    public static Enum<?> createState(String type, int state) {
        Enum<?> defaultState;
        switch (type) {
            case "farmland":
                defaultState = Optional.ofNullable(FarmingObjType.getByCode(state).get()).orElse(FarmingObjType.FREE);
                break;
            case "cook":
                defaultState = Optional.ofNullable(KitchenObjType.getByCode(state).get()).orElse(KitchenObjType.FREE);
                break;
            case "table":
                defaultState = Optional.ofNullable(DiningTableObjType.getByCode(state).get()).orElse(DiningTableObjType.FREE);
                break;
            case "bed":
                defaultState = Optional.ofNullable(BedObjType.getByCode(state).get()).orElse(BedObjType.FREE);
                break;
            case "butcher":
                defaultState = Optional.ofNullable(ButcherObjType.getByCode(state).get()).orElse(ButcherObjType.UNSLAUGHTERED);
                break;
            case "breeding":
                defaultState = Optional.ofNullable(BreedingObjType.getByCode(state).get()).orElse(BreedingObjType.FREE);
                break;
            default:
                defaultState = DefaultState.DEFAULT;
        }
        return defaultState;
    }

    // 获取物品的所有可能状态
    public static Enum<?>[] getAllPossibleStates(String type) {
        switch (type) {
            case "farmland":
                return FarmingObjType.values();
            case "cook":
                return KitchenObjType.values();
            case "table":
                return DiningTableObjType.values();
            case "bed":
                return BedObjType.values();
            case "butcher":
                return ButcherObjType.values();
            case "breeding":
                return BreedingObjType.values();
            default:
                return new Enum<?>[0];
        }
    }
}
