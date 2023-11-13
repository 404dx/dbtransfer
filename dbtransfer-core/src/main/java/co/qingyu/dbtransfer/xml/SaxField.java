package co.qingyu.dbtransfer.xml;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class SaxField {

    private Object obj;
    private Class<?> type;
    private Class<?>[] genericType;
    private Field field;

    public SaxField(Object obj) {
        this.obj = obj;
    }

    public SaxField(Field field, Object obj) {
        this.field = field;
        this.obj = obj;
        this.initField();
    }

    private void initField() {
        this.type = this.field.getType();
        Type genericType = this.field.getGenericType();
        if (ParameterizedType.class.isAssignableFrom(genericType.getClass())) {
            ParameterizedType type = (ParameterizedType) genericType;
            Class aClass = (Class) type.getActualTypeArguments()[0];
            this.genericType = new Class[]{aClass};
        }
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Class<?>[] getGenericType() {
        return genericType;
    }

    public void setGenericType(Class<?>[] genericType) {
        this.genericType = genericType;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
