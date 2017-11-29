package com.nee.ims;

import java.util.Map;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * Created by heikki on 17/8/19.
 */
@EnableTransactionManagement(proxyTargetClass=true)
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "com.nee.ims.data, com.nee.ims.server, com.nee.ims.service, com.nee.ims.common.aop")
public class Application {

    private Vertx vertx;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        Map<String, Object> map = ctx.getBeansWithAnnotation(Aspect.class);
        for (String key : map.keySet()) {
            System.out.println(map.get(key));
        }
    }

    /**
     * A singleton instance of {@link Vertx} which is used throughout the application
     * @return An instance of {@link Vertx}
     */
    @Bean
    public Vertx getVertxInstance() {
        if (this.vertx==null) {

            this.vertx = Vertx.vertx();
        }
        return this.vertx;
    }

    /**
     * A singleton instance of {@link AsyncSQLClient} which is used throughout the application
     * @return An instance of {@link AsyncSQLClient}
     */
    @Bean
    public AsyncSQLClient getMysqlClientInstance() {
        JsonObject mySQLClientConfig = new JsonObject().put("host", "106.15.205.55").put("port", 3306)
                .put("database", "keyfanshion").put("username", "root").put("password", "12345Aa")
                .put("max_pool_size", 100);;




        return MySQLClient.createShared(vertx, mySQLClientConfig);
    }
}
