package com.infinity.common.cache;

import com.infinity.common.cache.lv1.Lv1ObjectCacheService;
import com.infinity.common.cache.lv2.Lv2ObjectCacheService;

public class ObjectCacheService implements ObjectCacheServiceInterface {

	@Override
	public void removeObject(String key) {
		Lv1ObjectCacheService.getInstance().removeObject(key);
		Lv2ObjectCacheService.getInstance().removeObject(key);
		//TODO 广播通知其他节点
	}

	@Override
	public <T extends CacheObj> void saveObject(String key, T object) {
		Lv2ObjectCacheService.getInstance().saveObject(key, object);
		Lv1ObjectCacheService.getInstance().saveObject(key, object);
		//TODO 广播通知其他节点
	}

	@Override
	public <T extends CacheObj> T get(String key, Class<T> c) {
		T t = Lv1ObjectCacheService.getInstance().get(key, c);
		if(t==null){
			t=Lv2ObjectCacheService.getInstance().get(key, c);
		}
		return t;
	}

}
