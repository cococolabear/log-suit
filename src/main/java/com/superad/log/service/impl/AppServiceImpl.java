package com.superad.log.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.superad.log.bean.ClickLog;
import com.superad.log.bean.Counter;
import com.superad.log.bean.InstallLog;
import com.superad.log.core.LogConstant;
import com.superad.log.dao.CacheDao;
import com.superad.log.dao.LogDao;
import com.superad.log.service.AppService;

@Component
public class AppServiceImpl implements AppService {

	@Resource
	private LogDao logDao;

	@Resource
	private CacheDao cacheDao;

	private DateTimeFormatter dFormatter = DateTimeFormat.forPattern("yyyyMMdd");

	private DateTimeFormatter hFormatter = DateTimeFormat.forPattern("yyyyMMddHH");

	// 区分缓存的key
	private final String IP_PREFIX = "ip_";

	private final String RIP_PREFIX = "rip_";

	@Override
	public void saveClickLogs(List<ClickLog> logs) {

		Iterator<ClickLog> iterator = logs.iterator();

		List<Counter> clist = new ArrayList<>();

		while (iterator.hasNext()) {
			ClickLog clickLog = iterator.next();

			long ts = clickLog.getClick_time() * 1000;
			String day = new DateTime(ts).toString(dFormatter);
			String hour = new DateTime(ts).toString(hFormatter);
			int time = Integer.valueOf(String.valueOf(DateTime.parse(hour, hFormatter).getMillis()).substring(0, 10));

			Counter counter = new Counter();
			counter.setTime(time);
			counter.setAdvertiser(clickLog.getAdvertiser());
			counter.setChannel_id(clickLog.getChannel_id());
			counter.setCampaign_id(clickLog.getCampaign_id());
			counter.setBd(clickLog.getBd());
			counter.setPm(clickLog.getPm());
			counter.setOm(clickLog.getOm());
			counter.setPrice_mode(clickLog.getPrice_mode());
			counter.setTraffic_source(clickLog.getTraffic_source());

			// 避免太长
			if (clickLog.getCh_subid() != null && clickLog.getCh_subid().length() > 100) {
				clickLog.setCh_subid(clickLog.getCh_subid().substring(0, 99));
			}
			counter.setCh_subid(clickLog.getCh_subid());

			// 避免太长
			if (clickLog.getCategory() != null && clickLog.getCategory().length() > 100) {
				clickLog.setCategory(clickLog.getCategory().substring(0, 99));
			}
			counter.setCategory(clickLog.getCategory());
			counter.setClick_count(1);
			counter.setAdv_price(clickLog.getAdv_price());
			counter.setPay_out(clickLog.getPay_out());

			// 保存来源
			switch (clickLog.getPl()) {
			case "ios":
				counter.setIos(1);
				break;
			case "android":
				counter.setAndroid(1);
				break;
			default:
				counter.setOther(1);
				break;
			}

			// 导量对象
			Counter fcounter = new Counter();
			BeanUtils.copyProperties(counter, fcounter);

			// 唯一ip缓存key
			String skey = IP_PREFIX + clickLog.getCampaign_id() + LogConstant.COMMA + clickLog.getChannel_id();
			if (!cacheDao.hasIpByKey(skey, String.valueOf(day), clickLog.getIp_long())) {
				counter.setClick_ip_count(1);
			}

			// 计算导量
			if (clickLog.getRedirect_campaign_id() > 0) {
				counter.setRedirect_count(1);
				counter.setRedirect_to(clickLog.getRedirect_campaign_id());
				// 唯一ip缓存key
				String rkey = RIP_PREFIX + clickLog.getCampaign_id() + LogConstant.COMMA + clickLog.getChannel_id();
				if (!cacheDao.hasIpByKey(rkey, day, clickLog.getIp_long())) {
					counter.setRedirect_ip_count(1);
				}

				// 设置导量属性
				fcounter.setCampaign_id(clickLog.getRedirect_campaign_id());
				fcounter.setRedirect_from(clickLog.getCampaign_id());
				String mkey = IP_PREFIX + fcounter.getCampaign_id() + LogConstant.COMMA + clickLog.getChannel_id();
				if (!cacheDao.hasIpByKey(mkey, day, clickLog.getIp_long())) {
					fcounter.setClick_ip_count(1);
				}
				String nkey = RIP_PREFIX + fcounter.getCampaign_id() + LogConstant.COMMA + clickLog.getChannel_id();
				if (!cacheDao.hasIpByKey(nkey, day, clickLog.getIp_long())) {
					fcounter.setRedirect_ip_count(1);
				}
				clist.add(fcounter);
			}
			clist.add(counter);
		}

		Map<String, Counter> smap = new HashMap<>();
		for (Counter counter : clist) {
			if (smap.containsKey(counter.line())) {
				Counter current = smap.get(counter.line());

				double adv_price = (current.getAdv_price() + counter.getAdv_price()) / 2;
				double pay_out = (current.getPay_out() + counter.getPay_out()) / 2;
				current.setAdv_price(adv_price);
				current.setPay_out(pay_out);
				current.setSum_adv_price(current.getSum_adv_price() + counter.getAdv_price());
				current.setSum_pay_out(current.getSum_pay_out() + counter.getPay_out());

				current.setClick_count(current.getClick_count() + counter.getClick_count());
				current.setClick_ip_count(current.getClick_ip_count() + counter.getClick_ip_count());

				current.setAndroid(current.getAndroid() + counter.getAndroid());
				current.setIos(current.getIos() + counter.getIos());
				current.setOther(current.getOther() + counter.getOther());

				current.setRedirect_count(current.getRedirect_count() + counter.getRedirect_count());
				current.setRedirect_ip_count(current.getRedirect_ip_count() + counter.getRedirect_ip_count());
			} else {
				smap.put(counter.line(), counter);
			}
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (Counter counter : smap.values()) {

			Map<String, Object> map = new HashMap<>();
			map.put("time", counter.getTime());
			map.put("advertiser", counter.getAdvertiser());
			map.put("bd", counter.getBd());
			map.put("pm", counter.getPm());
			map.put("om", counter.getOm());
			map.put("channel_id", counter.getChannel_id());
			map.put("campaign_id", counter.getCampaign_id());
			map.put("ch_subid", counter.getCh_subid());
			map.put("category", counter.getCategory());
			map.put("price_mode", counter.getPrice_mode());
			map.put("traffic_source", counter.getTraffic_source());
			map.put("redirect_to", counter.getRedirect_to());
			map.put("redirect_from", counter.getRedirect_from());
			map.put("adv_price", String.format("%.2f", counter.getAdv_price()));
			map.put("sum_adv_price", String.format("%.2f", counter.getSum_adv_price()));
			map.put("pay_out", String.format("%.2f", counter.getPay_out()));
			map.put("sum_pay_out", String.format("%.2f", counter.getSum_pay_out()));
			map.put("ios", counter.getIos());
			map.put("android", counter.getAndroid());
			map.put("other", counter.getOther());
			map.put("click_count", counter.getClick_count());
			map.put("click_ip_count", counter.getClick_ip_count());
			map.put("redirect_count", counter.getRedirect_count());
			map.put("redirect_ip_count", counter.getRedirect_ip_count());

			list.add(map);
		}

		logDao.save(list, "adv_price", "sum_adv_price", "pay_out", "sum_pay_out", "click_count", "click_ip_count",
				"redirect_count", "redirect_ip_count", "ios", "android", "other");
	}

	@Override
	public void saveInstallLogs(List<InstallLog> logs) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Iterator<InstallLog> iterator = logs.iterator();

		while (iterator.hasNext()) {

			Map<String, Object> map = new HashMap<>();

			InstallLog installLog = iterator.next();

			long ts = installLog.getFeed_time() * 1000;
			String hour = new DateTime(ts).toString(hFormatter);
			int time = Integer.valueOf(String.valueOf(DateTime.parse(hour, hFormatter).getMillis()).substring(0, 10));
			map.put("time", time);
			map.put("channel_id", installLog.getChannel_id());

			// 避免太长
			if (installLog.getCh_subid().length() > 100) {
				installLog.setCh_subid(installLog.getCh_subid().substring(0, 99));
			}
			map.put("ch_subid", installLog.getCh_subid());

			// 设置广告属性
			map.put("advertiser", installLog.getAdvertiser());
			map.put("bd", installLog.getBd());
			map.put("pm", installLog.getPm());
			map.put("om", installLog.getOm());

			// 避免太长
			if (installLog.getCategory().length() > 100) {
				installLog.setCategory(installLog.getCategory().substring(0, 99));
			}
			map.put("category", installLog.getCategory());
			map.put("price_mode", installLog.getPrice_mode());
			map.put("traffic_source", installLog.getTraffic_source());

			// 设置安装数
			map.put("install_count", 1);

			// 设置转发数
			map.put("post_count", 0);
			map.put("redirect_post_count", 0);

			if (installLog.getIs_post() == 1) {
				map.put("post_count", 1);
			}

			// 如果来源相同
			if (installLog.getSource_campaign_id() == installLog.getCampaign_id()) {
				map.put("campaign_id", installLog.getCampaign_id());
				map.put("redirect_to", 0);
				map.put("redirect_from", 0);

			} else {
				// 设置导入对象
				Map<String, Object> from = new HashMap<String, Object>(map);
				from.put("campaign_id", installLog.getSource_campaign_id());
				from.put("redirect_to", installLog.getCampaign_id());
				from.put("redirect_from", 0);

				// 只计算一次post_count
				from.put("post_count", 0);

				list.add(from);

				// 设置原对象
				map.put("campaign_id", installLog.getCampaign_id());
				map.put("redirect_to", 0);
				map.put("redirect_from", installLog.getSource_campaign_id());

				if (installLog.getIs_post() == 1) {
					map.put("redirect_post_count", 1);
				}
			}
			list.add(map);
		}

		logDao.save(list, "install_count", "post_count", "redirect_post_count");
	}
}
