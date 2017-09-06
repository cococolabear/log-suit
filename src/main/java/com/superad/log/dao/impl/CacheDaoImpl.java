package com.superad.log.dao.impl;

import javax.annotation.Resource;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.superad.log.core.LogConstant;
import com.superad.log.dao.CacheDao;

@Component
public class CacheDaoImpl implements CacheDao {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private RocksDB rocksdb;

	@Override
	public boolean hasIpByKey(String key, String period, long ip) {
		String rkey = LogConstant.CLICK_LOG + LogConstant.ULINE + period + LogConstant.ULINE + key + LogConstant.COMMA
				+ ip;
		try {
			byte[] bytes = rocksdb.get(rkey.getBytes());
			if (bytes == null) {
				rocksdb.put(rkey.getBytes(), LogConstant.ONE.getBytes());
				return false;
			} else {
				return true;
			}
		} catch (RocksDBException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public void expire(String period) {
		// period 以前的都不要
		RocksIterator iterator = rocksdb.newIterator();

		// 清除click
		String rkey = LogConstant.CLICK_LOG + LogConstant.ULINE;
		for (iterator.seek(rkey.getBytes()); iterator.isValid(); iterator.next()) {
			String key = new String(iterator.key());
			long current = Long.parseLong(key.substring(rkey.length(), rkey.length() + period.length()));
			if (current < Long.parseLong(period)) {
				delete(key);
			}
		}
	}

	@Override
	public void delete(String key) {
		try {
			rocksdb.delete(key.getBytes());
		} catch (RocksDBException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
