package com.worldbiomusic.minigameworld.managers.language;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wbm.plugin.util.ServerTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.util.LangUtils;
import com.worldbiomusic.minigameworld.util.Setting;

import me.smessie.MultiLanguage.api.Language;

public class LanguageManager {
	private DataManager dataManager;
	private List<String> loadedLanguages;
	private List<LanguageFile> loadedLanguageFiles;

	public LanguageManager(DataManager dataManager) {
		this.dataManager = dataManager;
		this.loadedLanguages = new ArrayList<>();
		this.loadedLanguageFiles = new ArrayList<>();

		init();
		
		// setup static variable
		LangUtils.languageManager = this;
	}

	public void init() {
		setupFiles();
		registerLoadedLanguageFiles();
	}

	private void setupFiles() {
		try {
			if (ServerTool.isPluginEnabled("AdvancedMultiLanguage")) {
				setupAllLanguageFiles();
			} else {
				setupDefaultLanguageFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupAllLanguageFiles() {
		// get all languages from API
		List<Language> langs = Arrays.asList(Language.values());

		List<Language> notExistLangs = new ArrayList<>();

		// create language files
		langs.forEach(lang -> {
			try {
				createLanguageFile(lang.toString());
				this.loadedLanguages.add(lang.toString());
			} catch (Exception e) {
				notExistLangs.add(lang);
			}
		});

		// print not exist language files
//		if (!notExistLangs.isEmpty()) {
//			Utils.warning("Not exist language files: " + notExistLangs.toString());
//		}
	}

	private void setupDefaultLanguageFile() {
		createLanguageFile("EN");
		this.loadedLanguages.add("EN");
	}

	private void createLanguageFile(String language) {
		String fileName = Setting.MESSAGES_DIR + File.separator + language + ".yml";
		boolean isExist = new File(MiniGameWorldMain.getInstance().getDataFolder(), fileName).exists();

		// if can edit messages and file is already exist, return
		if (Setting.EDIT_MESSAGES && isExist) {
			return;
		}

		MiniGameWorldMain.getInstance().saveResource(fileName, true);
	}

	private void registerLoadedLanguageFiles() {
		this.loadedLanguageFiles.clear();

		this.loadedLanguages.forEach(lang -> {
			LanguageFile langFile = new LanguageFile(lang);
			loadedLanguageFiles.add(langFile);
			dataManager.registerYamlMember(langFile);
		});
	}

	public String getMessage(String lang, String msgKey) {
		LanguageFile file = getLanguageFile(lang);
		if (file == null) {
			return null;
		}

		return file.getMessage(msgKey);
	}

	public LanguageFile getLanguageFile(String lang) {
		if (!this.loadedLanguages.contains(lang)) {
			return null;
		}

		for (LanguageFile file : this.loadedLanguageFiles) {
			if (file.getLanguage() == lang) {
				return file;
			}
		}
		return null;
	}
}
