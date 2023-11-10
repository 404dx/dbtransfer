package com.bird.dbtransfer.xml;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractSaxParseXmlHandler extends DefaultHandler {

    public Object getResult(){
      return null;
    }

    public <T> T getResult(Class<T> tClass){
        return null;
    }

    /**
     * 是否java基础数据类型
     * @param clazz
     * @return
     */
    protected boolean isNotBasicDataType(Class clazz){
        if(int.class.isAssignableFrom(clazz)||double.class.isAssignableFrom(clazz)||long.class.isAssignableFrom(clazz)
                || short.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz)
                || boolean.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)){
            return false;
        }
        return true;
    }

    public static String formatValue(String string){
        if(StringUtils.isNotBlank(string)){
            string = string.replace("'","''").
                    replace("&","'||chr(38)||'");
            return string;
        }else {
            return string;
        }
    }

}
