package com.nee.ims.controller;

import com.nee.ims.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by heikki on 17/8/20.
 */
@RestController
@RequestMapping("api/test")
public class TestController {

    @Autowired
    private ITestService testService;

    @RequestMapping("test1")
    public String test1() {
        return testService.test();
    }
}
