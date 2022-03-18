package com.worldbiomusic.minigameworld.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wbm.plugin.util.ServerTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;

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
		MiniGameWorldMain.getInstance().saveResource("messages" + File.separator + language + ".yml", true);
	}
}
