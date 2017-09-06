package com.superad.log.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Meter;
import com.superad.log.core.LogConstant;
import com.superad.log.dao.LogDao;

@Component
public class LogDaoImpl implements LogDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Value("${spring.datasource.table}")
	private String table;

	private Meter meter = LogConstant.METRICREGISTRY.meter(LogConstant.SAVE);

	@Override
	public int[] save(List<Map<String, Object>> list, String... columns) {

		if (list.isEmpty()) {
			return new int[] { 0 };
		}

		List<String> sqls = new ArrayList<>();

		for (Map<String, Object> map : list) {

			StringBuffer buffer = new StringBuffer();

			buffer.append("INSERT INTO " + table + " (");

			String key = "";
			String value = "";
			for (Entry<String, Object> entry : map.entrySet()) {
				key += entry.getKey() + LogConstant.COMMA;
				value += "'" + String.valueOf(entry.getValue()).replaceAll("'", "") + "'" + LogConstant.COMMA;
			}

			buffer.append(key);
			buffer.delete(buffer.length() - 1, buffer.length());

			buffer.append(" ) VALUES (");

			buffer.append(value);
			buffer.delete(buffer.length() - 1, buffer.length());

			buffer.append(") ON DUPLICATE KEY UPDATE ");

			for (String column : columns) {

				switch (column) {
				case "adv_price":
					buffer.append(column + " = (adv_price + " + map.get(column) + " )/2 ,");
					break;
				case "pay_out":
					buffer.append(column + " = (pay_out + " + map.get(column) + " )/2 ,");
					break;
				default:
					buffer.append(column + " = " + column + " + " + map.get(column) + ",");
					break;
				}

			}

			buffer.delete(buffer.length() - 1, buffer.length());
			sqls.add(buffer.toString());
		}

		meter.mark(sqls.size());

		int[] result = jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));

		return result;
	}

}
