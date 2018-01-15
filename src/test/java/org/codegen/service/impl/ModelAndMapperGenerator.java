package org.codegen.service.impl;

import org.codegen.service.CodeGenerator;
import org.codegen.service.CodeGeneratorManager;
import org.codegen.util.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Model & Mapper 代码生成器
 * Created by zhh on 2017/09/20.
 */

public class ModelAndMapperGenerator extends CodeGeneratorManager implements CodeGenerator {

    @Override
    public void genCode(String tableName, String modelName, String sign) {
        Context initConfig = initConfig(tableName, modelName, sign);
        List<String> warnings = null;
        MyBatisGenerator generator = null;
        try {
            Configuration cfg = new Configuration();
            cfg.addContext(initConfig);
            cfg.validate();

            DefaultShellCallback callback = new DefaultShellCallback(true);
            warnings = new ArrayList<String>();
            generator = new MyBatisGenerator(cfg, callback, warnings);
            generator.generate(null);
        } catch (Exception e) {
            throw new RuntimeException("Model 和  Mapper 生成失败!", e);
        }

        if (generator == null || generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
            throw new RuntimeException("Model 和  Mapper 生成失败, warnings: " + warnings);
        }

        if (StringUtils.isNullOrEmpty(modelName)) {
            modelName = tableNameConvertUpperCamel(tableName);
        }

        logger.info("{}.java 生成成功!", modelName);
        logger.info("{}Mapper.java 生成成功!", modelName);
        logger.info("{}Mapper.xml 生成成功!", modelName);
    }

    /**
     * 完善初始化环境
     * @param tableName 表名
     * @param modelName 自定义实体类名, 为null则默认将表名下划线转成大驼峰形式
     * @param sign 区分字段, 规定如表 gen_test_demo, 则 test 即为区分字段
     */
    private Context initConfig(String tableName, String modelName, String sign) {
        Context context = null;
        try {
            context = new Context(ModelType.FLAT);
            context.setId("Potato");
            context.setTargetRuntime("MyBatis3Simple");
            context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
            context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

            // 数据库连接配置
            JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
            jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
            jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
            jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
            jdbcConnectionConfiguration.setDriverClass(JDBC_DRIVER_CLASS_NAME);
            context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

            // sql生成配置
            SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
            sqlMapGeneratorConfiguration.setTargetProject(PROJECT_PATH + RESOURCES_PATH);
            if (StringUtils.isNullOrEmpty(sign)) {
                sqlMapGeneratorConfiguration.setTargetPackage("mapper");
            } else {
                sqlMapGeneratorConfiguration.setTargetPackage("mapper." + sign);
            }
            context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

            // Mapper 插件配置
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
            pluginConfiguration.addProperty("mappers", MAPPER_INTERFACE_REFERENCE);
            context.addPluginConfiguration(pluginConfiguration);

            // java model生成配置
            JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
            javaModelGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
            if (StringUtils.isNullOrEmpty(sign)) {
                javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE);
            } else {
                javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE + "." + sign);
            }
            context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

            // dao 生成配置
            JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
            javaClientGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
            if (StringUtils.isNullOrEmpty(sign)) {
                javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE);
            } else {
                javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE + "." + sign);
            }
            javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
            context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

            TableConfiguration tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(tableName);
            tableConfiguration.setDomainObjectName(modelName);
            tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
            context.addTableConfiguration(tableConfiguration);
        } catch (Exception e) {
            throw new RuntimeException("ModelAndMapperGenerator 初始化环境异常!", e);
        }
        return context;
    }
}
