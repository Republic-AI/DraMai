package com.infinity.common.cache;

public interface ObjectCacheServiceInterface {
	public void removeObject(String key);
	public <T extends CacheObj> void saveObject(String key, T object);
	public <T extends CacheObj> T get(String key, Class<T> c) ;
}
