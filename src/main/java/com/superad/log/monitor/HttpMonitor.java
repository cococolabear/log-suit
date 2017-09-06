package com.superad.log.monitor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Meter;
import com.superad.log.core.LogConstant;
import com.superad.log.core.Report;

@Component
public class HttpMonitor implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Server server;

	private static final String MONITOR_URL = "/metrics";

	private static final String LINE = "\r\n";

	@Value("${application.monitor.port}")
	private int MPORT = 23333;

	private void start() {
		server = new Server(MPORT);
		try {
			server.setHandler(new JettylHandler());
			server.start();
			server.join();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void stop() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private class JettylHandler extends AbstractHandler {

		@Override
		public void handle(String text, Request request, HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse) throws IOException, ServletException {

			if (httpServletRequest.getRequestURI().equalsIgnoreCase(MONITOR_URL)) {
				httpServletResponse.setContentType("application/json;charset=utf-8");
				Meter click = (Meter) LogConstant.METRICREGISTRY.getMetrics().get(LogConstant.CLICK_LOG);
				Meter install = (Meter) LogConstant.METRICREGISTRY.getMetrics().get(LogConstant.INSTALL_LOG);
				Meter save = (Meter) LogConstant.METRICREGISTRY.getMetrics().get(LogConstant.SAVE);
				Report report = new Report();

				Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
				report.setTime(sdf.format(calendar.getTime()));
				report.setType(LogConstant.APPLICATION);

				if (click == null || install == null || save == null) {
					httpServletResponse.getWriter().write(report.toString() + LINE);
					httpServletResponse.getWriter().flush();
					return;
				}

				// 设置监控项
				report.setClick(click.getCount());
				report.setCspeed(click.getFifteenMinuteRate());

				report.setInstall(install.getCount());
				report.setIspeed(install.getFifteenMinuteRate());

				report.setSspeed(save.getFifteenMinuteRate());

				httpServletResponse.getWriter().write(report.toString() + LINE);
				httpServletResponse.getWriter().flush();

			} else {

				String rmsg = "For Log Suit metrics please click <a href =." + MONITOR_URL + "> here</a>" + LINE;
				httpServletResponse.getWriter().write(rmsg);
				httpServletResponse.getWriter().flush();
			}

		}

	}

	@Override
	public void run() {
		this.start();
	}

}
