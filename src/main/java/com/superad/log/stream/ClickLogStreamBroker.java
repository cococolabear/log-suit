package com.superad.log.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Meter;
import com.google.gson.Gson;
import com.superad.log.bean.ClickLog;
import com.superad.log.core.LogConstant;
import com.superad.log.service.AppService;

@Component
public class ClickLogStreamBroker extends AbstractStreamFactory implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private ExecutorService executorService = Executors
			.newFixedThreadPool(2 * (Runtime.getRuntime().availableProcessors() + 1));

	@Resource
	private AppService appService;

	@Value("${application.kafka.click.topic}")
	private String topic;

	@Value("${application.kafka.click.batch-size}")
	private int batch;

	private Meter meter = LogConstant.METRICREGISTRY.meter(LogConstant.CLICK_LOG);

	public void onReceive(List<String> messages) {

		meter.mark(messages.size());

		List<ClickLog> list = new ArrayList<ClickLog>();

		for (String message : messages) {

			ClickLog clickLog = null;

			try {
				clickLog = new Gson().fromJson(message, ClickLog.class);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				continue;
			}

			if (clickLog.getCampaign_id() <= 0 || clickLog.getChannel_id() <= 0) {
				continue;
			}

			list.add(clickLog);
		}

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					appService.saveClickLogs(list);
					commit();
					continues.incrementAndGet();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void run() {
		super.start(topic, batch);
	}
}
