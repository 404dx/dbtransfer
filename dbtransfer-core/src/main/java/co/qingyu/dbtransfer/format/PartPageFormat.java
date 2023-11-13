package co.qingyu.dbtransfer.format;

import co.qingyu.dbtransfer.consts.DataBaseType;

public class PartPageFormat {


    /**
     * 添加分页
     *
     * @param type   数据库类型
     * @param sql    sql
     * @param num    页码
     * @param length 每页数据量
     * @return
     */
    public String formatSql(String type, String sql, int num, int length) {
        if (DataBaseType.MYSQL.match(type)) {
            int start = num <= 1 ? 0 : (num - 1) * length;
            String template = "select * from ({sql}) limit " + start + "," + length;
            return template.replace("{sql}", sql);
        } else if (DataBaseType.ORACLE.match(type)) {
            int start = num <= 1 ? 0 : (num - 1) * length;
            int end = start + length;
            String template = "select * from (select temp.*,rownum as r_n from ({sql}) temp) where r_n > " + start + " and r_n <= " + end;
            return template.replace("{sql}", sql);
        } else {
            throw new RuntimeException("数据库类型：" + type + " 未找到匹配项");
        }
    }


}
