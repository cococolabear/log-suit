package com.superad.log.dao;

public interface CacheDao {

	public boolean hasIpByKey(String key, String period, long ip_long);

	public void expire(String period);

	public void delete(String key);

}
