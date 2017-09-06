package com.superad.log.core;

import com.codahale.metrics.MetricRegistry;

public class LogConstant {

	public static final MetricRegistry METRICREGISTRY = new MetricRegistry();

	public static final int INIT_SIZE = 100;

	public static final int CONN_SIZE = 2000;

	public static final int VALIDATE_TIME = 1000;

	public static final String APPLICATION = "LogSuit";

	public static final String ONE = "1";

	public static final String ULINE = "_";

	public static final String COMMA = ",";

	public static final String CLICK_LOG = "click_log";

	public static final String INSTALL_LOG = "install_log";

	public static final String CAMPAIGN = "campaign";

	public static final String SAVE = "save";

}
