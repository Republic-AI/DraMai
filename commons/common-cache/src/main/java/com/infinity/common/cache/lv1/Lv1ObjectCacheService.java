package com.infinity.common.cache.lv1;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.infinity.common.cache.CacheObj;
import com.infinity.common.cache.ObjectCacheServiceInterface;

public class Lv1ObjectCacheService implements ObjectCacheServiceInterface {
	private static Lv1ObjectCacheService svc=new Lv1ObjectCacheService();
	private Lv1ObjectCacheService(){}
	public static Lv1ObjectCacheService getInstance(){
		return svc;
	}
	
	
	private static LoadingCache<String, CacheObj> cache = CacheBuilder.newBuilder().refreshAfterWrite(3, TimeUnit.HOURS)// 给定时间内没有被读/写访问，则回收。
	            .expireAfterAccess(3, TimeUnit.HOURS)// 缓存过期时间和redis缓存时长一样
	            .maximumSize(300000).// 设置缓存个数
	            build(new CacheLoader<String,CacheObj>() {
					@Override
					public CacheObj load(String key) throws Exception {
						return null;
					}
	            });

	@Override
	public void removeObject(String key) {
		cache.invalidate(key);
	}

	@Override
	public <T extends CacheObj> void saveObject(String key, T object) {
		cache.put(key, object);
	}

	@Override
	public <T extends CacheObj> T get(String key, Class<T> c) {
		CacheObj cacheObj = null;
		try {
			cacheObj = cache.get(key);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return (T) cacheObj;
	}

}
