package com.infinity.common.cache.lv2;

import com.infinity.common.cache.ObjectCacheServiceInterface;
import com.infinity.common.cache.CacheObj;

public class Lv2ObjectCacheService implements ObjectCacheServiceInterface {
	private static Lv2ObjectCacheService svc=new Lv2ObjectCacheService();
	private Lv2ObjectCacheService(){}
	public static Lv2ObjectCacheService getInstance(){
		return svc;
	}
	@Override
	public void removeObject(String key) {
		ObjectCacheRedisService.remove(key);
	}

	@Override
	public <T extends CacheObj> void saveObject(String key, T object) {
		ObjectCacheRedisService.saveObject(key, object);
	}

	@Override
	public <T extends CacheObj> T get(String key, Class<T> c) {
		return ObjectCacheRedisService.get(key, c);
	}

}
