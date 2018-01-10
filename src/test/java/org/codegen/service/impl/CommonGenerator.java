package org.codegen.service.impl;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.codegen.service.CodeGeneratorManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础辅助类生成
 */
public class CommonGenerator extends CodeGeneratorManager {

    /**
     * 生成dao中通用的mapper
     */
    public CommonGenerator genCommonMapper() {
        try {
            String[] namearr = MAPPER_INTERFACE_REFERENCE.split("\\.");
            String mapperName = namearr[namearr.length-1];
            File mymapperfile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_DAO + mapperName +".java");
            if (!mymapperfile.getParentFile().exists()) {
                mymapperfile.getParentFile().mkdirs();
            }
            if (!mymapperfile.exists()) {//生成文件
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("MapperName", mapperName);
                save(data, "basemapper.ftl", mymapperfile);
                logger.info(MAPPER_INTERFACE_REFERENCE + ".java 生成成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(MAPPER_INTERFACE_REFERENCE + ".java 生成失败");
        }
        return this;
    }

    /**
     * 生成通用的Service接口
     */
    public CommonGenerator genCommonService() {
        try {
            String[] temparr2 = SERVICE_INTERFACE_REFERENCE.split("\\.");
            String serviceName = temparr2[temparr2.length-1];
            File servicefile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + serviceName + ".java");
            if (!servicefile.getParentFile().exists()) {
                servicefile.getParentFile().mkdirs();
            }
            if (!servicefile.exists()) {//生成文件
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("ServiceName", serviceName);
                save(data, "basemapper.ftl", servicefile);
                logger.info(SERVICE_INTERFACE_REFERENCE + ".java 生成成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(SERVICE_INTERFACE_REFERENCE + ".java 生成失败");
        }
        return this;
    }

    /**
     * 生成通用的抽象Service类
     * @return
     */
    public CommonGenerator genCommonAbstractService() {
        try {
            String[] temparr = MAPPER_INTERFACE_REFERENCE.split("\\.");
            String mapperName = temparr[temparr.length-1];
            String[] temparr2 = ABSTRACT_SERVICE_CLASS_REFERENCE.split("\\.");
            String absServiceName = temparr2[temparr2.length-1];
            File abservicefile = new File(PROJECT_PATH + JAVA_PATH + PACKAGE_PATH_SERVICE + absServiceName + ".java");
            if (!abservicefile.getParentFile().exists()) {
                abservicefile.getParentFile().mkdirs();
            }
            if (!abservicefile.exists()) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("MAPPER_INTERFACE_REFERENCE", MAPPER_INTERFACE_REFERENCE);
                data.put("MapperName", mapperName);
                data.put("AbstractServiceName", absServiceName);
                save(data, "baseabstractservice.ftl", abservicefile);
                logger.info(ABSTRACT_SERVICE_CLASS_REFERENCE + ".java 生成成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(ABSTRACT_SERVICE_CLASS_REFERENCE + ".java 生成失败");
        }
        return this;
    }

    /**
     * 生成文件并保存
     * @param data
     * @param templateName
     * @param file
     */
    private void save(Map data, String templateName, File file) throws IOException, TemplateException {
        data.put("date", DATE);
        data.put("author", AUTHOR);
        data.put("basePackage", BASE_PACKAGE);
        getFreemarkerConfiguration().getTemplate(templateName).process(data, new FileWriter(file));
    }

}
