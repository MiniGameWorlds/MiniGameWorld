package com.worldbiomusic.minigameworld.managers.language;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

public class LanguageFile implements YamlMember {
	private String language;
	private FileConfiguration config;

	public LanguageFile(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return this.language;
	}

	public String getMessage(String msgKey) {
		return this.config.getString("message." + msgKey);
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	@Override
	public String getFileName() {
		return "messages" + File.separator + language.toString() + ".yml";
	}

	@Override
	public void setData(YamlManager yamlManager, FileConfiguration config) {
		this.config = config;
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
