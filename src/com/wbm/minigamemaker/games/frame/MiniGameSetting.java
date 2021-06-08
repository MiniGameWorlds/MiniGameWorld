package com.wbm.minigamemaker.games.frame;

import org.bukkit.Location;

public class MiniGameSetting {

	/*
	 * 미니게임 설정값 관리 클래스
	 * 
	 * 파일 관리: minigames.json파일에 속성값이 있는지 여부
	 * 
	 * 기본값: 미니게임의 기본 세팅값
	 */
	// 파일 관리 o
	// 기본값: 없음
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
	// 파일 관리 x
	// 기본값: false
	// 설명: maxPlayerCount값이 강제 인원수로 설정됨
	private boolean forcePlayerCount;

	public MiniGameSetting(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.title = title;
		this.location = location;
		this.maxPlayerCount = maxPlayerCount;
		this.waitingTime = waitingTime;
		this.timeLimit = timeLimit;

		this.actived = true;
		this.settingFixed = false;
		this.scoreNotifying = false;
		this.forcePlayerCount = false;
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

	public void setForcePlayerCount(boolean forcePlayerCount) {
		this.forcePlayerCount = forcePlayerCount;
	}

	// getter
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

	public boolean isForcePlayerCount() {
		return forcePlayerCount;
	}

}
