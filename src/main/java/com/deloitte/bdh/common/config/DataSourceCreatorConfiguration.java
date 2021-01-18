/**
 * Copyright © 2018 organization baomidou
 * <pre>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <pre/>
 */
package com.deloitte.bdh.common.config;

import com.baomidou.dynamic.datasource.creator.BasicDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.creator.DruidDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.HikariDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.JndiDataSourceCreator;
import com.deloitte.bdh.common.properties.BdhDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DataSourceCreatorConfiguration {

	@Autowired
	private BdhDataSourceProperties properties;

	@Bean
	public DataSourceCreator dataSourceCreator() {
		DataSourceCreator dataSourceCreator = new DataSourceCreator();
		dataSourceCreator.setBasicDataSourceCreator(basicDataSourceCreator());
		dataSourceCreator.setJndiDataSourceCreator(jndiDataSourceCreator());
		dataSourceCreator.setDruidDataSourceCreator(druidDataSourceCreator());
		dataSourceCreator.setHikariDataSourceCreator(hikariDataSourceCreator());
		dataSourceCreator.setProperties(properties);
		return dataSourceCreator;
	}

	@Bean
	public BasicDataSourceCreator basicDataSourceCreator() {
		return new BasicDataSourceCreator();
	}

	@Bean
	public JndiDataSourceCreator jndiDataSourceCreator() {
		return new JndiDataSourceCreator();
	}

	@Bean
	public DruidDataSourceCreator druidDataSourceCreator() {
		return new DruidDataSourceCreator(properties.getDruid());
	}

	@Bean
	public HikariDataSourceCreator hikariDataSourceCreator() {
		return new HikariDataSourceCreator(properties.getHikari());
	}
}
