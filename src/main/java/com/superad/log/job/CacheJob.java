package com.superad.log.job;

import java.util.Calendar;

import javax.annotation.Resource;

import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.superad.log.dao.CacheDao;

@Configuration
@EnableScheduling
public class CacheJob {

	@Resource
	private CacheDao cacheDao;

	@Scheduled(cron = "0 0 2 * * ?", zone = "Europe/London") // 凌晨2点清除前一天的缓存
	public void delCacheData() {
		Calendar calendar = Calendar.getInstance();
		// 最多保留3天
		calendar.add(Calendar.DATE, -3);
		String period = FastDateFormat.getInstance("yyyyMMdd").format(calendar.getTime());
		cacheDao.expire(period);
	}
}
