package com.gemini.toolkit.universal.form.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.universal.form.mapper.ModelMapper;
import com.gemini.toolkit.universal.form.mapper.RelationMapper;
import com.gemini.toolkit.universal.form.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormServiceImpl implements FormService {
    public final static String linkStr = "{\n" +
            "\t\"modelName\": \"contractBase\",\n" +
            "\t\"next\": [{\n" +
            "\t\t\"modelName\": \"trialBase\",\n" +
            "\t\t\"next\": [{\n" +
            "\t\t\t\"modelName\": \"trialExtensionInfo\",\n" +
            "\t\t\t\"next\": []\n" +
            "\t\t}]\n" +
            "\t}, {\n" +
            "\t\t\"modelName\": \"contractDetail\",\n" +
            "\t\t\"next\": []\n" +
            "\t}, {\n" +
            "\t\t\"modelName\": \"reviewOrg\",\n" +
            "\t\t\"next\": [{\n" +
            "\t\t\t\"modelName\": \"commonStaffVw\",\n" +
            "\t\t\t\"next\": []\n" +
            "\t\t}]\n" +
            "\t}]\n" +
            "}";
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RelationMapper relationMapper;
    @Override
    public String getDataByModel(String model) {
        /*
        LambdaQueryWrapper<Relation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Relation::getDeleteFlg, "0")
                .and(wrapper1 -> wrapper1.eq(Relation::getToModel, model).eq(Relation::getType, RelTypeEnum.BUS.getName())//业务依赖逆向查找
                        .or(wrapper2 -> wrapper2.eq(Relation::getFromModel, model).eq(Relation::getType, RelTypeEnum.EX.getName()))//扩展关系双向查找
                        .or(wrapper2 -> wrapper2.eq(Relation::getToModel, model).eq(Relation::getType, RelTypeEnum.BUS.getName()))//扩展关系双向查找
                        .or(wrapper2 -> wrapper2.eq(Relation::getFromModel, model).eq(Relation::getType, RelTypeEnum.USE.getName()))//引用关系正向查找
                );

        List<Relation> relations = relationMapper.selectList(queryWrapper);
        for (Relation relation:relations) {
            String type = relation.getType();
            System.out.println(type);
        }
        */
        JSONObject jsonObject = JSONObject.parseObject(linkStr);

        initData(jsonObject);
        return jsonObject.toJSONString();
    }

    @Override
    public void initData(JSONObject jsonObject) {
        String modelName = jsonObject.getString("modelName");

        jsonObject.put("value","hello:" + modelName);
        JSONArray nextArray = jsonObject.getJSONArray("next");
        if(nextArray.size()>0){
            for (Object item : nextArray) {
                JSONObject itemJsonObject = (JSONObject) item;
                initData(itemJsonObject);
            }
        }
    }
}
