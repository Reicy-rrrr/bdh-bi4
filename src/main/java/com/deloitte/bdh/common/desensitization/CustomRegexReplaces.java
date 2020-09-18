package com.deloitte.bdh.common.desensitization;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * 自定义标签replaces， 用于多个正则表达式替换
 *
 * @author dahpeng
 * @date 2019/04/24
 */
@Plugin(name = "replaces", category = "Core", printObject = true)
public final class CustomRegexReplaces {

	private static final Logger logger = StatusLogger.getLogger();

	/**
	 * replace标签，复用log4j已有plugin， replaces 下可以0，1，多个replace
	 */
	private final RegexReplacement[] replaces;

	private CustomRegexReplaces(RegexReplacement[] replaces) {
		this.replaces = replaces;
	}

	/**
	 * 实现pluginFactory， 用于生成pugin
	 */
	@PluginFactory
	public static CustomRegexReplaces createRegexReplacement(
			@PluginElement("replaces") final RegexReplacement[] replaces) {
		if (replaces == null) {
			logger.info("no replaces is defined");
			return null;
		}
		if (replaces.length == 0) {
			logger.warn("have the replaces , but no replace is set");
			return null;
		}
		return new CustomRegexReplaces(replaces);
	}

	/**
	 * 格式化输出日志信息， 此方法会执行多个正则表达式匹配与替换
	 */
	public String format(String msg) {
		for (RegexReplacement replace : replaces) {
			msg = replace.format(msg);
		}
		return msg;
	}

}
