package com.nee.ims.service.impl;

import com.nee.ims.service.ITestService;
import org.springframework.stereotype.Service;

/**
 * Created by heikki on 17/8/20.
 */
@Service("testService")
public class TestServiceImpl implements ITestService {
    @Override
    public String test() {
        return "this is a test case";
    }
}
