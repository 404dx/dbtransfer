package co.qingyu.dbtransfer.xml;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SaxParseXmlEntityHandler extends AbstractSaxParseXmlHandler {

    /**
     * 标签对当中的内容，解析的字段名称
     * 例如：<title>内容</title> 取的值就是内容
     * 对应的实体类的字段就是 XML_CONTENT_ENTITY_NAME
     */
    public static final String XML_CONTENT_ENTITY_NAME = "";

    // 返回的实体类
    private Object entity;
    // 实体的类class
    private Class<?> entityClass;
    // 记录正在解析xml节点的层级
    private LinkedList<String> parseLevel = new LinkedList<>();
    // 正在解析节点的属性
    private Attributes startAttr;
    // 用于记录正在解析标签内容的节点
    private Map<String, Integer> charactersIndex = new HashMap<>();
    // 记录解析节点记录数
    private Map<String, ParseCount> elemCount = new HashMap<>();

    public SaxParseXmlEntityHandler(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        try {
            entity = entityClass.newInstance();
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        parseLevel.addLast(qName);
        charactersIndex.put(parseLevel.toString(), parseLevel.size());
        try {
            startAttr = attributes;
            if (attributes.getLength() > 0) {
                SaxField saxField = getObjectTargetField(entity, parseLevel);
                if (saxField != null && saxField.getObj() != null) {
                    Map<String, String> attrMap = this.attrToMap(attributes);
                    if (saxField.getType() != null && List.class.isAssignableFrom(saxField.getType())) {
                        addList((List) saxField.getObj(), attrMap, saxField.getGenericType()[0]);
                    } else {
                        setObjectByMap(saxField.getObj(), attrMap);
                    }
                }
            }
        } catch (Exception e) {
            //TransferLogger.error(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (ch != null && ch.length > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = start; i < start + length; i++) {
                switch (ch[i]) {
                    default:
                        buffer.append(ch[i]);
                }
            }
            String str = buffer.toString();
            try {
                SaxField saxField = getObjectTargetField(entity, parseLevel);
                if (saxField != null && saxField.getObj() != null) {
                    Map<String, String> attrMap = new HashMap<String, String>();
                    String attrName = XML_CONTENT_ENTITY_NAME;
                    if (StringUtils.isBlank(XML_CONTENT_ENTITY_NAME)) {
                        attrName = parseLevel.getLast();
                    }
                    attrMap.put(attrName, str);
                    if (saxField.getType() != null && List.class.isAssignableFrom(saxField.getType())) {
                        Integer i = charactersIndex.get(parseLevel.toString());
                        if (startAttr.getLength() == 0 && i != null && i == parseLevel.size()) {
                            charactersIndex.remove(parseLevel.toString());
                            addList((List) saxField.getObj(), attrMap, saxField.getGenericType()[0]);
                        } else {
                            List list = (List) saxField.getObj();
                            Object obj = list.get(list.size() - 1);
                            setObjectByMap(obj, attrMap, true);
                        }
                    } else {
                        setObjectByMap(saxField.getObj(), attrMap, true);
                    }
                }
            } catch (Exception e) {
                //TransferLogger.error(e);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        String levelName = parseLevel.toString();
        ParseCount count = elemCount.get(levelName);
        if (count == null) {
            count = new ParseCount();
            elemCount.put(parseLevel.toString(), count);
        }
        count.plus();
        charactersIndex.remove(levelName);
        parseLevel.removeLast();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public Object getResult() {
        return entity;
    }

    @Override
    public <T> T getResult(Class<T> tClass) {
        return (T) entity;
    }

    public Map<String, String> attrToMap(Attributes attributes) {
        if (attributes != null && attributes.getLength() > 0) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < attributes.getLength(); i++) {
                map.put(attributes.getQName(i), attributes.getValue(i));
            }
            return map;
        }
        return null;
    }


    public SaxField getObjectTargetField(Object objData, List<String> nameList) throws IllegalAccessException, InstantiationException {
        Field lastField = null;
        if (nameList != null && !nameList.isEmpty()) {
            if (nameList.size() == 1) {
                if (objData.getClass().getSimpleName().equalsIgnoreCase(nameList.get(0))) {
                    return new SaxField(objData);
                } else {
                    return null;
                }
            }
            for (int i = 1; i < nameList.size(); i++) {
                String name = nameList.get(i);
                if (objData == null) {
                    return null;
                }
                Field[] fields = objData.getClass().getDeclaredFields();
                if (fields.length == 0) {
                    return null;
                }
                int flag = 0;
                for (Field field : fields) {
                    flag++;
                    if (field.getName().equalsIgnoreCase(name)) {
                        lastField = field;
                        field.setAccessible(true);
                        Object obj = field.get(objData);
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            if (obj == null) {
                                if (List.class.isAssignableFrom(field.getType())) {
                                    obj = new ArrayList();
                                    setObjectFieldValue(objData, field.getName(), new Object[]{obj}, field.getType());
                                } else if (Map.class.isAssignableFrom(field.getType())) {
                                    obj = new HashMap();
                                    setObjectFieldValue(objData, field.getName(), new Object[]{obj}, field.getType());
                                }
                            }
                        } else if (!field.getType().isArray() && isNotBasicDataType(field.getType())) {
                            if (obj == null) {
                                obj = field.getType().newInstance();
                                setObjectFieldValue(objData, field.getName(), new Object[]{obj}, field.getType());
                            }
                        }
                        if (obj != null && List.class.isAssignableFrom(obj.getClass()) && !name.equalsIgnoreCase(nameList.get(nameList.size() - 1))) {
                            List list = (List) obj;
                            if (list.size() == 0) {
                                Type genericType = field.getGenericType();
                                ParameterizedType type = (ParameterizedType) genericType;
                                Class aClass = (Class) type.getActualTypeArguments()[0];
                                Object tempObj = aClass.newInstance();
                                list.add(tempObj);
                                obj = tempObj;
                            } else {
                                obj = list.get(list.size() - 1);
                            }
                        }
                        objData = obj;
                        break;
                    }
                    if (flag == fields.length && !field.getName().equalsIgnoreCase(name)) {
                        return null;
                    }
                }
            }
        }
        return new SaxField(lastField, objData);
    }

    public void addList(List list, Map<String, String> attrMap, Class clazz) {
        if (clazz != null) {
            Object obj = null;
            try {
                obj = clazz.newInstance();
                setObjectByMap(obj, attrMap);
                list.add(obj);
            } catch (Exception e) {
                //
            }
        }
    }

    public void setObjectByMap(Object obj, Map<String, String> attrMap, boolean isAppend) {
        if (attrMap != null && attrMap.size() > 0 && obj != null) {
            Set<String> keys = attrMap.keySet();
            for (String key : keys) {
                if (isAppend) {
                    appendObjectFieldValue(obj, key, new Object[]{attrMap.get(key)}, String.class);
                } else {
                    setObjectFieldValue(obj, key, new Object[]{attrMap.get(key)}, String.class);
                }
            }
        }
    }

    public void setObjectByMap(Object obj, Map<String, String> attrMap) {
        if (attrMap != null && attrMap.size() > 0 && obj != null) {
            Field[] fields = obj.getClass().getDeclaredFields();
            Set<String> keys = attrMap.keySet();
            for (String key : keys) {
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase(key)) {
                        setObjectFieldValue(obj, field.getName(), new Object[]{attrMap.get(key)}, String.class);
                        break;
                    }
                }
            }
        }
    }

    public void appendObjectFieldValue(Object obj, String fieldName, Object[] value, Class... parameterType) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                fieldName = field.getName();
                break;
            }
        }
        String name = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String setMethodName = "set" + name;
        String getMethodName = "get" + name;
        try {
            Method getMethod = obj.getClass().getMethod(getMethodName);
            Object sourceVal = getMethod.invoke(obj);
            sourceVal = sourceVal == null ? "" : sourceVal;
            Method setMethod = obj.getClass().getMethod(setMethodName, parameterType);
            setMethod.invoke(obj, sourceVal + "" + value[0]);
        } catch (Exception e) {
            //
        }
    }

    public void setObjectFieldValue(Object obj, String fieldName, Object[] value, Class... parameterType) {
        String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method method = obj.getClass().getMethod(methodName, parameterType);
            method.invoke(obj, value);
        } catch (Exception e) {
            //TransferLogger.info(e);
        }
    }


    public class ParseCount {
        private int startCount = 0;
        private int count = startCount;

        private void plus() {
            count = count + 1;
        }

        private void reduce() {
            count = count - 1;
        }

        public void reset() {
            count = startCount;
        }

        public int getCount() {
            return this.count;
        }

    }

}
