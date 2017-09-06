package com.superad.log.service;

import java.util.List;

import com.superad.log.bean.ClickLog;
import com.superad.log.bean.InstallLog;

public interface AppService {

	public void saveClickLogs(List<ClickLog> logs);

	public void saveInstallLogs(List<InstallLog> logs);

}
