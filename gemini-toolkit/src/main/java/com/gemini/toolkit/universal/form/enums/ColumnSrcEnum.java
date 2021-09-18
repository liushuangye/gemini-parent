package com.gemini.toolkit.universal.form.enums;

public enum ColumnSrcEnum {
    STATIC("STATIC","静态表"), EX("EX","扩展字段"), MODEL("MODEL","动态表单");
    // 成员变量
    private String name;
    private String alias;
    // 构造方法
    private ColumnSrcEnum(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }
    // 普通方法
    public static String getName(String alias) {
        for (ColumnSrcEnum c : ColumnSrcEnum.values()) {
            if (c.getAlias() == alias) {
                return c.name;
            }
        }
        return null;
    }
    public static String getAlias(String name) {
        for (ColumnSrcEnum c : ColumnSrcEnum.values()) {
            if (c.getName() == name) {
                return c.alias;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
