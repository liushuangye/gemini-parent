package com.gemini.toolkit.universal.form.service;

import com.alibaba.fastjson.JSONObject;

public interface FormService {
    String getDataByModel(String model);
    void initData(JSONObject jsonObject);
}
