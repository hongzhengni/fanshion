package com.nee.ims.server;

import javax.annotation.PostConstruct;

import com.nee.ims.data.entities.Product;
import com.nee.ims.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by heikki on 17/8/19.
 */

@Component
public class VertxServer {

    @Autowired
    private Vertx vertx;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OfficialAccountsService officialAccountsService;
    @Autowired
    private StockOrderService stockOrderService;

    @PostConstruct
    public void start() throws Exception {

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/user/v1/:id")
                .produces("application/json")
                .blockingHandler(userService::getUserById);
        router.put("/user/v1")
                .consumes("application/json")
                .produces("application/json")
                .handler(userService::addUser)
                /*.failureHandler()*/;
        router.get("/user/v1")
                .produces("application/json")
                .handler(userService::findAll);
        router.post("/user")
                .produces("application/json")
                .blockingHandler(userService::execute);
        router.post("/store")
                .produces("application/json")
                .blockingHandler(storeService::execute);
        router.post("/product")
                .produces("application/json")
                .blockingHandler(productService::execute);
        router.post("/official")
                .produces("application/json")
                .blockingHandler(officialAccountsService::execute);
        router.get("/official/wx")
                .blockingHandler(officialAccountsService::wx);
        router.post("/order")
                .produces("application/json")
                .blockingHandler(stockOrderService::execute);
        router.post("/test")
                .produces("application/json")
                .blockingHandler(stockOrderService::execute);

        vertx.createHttpServer().requestHandler(router::accept).listen(8000);
    }


}
