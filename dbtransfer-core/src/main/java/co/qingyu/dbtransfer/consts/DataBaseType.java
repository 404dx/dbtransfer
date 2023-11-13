package co.qingyu.dbtransfer.consts;

public enum DataBaseType {

    ORACLE("ORACLE"), MYSQL("MYSQL");

    private final String value;

    DataBaseType(String value) {
        this.value = value;
    }

    public static boolean exists(String type) {
        for (DataBaseType dataBaseType : DataBaseType.values()) {
            if (dataBaseType.getValue().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    public static String text() {
        StringBuilder result = new StringBuilder("[");
        DataBaseType[] dataBaseTypes = DataBaseType.values();
        for (int i = 0; i < dataBaseTypes.length; i++) {
            if (i == dataBaseTypes.length - 1) {
                result.append(dataBaseTypes[i].getValue());
            } else {
                result.append(dataBaseTypes[i].getValue()).append(",");
            }
        }
        result.append("]");
        return result.toString();
    }

    public String getValue() {
        return value;
    }

    public boolean match(String type) {
        return this.value.equalsIgnoreCase(type);
    }

}
