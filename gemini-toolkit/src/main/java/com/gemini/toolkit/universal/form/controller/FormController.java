package com.gemini.toolkit.universal.form.controller;

import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.universal.form.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form")
public class FormController {

    @Autowired
    FormService formService;


    @RequestMapping(value = "getDataByModel", method = RequestMethod.GET)
    public R getDataByModel(@RequestParam("model") String model) {

        return R.ok().put("data", formService.getDataByModel(model));

    }
}
