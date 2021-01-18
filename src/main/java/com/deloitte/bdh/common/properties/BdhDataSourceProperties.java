package com.deloitte.bdh.common.properties;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = BdhDataSourceProperties.PREFIX)
@Component
@RefreshScope
public class BdhDataSourceProperties extends DynamicDataSourceProperties {

	public static final String PREFIX = "spring.datasource.dynamic";

	public BdhDataSourceProperties() {
		super();
	}

}
