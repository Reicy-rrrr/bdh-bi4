package com.deloitte.bdh.common.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@RefreshScope
public class BiProperties {

    @Value("${nifi.transfer.url}")
    protected String nifiUrl;

    @Value("${nifi.transfer.username}")
    protected String nifiUserName;

    @Value("${nifi.transfer.password}")
    protected String nifiPwd;

    @Value("${nifi.transfer.expiredTime}")
    protected String expiredTime;

    @Value("${nifi.template.mapping}")
    private String mapping;

    @Value("${xxjob.transfer.url}")
    private String xxjobUrl;

    @Value("${nifi.mysql.driver}")
    private String mysqlDriver;

    @Value("${nifi.oracle.driver}")
    private String oracleDriver;

    @Value("${nifi.sqlserver.driver}")
    private String sqlServerDriver;

    @Value("${nifi.hana.driver}")
    private String hanaDriver;

    @Value("${nifi.hive.set}")
    private String hiveSet;

    @Value("${kafka.bootstrap.servers}")
    private String kafkaServers;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Value("${kafka.topic.email}")
    private String kafkaEmailTopic;

    @Value("${kafka.group.id}")
    private String kafkaGroupId;

    @Value("${evm.download.address}")
    private String evmDownLoadAddress;
}

