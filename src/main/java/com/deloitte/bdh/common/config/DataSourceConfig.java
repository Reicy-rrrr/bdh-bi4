package com.deloitte.bdh.common.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationInterceptor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.deloitte.bdh.common.properties.BdhDataSourceProperties;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author Ashen
 * @date 04/08/2020
 */
@Configuration
public class DataSourceConfig {

	@Autowired
	private BdhDataSourceProperties properties;

	@Bean
	@RefreshScope
	public DynamicDataSourceProvider dynamicDataSourceProvider() {
		Map<String, DataSourceProperty> datasourceMap = properties.getDatasource();
		return new AbstractDataSourceProvider() {
			@Override
			public Map<String, DataSource> loadDataSources() {
				Map<String, DataSource> dataSourceMap = createDataSourceMap(datasourceMap);
				return dataSourceMap;
			}
		};
	}

	@RefreshScope
	@Bean("dataSource")
	public DynamicRoutingDataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
		DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
		dataSource.setPrimary(properties.getPrimary());
		dataSource.setStrict(properties.getStrict());
		dataSource.setSeata(properties.getSeata());
		dataSource.setP6spy(properties.getP6spy());
		dataSource.setStrategy(properties.getStrategy());
		dataSource.setProvider(dynamicDataSourceProvider);
		return dataSource;
	}

	@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
	@Bean
	public DynamicDataSourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor(
			DsProcessor dsProcessor) {
		DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor();
		interceptor.setDsProcessor(dsProcessor);
		DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(
				interceptor);
		advisor.setOrder(properties.getOrder());
		return advisor;
	}

}
