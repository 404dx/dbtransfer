package co.qingyu.dbtransfer.format;

import co.qingyu.dbtransfer.consts.DataBaseType;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataBaseDateFormat {

    private final Map<String, String> DATABASE_FORMAT_STRING = new HashMap<>();

    private final FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public DataBaseDateFormat() {
        DATABASE_FORMAT_STRING.put(DataBaseType.ORACLE.getValue().toUpperCase(), "yyyy-mm-dd hh24:mi:ss");
        DATABASE_FORMAT_STRING.put(DataBaseType.MYSQL.getValue().toUpperCase(), "%Y-%m-%d %H:%i:%s");
    }

    public String getFormatString(String type) {
        return DATABASE_FORMAT_STRING.get(type.toUpperCase());
    }

    public String getDateFunc(Date date, String type) {
        if (DataBaseType.ORACLE.match(type)) {
            return "to_date(\"" + fastDateFormat.format(date) + "\",\"" + this.getFormatString(type) + "\")";
        } else if (DataBaseType.MYSQL.match(type)) {
            return "str_to_date(\"" + fastDateFormat.format(date) + "\",\"" + this.getFormatString(type) + "\")";
        }
        return "";
    }

}
