package com.bird.dbtransfer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * @author jackie
 * @since 2.0
 */
public class DbTest {

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        String username = "root", password = "jackie1234";
        String url = "jdbc:mysql://192.168.1.57:53306/test1?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&nullNamePatternMatchesAll=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String[] tables = {"TABLE", "VIEW"};
            //获取所有表信息
            ResultSet tableResult = databaseMetaData.getTables(connection.getCatalog(), connection.getSchema(), "%", tables);
            while (tableResult.next()) {
                String tableName = tableResult.getString("TABLE_NAME");
                String remarks = tableResult.getString("REMARKS");
                String tableCat = tableResult.getString("TABLE_CAT");
                String tableType = tableResult.getString("TABLE_TYPE");
                String generation = tableResult.getString("REF_GENERATION");
                System.out.println("=======================+" + tableName + "+===========================");
                System.out.println("表名称： " + tableName);
                System.out.println("表注释： " + remarks);
                System.out.println("表目录： " + tableCat);
                System.out.println("表类型 ： " + tableType);
                System.out.println("generation ： " + generation);
                //获取表字段信息
                ResultSet columns = databaseMetaData.getColumns(connection.getCatalog(), connection.getSchema(), tableName, "%");
                while (columns.next()) {
                    String cat = columns.getString("TABLE_CAT");
                    String schem = columns.getString("TABLE_SCHEM");
                    String colName = columns.getString("COLUMN_NAME");
                    String dataType = columns.getString("DATA_TYPE");
                    String typeName = columns.getString("TYPE_NAME");
                    Integer columnSize = columns.getInt("COLUMN_SIZE");
                    String columnsRemark = columns.getString("REMARKS");
                    String columnDef = columns.getString("COLUMN_DEF");
                    String nullable = columns.getString("IS_NULLABLE");
                    String autoincrement = columns.getString("IS_AUTOINCREMENT");
                    String generatedcolumn = columns.getString("IS_GENERATEDCOLUMN");
                    System.out.println("-------------------" + colName + "---------------------");
                    System.out.println("===>表目录：" + cat);
                    System.out.println("===>表模式：" + schem);
                    System.out.println("===>列名称：" + colName);
                    System.out.println("===>数据类型：" + dataType);
                    System.out.println("===>数据类型：" + typeName);
                    System.out.println("===>列大小：" + columnSize);
                    System.out.println("===>备注信息：" + columnsRemark);
                    System.out.println("===>默认值：" + columnDef);
                    System.out.println("===> ISO规则用于确定列的可为空性：" + nullable);
                    System.out.println("===> 是否自动递增：" + autoincrement);
                    System.out.println("===> 是否是生成的列：" + generatedcolumn);

                }
            }
            tableResult.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
