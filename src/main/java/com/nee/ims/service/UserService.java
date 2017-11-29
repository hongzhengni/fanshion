package com.nee.ims.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nee.ims.common.A0Json;
import com.nee.ims.common.Request;
import com.nee.ims.common.Result;
import com.nee.ims.common.constant.CommonConstant;
import com.nee.ims.common.constant.ErrorCodeEnum;
import com.nee.ims.common.exception.BusinessException;
import com.nee.ims.data.access.UserDao;
import com.nee.ims.data.access.UserTokenDao;
import com.nee.ims.data.entities.User;
import com.nee.ims.data.entities.UserToken;
import com.nee.ims.uitls.MD5Encrypt;
import com.nee.ims.uitls.StringUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.nee.ims.common.constant.ErrorCodeEnum.DUPLICATE_MOBILE;
import static com.nee.ims.common.constant.ErrorCodeEnum.ERROR_PARAM;

/**
 */
@Component
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserTokenDao userTokenDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private AsyncSQLClient mysqlClient;


    public void findAll(RoutingContext rc) {

        mysqlClient.getConnection(res -> {
            if (res.failed()) {
                System.out.println(res.toString() + "");
            } else {
                res.result().query("select user_id userId, user_name userName from user", r -> {
                    if (r.succeeded()) {
                        List<JsonObject> objects = r.result().getRows();
                        Result result = new Result.Builder().setData(objects).build();
                        rc.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(A0Json.encode(result));
                    }
                });
            }
        });

        //List<User> users = StreamSupport.stream(userDao.findAll().spliterator(), false).collect(Collectors.toList());


    }

    public User findUserByToken(String token) {

        if (StringUtils.isBlank(token)) {
            throw new BusinessException("用户token不能为空", ERROR_PARAM);
        }
        UserToken userToken = userTokenDao.findOneByToken(token);
        if (userToken == null) {
            throw new BusinessException(ErrorCodeEnum.SESSION_EXPIRED);
        }

        User user = userDao.findOne(userToken.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在", ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (user.getStatus() == CommonConstant.SWITCH_STATUS.OFF) {
            throw new BusinessException(ErrorCodeEnum.STOPPED);
        }
        return user;
    }

    /**
     * 添加用户
     *
     * @param rc 上下文
     */
    @Transactional
    public void addUser(RoutingContext rc) {
        String body = rc.getBodyAsString();

        Request<User> request = A0Json.decodeValue(body, new TypeReference<Request<User>>() {});

        User user = request.getParams();

        user.setUserId(StringUtils.uuid());
        user.setMobile("18922706689");
        user.setIsDelete(0);
        user.setStatus(1);

        User saved = userDao.save(user);

        if (saved != null) {
            rc.response().end(A0Json.encode(new Result.Builder().setData(saved).build()));
        } else {
            rc.response().end("Bad Request");
        }

    }

    public void getUserById(RoutingContext rc) {

        String id = rc.request().getParam("id");
        rc.response().
                putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(userDao.findOne(id)).build()));

    }

    private void register(RoutingContext routingContext, User user) {

        if (user.getUserType() == null) {
            throw new BusinessException("用户类型(userType)不能为空", ErrorCodeEnum.ERROR_PARAM);
        }
        User userDB = userDao.findOneByMobileAndUserType(user.getMobile(), user.getUserType());
        if (userDB != null) {
            throw new BusinessException(DUPLICATE_MOBILE);
        }
        user.setUserId(StringUtils.uuid());
        user.setPassword(MD5Encrypt.MD5(user.getPassword()));
        user.setIsDelete(CommonConstant.DELETE.NO);
        user.setStatus(CommonConstant.SWITCH_STATUS.ON);

        userDao.save(user);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private void login(RoutingContext routingContext, User user) {

        if (user.getUserType() == null) {
            throw new BusinessException("用户类型(userType)不能为空", ErrorCodeEnum.ERROR_PARAM);
        }

        User userDB = userDao.findFirstByMobileAndPasswordAndUserType(user.getMobile(),
                MD5Encrypt.MD5(user.getPassword()), user.getUserType());
        if (userDB == null) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_ERROR);
        }
        if (userDB.getUserType() != user.getUserType()) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_ERROR);
        }
        if (userDB.getStatus() == CommonConstant.SWITCH_STATUS.OFF) {
            throw new BusinessException(ErrorCodeEnum.STOPPED);
        }

        UserToken token = new UserToken();
        token.setId(StringUtils.uuid());
        token.setUserId(userDB.getUserId());
        token.setStatus(CommonConstant.SWITCH_STATUS.ON);
        token.setToken(StringUtils.uuid());

        userTokenDao.save(token);
        userDB.setToken(token.getToken());
        userDB.setLastLoginTime(new Date());

        userDao.save(userDB);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(userDB).build()));

    }

    private void logout(RoutingContext routingContext) {
        Request<User> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<User>>() {});
        User user = request.getParams();
        if (StringUtils.isBlank(user.getToken())) {
            throw new BusinessException(ErrorCodeEnum.ERROR_PARAM);
        }
        userTokenDao.deleteByToken(user.getToken());

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 忘记密码
     */
    private void forgetPassword(RoutingContext routingContext, User user) {
        User userDB = userDao.findFirstByMobile(user.getMobile());
        if (userDB == null) {
            throw new BusinessException("用户不存在", ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (userDB.getStatus() == CommonConstant.SWITCH_STATUS.OFF) {
            throw new BusinessException(ErrorCodeEnum.STOPPED);
        }

        userDB.setPassword(MD5Encrypt.MD5(user.getPassword()));
        userDao.save(userDB);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 修改密码
     */
    private void changePassword(RoutingContext routingContext, User user) {

        if (StringUtils.isBlank(user.getOldPassword())) {
            throw new BusinessException("旧密码不能为空", ERROR_PARAM);
        }


        User userDB = userDao.findFirstByMobileAndPassword(user.getMobile(), MD5Encrypt.MD5(user.getOldPassword()));
        if (userDB == null) {
            throw new BusinessException(ErrorCodeEnum.LOGIN_ERROR);
        }
        if (userDB.getStatus() == CommonConstant.SWITCH_STATUS.OFF) {
            throw new BusinessException(ErrorCodeEnum.STOPPED);
        }

        userDB.setPassword(MD5Encrypt.MD5(user.getPassword()));
        userDao.save(userDB);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 用户模块接口
     */
    public void execute (RoutingContext routingContext) {
        Request<User> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<User>>() {});
        User user = request.getParams();
        if (StringUtils.isBlank(user.getMobile())) {
            throw new BusinessException("手机号码(mobile)不能为空", ErrorCodeEnum.ERROR_PARAM);
        }

        if (StringUtils.isBlank(user.getPassword())) {
            throw new BusinessException("密码(password)不能为空", ErrorCodeEnum.ERROR_PARAM);
        }

        String method = request.getMethod();
        String version = request.getVersion();
        if (StringUtils.isBlank(method)) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
        if (StringUtils.isBlank(version)) {
            throw new BusinessException(ErrorCodeEnum.VERSION_ERROR);
        }

        if (StringUtils.equals(method, "register")) {
            register(routingContext, user);
        } else if (StringUtils.equals(method, "login")) {
            login(routingContext, user);
        } else if (StringUtils.equals(method, "logout")) {
            logout(routingContext);
        } else if (StringUtils.equals(method, "forget.password")) {
            forgetPassword(routingContext, user);
        } else if (StringUtils.equals(method, "change.password")) {
            changePassword(routingContext, user);
        } else {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
    }
}
