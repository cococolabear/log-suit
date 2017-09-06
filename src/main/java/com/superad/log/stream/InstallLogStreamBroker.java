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
import com.superad.log.bean.InstallLog;
import com.superad.log.core.LogConstant;
import com.superad.log.service.AppService;

@Component
public class InstallLogStreamBroker extends AbstractStreamFactory implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private ExecutorService executorService = Executors
			.newFixedThreadPool((Runtime.getRuntime().availableProcessors() + 1));

	@Resource
	private AppService appService;

	@Value("${application.kafka.install.topic}")
	private String topic;

	@Value("${application.kafka.install.batch-size}")
	private int batch;

	private Meter meter = LogConstant.METRICREGISTRY.meter(LogConstant.INSTALL_LOG);

	public void onReceive(List<String> messages) {

		meter.mark(messages.size());

		List<InstallLog> list = new ArrayList<InstallLog>();

		for (String message : messages) {

			InstallLog installLog = null;

			try {
				installLog = new Gson().fromJson(message, InstallLog.class);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				continue;
			}

			if (installLog.getCampaign_id() <= 0 || installLog.getChannel_id() <= 0) {
				continue;
			}

			list.add(installLog);
		}

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					appService.saveInstallLogs(list);
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