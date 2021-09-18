package com.gemini.toolkit.universal.form.enums;

public enum RelTypeEnum {
    /**
     * BUS:例如伦理依赖立项
     * EX:例如项目和项目扩展信息
     * USE:例如会议引用了人员
     */
    BUS("BUS","业务依赖"), EX("EX","属性扩展"), USE("USE","引用");
    // 成员变量
    private String name;
    private String alias;
    // 构造方法
    private RelTypeEnum(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }
    // 普通方法
    public static String getName(String alias) {
        for (RelTypeEnum c : RelTypeEnum.values()) {
            if (c.getAlias() == alias) {
                return c.name;
            }
        }
        return null;
    }
    public static String getAlias(String name) {
        for (RelTypeEnum c : RelTypeEnum.values()) {
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
