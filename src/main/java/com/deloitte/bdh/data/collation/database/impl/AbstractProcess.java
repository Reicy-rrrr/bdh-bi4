package com.deloitte.bdh.data.collation.database.impl;

import com.deloitte.bdh.common.dialect.HanaDialect;
import com.deloitte.bdh.data.collation.dao.bi.DbSelectorMapper;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.enums.SourceTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.page.PageAutoDialect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractProcess {

    @Autowired
    DataSource dataSource;

    protected Connection connection(DbContext context) throws Exception {
        if (SourceTypeEnum.File_Csv.equals(context.getSourceTypeEnum()) || SourceTypeEnum.File_Excel.equals(context.getSourceTypeEnum())) {
            return dataSource.getConnection();
        }
        Class.forName(context.getDriverName());
        return DriverManager.getConnection(context.getDbUrl(), context.getDbUserName(), context.getDbPassword());
    }

    protected TableData getTableData(DbContext context) throws Exception {
        Connection con = this.connection(context);
        PreparedStatement statement = con.prepareStatement(selectSql(context));
        ResultSet result = statement.executeQuery();
        ResultSetMetaData metaData = result.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = Lists.newArrayList();

        String tableName = context.getTableName();
        while (result.next()) {
            LinkedHashMap<String, Object> rowData = Maps.newLinkedHashMap();
            for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                // hive??????????????????????????????????????????
                String columnName = metaData.getColumnName(colIndex).replace(tableName + ".", "");
                if ("TEMP_NUM".equals(columnName)) {
                    continue;
                }
                rowData.put(columnName, result.getObject(colIndex));
            }
            rows.add(rowData);
        }
        this.close(con);
        TableData tableData = new TableData();
        tableData.setRows(rows);

        long count = 0L;
        if (SourceTypeEnum.Hive.equals(context.getSourceTypeEnum())
                || SourceTypeEnum.Hive2.equals(context.getSourceTypeEnum())) {
            count = context.getSize();
        } else {
            count = getTableCount(context);
        }
        tableData.setTotal(count);
        Integer page = context.getPage();
        Integer size = context.getSize();
        if (size * page >= count) {
            tableData.setMore(false);
        } else {
            tableData.setMore(true);
        }
        return tableData;
    }

    protected long getTableCount(DbContext context) throws Exception {
        Connection con = this.connection(context);
        PreparedStatement statement = con.prepareStatement(countSql(context));
        ResultSet result = statement.executeQuery();
        long count = 0L;
        while (result.next()) {
            count = result.getLong(1);
        }
        this.close(con);
        return count;
    }

    protected List<Map<String, Object>> executeQuery(DbContext context) throws Exception {
        Connection con = this.connection(context);
        PreparedStatement statement = con.prepareStatement(context.getQuerySql());
        ResultSet result = statement.executeQuery();
        ResultSetMetaData metaData = result.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> rows = Lists.newArrayList();

        String tableName = context.getTableName();
        while (result.next()) {
            Map<String, Object> rowData = Maps.newLinkedHashMap();
            for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                // hive??????????????????????????????????????????
                String columnName = metaData.getColumnName(colIndex).replace(tableName + ".", "");
                if ("TEMP_NUM".equals(columnName)) {
                    continue;
                }
                rowData.put(columnName, result.getObject(colIndex));
            }
            rows.add(rowData);
        }
        this.close(con);
        return rows;
    }

    protected PageInfo<Map<String, Object>> executePageQuery(DbContext context) throws Exception {
        // ??????hana??????
        PageAutoDialect.registerDialectAlias("sap", HanaDialect.class);
        if (null == context.getPage()) {
            return new PageInfo(this.executeQuery(context));
        }
        SqlSession sqlSession = sqlSession(context);
        // ?????????Dao???????????????????????????
        DbSelectorMapper mapper = sqlSession.getMapper(DbSelectorMapper.class);
        // ????????????????????????
        Integer page = context.getPage();
        Integer size = context.getSize() == null ? 10 : context.getSize();
        PageHelper.startPage(page, size);
        PageInfo<Map<String, Object>> results = new PageInfo(mapper.executeQuery(context.getQuerySql()));
        // ???????????????SqlSession
        sqlSession.close();
        if (CollectionUtils.isNotEmpty(results.getList())) {
            for (Map<String, Object> map : results.getList()) {
                map.remove("PAGEHELPER_ROW_ID");
            }
        }
        return results;
    }

    private SqlSession sqlSession(DbContext context) {
        // SqlSessionFactory?????????
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // configuration????????????mybatis???config?????????Configuration???????????????????????????
        Configuration configuration = new Configuration();
        Environment environment = new Environment.Builder("selector").transactionFactory(new JdbcTransactionFactory()).dataSource(dataSource(context)).build();
        configuration.setEnvironment(environment);
        // ??????PageHelper???????????????
        configuration.addInterceptor(pageInterceptor());
        // ??????Mapper??????
        configuration.addMapper(DbSelectorMapper.class);
        // ??????SqlSessionFactory??????SqlSession
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(configuration);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }

    private DataSource dataSource(DbContext context) {
        Properties properties = new Properties();
        properties.setProperty("url", context.getDbUrl());
        properties.setProperty("username", context.getDbUserName());
        properties.setProperty("password", context.getDbPassword());
        properties.setProperty("driver", context.getDriverName());
        properties.setProperty("driver.encoding", "UTF-8");

        PooledDataSourceFactory factory = new PooledDataSourceFactory();
        factory.setProperties(properties);
        DataSource dataSource = factory.getDataSource();
        return dataSource;
    }

    private PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "false");
        properties.setProperty("rowBoundsWithCount", "false");
        properties.setProperty("pageSizeZero", "true");
        properties.setProperty("reasonable", "false");
        properties.setProperty("supportMethodsArguments", "false");
        properties.setProperty("returnPageInfo", "none");
        properties.setProperty("autoRuntimeDialect", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    protected abstract String tableSql(DbContext context);

    protected abstract String fieldSql(DbContext context);

    protected abstract String selectSql(DbContext context);

    protected abstract String buildQueryLimit(DbContext context);

    protected String countSql(DbContext context) {
        if (StringUtils.isNotBlank(context.getCondition())) {
            return "SELECT COUNT(1) FROM " + context.getTableName() + " WHERE " + context.getCondition();
        }
        return "SELECT COUNT(1) FROM " + context.getTableName();
    }

    protected void close(Connection con) throws Exception {
        if (con != null) {
            con.close();
        }
    }
}
