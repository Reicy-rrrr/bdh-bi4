package com.deloitte.bdh;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ashen
 * @date 04/08/2020
 */
public class CodeGenerator_UI {

	private static final String APPLICATION_MODULE = "data.analyse";
	private static final String DAO_MODULE = "bi";
	private static final String DSConstantName = "BI_DB";
	private static final boolean fileOverrideFlag = false;

	public static void main(String[] args) {
		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		String projectPath = System.getProperty("user.dir");
		gc.setOutputDir(projectPath + "/src/main/java");
		gc.setAuthor("bo.wang");
		gc.setOpen(false);
		gc.setServiceName("%sService");
		gc.setBaseResultMap(true);
		gc.setFileOverride(fileOverrideFlag);
		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setUrl("jdbc:mysql://10.81.128.4:3306/dbh-bi");
		// dsc.setSchemaName("public");
		dsc.setDriverName("com.mysql.cj.jdbc.Driver");
		dsc.setUsername("portal");
		dsc.setPassword("Dtt123456!");
		dsc.setTypeConvert(new MySqlTypeConvert() {
			@Override
			public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
				String t = fieldType.toLowerCase();
				if (t.contains("char")) {
					return DbColumnType.STRING;
				} else {
					if (!t.contains("date") && !t.contains("timestamp")) {
						if (t.contains("number")) {
							if (t.matches("number\\(1\\)")) {
								return DbColumnType.INTEGER;
							}
							if (t.matches("number\\(+\\d\\)")) {
								return DbColumnType.BIG_DECIMAL;
							}

							if (t.matches("number\\(+\\d{2}+\\)")) {
								return DbColumnType.BIG_DECIMAL;
							}
							return DbColumnType.BIG_DECIMAL;
						}
						if (t.contains("float")) {
							return DbColumnType.BIG_DECIMAL;
						}

						if (t.contains("clob")) {
							return DbColumnType.CLOB;
						}

						if (t.contains("blob")) {
							return DbColumnType.BLOB;
						}

						if (t.contains("binary")) {
							return DbColumnType.BYTE_ARRAY;
						}

						if (t.contains("raw")) {
							return DbColumnType.BYTE_ARRAY;
						}
					} else {
						switch (globalConfig.getDateType()) {
							case ONLY_DATE:
								return DbColumnType.DATE;
							case SQL_PACK:
								return DbColumnType.TIMESTAMP;
							case TIME_PACK:
								return DbColumnType.LOCAL_DATE_TIME;
						}
					}
					return DbColumnType.STRING;
				}
			}
		});
		mpg.setDataSource(dsc);

		// 包配置
		PackageConfig pc = new PackageConfig();
		//pc.setModuleName(scanner("platform"));
		pc.setParent("com.deloitte.bdh."+APPLICATION_MODULE);
		pc.setModuleName(null);
		pc.setMapper("dao." + DAO_MODULE);
		pc.setEntity("model");
		mpg.setPackageInfo(pc);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				Map<String, Object> map = new HashMap<>();
				map.put("DSConstantName", DSConstantName);
				this.setMap(map);
			}
		};

		// 如果模板引擎是 freemarker
		String templatePath = "/templates/mapper.xml.ftl";
		// 如果模板引擎是 velocity
		// String templatePath = "/templates/mapper.xml.vm";

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
				return projectPath + "/src/main/resources/mapper/" + DAO_MODULE
						+ "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
			}
		});
		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();
		templateConfig.setXml(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntityLombokModel(false);
		strategy.setRestControllerStyle(true);
		strategy.setSuperMapperClass("com.deloitte.bdh.common.base.Mapper");
		strategy.setSuperServiceClass("com.deloitte.bdh.common.base.Service");
		strategy.setSuperServiceImplClass("com.deloitte.bdh.common.base.AbstractService");
		// 公共父类
		// 写于父类中的公共字段
		strategy.setInclude("BI_UI_ANALYSE_DEFAULT_CATEGORY");
		strategy.setControllerMappingHyphenStyle(false);
		mpg.setStrategy(strategy);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.execute();
	}

}
