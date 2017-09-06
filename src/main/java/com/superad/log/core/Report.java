package com.superad.log.core;

import com.google.gson.Gson;

public class Report {

	private long click;

	private double cspeed;

	private long install;

	private double ispeed;

	private double sspeed;

	private String type;

	private String time;

	private String unit = "15min";

	public long getClick() {
		return click;
	}

	public void setClick(long click) {
		this.click = click;
	}

	public double getCspeed() {
		return cspeed;
	}

	public void setCspeed(double cspeed) {
		this.cspeed = cspeed;
	}

	public long getInstall() {
		return install;
	}

	public void setInstall(long install) {
		this.install = install;
	}

	public double getIspeed() {
		return ispeed;
	}

	public void setIspeed(double ispeed) {
		this.ispeed = ispeed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getSspeed() {
		return sspeed;
	}

	public void setSspeed(double sspeed) {
		this.sspeed = sspeed;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
