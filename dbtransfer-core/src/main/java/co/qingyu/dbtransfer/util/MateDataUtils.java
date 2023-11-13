package co.qingyu.dbtransfer.util;

import co.qingyu.dbtransfer.consts.DataBaseType;

import java.util.HashMap;
import java.util.Map;

public class MateDataUtils {

    public static final String COL_NAME = "COL_NAME";
    public static final String COL_TYPE = "COL_TYPE";
    public static final String COL_TYPE_LENGTH = "COL_TYPE_LENGTH";
    public static final Map<String, MateDataColumn> mateDataColumnMap = new HashMap<>();

    static {
        // 获取matedata 当中的所需要的列名称
        MateDataColumn oracle = new MateDataColumn(DataBaseType.ORACLE.getValue());
        oracle.addNameMap(new String[]{COL_NAME, COL_TYPE, COL_TYPE_LENGTH},
                new String[]{"COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE"});
        mateDataColumnMap.put(DataBaseType.ORACLE.getValue(), oracle);
        MateDataColumn mysql = new MateDataColumn(DataBaseType.ORACLE.getValue());
        mysql.addNameMap(new String[]{COL_NAME, COL_TYPE, COL_TYPE_LENGTH},
                new String[]{"COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE"});
        mateDataColumnMap.put(DataBaseType.MYSQL.getValue(), mysql);
    }

    public static String getColumnByDBType(String dbType, String colName) {
        dbType = dbType.toUpperCase();
        return mateDataColumnMap.get(dbType).getRealName(colName);
    }


    static class MateDataColumn {
        private String type;
        private Map<String, String> realNameMap;

        public MateDataColumn() {
        }

        public MateDataColumn(String type) {
            this.type = type;
        }

        public MateDataColumn(String type, Map<String, String> realNameMap) {
            this.type = type;
            this.realNameMap = realNameMap;
        }

        public void addNameMap(String[] names, String[] values) {
            if (this.realNameMap == null) {
                this.realNameMap = new HashMap<>();
            }
            for (int i = 0; i < names.length; i++) {
                this.realNameMap.put(names[i], values[i]);
            }
        }

        public String getRealName(String colName) {
            return this.realNameMap.get(colName);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, String> getRealNameMap() {
            return realNameMap;
        }

        public void setRealNameMap(Map<String, String> realNameMap) {
            this.realNameMap = realNameMap;
        }
    }

}
