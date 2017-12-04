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
import com.nee.ims.uitls.GenerateCodeUtils;
import com.nee.ims.uitls.StringUtils;
import com.nee.ims.uitls.WxSignUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.nee.ims.common.constant.ErrorCodeEnum.DATA_NOT_EXIST;
import static com.nee.ims.common.constant.ErrorCodeEnum.ERROR_PARAM;
import static com.nee.ims.common.constant.ErrorCodeEnum.SYSTEM_ERROR;

/**
 */
@Component
public class OfficialAccountsService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserTokenDao userTokenDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private StoreFollowDao storeFollowDao;
    @Autowired
    private ProductPictureDao productPictureDao;
    @Autowired
    private ProductCollectionDao productCollectionDao;
    @Autowired
    private ProductPermissionDao productPermissionDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ContactGroupRelationDao contactGroupRelationDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    @Autowired
    private AsyncSQLClient mysqlClient;

    /** 统一支付接口 */
    String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    private final String App_Id = "wx158a2d2aa408aa0f";
    private final String App_Secret = "33570c9b95e1e175dc221cd0bce77658";


    @Value("${weChat.pay.appId}")
    private String wechatAppId;
    @Value("${weChat.pay.mchId}")
    private String wechatMchId;
    @Value("${weChat.pay.appSecret}")
    private String wechatAppSecret;
    @Value("${weChat.pay.payPartnerKey}")
    private String wechatPartnerKey;

    private HttpClient client = Vertx.vertx().createHttpClient();

    /**
     * 公众号微信客户直接登录
     */
    private void login(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String code = params.getString("code");
        if (StringUtils.isBlank(code)) {
            throw new BusinessException("code 不能为空", ERROR_PARAM);
        }

        String authUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code ";
        authUrl = authUrl.replace("APPID", App_Id);
        authUrl = authUrl.replace("SECRET", App_Secret);
        authUrl = authUrl.replace("CODE", code);

        String response = WxSignUtil.httpsRequest(authUrl, "POST", null);
        System.out.println(response);

        AuthInfo authInfo = Json.decodeValue(response, AuthInfo.class);
        if (authInfo.getErrcode() != null) {
            throw new BusinessException(authInfo.getErrmsg(), SYSTEM_ERROR.getCode());
        }
        String openId = authInfo.getOpenid();

        User userDB = userDao.findFirstByWeixinAndUserType(openId, 3);
        if (userDB == null) {

            String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
            userInfoUrl = userInfoUrl.replace("ACCESS_TOKEN", authInfo.getAccess_token());
            userInfoUrl = userInfoUrl.replace("OPENID", authInfo.getOpenid());
            response = WxSignUtil.httpsRequest(userInfoUrl, "POST", null);

            WxUserInfo wxUserInfo = Json.decodeValue(response, WxUserInfo.class);
            if (wxUserInfo.getErrcode() != null) {
                throw new BusinessException(wxUserInfo.getErrmsg(), SYSTEM_ERROR.getCode());
            }

            userDB = new User();
            userDB.setUserId(StringUtils.uuid());
            userDB.setIsDelete(CommonConstant.DELETE.NO);
            userDB.setStatus(CommonConstant.SWITCH_STATUS.ON);
            // 商户的userType ＝ 3
            userDB.setUserType(3);
            userDB.setMobile("8888888888");
            userDB.setWeixin(openId);
            userDB.setAvatarUrl(wxUserInfo.getHeadimgurl());
            userDB.setUserName(wxUserInfo.getNickname());
            userDB.setGender(wxUserInfo.getSex());


            UserToken token = new UserToken();
            token.setId(StringUtils.uuid());
            token.setUserId(userDB.getUserId());
            token.setStatus(CommonConstant.SWITCH_STATUS.ON);
            token.setToken(StringUtils.uuid());
            userTokenDao.save(token);

            userDB.setToken(token.getToken());
            userDB.setLastLoginTime(new Date());

            userDao.save(userDB);
        } else {
            userDB.setUpdateTime(new Date());
            UserToken userToken = userTokenDao.findOneByUserId(userDB.getUserId());
            userToken.setToken(StringUtils.uuid());
            userDB.setToken(userToken.getToken());
            userTokenDao.save(userToken);

            userDao.save(userDB);
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(userDB).build()));

    }



    /**
     * 公众号微信客户直接登录
     */
    private void login2(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String code = params.getString("code");
        if (StringUtils.isBlank(code)) {
            throw new BusinessException("code 不能为空", ERROR_PARAM);
        }

        String authUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code ";
        authUrl = authUrl.replace("APPID", "wx5cbbfebdc5854c63");
        authUrl = authUrl.replace("SECRET", "9a276ad3aa1ffab362dcea7ccbab27f9");
        authUrl = authUrl.replace("CODE", code);

        String response = WxSignUtil.httpsRequest(authUrl, "POST", null);
        System.out.println(response);

        AuthInfo authInfo = Json.decodeValue(response, AuthInfo.class);
        if (authInfo.getErrcode() != null) {
            throw new BusinessException(authInfo.getErrmsg(), SYSTEM_ERROR.getCode());
        }
        String openId = authInfo.getOpenid();

        User userDB = userDao.findFirstByWeixinAndUserType(openId, 3);
        if (userDB == null) {

            String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
            userInfoUrl = userInfoUrl.replace("ACCESS_TOKEN", authInfo.getAccess_token());
            userInfoUrl = userInfoUrl.replace("OPENID", authInfo.getOpenid());
            response = WxSignUtil.httpsRequest(userInfoUrl, "POST", null);

            WxUserInfo wxUserInfo = Json.decodeValue(response, WxUserInfo.class);
            if (wxUserInfo.getErrcode() != null) {
                throw new BusinessException(wxUserInfo.getErrmsg(), SYSTEM_ERROR.getCode());
            }

            userDB = new User();
            userDB.setUserId(StringUtils.uuid());
            userDB.setIsDelete(CommonConstant.DELETE.NO);
            userDB.setStatus(CommonConstant.SWITCH_STATUS.ON);
            // 商户的userType ＝ 3
            userDB.setUserType(3);
            userDB.setMobile("8888888888");
            userDB.setWeixin(openId);
            userDB.setAvatarUrl(wxUserInfo.getHeadimgurl());
            userDB.setUserName(wxUserInfo.getNickname());
            userDB.setGender(wxUserInfo.getSex());


            UserToken token = new UserToken();
            token.setId(StringUtils.uuid());
            token.setUserId(userDB.getUserId());
            token.setStatus(CommonConstant.SWITCH_STATUS.ON);
            token.setToken(StringUtils.uuid());
            userTokenDao.save(token);

            userDB.setToken(token.getToken());
            userDB.setLastLoginTime(new Date());

            userDao.save(userDB);
        } else {
            userDB.setUpdateTime(new Date());
            UserToken userToken = userTokenDao.findOneByUserId(userDB.getUserId());
            userToken.setToken(StringUtils.uuid());
            userDB.setToken(userToken.getToken());
            userTokenDao.save(userToken);

            userDao.save(userDB);
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(userDB).build()));

    }



    /**
     * 首页热门店铺
     */
    private void homePageStore(RoutingContext routingContext) {

        Request<Store> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Store>>() {
        });
        Store store = request.getParams();
        PageRequest pageRequest = new PageRequest(store.getPageNo(), store.getPageSize(), Sort.Direction.DESC, "createTime");

        User user = userService.findUserByToken(store.getToken());
        Integer userType = user.getUserType() - 1;

        Iterable<User> users = userDao.findAllByUserType(userType);
        List<String> userIds = new ArrayList<>();
        users.forEach(single -> {
            userIds.add(single.getUserId());
        });

        if (CollectionUtils.isEmpty(userIds)) {
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                    .end(A0Json.encode(new Result.Builder().build()));
        }

        Page<Store> page = storeDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicate = new ArrayList<>();

            if (StringUtils.isNotBlank(store.getKeyWord())) {
                predicate.add(criteriaBuilder.like(root.get("storeName").as(String.class), "%" + store.getKeyWord() + "%"));
            }
            if (store.getBusinessLineName() != null) {
                predicate.add(criteriaBuilder.equal(root.get("businessLineId").as(Integer.class), store.getBusinessLineId()));
            }
            predicate.add(root.get("userId").in(userIds));

            Predicate[] pre = new Predicate[predicate.size()];
            return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
        }, pageRequest);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(page).build()));
    }

    /**
     * 首页热门商品
     */
    private void homePageProduct(RoutingContext routingContext) {
        this.queryStoreProduct(routingContext);
    }

    /**
     * 获取店铺信息
     */
    private void getStore(RoutingContext routingContext) {
        Request<Store> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Store>>() {
        });
        Store store = request.getParams();
        if (StringUtils.isBlank(store.getStoreId())) {
            throw new BusinessException("storeId 不能为空", ERROR_PARAM);
        }

        Store storeDB = storeDao.findOne(store.getStoreId());
        if (storeDB == null) {
            throw new BusinessException("店铺不存在", DATA_NOT_EXIST);
        }
        storeDB.setFansNum(storeFollowDao.countByStoreId(store.getStoreId()));

        if (StringUtils.isNotBlank(store.getToken())) {
            User user = userService.findUserByToken(store.getToken());
            if (storeFollowDao.findOneByUserIdAndStoreId(user.getUserId(), store.getStoreId()) != null) {
                storeDB.setHasFollow(true);
            }
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(storeDB).build()));
    }

    /**
     * 店铺 商品列表
     */
    private void queryStoreProduct(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();

        User user = userService.findUserByToken(product.getToken());
        Integer userType = user.getUserType() - 1;

        Iterable<User> users = userDao.findAllByUserType(userType);
        List<String> userIds = new ArrayList<>();
        users.forEach(single -> {
            userIds.add(single.getUserId());
        });

        PageRequest pageRequest = new PageRequest(product.getPageNo(), product.getPageSize(), Sort.Direction.DESC, "createTime");

        Page<Product> page = productDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicate = new ArrayList<>();

            predicate.add(root.get("userId").in(userIds));
            if (StringUtils.isNotBlank(product.getKeyWord())) {
                predicate.add(criteriaBuilder.like(root.get("productName").as(String.class), "%" + product.getKeyWord() + "%"));
            }
            if (StringUtils.isNotBlank(product.getCategory())) {
                predicate.add(criteriaBuilder.equal(root.get("category").as(String.class), product.getCategory()));
            }
            if (StringUtils.isNotBlank(product.getStoreId())) {
                predicate.add(criteriaBuilder.equal(root.get("storeId").as(String.class), product.getStoreId()));
            }
            if (product.getBusinessLineId() != null) {
                predicate.add(criteriaBuilder.equal(root.get("businessLineId").as(Integer.class), product.getBusinessLineId()));
            }
            if (product.getStartDate() != null) {
                predicate.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(String.class), product.getStartDate()));
            }
            if (product.getEndDate() != null) {
                predicate.add(criteriaBuilder.lessThan(root.get("createTime").as(String.class), product.getEndDate()));
            }
            Predicate[] pre = new Predicate[predicate.size()];
            return criteriaQuery.where(predicate.toArray(pre)).getRestriction();

        }, pageRequest);

        page.forEach(single -> {
            getVisible(single, user.getUserId());

            ProductPicture productPicture = productPictureDao.findFirstByProductId(single.getProductId());
            if (productPicture != null)
                single.setPictureUrl(productPicture.getPictureUrl());
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(page).build()));
    }

    private void getVisible(Product single, String userId) {
        if (StringUtils.equals("1", single.getVisible())) {
            single.setHasPermission(true);
        } else {
            ProductPermission pp = productPermissionDao.findOneByProductAndUserId(single, userId);
            if (pp != null && new Date().compareTo(pp.getValidateTime()) <= 0) {
                single.setHasPermission(true);
            }
        }
    }

    /**
     * 查询一个店铺的分类
     */
    private void queryStoreCategory(RoutingContext routingContext) {

        Request<Store> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Store>>() {
        });
        Store store = request.getParams();
        if (store == null || StringUtils.isBlank(store.getStoreId())) {
            throw new BusinessException("storeId不能为空", ERROR_PARAM);
        }

        Iterable<Category> iterator = categoryDao.findByStoreId(store.getStoreId(), new Sort(Sort.Direction.ASC, "createTime"));

        List<Category> list = new ArrayList<>();
        iterator.forEach(list::add);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(list).build()));
    }

    /**
     * 获取商品详情
     */
    private void getProductDetail(RoutingContext routingContext) {

        productService.getProductDetail(routingContext);
    }

    /**
     * 查询关注的店铺
     */
    private void queryFollowStore(RoutingContext routingContext) {
        Request<StoreFollow> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<StoreFollow>>() {
        });
        StoreFollow storeFollow = request.getParams();

        if (StringUtils.isBlank(storeFollow.getToken())) {
            throw new BusinessException("token can not be null", ERROR_PARAM);
        }
        User user = userService.findUserByToken(storeFollow.getToken());
        Pageable pageable = new PageRequest(storeFollow.getPageNo(), storeFollow.getPageSize(), Sort.Direction.DESC, "createTime");
        Page<StoreFollow> iterable = storeFollowDao.findAllByUserId(user.getUserId(), pageable);


        iterable.forEach(single -> {
            Store store = storeDao.findOne(single.getStoreId());
            if (store == null) {
               return;
            }
            User user1 = userDao.findOne(store.getUserId());
            if (user1 != null) {
                store.setUserName(user1.getUserName());
                store.setAvatarUrl(user1.getAvatarUrl());
            }
            single.setStore(store);
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(iterable).build()));
    }

    /**
     * 关注店铺
     */
    private void createFollowStore(RoutingContext routingContext) {

        Request<StoreFollow> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<StoreFollow>>() {
        });
        StoreFollow storeFollow = request.getParams();

        if (StringUtils.isBlank(storeFollow.getToken())) {
            throw new BusinessException("token can not be null", ERROR_PARAM);
        }
        if (StringUtils.isBlank(storeFollow.getStoreId())) {
            throw new BusinessException("storeId can not be null", ERROR_PARAM);
        }
        Store store = storeDao.findOne(storeFollow.getStoreId());
        if (store == null) {
            throw new BusinessException("store not found", DATA_NOT_EXIST);
        }

        User user = userService.findUserByToken(storeFollow.getToken());
        StoreFollow storeFollowDB = storeFollowDao.findOneByUserIdAndStoreId(user.getUserId(), storeFollow.getStoreId());
        if (storeFollowDB == null) {
            storeFollow.setIsDelete(CommonConstant.DELETE.NO);
            storeFollow.setFollowId(StringUtils.uuid());
            storeFollow.setUserId(user.getUserId());

            storeFollowDao.save(storeFollow);
        }
        ContactGroupRelation cgr = new ContactGroupRelation();
        cgr.setCreateTime(new Date());
        cgr.setRelationId(StringUtils.uuid());
        cgr.setGroupId(store.getStoreId().substring(0, 29) + "000");
        cgr.setUser(user);
        cgr.setIsDelete(CommonConstant.DELETE.NO);

        try {
            contactGroupRelationDao.save(cgr);
        } catch (DataIntegrityViolationException e) {
        }


        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 取消关注
     */
    private void cancelFollowStore(RoutingContext routingContext) {

        Request<StoreFollow> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<StoreFollow>>() {
        });
        StoreFollow storeFollow = request.getParams();

        if (StringUtils.isBlank(storeFollow.getStoreId())) {
            throw new BusinessException("storeId can not be null", ERROR_PARAM);
        }

        User user = userService.findUserByToken(storeFollow.getToken());

        StoreFollow storeFollowDB = storeFollowDao.findOneByUserIdAndStoreId(user.getUserId(), storeFollow.getStoreId());

        if (storeFollowDB != null) {
            storeFollowDao.delete(storeFollowDB.getFollowId());
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询收藏的商品
     */
    private void queryCollectProduct(RoutingContext routingContext) {
        Request<ProductCollection> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<ProductCollection>>() {
        });
        ProductCollection productCollect = request.getParams();
        String token = productCollect.getToken();
        User user = userService.findUserByToken(token);
        Pageable pageable = new PageRequest(productCollect.getPageNo(), productCollect.getPageSize(), Sort.Direction.DESC, "createTime");
        Page<ProductCollection> iterable = productCollectionDao.findAllByUserId(user.getUserId(), pageable);


        iterable.forEach(single -> {
            Product product = productDao.findOne(single.getProductId());
            if (product != null) {
                product.setPictureUrl(productPictureDao.findFirstByProductId(product.getProductId()).getPictureUrl());
            }

            single.setProduct(product);
        });
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(iterable).build()));
    }


    /**
     * 取消收藏的商品
     */
    private void cancelCollectProduct(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String productId = params.getString("productId");

        User user = userService.findUserByToken(token);

        if (StringUtils.isBlank(productId)) {
            throw new BusinessException("productId can not be null", ERROR_PARAM);
        }
        ProductCollection collection = productCollectionDao.findOneByUserIdAndProductId(user.getUserId(), productId);
        if (collection != null) {
            productCollectionDao.delete(collection.getCollectId());
        }
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 收藏商品
     */
    private void createCollectProduct(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String productId = params.getString("productId");
        User user = userService.findUserByToken(token);
        Product product = productDao.findOne(productId);
        if (product == null) {
            throw new BusinessException("product not found", DATA_NOT_EXIST);
        }
        if (productCollectionDao.findOneByUserIdAndProductId(user.getUserId(), productId) == null) {
            ProductCollection productCollection = new ProductCollection();

            productCollection.setIsDelete(CommonConstant.DELETE.NO);
            productCollection.setCollectId(StringUtils.uuid());
            productCollection.setProductId(productId);
            productCollection.setUserId(user.getUserId());
            productCollectionDao.save(productCollection);
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询热销商品
     *
     * @param routingContext
     */
    private void queryRankList(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("token can not be null", ErrorCodeEnum.NO_PARAM);
        }
        if (StringUtils.isBlank(params.getString("storeId"))) {
            throw new BusinessException("storeId can not be null", ErrorCodeEnum.NO_PARAM);
        }
        Store store = storeDao.findOne(params.getString("storeId"));

        mysqlClient.getConnection(c -> {
            if (c.succeeded()) {
                c.result().query("SELECT\n" +
                        "  i.product_id,\n" +
                        "  count(i.product_id) seal_num\n" +
                        "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id AND o.store_id = '" + store.getStoreId() + "'\n" +
                        "  INNER JOIN product p ON p.product_id = i.product_id\n" +
                        "GROUP BY i.product_id\n" +
                        "ORDER BY seal_num DESC limit 15", r -> {
                    if (r.succeeded()) {
                        List<Product> products = new ArrayList<>();
                        r.result().getRows().forEach(single -> {
                            String productId = single.getString("product_id");
                            Product product = productDao.findOne(productId);
                            if (product != null) {
                                ProductPicture productPicture = productPictureDao.findFirstByProductId(productId);
                                product.setPictureUrl(productPicture.getPictureUrl());
                                products.add(product);
                            }
                        });
                        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                                .end(A0Json.encode(new Result.Builder().setData(products).build()));
                    } else {
                        System.out.println(r.cause());
                    }
                });
            } else {
                System.out.println(c.cause());
            }
        });
    }

    /**
     * 申请商品访问
     *
     * @param routingContext context
     */
    private void applyProductVisible(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("token can not be null", ErrorCodeEnum.NO_PARAM);
        }
        if (StringUtils.isBlank(params.getString("productId"))) {
            throw new BusinessException("productId can not be null", ErrorCodeEnum.NO_PARAM);
        }
        User customer = userService.findUserByToken(params.getString("token"));
        Product product = productDao.findOne(params.getString("productId"));
        if (product == null) {
            throw new BusinessException("product not found", DATA_NOT_EXIST);
        }
        Store store = storeDao.findOne(product.getStoreId());

        Message message = new Message();
        message.setMessageId(StringUtils.uuid());
        message.setProductId(product.getProductId());
        message.setStatus(1);
        message.setContent("");
        message.setStoreId(store.getStoreId());
        message.setStoreName(store.getStoreName());
        message.setType(1);
        message.setUserId(customer.getUserId());
        message.setCreateTime(new Date());

        messageDao.save(message);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));

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
        User user = userService.findUserByToken(params.getString("token"));
        Integer pageSize = params.getInteger("pageSize") == null ? 9 : params.getInteger("pageSize");
        Integer pageNo = params.getInteger("pageNo") == null ? 0 : params.getInteger("pageNo");
        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "createTime");
        Page<Message> messages = messageDao.findAllByUserId(user.getUserId(), pageable);

        messages.forEach(message -> {
            if (StringUtils.isNotBlank(message.getStoreId())) {
                Store store = storeDao.findOne(message.getStoreId());
                if (store != null) {
                    message.setStoreLogo(store.getLogo());
                }
            }
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(messages).build()));
    }

    private void wxSign(RoutingContext routingContext) {

        HttpServerRequest request = routingContext.request();
        System.out.println(request.uri());

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        Store store = storeService.getStoreByToken(token);
        String mobile = params.getString("mobile");
        Integer cent = params.getInteger("cent");

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appid", wechatAppId); // appid
        paramMap.put("mch_id", wechatMchId); // 商户号
        paramMap.put("nonce_str", GenerateCodeUtils.randomLetterAndNumberCode(32)); // 随机字符串，不长于32位
        paramMap.put("body", store.getStoreName() + " 添加端口 " + mobile); // 描述
        //paramMap.put("attach", weChatPayOrderBO.getBody());
        paramMap.put("out_trade_no", "D" + GenerateCodeUtils.datetimeAndRandomNumberCode("yyyyMMddHHmmss", 6)); // 商户订单号
        paramMap.put("total_fee", "" + cent); // 金额必须为整数，单位为分
        paramMap.put("spbill_create_ip", "192.168.0.1"); // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        paramMap.put("notify_url", "http://106.15.205.55/official/wx"); // 支付成功后，回调地址
        paramMap.put("trade_type", "APP"); // 交易类型

        String sign = WxSignUtil.genWeChatSign(paramMap, wechatPartnerKey); // 根据微信签名规则，生成签名
        paramMap.put("sign", sign);

        // 将map转换为xml，并使用CDATA标签转义
        String requestXML = WxSignUtil.stringMapToXML(paramMap);

        // Https请求
        String responseXML = WxSignUtil.httpsRequest(UNIFIED_ORDER_URL, "POST", requestXML);

        // 将返回的xml结果转换为map
        Map<String, String> resultMap = WxSignUtil.xmlToMap(responseXML);
        if (resultMap == null || resultMap.isEmpty()) {
            throw new BusinessException("响应数据错误", ErrorCodeEnum.OUT_API_ERROR);

        }

        // 检查返回状态码
        if (org.apache.commons.lang3.StringUtils.equals(resultMap.get("return_code"), "FAIL")) {
            throw new BusinessException(resultMap.get("err_code_des"), ErrorCodeEnum.OUT_API_ERROR);

        }

        // 检查业务结果
        if (org.apache.commons.lang3.StringUtils.equals(resultMap.get("result_code"), "FAIL")) {
            throw new BusinessException(resultMap.get("err_code_des"), ErrorCodeEnum.OUT_API_ERROR);
        }

        String prepayId = resultMap.get("prepay_id");// 预支付单号

        Map<String, String> map = new HashMap<>();
        map.put("appid", wechatAppId);// 应用ID
        map.put("partnerid", wechatMchId);// 商户号
        map.put("prepayid", prepayId);// 预支付交易会话ID
        map.put("package", "Sign=WXPay");// 暂填写固定值Sign=WXPay
        map.put("noncestr", GenerateCodeUtils.randomLetterAndNumberCode(32));// 随机字符串
        map.put("timestamp", WxSignUtil.createTimestamp());

        String paySign = WxSignUtil.genWeChatSign(map, wechatPartnerKey); // 根据微信签名规则，生成签名


        JsonObject response = new JsonObject();
        response.put("appId", wechatAppId);
        response.put("partnerId", wechatMchId);
        response.put("prepayId", prepayId);
        response.put("packageName", map.get("package"));
        response.put("noncestr", map.get("noncestr"));
        response.put("timestamp", Long.valueOf(map.get("timestamp")));
        response.put("sign", paySign);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(response).build()));

    }

    /**
     * 公众号模块接口
     */
    public void execute(RoutingContext routingContext) {
        Request request = A0Json.decodeValue(routingContext.getBodyAsString(), Request.class);

        String method = request.getMethod();
        String version = request.getVersion();
        if (StringUtils.isBlank(method)) {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
        if (StringUtils.isBlank(version)) {
            throw new BusinessException(ErrorCodeEnum.VERSION_ERROR);
        }

        if (StringUtils.equals(method, "login")) {
            login(routingContext);
        } else if (StringUtils.equals(method, "login2")) {
            login2(routingContext);
        } else if (StringUtils.equals(method, "home.page.store")) {
            homePageStore(routingContext);
        } else if (StringUtils.equals(method, "home.page.product")) {
            homePageProduct(routingContext);
        } else if (StringUtils.equals(method, "get.store")) {
            getStore(routingContext);
        } else if (StringUtils.equals(method, "query.store.product")) {
            queryStoreProduct(routingContext);
        } else if (StringUtils.equals(method, "query.store.category")) {
            queryStoreCategory(routingContext);
        } else if (StringUtils.equals(method, "get.product.detail")) {
            getProductDetail(routingContext);
        } else if (StringUtils.equals(method, "create.follow.store")) {
            createFollowStore(routingContext);
        } else if (StringUtils.equals(method, "cancel.follow.store")) {
            cancelFollowStore(routingContext);
        } else if (StringUtils.equals(method, "query.follow.store")) {
            queryFollowStore(routingContext);
        } else if (StringUtils.equals(method, "create.collect.product")) {
            createCollectProduct(routingContext);
        } else if (StringUtils.equals(method, "cancel.collect.product")) {
            cancelCollectProduct(routingContext);
        } else if (StringUtils.equals(method, "query.collect.product")) {
            queryCollectProduct(routingContext);
        } else if (StringUtils.equals(method, "query.rank.list")) {
            queryRankList(routingContext);
        } else if (StringUtils.equals(method, "apply.product.visible")) {
            applyProductVisible(routingContext);
        } else if (StringUtils.equals(method, "query.my.message")) {
            queryMyMessage(routingContext);
        } else if (StringUtils.equals(method, "wx.sign")) {
            wxSign(routingContext);
        } else {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
    }




    /**
     * 微信
     */
    public void wx(RoutingContext routingContext) {
        try {
            String signature = routingContext.request().getParam("signature");
            if (StringUtils.isNotBlank(signature)) {
                String timestamp = routingContext.request().getParam("timestamp");
                String nonce = routingContext.request().getParam("nonce");
                String echostr = routingContext.request().getParam("echostr");

                if (WxSignUtil.checkSignature(signature, timestamp, nonce)) {
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                            .end(echostr);
                }
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end("");
    }


    /**
     * @param decript 要加密的字符串
     * @return 加密的字符串
     * SHA1加密
     */
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
