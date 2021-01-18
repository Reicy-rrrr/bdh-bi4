package com.deloitte.bdh.common.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 日期工具类，用于日期时间与字符串之间的转换
 *
 * @author dahpeng
 */
public class DateFormatThreadLocal extends ThreadLocal<DateFormat> {

  /**
   * 日期时间格式
   */
  private String pattern;

  /**
   * @param pattern 日期时间格式
   */
  public DateFormatThreadLocal(String pattern) {
    this.pattern = pattern;
  }

  @Override
  protected DateFormat initialValue() {
    return new SimpleDateFormat(pattern);
  }

}
