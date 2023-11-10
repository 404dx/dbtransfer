package com.bird.dbtransfer.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtil {

     // 转义符
    public static final String ESCAPE_CHAR = "\\";
    // 默认前缀
    public static final String DEFAULT_PREFIX = "{";
    // 默认后缀
    public static final String DEFAULT_SUFFIX = "}";

    /**
     * 根据实体名称进行占位符替换
     * 例如：abc{entityName}def 将实体类中entityName这个值替换到{entityName}中
     *
     * @return
     */
    public static String formatStringByEntity(String str, Object obj) {
        return parseAndReplace(str, obj, DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

    /**
     * @param str    需要替换的字符
     * @param obj    对应替换属性的对象
     * @param prefix 占位符的前缀 例如:${
     * @param suffix 占位符后缀  例如:}
     */
    public static String formatStringByEntity(String str, Object obj, String prefix, String suffix) {
        return parseAndReplace(str, obj, prefix, suffix);
    }

    /**
     * 解析占位符
     */
    public static String[] parsePlaceholder(String content, String prefix, String suffix) {
        if (StringUtils.isBlank(prefix) || StringUtils.isBlank(suffix)) {
            throw new RuntimeException("占位符前缀或后缀不能为空!");
        }
        String regex = ".{0,1}(" + mergeStr(prefix) + "([\\w\\d]+|[\\w\\d]+\\.[\\w\\d]+)" + mergeStr(suffix) + ")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        List<String> list = new LinkedList<>();
        while (matcher.find()) {
            String str = matcher.group();
            if (!ESCAPE_CHAR.equals(String.valueOf(str.charAt(0)))) {
                int strIndex = str.indexOf(prefix.charAt(0));
                String temp = str.substring(strIndex + prefix.length(), str.length() - suffix.length());
                if (!list.contains(temp)) {
                    list.add(temp);
                }
            }
        }
        return list.toArray(new String[]{});
    }

    public static String parseAndReplace(String content, Object obj, String prefix, String suffix) {
        if (StringUtils.isBlank(prefix) || StringUtils.isBlank(suffix)) {
            throw new RuntimeException("占位符前缀或后缀不能为空!");
        }
        String result = content;
        String regex = ".{0,1}(" + mergeStr(prefix) + "([\\w\\d]+|[\\w\\d]+\\.[\\w\\d]+)" + mergeStr(suffix) + ")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String str = matcher.group();
            if (!ESCAPE_CHAR.equals(String.valueOf(str.charAt(0)))) {
                int strIndex = str.indexOf(prefix.charAt(0));
                String temp = str.substring(strIndex + prefix.length(), str.length() - suffix.length());
                if (StringUtils.isNotBlank(temp)) {
                    Object val = getObjectValueByName(obj, temp);
                    val = val == null ? "" : val;
                    result = result.replace(prefix + temp + suffix, val.toString());
                }
            }
        }
        return result;
    }

    public static Object getObjectValueByName(Object obj, String name) {
        Object result = null;
        if (obj != null) {
            try {
                Field field = obj.getClass().getDeclaredField(name);
                field.setAccessible(true);
                result = field.get(obj);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return result;
    }

    /**
     * 将占位符与转义符合并
     */
    private static String mergeStr(String str) {
        String reuslt = "";
        for (char c : str.toCharArray()) {
            reuslt += ESCAPE_CHAR + c;
        }
        return reuslt;
    }
}
