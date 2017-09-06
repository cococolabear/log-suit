package com.superad.log.dao;

import java.util.List;
import java.util.Map;

public interface LogDao {

	public int[] save(List<Map<String, Object>> list, String... columns);
}
