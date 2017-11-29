package com.nee.ims.service;

import com.nee.ims.common.A0Json;
import com.nee.ims.common.Result;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Service;

/**
 * @Author: heikki.
 * @Description:
 * @DATE: 下午2:52 17/9/24.
 */
@Service
public class FailureHandlerService {

    public void failure(RoutingContext routingContext) {

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setCode("").setMessage("系统异常").build()));
    }
}
