package com.deloitte.bdh.data.analyse.service.impl;

import com.deloitte.bdh.common.constant.LanguageConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ResourceBundle;

/**
 * 语言国际化
 *
 * @author pengdh
 * @date 2018/10/11
 */
@Service
public class LocaleMessageService {

	/**
	 * @param key 对应messages配置的key.
	 * @param lang 语言标识
	 * @return String
	 */
	public String getMessage(String key, String lang) {
		if (StringUtils.equals(LanguageConstant.EN.getLanguage(), lang)) {
			ResourceBundle bundle = ResourceBundle.getBundle("messages_en_US");
			return bundle.getString(key);
		} else if (StringUtils.equals(LanguageConstant.HK.getLanguage(), lang)) {
			ResourceBundle bundle = ResourceBundle.getBundle("messages_zh_HK");
			return bundle.getString(key);
		} else {
			ResourceBundle bundle = ResourceBundle.getBundle("messages_zh_CN");
			return bundle.getString(key);
		}
	}

}
