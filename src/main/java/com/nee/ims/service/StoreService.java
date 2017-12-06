package com.nee.ims.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nee.ims.common.A0Json;
import com.nee.ims.common.Request;
import com.nee.ims.common.Result;
import com.nee.ims.common.constant.CommonConstant;
import com.nee.ims.common.constant.ErrorCodeEnum;
import com.nee.ims.common.exception.BusinessException;
import com.nee.ims.data.access.*;
import com.nee.ims.data.entities.*;
import com.nee.ims.uitls.DateUtils;
import com.nee.ims.uitls.MD5Encrypt;
import com.nee.ims.uitls.StringUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.nee.ims.common.constant.ErrorCodeEnum.*;

/**
 */
@Component
public class StoreService {

    @Autowired
    private StoreDao storeDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ContactGroupDao contactGroupDao;
    @Autowired
    private ProductRememberDao productRememberDao;
    @Autowired
    private ContactGroupRelationDao contactGroupRelationDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserService userService;
    @Autowired
    private StockOrderService stockOrderService;
    @Autowired
    private ProductPictureDao productPictureDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private StoreUserDao storeUserDao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private AsyncSQLClient mysqlClient;


    /**
     * 创建店铺
     *
     * @param userId
     */
    public void createStore(String userId) {

        Store store = new Store();
        store.setUserId(userId);
        store.setIsDelete(CommonConstant.DELETE.NO);

        storeDao.save(store);
    }

    /**
     * 根据token 获取店铺
     *
     * @param token
     * @return
     */
    public Store getStoreByToken(String token) {
        User user = userService.findUserByToken(token);
        Store store = storeDao.findOneByUserId(user.getUserId());

        if (store == null) {
            StoreUser su = storeUserDao.findOneByUserId(user.getUserId());
            if (su != null && StringUtils.isNotBlank(su.getStoreId())) {
                store = storeDao.findOne(su.getStoreId());
                if (store != null) {
                    return store;
                }
            }
            throw new BusinessException("不存在符合的店铺", DATA_NOT_EXIST);
        }
        return store;
    }

    /**
     * 创建店铺
     */
    public void createStore(RoutingContext routingContext) {
        Request<Store> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Store>>() {
        });
        Store store = request.getParams();
        if (store.getToken() == null) {
            throw new BusinessException("用户Token不能为空", ERROR_PARAM);
        }
        if (store.getAddress() == null) {
            throw new BusinessException("详细地址不能为空", ERROR_PARAM);
        }
        if (store.getBusinessLineId() == null) {
            throw new BusinessException("经营种类 businessLineId不能为空", ERROR_PARAM);
        }
        if (store.getBusinessLineName() == null) {
            throw new BusinessException("经营种类Name不能为空", ERROR_PARAM);
        }
        if (store.getStoreName() == null) {
            throw new BusinessException("店铺名称不能为空", ERROR_PARAM);
        }
        if (store.getStoreSign() == null) {
            //throw new BusinessException("店铺招牌不能为空", ERROR_PARAM);
        }
        if (store.getLogo() == null) {
            //throw new BusinessException("店铺Logo不能为空", ERROR_PARAM);
        }
        User user = userService.findUserByToken(store.getToken());
        if (user == null) {
            throw new BusinessException("不存在该用户", DATA_NOT_EXIST);
        }

        if (storeDao.findOneByUserId(user.getUserId()) != null) {
            throw new BusinessException("该用户已经存在店铺", ERROR_PARAM);
        }

        store.setUserId(user.getUserId());
        store.setStoreId(StringUtils.uuid());
        store.setIsDelete(CommonConstant.DELETE.NO);
        storeDao.save(store);


        ContactGroup contactGroup = new ContactGroup();

        contactGroup.setStoreId(store.getStoreId());
        contactGroup.setIsDelete(CommonConstant.DELETE.NO);
        contactGroup.setGroupId(store.getStoreId().substring(0, 29) + "000");
        contactGroup.setGroupName("陌生人");

        contactGroupDao.save(contactGroup);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 修改店招
     *
     * @param routingContext
     */
    private void modifyStoreSign(RoutingContext routingContext) {

        Request<Store> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Store>>() {
        });
        Store store = request.getParams();

        if (StringUtils.isBlank(store.getStoreId())) {
            throw new BusinessException("店铺ID不能为空", ERROR_PARAM);
        }
        Store storeDB = storeDao.findOne(store.getStoreId());
        if (storeDB == null) {
            throw new BusinessException("不存在相应的店铺", DATA_NOT_EXIST);
        }
        storeDB.setStoreSign(store.getStoreSign());

        storeDao.save(storeDB);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));

    }

    /**
     * 修改店铺
     *
     * @param routingContext
     */
    private void modifyStore(RoutingContext routingContext) {
        Request<Store> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Store>>() {
        });
        Store store = request.getParams();
        if (store.getStoreId() == null) {
            throw new BusinessException("店铺ID不能为空", ERROR_PARAM);
        }
        Store storeDB = storeDao.findOne(store.getStoreId());

        if (storeDB == null) {
            throw new BusinessException("不存在相应的店铺", DATA_NOT_EXIST);
        }


        if (store.getBusinessLineId() != null
                && storeDB.getBusinessLineId().compareTo(store.getBusinessLineId()) != 0) {

            productDao.updateProductBusinessLineId(store.getBusinessLineId(), store.getStoreId());
        }

        storeDB.setAddress(store.getAddress());
        storeDB.setFullAddress(store.getFullAddress());
        storeDB.setBusinessLineId(store.getBusinessLineId());
        storeDB.setBusinessLineName(store.getBusinessLineName());
        storeDB.setLogo(store.getLogo());
        storeDB.setStoreName(store.getStoreName());
        storeDB.setStoreSign(store.getStoreSign());
        storeDB.setUpdateTime(new Date());


        storeDao.save(storeDB);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 根据用户获取店铺信息
     */
    private void getStore(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }
        User user = userService.findUserByToken(token);
        Store storeDB = storeDao.findOneByUserId(user.getUserId());

        if (storeDB == null) {
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                    .end(A0Json.encode(new Result.Builder().build()));
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            String today = DateUtils.formatDateTime(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String yesterday = DateUtils.formatDateTime(calendar.getTime());

            CompletableFuture allFuture = new CompletableFuture();
            mysqlClient.getConnection(c -> {
                if (c.failed()) allFuture.completeExceptionally(c.cause());
                else {
                    c.result().queryWithParams("SELECT sum(i.price * d.num) earnings\n" +
                                    "FROM stock_order o INNER JOIN stock_order_item i ON o.order_id = i.order_id\n" +
                                    "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                                    "WHERE o.store_id = ? AND o.status = 5",
                            new JsonArray().add(storeDB.getStoreId()), r -> {
                                if (r.failed()) allFuture.completeExceptionally(r.cause());
                                else {
                                    storeDB.setEarnings(r.result().getRows().get(0).getDouble("earnings"));
                                    allFuture.complete(1);
                                    c.result().close();
                                }
                            });
                }
            });
            allFuture.thenCompose(res -> {
                CompletableFuture future = new CompletableFuture();
                mysqlClient.getConnection(c -> {
                    if (c.failed()) future.completeExceptionally(c.cause());
                    else {
                        c.result().queryWithParams("SELECT sum(i.price * d.num) earnings\n" +
                                        "FROM stock_order o INNER JOIN stock_order_item i ON o.order_id = i.order_id\n" +
                                        "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                                        "WHERE o.store_id = ? AND o.create_time > ? AND o.status = 5",
                                new JsonArray().add(storeDB.getStoreId()).add(today), r -> {
                                    if (r.failed()) {
                                        System.out.println(r.cause());
                                        future.completeExceptionally(r.cause());
                                    } else {
                                        storeDB.setTodayEarnings(r.result().getRows().get(0).getDouble("earnings"));
                                        future.complete(1);
                                    }
                                    c.result().close();
                                });
                    }
                });
                return future;
            }).thenCompose(res -> {
                CompletableFuture future = new CompletableFuture();
                mysqlClient.getConnection(c -> {
                    if (c.failed()) allFuture.completeExceptionally(c.cause());
                    else {
                        c.result().queryWithParams("SELECT count(o.order_id) num\n" +
                                        "FROM stock_order o WHERE o.store_id = ? AND o.create_time > ? AND o.status = 5",
                                new JsonArray().add(storeDB.getStoreId()).add(today), r -> {
                                    if (r.failed()) future.completeExceptionally(r.cause());
                                    else {
                                        storeDB.setTodayOrderNum(r.result().getRows().get(0).getLong("num"));
                                        future.complete(1);

                                    }
                                    c.result().close();
                                });
                    }
                });
                return future;
            }).thenCompose(res -> {
                CompletableFuture future = new CompletableFuture();
                mysqlClient.getConnection(c -> {
                    if (c.failed()) allFuture.completeExceptionally(c.cause());
                    else {
                        c.result().queryWithParams("SELECT count(follow_id) num\n" +
                                        "FROM store_follow\n" +
                                        "WHERE store_id = ? AND create_time > ?",
                                new JsonArray().add(storeDB.getStoreId()).add(today), r -> {
                                    if (r.failed()) future.completeExceptionally(r.cause());
                                    else {
                                        storeDB.setTodayFanNum(r.result().getRows().get(0).getLong("num"));
                                        future.complete(1);
                                    }
                                    c.result().close();
                                });
                    }
                });
                return future;
            }).thenApply(res -> {
                CompletableFuture future = new CompletableFuture();
                mysqlClient.getConnection(c -> {
                    if (c.failed()) allFuture.completeExceptionally(c.cause());
                    else {
                        c.result().queryWithParams("SELECT count(follow_id) num\n" +
                                        "FROM store_follow\n" +
                                        "WHERE store_id = ?",
                                new JsonArray().add(storeDB.getStoreId()), r -> {
                                    if (r.failed()) future.completeExceptionally(r.cause());
                                    else {
                                        storeDB.setFansNum(r.result().getRows().get(0).getLong("num"));
                                        future.complete(1);
                                    }
                                    c.result().close();
                                });
                    }
                });
                return future;
            }).thenCompose(res -> {
                CompletableFuture future = new CompletableFuture();
                mysqlClient.getConnection(c -> {
                    if (c.failed()) allFuture.completeExceptionally(c.cause());
                    else {
                        c.result().queryWithParams("SELECT count(follow_id) num\n" +
                                        "FROM store_follow\n" +
                                        "WHERE store_id = ? AND create_time > ? AND create_time < ?",
                                new JsonArray().add(storeDB.getStoreId()).add(yesterday).add(today), r -> {
                                    if (r.failed()) future.completeExceptionally(r.cause());
                                    else {
                                        storeDB.setYesterdayFanNum(r.result().getRows().get(0).getLong("num"));
                                        future.complete(1);
                                    }
                                    c.result().close();
                                });
                    }
                });
                return future;
            }).whenComplete((res, ex) -> {
                if (ex != null) {
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                            .end(A0Json.encode(new Result.Builder().setCode(SYSTEM_ERROR.getCode() + "").setMessage(ex.toString()).build()));
                } else {
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                            .end(A0Json.encode(new Result.Builder().setData(storeDB).build()));
                }
            });
        }
    }


    /**
     * 查询联系人
     *
     * @param routingContext
     */
    private void queryContactPeople(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        Store store = getStoreByToken(token);

        Iterable<ContactGroup> groups = contactGroupDao.findAllByStoreId(store.getStoreId()
                , new Sort(Sort.Direction.ASC, "createTime"));
        groups.forEach(group -> {
            Iterable<ContactGroupRelation> relations = contactGroupRelationDao
                    .findAllByGroupId(group.getGroupId());
            List<User> users = new ArrayList<User>();
            relations.forEach(relation -> {
                User user = relation.getUser();
                if (user.getUserType() == 2) {
                    Store store1 = storeDao.findOneByUserId(user.getUserId());
                    user.setAvatarUrl(store1.getLogo());
                    user.setUserName(store1.getStoreName());
                    user.setAddress(store1.getAddress());
                } else {
                    Address address = addressDao.findFirstByUserId(user.getUserId());
                    if (address != null) {
                        user.setAddress(address.getAddress());
                    }
                }
                users.add(user);
            });
            group.setUsers(users);
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(groups).build()));

    }

    /**
     * 根据用户ID获取店铺
     *
     * @param userId
     * @return
     */
    public Store getStoreByUserId(String userId) {
        Store store = storeDao.findOneByUserId(userId);
        if (store == null) {
            StoreUser su = storeUserDao.findOneByUserId(userId);
            if (su != null && StringUtils.isNotBlank(su.getStoreId())) {
                store = storeDao.findOne(su.getStoreId());
                if (store != null) {
                    return store;
                }
            }
            throw new BusinessException("不存在符合的店铺", DATA_NOT_EXIST);
        }
        return store;
    }

    private void deleteContactGroup(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String groupId = params.getString("groupId");
        if (StringUtils.isBlank(groupId)) {
            throw new BusinessException("groupId can not be null", NO_PARAM);
        }
        Store store = getStoreByToken(token);
        if (groupId.equals(store.getStoreId().substring(0, 29) + "000")) {
            throw new BusinessException("该群组不能被删除", ERROR_LOGIC);
        }
        contactGroupDao.delete(groupId);

        contactGroupRelationDao.update(groupId, store.getStoreId().substring(0, 29) + "000");

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private void changeContactPeopleGroup(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");

        String userId = params.getString("userId");
        String groupId = params.getString("groupId");
        String oldGroupId = params.getString("oldGroupId");

        contactGroupRelationDao.update(groupId, oldGroupId, userId);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));

    }

    private void queryStoreRemembers(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }

        Store store = this.getStoreByToken(params.getString("token"));

        Product productDB = productDao.findFirstByStoreId(store.getStoreId(), new Sort(Sort.Direction.DESC, "updateTime"));
        Map<String, Map<String, Object>> remember = new HashMap<>();
        if (productDB != null) {
            Iterable<ProductRemember> remembers = productRememberDao.findAllByProductId(productDB.getProductId());
            remembers.forEach(single -> {
                Map<String, Object> map = new HashMap<>();
                map.put("defaultShow", single.getDefaultShow());
                map.put("remember", single.getRemember());
                map.put("value", single.getValue());
                remember.put(single.getAttributeCode(), map);
            });
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(remember).build()));
    }

    /**
     * 创建分组
     */
    private void createContactGroup(RoutingContext routingContext) {

        Request<ContactGroup> request = A0Json.decodeValue(routingContext.getBodyAsString(),
                new TypeReference<Request<ContactGroup>>() {
                });
        ContactGroup contactGroup = request.getParams();
        if (StringUtils.isBlank(contactGroup.getGroupName())) {
            throw new BusinessException("groupName can not be null", NO_PARAM);
        }
        Store store = getStoreByToken(contactGroup.getToken());
        contactGroup.setStoreId(store.getStoreId());
        contactGroup.setIsDelete(CommonConstant.DELETE.NO);
        contactGroup.setGroupId(StringUtils.uuid());

        contactGroupDao.save(contactGroup);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private void getContactPeopleInfo(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String userId = params.getString("userId");
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException("userId can not be null", ERROR_PARAM);
        }

        /**
         * 头像 （有字段但是空的） 用户名字没有   地址   备注名称  分组名称 还有分组id
         */
        Store store = this.getStoreByToken(token);
        User user = userDao.findOne(userId);

        if (user == null) {
            throw new BusinessException("user not found", DATA_NOT_EXIST);
        }

        String addressStr = null;

        if (user.getUserType() == 2) {
            Store store1 = storeDao.findOneByUserId(user.getUserId());
            user.setAvatarUrl(store1.getLogo());
            user.setUserName(store1.getStoreName());
            addressStr = store1.getAddress();
        } else {
            Address address = addressDao.findFirstByUserId(user.getUserId());
            if (address != null) {
                addressStr = address.getAddress();
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("avatarUrl", user.getAvatarUrl());
        map.put("userName", user.getUserName());
        map.put("userId", user.getUserId());
        map.put("address", addressStr);
        //map.put("remark", null);

        map.put("avatarUrl", user.getAvatarUrl());

        CompletableFuture<JsonObject> future = new CompletableFuture();
        mysqlClient.getConnection(c -> {
            if (c.failed()) {
                future.completeExceptionally(c.cause());
            }
            if (c.succeeded()) {
                c.result().queryWithParams("SELECT\n" +
                                "  g.group_id,\n" +
                                "  g.group_name\n" +
                                "FROM contact_group g INNER JOIN contact_group_relation r ON r.group_id = g.group_id\n" +
                                "WHERE g.store_id = ? AND r.user_id = ?", new JsonArray().add(store.getStoreId()).add(user.getUserId()),
                        r -> {
                            if (r.failed()) {
                                future.completeExceptionally(r.cause());
                            }
                            if (r.succeeded()) {
                                future.complete(r.result().getRows().get(0));
                            }
                            c.result().close();
                        });
            }
        });

        future.whenComplete((res, ex) -> {
           if (ex == null && res != null) {
               map.put("groupName", res.getString("group_name"));
               map.put("groupId", res.getString("group_id"));
           }
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                    .end(A0Json.encode(new Result.Builder().setData(map).build()));
        });


    }

    /**
     * 查询我的消息
     *
     * @param routingContext
     */
    private void queryMyMessage(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("token can not be null", ErrorCodeEnum.NO_PARAM);
        }
        Store store = this.getStoreByToken(params.getString("token"));
        Integer pageSize = params.getInteger("pageSize") == null ? 9 : params.getInteger("pageSize");
        Integer pageNo = params.getInteger("pageNo") == null ? 0 : params.getInteger("pageNo");
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "createTime");
        Page<Message> messages = messageDao.findAllByStoreId(store.getStoreId(), pageable);

        messages.forEach(message -> {
            if (message.getType() == 1) {
                if (StringUtils.isNotBlank(message.getUserId())) {
                    User user = userDao.findOne(message.getUserId());
                    if (user.getUserType() == 2) {
                        Store store1 = storeDao.findOneByUserId(user.getUserId());
                        user.setAvatarUrl(store1.getLogo());
                        user.setUserName(store1.getStoreName());
                    }
                    message.setUser(user);
                }
            }
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(messages).build()));
    }


    /**
     * 处理申请消息
     *
     * @param routingContext context
     */
    private void handleApplyMessage(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("token can not be null", ErrorCodeEnum.NO_PARAM);
        }
        String messageId = params.getString("messageId");
        if (StringUtils.isBlank(messageId)) {
            throw new BusinessException("messageId can not be null", ErrorCodeEnum.NO_PARAM);
        }
        Integer agreeDay = params.getInteger("agreeDay");
        Integer handleStatus = params.getInteger("handleStatus");
        if (handleStatus == null || handleStatus < 2 || handleStatus > 3) {
            throw new BusinessException("messageId can not be null", ErrorCodeEnum.ERROR_PARAM);
        }
        User user = userService.findUserByToken(params.getString("token"));
        Message message = messageDao.findOne(messageId);
        message.setStatus(handleStatus);
        message.setAgreeDay(agreeDay);
        message.setUpdateTime(new Date());

        messageDao.save(message);

        if (handleStatus == 2) {
            productService.saveProductPermission(message.getProductId(), message.getUserId(), agreeDay);
        }


        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 一键倒入商品
     *
     * @param routingContext
     */
    private void importProduct(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("token can not be null", ErrorCodeEnum.NO_PARAM);
        }
        /*String categoryId = params.getString("categoryId");
        if (StringUtils.isBlank(categoryId)) {
            throw new BusinessException("category can not be null", ErrorCodeEnum.NO_PARAM);
        }*/

        JsonArray productIds = params.getJsonArray("productIds");

        String toCategoryId = params.getString("toCategoryId");
        if (StringUtils.isBlank(toCategoryId)) {
            throw new BusinessException("toCategoryId can not be null", ErrorCodeEnum.NO_PARAM);
        }

        User user = userService.findUserByToken(params.getString("token"));
        Store store = this.getStoreByUserId(user.getUserId());

        productIds.forEach(productId -> {
            Product product = productDao.findOne((String) productId);
            if (product == null) {
                return;
            }
            product.setProductId(StringUtils.uuid());
            product.setCreateTime(new Date());
            product.setUpdateTime(new Date());
            product.setUserId(user.getUserId());
            product.setStoreId(store.getStoreId());
            product.setCategory(toCategoryId);
            productDao.save(product);

            Iterable<ProductPicture> pictures = productPictureDao.findAllByProductId((String) productId);
            pictures.forEach(picture -> {
                picture.setPictureId(StringUtils.uuid());
                picture.setProductId(product.getProductId());
                picture.setCreateTime(new Date());
                picture.setUpdateTime(new Date());

                productPictureDao.save(picture);
            });
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 统计分析
     *
     * @param routingContext
     */
    private void statisticsAnalyze(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        String token = params.getString("token");
        Store store = this.getStoreByToken(token);
        if (store == null) {
            throw new BusinessException("store not found", DATA_NOT_EXIST);
        }

        Map<String, Object> resultMap = new HashMap<>();

        CompletableFuture allFuture = new CompletableFuture();

        JsonArray queryParams = new JsonArray().add(store.getStoreId()).add(CommonConstant.ORDER_STATUS.FINISH);


        mysqlClient.getConnection(connection -> {
            if (connection.failed()) {
                allFuture.completeExceptionally(connection.cause());
            }
            if (connection.succeeded()) {
                connection.result().queryWithParams("SELECT sum(i.price * d.num) earnings \n" +
                        "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id\n" +
                        "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                        "WHERE o.store_id = ? and o.status = ?", queryParams, result -> {
                    if (result.failed()) allFuture.completeExceptionally(result.cause());
                    else {
                        resultMap.put("earnings", result.result().getRows().get(0).getDouble("earnings"));
                        allFuture.complete(resultMap);
                    }
                    connection.result().close();
                });
            }
        });
        allFuture.thenCompose((res) -> {
            CompletableFuture future = new CompletableFuture();
            mysqlClient.getConnection(connection -> {
                if (connection.failed()) {
                    future.completeExceptionally(connection.cause());
                } else {

                    connection.result().queryWithParams("SELECT sum(d.num) sales_volume \n" +
                            "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id\n" +
                            "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                            "WHERE o.store_id = ? and o.status = ?", queryParams, result -> {
                        if (result.failed()) future.completeExceptionally(result.cause());
                        else {
                            resultMap.put("salesVolume", result.result().getRows().get(0).getString("sales_volume"));
                            future.complete(resultMap);
                        }
                        connection.result().close();
                    });
                }
            });
            return future;
        }).thenCompose(res -> {
            CompletableFuture future = new CompletableFuture();
            mysqlClient.getConnection(connection -> {
                if (connection.failed()) {
                    future.completeExceptionally(connection.cause());
                }
                if (connection.succeeded()) {
                    connection.result().queryWithParams("SELECT\n" +
                            "  sum(d.num)                          sales_volume,\n" +
                            "  date_format(o.create_time, '%m') monthNum\n" +
                            "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id\n" +
                            "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                            "  WHERE o.store_id = ? AND o.status = ?\n" +
                            "GROUP BY monthNum\n" +
                            "ORDER BY sales_volume DESC", queryParams, result -> {
                        if (result.failed()) {
                            future.completeExceptionally(result.cause());
                        } else {
                            resultMap.put("monthSalesVolume", result.result().getRows());
                            future.complete(resultMap);
                        }
                        connection.result().close();
                    });
                }
            });
            return future;
        }).thenCompose(res -> {
            CompletableFuture<List<JsonObject>> future = new CompletableFuture();
            mysqlClient.getConnection(connection -> {
                if (connection.failed()) {
                    future.completeExceptionally(connection.cause());
                }
                if (connection.succeeded()) {
                    connection.result().queryWithParams("SELECT\n" +
                            "  sum(d.num) sales_volume,\n" +
                            "  p.category\n" +
                            "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id\n" +
                            "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id" +
                            "  INNER JOIN product p on p.product_id = i.product_id \n" +
                            "  WHERE o.store_id = ? AND o.status = ? \n" +
                            "GROUP BY category\n" +
                            "ORDER BY sales_volume DESC", queryParams, result -> {
                        if (result.failed()) {
                            future.completeExceptionally(result.cause());
                        } else {
                            List<JsonObject> results = result.result().getRows();
                            for (JsonObject object : results) {
                                String category = object.getString("category");
                                if (StringUtils.isNotBlank(category)) {
                                    Category category1 = categoryDao.findOne(category);
                                    if (category1 != null) {
                                        object.put("category", category1.getCategoryName());
                                    }
                                }
                            }

                            resultMap.put("categorySalesVolume", result.result().getRows());
                            future.complete(result.result().getRows());
                        }
                        connection.result().close();
                    });
                }
            });
            return future;
        }).whenComplete((res, ex) -> {
            if (ex != null) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .end(A0Json.encode(new Result.Builder().setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode() + "")
                                .setMessage(ErrorCodeEnum.SYSTEM_ERROR.getMessage()).build()));
            } else {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .end(A0Json.encode(new Result.Builder().setData(resultMap).build()));
            }
        });
    }

    /**
     * 添加手机端口
     *
     * @param routingContext routingCoutext
     */
    private void addMobilePort(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String mobile = params.getString("mobile");

        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException("mobile can not be null", NO_PARAM);
        }
        Store store = this.getStoreByToken(token);

        User adminUser = userDao.findOne(store.getUserId());

        User user = userDao.findOneByMobileAndUserType(mobile, adminUser.getUserType());
        if (user == null) {
            user = new User();
            user.setUserId(StringUtils.uuid());
            user.setUserName(store.getStoreName() + "管理员");
            user.setPassword(MD5Encrypt.MD5("123456"));
            user.setIsDelete(CommonConstant.DELETE.NO);
            user.setStatus(CommonConstant.SWITCH_STATUS.ON);
            user.setMobile(mobile);
            user.setUserType(adminUser.getUserType());
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());

            userDao.save(user);
        }
        StoreUser suDB = storeUserDao.findOneByStoreIdAndUserId(store.getStoreId(), user.getUserId());
        if (suDB == null) {
            StoreUser su = new StoreUser();
            su.setId(StringUtils.uuid());
            su.setStoreId(store.getStoreId());
            su.setUserId(user.getUserId());
            su.setStatus(CommonConstant.SWITCH_STATUS.ON);
            su.setCreateTime(new Date());
            storeUserDao.save(su);
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private void shareStore(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");

        Store store = this.getStoreByToken(token);
        if (store == null) {
            throw new BusinessException("store not found", DATA_NOT_EXIST);
        }
        Map<String, String> map = new HashMap<>();
        map.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=wx158a2d2aa408aa0f&redirect_uri=http://www.mengxue-web.cn/serviceProject/html/shopIndex.html?" +
                "storeId=" + store.getStoreId() + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect");
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(map).build()));
    }

    /**
     * 店铺模块接口
     */
    public void execute(RoutingContext routingContext) {

        JsonObject request = routingContext.getBodyAsJson();
        if (request == null) {
            throw new BusinessException("request body can not be null", ERROR_PARAM);
        }

        String method = request.getString("method");
        String version = request.getString("version");

        if (StringUtils.isBlank(method)) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
        if (StringUtils.isBlank(version)) {
            throw new BusinessException(ErrorCodeEnum.VERSION_ERROR);
        }

        if (StringUtils.equals(method, "modify.store.sign")) {
            modifyStoreSign(routingContext);
        } else if (StringUtils.equals(method, "create.store")) {
            createStore(routingContext);
        } else if (StringUtils.equals(method, "modify.store")) {
            modifyStore(routingContext);
        } else if (StringUtils.equals(method, "get.store")) {
            getStore(routingContext);
        } else if (StringUtils.equals(method, "query.contact.people")) {
            queryContactPeople(routingContext);
        } else if (StringUtils.equals(method, "create.contact.group")) {
            createContactGroup(routingContext);
        } else if (StringUtils.equals(method, "delete.contact.group")) {
            deleteContactGroup(routingContext);
        } else if (StringUtils.equals(method, "change.contact.people.group")) {
            changeContactPeopleGroup(routingContext);
        } else if (StringUtils.equals(method, "query.store.remembers")) {
            queryStoreRemembers(routingContext);
        } else if (StringUtils.equals(method, "get.contact.people.info")) {
            getContactPeopleInfo(routingContext);
        } else if (StringUtils.equals(method, "query.my.message")) {
            queryMyMessage(routingContext);
        } else if (StringUtils.equals(method, "handle.apply.message")) {
            handleApplyMessage(routingContext);
        } else if (StringUtils.equals(method, "import.product")) {
            importProduct(routingContext);
        } else if (StringUtils.equals(method, "store.statistics.analyze")) {
            statisticsAnalyze(routingContext);
        } else if (StringUtils.equals(method, "add.mobile.port")) {
            addMobilePort(routingContext);
        } else if (StringUtils.equals(method, "share.store")) {
            shareStore(routingContext);
        } else {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
    }


}
