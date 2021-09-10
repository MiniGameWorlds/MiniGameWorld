package com.wbm.minigamemaker.games.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	// 기본값: 초기값
	// 설명: 미니게임 이름(참가 이름)
	private String title;

	// 파일 관리 o
	// 기본값: new Location(Bukkit.getWorld("world"), 0, 4, 0)
	// 설명: 미니게임 플레이 장소
	private Location location;

	// 파일 관리 o
	// 기본값: 초기값
	// 설명: 최대 참여 인원수
	private int maxPlayerCount;

	// 파일 관리 o
	// 기본값: 초기값
	// 설명: 게임 대기 시간 (초)
	private int waitingTime;

	// 파일 관리 o
	// 기본값: 초기값
	// 설명: 게임 진행 시간 (초)
	private int timeLimit;

	// 파일 관리 o
	// 기본값: true
	// 설명: 게임 활성화 여부
	private boolean active;

	// 파일 관리 x
	// 기본값: false
	// 설명: 미니게임의 특정 세팅값(waitingTime, timeLimit, maxPlayerCount) 고정 여부
	private boolean settingFixed;

	// 파일 관리 x
	// 기본값: false
	// 설명: 스코어 변동 알림 여부
	private boolean scoreNotifying;

	// 파일 관리 x
	// 기본값: false
	// 설명: 인원수가 maxPlayerCount값이어야 게임이 진행됨
	private boolean forcePlayerCount;

	// 파일 관리 o
	// 기본값: false
	// 설명: 미니게임 튜토리얼
	private List<String> tutorial;

	// 파일 관리 o
	// 기본값: 초기값
	// 설명: 커스텀 데이터 설정 섹션
	private Map<String, Object> customData;

	public MiniGameSetting(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.title = title;
		this.location = location;
		this.maxPlayerCount = maxPlayerCount;
		this.waitingTime = waitingTime;
		this.timeLimit = timeLimit;

		this.active = true;
		this.settingFixed = false;
		this.scoreNotifying = false;
		this.forcePlayerCount = false;
		this.tutorial = new ArrayList<String>();
		this.customData = new HashMap<String, Object>();
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

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setForcePlayerCount(boolean forcePlayerCount) {
		this.forcePlayerCount = forcePlayerCount;
	}

	public void setTutorial(List<String> tutorial) {
		this.tutorial = tutorial;
	}

	public void setCustomData(Map<String, Object> customData) {
		this.customData = customData;
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

	public boolean isActive() {
		return active;
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

	public List<String> getTutorial() {
		return this.tutorial;
	}

	public Map<String, Object> getCustomData() {
		return this.customData;
	}

	public Map<String, Object> getFileSetting() {
		// return settings that exist in minigames.yml
		Map<String, Object> setting = new HashMap<String, Object>();

		setting.put("title", this.getTitle());
		setting.put("location", this.getLocation());
		setting.put("maxPlayerCount", this.getMaxPlayerCount());
		setting.put("waitingTime", this.getWaitingTime());
		setting.put("timeLimit", this.getTimeLimit());
		setting.put("active", this.isActive());
		setting.put("tutorial", this.getTutorial());
		setting.put("customData", this.customData);

		return setting;
	}

	public void setFileSetting(Map<String, Object> setting) {
		/*
		 * apply "maxPlayerCount", "waitingTime", "timeLimit" when "settingFixed" is false
		 */

		// title
		String title = (String) setting.get("title");
		this.setTitle(title);

		// location
		Location location = (Location) setting.get("location");
		this.setLocation(location);

		// when settingFixed is false: apply maxPlayerCount, timeLimit, waitingTime
		if (!isSettingFixed()) {
			// maxPlayerCount
			int maxPlayerCount = (int) setting.get("maxPlayerCount");
			this.setMaxPlayerCount(maxPlayerCount);

			// waitingTime
			int waitingTime = (int) setting.get("waitingTime");
			this.setWaitingTime(waitingTime);

			// timeLimit
			int timeLimit = (int) setting.get("timeLimit");
			this.setTimeLimit(timeLimit);
		}

		// active
		boolean active = (boolean) setting.get("active");
		this.setActive(active);

		// tutorial
		@SuppressWarnings("unchecked")
		List<String> tutorial = (List<String>) setting.get("tutorial");
		this.setTutorial(tutorial);

		// customData
		@SuppressWarnings("unchecked")
		Map<String, Object> customData = (Map<String, Object>) setting.get("customData");
		this.setCustomData(customData);
	}

}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
