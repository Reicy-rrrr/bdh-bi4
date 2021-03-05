package com.deloitte.bdh.common.constant;


import com.deloitte.bdh.common.exception.BizException;

/**
 * 语言版本
 *
 * @author ashen
 */
public enum LanguageConstant {

	/**
	 * 中文版
	 */
	CN("cn"),

	/**
	 * 英文版
	 */
	EN("en"),

	HK("hk");

	private String language;

	LanguageConstant(String language){
		this.language = language;
	}

	public static LanguageConstant getLanguageConstant(String language) {
		boolean has = false;
		for (LanguageConstant e: LanguageConstant.values()){
			if(e.getLanguage().equals(language)){
				has = true;
				return e;
			}
		}
		throw new BizException("未匹配到["+language+"]对应的语言版本");
	}

	public boolean isChinese(){
		return LanguageConstant.CN.getLanguage().equals(this.language);
	}

	public boolean isEnglish(){
		return LanguageConstant.EN.getLanguage().equals(this.language);
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}
}
