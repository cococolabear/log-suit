package com.superad.log.bean;

import com.superad.log.core.LogConstant;

public class Counter {

	private long time;

	private int advertiser;

	private int channel_id;

	private int campaign_id;

	private String ch_subid;

	private int bd;

	private int pm;

	private int om;

	private String category;

	private String price_mode;

	private String traffic_source;

	private int redirect_to;

	private int redirect_from;

	private double adv_price;

	private double sum_adv_price;

	private double pay_out;

	private double sum_pay_out;

	private int ios;

	private int android;;

	private int other;;

	private int click_count;

	private int click_ip_count;

	private int redirect_count;

	private int redirect_ip_count;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(int advertiser) {
		this.advertiser = advertiser;
	}

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public int getCampaign_id() {
		return campaign_id;
	}

	public void setCampaign_id(int campaign_id) {
		this.campaign_id = campaign_id;
	}

	public String getCh_subid() {
		return ch_subid;
	}

	public void setCh_subid(String ch_subid) {
		this.ch_subid = ch_subid;
	}

	public int getBd() {
		return bd;
	}

	public void setBd(int bd) {
		this.bd = bd;
	}

	public int getPm() {
		return pm;
	}

	public void setPm(int pm) {
		this.pm = pm;
	}

	public int getOm() {
		return om;
	}

	public void setOm(int om) {
		this.om = om;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPrice_mode() {
		return price_mode;
	}

	public void setPrice_mode(String price_mode) {
		this.price_mode = price_mode;
	}

	public String getTraffic_source() {
		return traffic_source;
	}

	public void setTraffic_source(String traffic_source) {
		this.traffic_source = traffic_source;
	}

	public int getRedirect_to() {
		return redirect_to;
	}

	public void setRedirect_to(int redirect_to) {
		this.redirect_to = redirect_to;
	}

	public int getRedirect_from() {
		return redirect_from;
	}

	public void setRedirect_from(int redirect_from) {
		this.redirect_from = redirect_from;
	}

	public double getAdv_price() {
		return adv_price;
	}

	public void setAdv_price(double adv_price) {
		this.adv_price = adv_price;
	}

	public double getSum_adv_price() {
		return sum_adv_price;
	}

	public void setSum_adv_price(double sum_adv_price) {
		this.sum_adv_price = sum_adv_price;
	}

	public double getPay_out() {
		return pay_out;
	}

	public void setPay_out(double pay_out) {
		this.pay_out = pay_out;
	}

	public double getSum_pay_out() {
		return sum_pay_out;
	}

	public void setSum_pay_out(double sum_pay_out) {
		this.sum_pay_out = sum_pay_out;
	}

	public int getIos() {
		return ios;
	}

	public void setIos(int ios) {
		this.ios = ios;
	}

	public int getAndroid() {
		return android;
	}

	public void setAndroid(int android) {
		this.android = android;
	}

	public int getOther() {
		return other;
	}

	public void setOther(int other) {
		this.other = other;
	}

	public int getClick_count() {
		return click_count;
	}

	public void setClick_count(int click_count) {
		this.click_count = click_count;
	}

	public int getClick_ip_count() {
		return click_ip_count;
	}

	public void setClick_ip_count(int click_ip_count) {
		this.click_ip_count = click_ip_count;
	}

	public int getRedirect_count() {
		return redirect_count;
	}

	public void setRedirect_count(int redirect_count) {
		this.redirect_count = redirect_count;
	}

	public int getRedirect_ip_count() {
		return redirect_ip_count;
	}

	public void setRedirect_ip_count(int redirect_ip_count) {
		this.redirect_ip_count = redirect_ip_count;
	}

	public String line() {
		String line = time + LogConstant.COMMA + advertiser + LogConstant.COMMA + channel_id + LogConstant.COMMA
				+ campaign_id + LogConstant.COMMA + ch_subid + LogConstant.COMMA + bd + LogConstant.COMMA + pm
				+ LogConstant.COMMA + LogConstant.COMMA + om + LogConstant.COMMA + category + LogConstant.COMMA
				+ price_mode + LogConstant.COMMA + traffic_source + LogConstant.COMMA + redirect_to + LogConstant.COMMA
				+ redirect_from;
		return line;
	}
}
