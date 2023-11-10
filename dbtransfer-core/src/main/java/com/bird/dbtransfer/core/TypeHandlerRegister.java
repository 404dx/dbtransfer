package com.bird.dbtransfer.core;

import com.bird.dbtransfer.type.*;

import java.util.HashMap;
import java.util.Map;

public class TypeHandlerRegister {

    private final Map<String, TypeHandler<?>>  TYPE_HANDLER_MAP = new HashMap<>();
    private final TypeHandler<String> defaultTypeHandler= new DefaultTypeHandler();

    public TypeHandlerRegister() {
        this.register("CLOB",new ClobTypeHandler());
        this.register("BLOB",new BlobTypeHandler());
        this.register("NCLOB",new NClobHandler());
        this.register("DATE",new DateTypeHandler());
        this.register("DATETIME",new DateTimeTypeHandler());
        this.register("TIMESTAMP",new TimestampTypeHandler());
        this.register("TIME",new TimeTypeHandler());
        this.register("BIT",new BitTypeHandler());
    }

    public void register(String name,TypeHandler<?> typeHandler){
        TYPE_HANDLER_MAP.put(name,typeHandler);
    }

    public TypeHandler<?> getTypeHandler(String type){
        TypeHandler<?>  typeHandler = this.TYPE_HANDLER_MAP.get(type);
        if(typeHandler == null){
            typeHandler = defaultTypeHandler;
        }
        return typeHandler;
    }

}
