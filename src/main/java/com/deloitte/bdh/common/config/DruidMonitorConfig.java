package com.deloitte.bdh.common.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * druid 监控
 *
 * @author pengdh
 * @date 2018/06/12
 */
@Configuration
public class DruidMonitorConfig {

	/**
	 * 注册ServletRegistrationBean
	 */
	@Bean
	public ServletRegistrationBean registrationBean() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		/** 初始化参数配置，initParams**/
		//白名单
		//多个ip逗号隔开
//		bean.addInitParameter("allow", "127.0.0.1");
		//IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
		//bean.addInitParameter("deny", "192.168.1.110");
		//登录查看信息的账号密码.
		bean.addInitParameter("loginUsername", "deloitte");
		bean.addInitParameter("loginPassword", "123456");
		//是否能够重置数据.
		bean.addInitParameter("resetEnable", "false");
		return bean;
	}

	/**
	 * 注册FilterRegistrationBean
	 */
	@Bean
	public FilterRegistrationBean druidStatFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
		//添加过滤规则.
		bean.addUrlPatterns("/*");
		// 关闭session监控,防止抛 “session ip change too many” 异常
		bean.addInitParameter("sessionStatEnable", "false");
		//添加不需要忽略的格式信息.
		bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		bean.addInitParameter("profileEnable", "true");
		return bean;
	}

}
