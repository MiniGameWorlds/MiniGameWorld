package com.wbm.minigamemaker.games.frame;

import org.bukkit.Location;

public class MiniGameSetting {

	/*
	 * 미니게임 설정값 관리 클래스
	 */
	// 파일 관리 o
	private String title;
	// 파일 관리 o
	// 기본값: new Location(Bukkit.getWorld("world"), 0, 4, 0)
	private Location location;
	// 파일 관리 o
	private int maxPlayerCount;
	// 파일 관리 o
	private int waitingTime;
	// 파일 관리 o
	private int timeLimit;
	// 파일 관리 o
	// 기본값: true
	private boolean actived;
	// 파일 관리 x
	// 기본값: false
	private boolean settingFixed;
	// 파일 관리 x
	// 기본값: false
	private boolean scoreNotifying;

	public MiniGameSetting(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.title = title;
		this.location = location;
		this.maxPlayerCount = maxPlayerCount;
		this.waitingTime = waitingTime;
		this.timeLimit = timeLimit;

		this.actived = true;
		this.settingFixed = false;
		this.scoreNotifying = false;
	}

	public void setSettingFixed(boolean settingFixed) {
		this.settingFixed = settingFixed;
	}

	public void setScoreNotifying(boolean scoreNotifying) {
		this.scoreNotifying = scoreNotifying;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setMaxPlayerCount(int maxPlayerCount) {
		this.maxPlayerCount = maxPlayerCount;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void setActived(boolean actived) {
		this.actived = actived;
	}

	/*
	 * getter는 public해도 상관없음
	 */
	public String getTitle() {
		return title;
	}

	public Location getLocation() {
		return location;
	}

	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public boolean isActived() {
		return actived;
	}

	public boolean isSettingFixed() {
		return settingFixed;
	}

	public boolean isScoreNotifying() {
		return scoreNotifying;
	}

}
