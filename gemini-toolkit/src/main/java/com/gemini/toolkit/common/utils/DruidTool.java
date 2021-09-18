package com.gemini.toolkit.common.utils;

import com.alibaba.druid.filter.config.ConfigTools;
import com.gemini.toolkit.config.DruidConfig;

/**
 * 用于加密密码
 */
public class DruidTool {
    public final static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI+gLhi9Ouix8sKcs6s4EETGUC0dfM+E1FAo/CjsqXtKkMJJuRAElVs0wQpR07i45Ymstl12raLncs4dBQJgifXORyv2QdohZgdcl+RSb8FqltvIXp6SoqT5xRFcRdNIKXp22H8it38Uehw1Xk1hFjMxDt9mAGlzWphm1sftQvvrAgMBAAECgYEAhmWvfPrl+wU5Ux7riZbGsnSYZf8n4dA8FF6lx23eEj7uQsBV6kU/tb8hvJRheIOcVz5E0CTTypKl/XVuUJEhxm8WwojwvNUUgkXadGhQk4KgBhVEqF9Rz39TuFxz1FBGB4Aqk4ih9AkTJZnxhhO+NNHAZNTfCkmr/k76Es7+9tECQQDzO2lI1qabrCsXSpMbwq4TgvgimCq3/UgYzuZTr48cKmVhebR45n3k01Dvf5n0WU1FbNqzaaCfEo8gXzPPOi2/AkEAlyo+W6/SOdaELCK4b2s+7KHTHIrgeO44Nt2siAw0+gCRw5W6aVcxR4hX0iJCbS3fP/namPmwLBOSgloEK7gU1QJANM8gY80e7UurK2pomC1jiB72auRo2fum7KP3RBIo2y+lRU2cTmeUy9rJhsZIkdHVwg4JSczUL9Vit9+TqxlxlwJAW672dVu1UPkn3zEL1iAg4RrhVU1yCkbJ95UJoNZuhzczMYIttth4fh9WzI+U5G1PG5lqGSBP1tvkH4WDRBT5JQJBAIecF0ijYYzOCqv/mj6ortWDw2Wd+KCsFL+D/29NCgs7kQ7RJm85Rmjva2QrSv5dl5V33gb4vysi0xemwtaBptY=";
    public static void main(String[] args) {
        try {
            String oriPwd = "SY&(1kd7cjAA";//密码明文
            String password = ConfigTools.encrypt(PRIVATE_KEY, oriPwd);//私钥加密
            System.out.println("加密后的密码："+password);
            String pwd = ConfigTools.decrypt(DruidConfig.PUBLIC_KEY, password);
            System.out.println("解密后的密码："+pwd);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
