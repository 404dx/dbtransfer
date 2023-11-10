package com.bird.dbtransfer.util;

import com.bird.dbtransfer.TransferException;
import com.bird.dbtransfer.config.DataSourceConfig;
import com.bird.dbtransfer.config.FieldRelation;
import com.bird.dbtransfer.config.TransferConfig;
import com.bird.dbtransfer.config.TransferRelation;
import com.bird.dbtransfer.consts.TransferType;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransferConfigParseUtils {

    public static boolean isBoolean(String string){
        return StringUtils.isNotBlank(string) && ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string));
    }

    public static TransferConfig getTransferConfig(String path) throws TransferException{
        try {
            TransferConfig config = new TransferConfig();
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));
            Element rootElem = document.getRootElement();
            config.setFrom(rootElem.attributeValue("from"));
            config.setTo(rootElem.attributeValue("to"));
            String type = rootElem.attributeValue("type");
            String outPath = rootElem.attributeValue("path");
            if(TransferType.SQL.getValue().equalsIgnoreCase(type) && StringUtils.isBlank(outPath)){
                throw new RuntimeException("转换类型(type)为"+type+" 必须填上path 属性指定输出路径");
            }
            config.setType(type);
            config.setPath(outPath);
            String isAll = rootElem.attributeValue("isAll");
            if(!isBoolean(isAll)){
                throw new RuntimeException("属性：isAll 只能是true 或者 false");
            }
            config.setAll(Boolean.parseBoolean(isAll));
            Element dataSources = rootElem.element("datasources");
            List<DataSourceConfig> sourceList = new ArrayList<>();
            dataSources.elements().forEach(o -> {
                Element elem = (Element)o;
                DataSourceConfig dataSourceConfig = new DataSourceConfig();
                dataSourceConfig.setName(elem.element("name").getText());
                dataSourceConfig.setType(elem.element("type").getText());
                dataSourceConfig.setUrl(elem.element("url").getText());
                dataSourceConfig.setDriver(elem.element("driver").getText());
                dataSourceConfig.setUsername(elem.element("username").getText());
                dataSourceConfig.setPassword(elem.element("password").getText());
                sourceList.add(dataSourceConfig);
            });
            config.setDatasources(sourceList);
/*            List<TransferRelation> relationList = new ArrayList<>();
            Element relations = rootElem.element("relations");
            relations.elements().forEach(o -> {
                Element elem = (Element)o;
                TransferRelation transferRelation = new TransferRelation();
                String onlyConfig = elem.attributeValue("onlyConfig");
                if (!isBoolean(onlyConfig)) {
                    throw new RuntimeException("属性：onlyConfig 只能是true 或者 false");
                }
                transferRelation.setOnlyConfig(Boolean.parseBoolean(onlyConfig));
                transferRelation.setSource(elem.element("source").getText());
                transferRelation.setTarget(elem.element("target").getText());
                List<FieldRelation> fieldRelations = new ArrayList<>();
                Element elemFields = elem.element("fields");
                if( elemFields != null){
                    elemFields.elements().forEach(f -> {
                        Element fieldElem = (Element)f;
                        FieldRelation fieldRelation = new FieldRelation();
                        fieldRelation.setSource(fieldElem.attributeValue("source"));
                        fieldRelation.setTarget(fieldElem.attributeValue("target"));
                        fieldRelations.add(fieldRelation);
                    });
                }
                transferRelation.setFields(fieldRelations);
                relationList.add(transferRelation);
            });
            config.setRelations(relationList);*/
            config.setRelations(Collections.emptyList());
            return  config;
        } catch (Exception e) {
            throw new TransferException(e);
        }
    }

    private static boolean isNotBasicDataType(Class clazz){
        if(int.class.isAssignableFrom(clazz)||double.class.isAssignableFrom(clazz)||long.class.isAssignableFrom(clazz)
                || short.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz)
                || boolean.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)){
            return false;
        }
        return true;
    }

}
