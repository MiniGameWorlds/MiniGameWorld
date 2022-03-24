package com.worldbiomusic.minigameworld.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wbm.plugin.util.ServerTool;
import com.wbm.plugin.util.Utils;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.util.Setting;

import me.smessie.MultiLanguage.api.Language;

public class LanguageManager {
	public void setupFiles() {
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
		List<String> langs = new ArrayList<>();
		Arrays.asList(Language.values()).forEach(lang -> langs.add(lang.toString()));

		List<String> notExistLangs = new ArrayList<>();

		// create language files
		langs.forEach(lang -> {
			try {
				createLanguageFile(lang);
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
	}

	private void createLanguageFile(String language) {
		String fileName = "messages" + File.separator + language + ".yml";

		boolean isExist = new File(MiniGameWorldMain.getInstance().getDataFolder(), fileName).exists();
		Utils.warning(language + " is exist: " + isExist);

		// if can edit messages and file is already exist, return
		if (Setting.EDIT_MESSAGES && isExist) {
			return;
		}

		MiniGameWorldMain.getInstance().saveResource(fileName, true);
	}
}
