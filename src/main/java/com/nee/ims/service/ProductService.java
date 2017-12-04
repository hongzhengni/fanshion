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
import com.nee.ims.uitls.StringUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static com.nee.ims.common.constant.ErrorCodeEnum.*;

/**
 */
@Component
public class ProductService {

    @Autowired
    private StoreService storeService;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private ProductPictureDao productPictureDao;
    @Autowired
    private ProductRememberDao productRememberDao;
    @Autowired
    private ProductCollectionDao productCollectionDao;
    @Autowired
    private ProductPermissionDao productPermissionDao;
    @Autowired
    private StoreFollowDao storeFollowDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ColorDao colorDao;
    @Autowired
    private SizeDao sizeDao;
    @Autowired
    private SendProductDao sendProductDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AsyncSQLClient mysqlClient;

    /**
     * 创建商品
     *
     * @param routingContext
     */
    void createProduct(RoutingContext routingContext) {

        Request<ProductVO> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<ProductVO>>() {
        });
        ProductVO productVO = request.getParams();

        validateProductParam(productVO);

        User user = userService.findUserByToken(productVO.getToken());
        Store store = storeService.getStoreByUserId(user.getUserId());
        if (store == null) {
            throw new BusinessException("店铺不存在", DATA_NOT_EXIST);
        }

        Product product = new Product();
        product.setProductId(StringUtils.uuid());
        product.setStoreId(store.getStoreId());
        product.setBusinessLineId(store.getBusinessLineId());
        product.setUserId(user.getUserId());
        product.setIsDelete(CommonConstant.DELETE.NO);
        product.setStatus(CommonConstant.SWITCH_STATUS.ON);
        product.setProductCode(productVO.getProductCode());

        savePicture(productVO.getPictureUrls(), product.getProductId());

        saveProductAndRemember(productVO, product);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));

    }

    /**
     * 保存商品图片
     *
     * @param picUrls
     * @param productId
     */
    private void savePicture(List<String> picUrls, String productId) {
        if (CollectionUtils.isNotEmpty(picUrls)) {
            for (String picUrl : picUrls) {
                ProductPicture pp = new ProductPicture();
                pp.setIsDelete(CommonConstant.DELETE.NO);
                pp.setProductId(productId);
                pp.setPictureUrl(picUrl);
                pp.setSort(0);
                pp.setPictureId(StringUtils.uuid());
                productPictureDao.save(pp);
            }
        }
    }

    /**
     * 保存商品记忆功能
     *
     * @param object
     * @param productId
     * @param storeId
     * @param attrCode
     */
    private void saveRemember(Map object, String productId, String storeId, String attrCode) {
        ProductRemember remember = new ProductRemember();
        remember.setStoreId(storeId);
        remember.setDefaultShow((Boolean) object.get("defaultShow"));
        remember.setRemember((Boolean) object.get("remember"));
        remember.setAttributeCode(attrCode);
        remember.setAttributeName("");
        remember.setProductId(productId);
        if (object.get("value") != null) {
            remember.setValue((String.valueOf(object.get("value"))));
        }
        remember.setIsDelete(CommonConstant.DELETE.NO);
        remember.setRememberId(StringUtils.uuid());

        productRememberDao.save(remember);
    }

    public static void main(String[] args) {

        System.out.println(new Date().getTime());

        ProductVO productVO = new ProductVO();

        Product product = new Product();

        Map aPriceInfo = new HashedMap();
        aPriceInfo.put("value", "0.03");
        aPriceInfo.put("remember", true);
        aPriceInfo.put("defaultShow", false);

        productVO.setPriceAInfo(aPriceInfo);

        Class<ProductVO> clazz = ProductVO.class;
        Class pClazz = product.getClass();

        Field[] fs = clazz.getDeclaredFields();
        try {
            for (Field field : fs) {
                if (field.getType().getSimpleName().equals("JsonObject")) {
                    String attrName = field.getName();
                    String methodName = "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                    Method method = clazz.getDeclaredMethod(methodName);
                    JsonObject object = (JsonObject) method.invoke(productVO);


                    Field pField = pClazz.getDeclaredField(attrName.replace("Info", ""));
                    String setMethodName = methodName.replace("Info", "").replace("get", "set");
                    Method setMethod = pClazz.getDeclaredMethod(setMethodName, pField.getType());
                    if (object != null && object.getValue("value") != null) {
                        if (pField.getType().getName().equals("java.lang.String")) {
                            setMethod.invoke(product, object.getString("value"));
                        } else if (pField.getType().getName().equals("java.lang.Integer")) {
                            setMethod.invoke(product, object.getInteger("value"));
                        }

                        ProductRemember remember = new ProductRemember();
                        remember.setStoreId("");
                        remember.setDefaultShow(object.getBoolean("defaultShow"));
                        remember.setRemember(object.getBoolean("remember"));
                        remember.setAttributeCode(attrName.replace("Info", ""));
                        remember.setAttributeName("");
                        remember.setProductId("");
                        remember.setValue(object.getString("value"));
                        remember.setIsDelete(CommonConstant.DELETE.NO);
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR);
        }

        System.out.println(A0Json.encode(product));
    }

    /**
     * 修改商品信息
     *
     * @param routingContext
     */
    private void modifyProduct(RoutingContext routingContext) {

        Request<ProductVO> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<ProductVO>>() {
        });
        ProductVO productVO = request.getParams();

        validateProductParam(productVO);
        if (StringUtils.isBlank(productVO.getProductId())) {
            throw new BusinessException("productId 不能为空", ERROR_PARAM);
        }

        User user = userService.findUserByToken(productVO.getToken());

        Product product = productDao.findOne(productVO.getProductId());
        product.setUpdateTime(new Date());

        productPictureDao.deleteByProductId(product.getProductId());
        savePicture(productVO.getPictureUrls(), product.getProductId());
        productRememberDao.deleteByProductId(product.getProductId());


        saveProductAndRemember(productVO, product);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private void saveProductAndRemember(ProductVO productVO, Product product) {
        Class<ProductVO> clazz = ProductVO.class;
        Class pClazz = product.getClass();

        Field[] fs = clazz.getDeclaredFields();

        try {
            for (Field field : fs) {

                if (field.getType().getName().equals("java.util.Map")) {
                    String attrName = field.getName();
                    String methodName = "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                    Method method = clazz.getDeclaredMethod(methodName);

                    Map object = (LinkedHashMap) method.invoke(productVO);


                    Field pField = pClazz.getDeclaredField(attrName.replace("Info", ""));
                    String setMethodName = methodName.replace("Info", "").replace("get", "set");
                    Method setMethod = pClazz.getDeclaredMethod(setMethodName, pField.getType());
                    if (object != null) {
                        if (pField.getType().getName().equals("java.lang.String")) {
                            setMethod.invoke(product, object.get("value"));
                        } else if (pField.getType().getName().equals("java.lang.Integer")) {
                            if (object.get("value") != null) {
                                setMethod.invoke(product, object.get("value"));
                            }
                        }

                        saveRemember(object, product.getProductId(), product.getStoreId(), attrName.replace("Info", ""));
                    }
                }
            }
            productDao.save(product);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage(), SYSTEM_ERROR);
        }
    }

    private void validateProductParam(ProductVO productVO) {
        if (productVO.getToken() == null) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }
        if (CollectionUtils.isEmpty(productVO.getPictureUrls())) {
            throw new BusinessException("商品图片不能为空", ERROR_PARAM);
        }
        if (StringUtils.isNotBlank(productVO.getProductCode())) {
            //throw new BusinessException("货号不能为空", ERROR_PARAM);
            Product product = productDao.findFirstByProductCode(productVO.getProductCode());
            if (product != null) {
                throw new BusinessException("product code duplicate", DUPLICATE_CUSTOMER_NAME);
            }

        }
        if (productVO.getProductNameInfo() == null) {
            //throw new BusinessException("名称不能为空", ERROR_PARAM);
        }
        if (productVO.getSizeInfo() == null) {
            //throw new BusinessException("size不能为空", ERROR_PARAM);
        }
        if (productVO.getColorInfo() == null) {
            //throw new BusinessException("颜色不能为空", ERROR_PARAM);
        }
        if (productVO.getReferencePriceInfo() == null) {
            //throw new BusinessException("参考价格不能为空", ERROR_PARAM);
        }
    }

    /**
     * 新增分类
     *
     * @param routingContext
     */
    private void createCategory(RoutingContext routingContext) {
        Request<Category> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Category>>() {
        });
        Category category = request.getParams();
        if (category.getToken() == null) {
            throw new BusinessException("user's token can not be null", ERROR_PARAM);
        }
        if (StringUtils.isBlank(category.getCategoryName())) {
            throw new BusinessException("categoryName can not be null");
        }
        User user = userService.findUserByToken(category.getToken());
        if (user == null) {
            throw new BusinessException("user is not exist", DATA_NOT_EXIST);
        }
        Store store = storeService.getStoreByUserId(user.getUserId());
        if (store == null) {
            throw new BusinessException("store is not exist", DATA_NOT_EXIST);
        }
        category.setStoreId(store.getStoreId());
        category.setUserId(user.getUserId());
        category.setIsDelete(CommonConstant.DELETE.NO);
        category.setCategoryId(StringUtils.uuid());

        categoryDao.save(category);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 删除分类
     *
     * @param routingContext
     */
    private void deleteCategory(RoutingContext routingContext) {
        Request<Category> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Category>>() {
        });
        Category category = request.getParams();
        if (category == null || StringUtils.isBlank(category.getCategoryId())) {
            throw new BusinessException("分类ID不能为空", ERROR_PARAM);
        }

        categoryDao.delete(category.getCategoryId());

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * @param routingContext
     */
    private void queryCategory(RoutingContext routingContext) {

        Request<Category> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Category>>() {
        });
        Category category = request.getParams();
        if (category == null || StringUtils.isBlank(category.getToken())) {
            throw new BusinessException("用户Token不能为空", ERROR_PARAM);
        }

        Store store = storeService.getStoreByToken(category.getToken());

        Iterable<Category> iterator = categoryDao.findByStoreId(store.getStoreId(), new Sort(Sort.Direction.ASC, "createTime"));

        List<Category> list = new ArrayList<>();
        iterator.forEach(single -> {
            list.add(single);
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(list).build()));

    }

    /**
     * 查询商品
     *
     * @param routingContext
     */
    private void queryProduct(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (StringUtils.isBlank(product.getToken())) {
            throw new BusinessException("用户token不能为空", ERROR_PARAM);
        }
        Store store = storeService.getStoreByToken(product.getToken());


        PageRequest pageRequest = new PageRequest(product.getPageNo(), product.getPageSize(), Sort.Direction.DESC, "createTime");
        Page<Product> page = productDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicate = new ArrayList<>();
            if (product.getStatus() != null) {
                predicate.add(criteriaBuilder.equal(root.get("status").as(Integer.class), product.getStatus()));
            }
            if (product.getStartDate() != null) {
                predicate.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(String.class), product.getStartDate()));
            }
            if (product.getEndDate() != null) {
                predicate.add(criteriaBuilder.lessThan(root.get("createTime").as(String.class), product.getEndDate()));
            }
            if (StringUtils.isNotBlank(product.getCategory())) {
                predicate.add(criteriaBuilder.equal(root.get("category").as(String.class), product.getCategory()));
            }
            predicate.add(criteriaBuilder.equal(root.get("storeId").as(String.class), store.getStoreId()));
            Predicate[] pre = new Predicate[predicate.size()];
            return criteriaQuery.where(predicate.toArray(pre)).getRestriction();
        }, pageRequest);
        page.forEach(single -> {
            Iterable<ProductPicture> productPictures = productPictureDao.findAllByProductId(single.getProductId());
            single.setPictureUrls(new ArrayList<>());
            productPictures.forEach(productPicture -> {
                single.getPictureUrls().add(productPicture.getPictureUrl());
            });
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(page).build()));
    }

    /**
     * 修改商品所属分类
     *
     * @param routingContext
     */
    private void changeProductCategory(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (CollectionUtils.isEmpty(product.getProductIds())) {
            throw new BusinessException("商品 productId 不能为空", ERROR_PARAM);
        }
        if (StringUtils.isBlank(product.getCategory())) {
            throw new BusinessException("商品 category 不能为空", ERROR_PARAM);
        }
        product.getProductIds().forEach(single -> {
            Product productDB = productDao.findOne(single);
            if (productDB != null) {
                productDB.setCategory(product.getCategory());
                productDB.setUpdateTime(new Date());
                productDao.save(productDB);
            }

        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 下架
     *
     * @param routingContext
     */
    private void soldOut(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (CollectionUtils.isEmpty(product.getProductIds())) {
            throw new BusinessException("商品 productIds 不能为空", ERROR_PARAM);
        }
        userService.findUserByToken(product.getToken());
        productDao.updateProductStatus(CommonConstant.SWITCH_STATUS.OFF, product.getProductIds());

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 删除
     *
     * @param routingContext
     */
    private void deleteProduct(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (CollectionUtils.isEmpty(product.getProductIds())) {
            throw new BusinessException("商品 productIds 不能为空", ERROR_PARAM);
        }
        userService.findUserByToken(product.getToken());
        productDao.deleteProducts(product.getProductIds());

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 上架
     *
     * @param routingContext
     */
    private void putAway(RoutingContext routingContext) {
        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (CollectionUtils.isEmpty(product.getProductIds())) {
            throw new BusinessException("商品 productIds 不能为空", ERROR_PARAM);
        }

        userService.findUserByToken(product.getToken());

        productDao.updateProductStatus(CommonConstant.SWITCH_STATUS.ON, product.getProductIds());

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询尺码表
     *
     * @param routingContext
     */
    private void querySize(RoutingContext routingContext) {
        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (StringUtils.isBlank(product.getToken())) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }

        Store store = storeService.getStoreByToken(product.getToken());
        Iterable<Size> iterator = sizeDao.findByStoreId(store.getStoreId(), new Sort(Sort.Direction.ASC, "groupName", "sizeName"));

        Result result = new Result.Builder().build();
        ;
        if (iterator.iterator().hasNext()) {
            Map<String, List<Size>> map = new HashMap<>();
            iterator.forEach(single -> {
                map.putIfAbsent(single.getGroupName(), new ArrayList<>());
                map.get(single.getGroupName()).add(single);
            });
            result = new Result.Builder().setData(map).build();
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(result));
    }

    /**
     * 创建尺码
     *
     * @param routingContext
     */
    private void createSize(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }
        Store store = storeService.getStoreByToken(params.getString("token"));
        JsonArray sizes = params.getJsonArray("sizes");

        sizes.forEach(single -> {

            JsonObject size = (JsonObject) single;
            String groupName = size.getString("groupName");
            String sizeName = size.getString("sizeName");
            if (StringUtils.isBlank(groupName)) {
                throw new BusinessException("groupName 不能为空", ERROR_PARAM);
            }
            if (StringUtils.isBlank(sizeName)) {
                throw new BusinessException("sizeName 不能为空", ERROR_PARAM);
            }
            if (sizeDao.findOneByStoreIdAndSizeNameAndGroupName(store.getStoreId(),
                    sizeName, groupName) == null) {
                Size sizeDB = new Size();

                sizeDB.setIsDelete(CommonConstant.DELETE.NO);
                sizeDB.setStoreId(store.getStoreId());
                sizeDB.setSizeId(StringUtils.uuid());
                sizeDB.setSizeName(sizeName);
                sizeDB.setGroupName(groupName);

                sizeDao.save(sizeDB);
            }
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询颜色
     *
     * @param routingContext
     */
    private void queryColor(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (StringUtils.isBlank(product.getToken())) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }

        Store store = storeService.getStoreByToken(product.getToken());
        Iterable<Color> iterator = colorDao.findByStoreId(store.getStoreId(), new Sort(Sort.Direction.ASC, "groupName", "colorName"));
        Result result = new Result.Builder().build();
        if (iterator.iterator().hasNext()) {
            Map<String, List<Color>> map = new HashMap<>();
            iterator.forEach(single -> {
                map.putIfAbsent(single.getGroupName(), new ArrayList<>());
                map.get(single.getGroupName()).add(single);
            });
            result = new Result.Builder().setData(map).build();
        }


        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(result));
    }

    /**
     * 创建颜色
     *
     * @param routingContext
     */
    private void createColor(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");

        /*if (StringUtils.isBlank(color.getToken())) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }
        if (StringUtils.isBlank(color.getGroupName())) {
            throw new BusinessException("颜色 groupName 不能为空", ERROR_PARAM);
        }
        if (StringUtils.isBlank(color.getColorName())) {
            throw new BusinessException("颜色 colorName 不能为空", ERROR_PARAM);
        }
        Store store = storeService.getStoreByToken(color.getToken());

        if (colorDao.findOneByStoreIdAndColorNameAndGroupName(store.getStoreId(),
                color.getColorName(), color.getGroupName()) != null) {
            throw new BusinessException("该颜色已经存在，无需重复添加", DUPLICATE_DATA);
        }

        color.setIsDelete(CommonConstant.DELETE.NO);
        color.setStoreId(store.getStoreId());
        color.setColorId(StringUtils.uuid());

        colorDao.save(color);*/

        if (StringUtils.isBlank(params.getString("token"))) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }
        Store store = storeService.getStoreByToken(params.getString("token"));
        JsonArray colors = params.getJsonArray("colors");

        colors.forEach(single -> {

            JsonObject color = (JsonObject) single;

            String groupName = color.getString("groupName");
            String colorName = color.getString("colorName");
            if (StringUtils.isBlank(groupName)) {
                throw new BusinessException("颜色 groupName 不能为空", ERROR_PARAM);
            }
            if (StringUtils.isBlank(colorName)) {
                throw new BusinessException("颜色 colorName 不能为空", ERROR_PARAM);
            }


            if (colorDao.findOneByStoreIdAndColorNameAndGroupName(store.getStoreId(),
                    colorName, colorName) == null) {
                Color colorDB = new Color();

                colorDB.setIsDelete(CommonConstant.DELETE.NO);
                colorDB.setStoreId(store.getStoreId());
                colorDB.setColorId(StringUtils.uuid());
                colorDB.setColorName(colorName);
                colorDB.setGroupName(groupName);

                colorDao.save(colorDB);
            }
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 获取商品详情
     *
     * @param routingContext
     */
    void getProductDetail(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String productId = params.getString("productId");

        if (StringUtils.isBlank(productId)) {
            throw new BusinessException("productId can not be null", NO_PARAM);
        }
        User user = userService.findUserByToken(params.getString("token"));
        Product product = new Product();
        product.setProductId(productId);

        ProductPermission pp = productPermissionDao.findOneByProductAndUserId(product, user.getUserId());

        final Product productDB = productDao.findOne(productId);
        if (productDB == null) {
            throw new BusinessException("product is not exists", DATA_NOT_EXIST);
        }
        if (StringUtils.equals("2", productDB.getVisible()) && (pp == null || new Date().compareTo(pp.getValidateTime()) > 0)) {
            throw new BusinessException(ErrorCodeEnum.NO_PRODUCT_PERMISSION);
        }

        productDB.setPictureUrls(new ArrayList<>());

        Iterable<ProductPicture> iterable =
                productPictureDao.findAllByProductId(productDB.getProductId());

        iterable.forEach(single -> {
            productDB.getPictureUrls().add(single.getPictureUrl());
        });
        /*if (StringUtils.isNotBlank(productDB.getCategory())) {

        }*/
        if (StringUtils.isNotBlank(productDB.getColor())) {
            String[] ids = productDB.getColor().replace(" ", "").split(",");
            Iterable<Color> iterable1 = colorDao.findAll(Arrays.asList(ids));
            List<Map<String, String>> colors = new ArrayList<>();
            iterable1.forEach(single -> {
                Map<String, String> map = new HashMap<String, String>();
                map.put("colorId", single.getColorId());
                map.put("colorName", single.getColorName());
                colors.add(map);

            });
            productDB.setColors(colors);
        }
        if (StringUtils.isNotBlank(productDB.getSize())) {
            String[] ids = productDB.getSize().replace(" ", "").split(",");
            Iterable<Size> iterable1 = sizeDao.findAll(Arrays.asList(ids));
            List<Map<String, String>> sizes = new ArrayList<>();
            iterable1.forEach(single -> {
                Map<String, String> map = new HashMap<String, String>();
                map.put("sizeId", single.getSizeId());
                map.put("sizeName", single.getSizeName());
                sizes.add(map);
            });
            productDB.setSizes(sizes);
        }
        // 是否收藏
        ProductCollection pc = productCollectionDao.findOneByUserIdAndProductId(user.getUserId()
                , productDB.getProductId());
        if (pc != null) {
            productDB.setCollect(true);
        }
        // 店铺信息
        Store store = storeDao.findOne(productDB.getStoreId());
        if (store != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("storeName", store.getStoreName());
            map.put("storeLogo", store.getLogo());
            map.put("userId", store.getUserId());
            map.put("hasFollow", false);
            if (storeFollowDao.findOneByUserIdAndStoreId(user.getUserId(), store.getStoreId()) != null) {
                map.put("hasFollow", true);
            }
            productDB.setStoreInfo(map);
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(productDB).build()));

    }

    /**
     * 编辑商品的时候获取详情
     *
     * @param routingContext 路由信息
     */
    private void getProduct4Edit(RoutingContext routingContext) {

        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (StringUtils.isBlank(product.getToken())) {
            throw new BusinessException("用户 token 不能为空", ERROR_PARAM);
        }
        if (StringUtils.isBlank(product.getProductId())) {
            throw new BusinessException("productId 不能为空", ERROR_PARAM);
        }

        final Product productDB = productDao.findOne(product.getProductId());
        if (productDB == null) {
            throw new BusinessException("product is not exists", DATA_NOT_EXIST);
        }

        productDB.setPictureUrls(new ArrayList<>());

        Iterable<ProductPicture> iterable =
                productPictureDao.findAllByProductId(productDB.getProductId());

        iterable.forEach(single -> {
            productDB.getPictureUrls().add(single.getPictureUrl());
        });

        Map<String, Map<String, Object>> remember = new HashMap<>();
        productDB.setRemember(remember);
        Iterable<ProductRemember> remembers = productRememberDao.findAllByProductId(productDB.getProductId());
        remembers.forEach(single -> {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("defaultShow", single.getDefaultShow());
            map.put("remember", single.getRemember());
            remember.put(single.getAttributeCode(), map);
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(productDB).build()));
    }

    /**
     * 设置商品可见性
     *
     * @param routingContext context
     */
    private void configProductVisible(RoutingContext routingContext) {
        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (CollectionUtils.isEmpty(product.getProductIds())) {
            throw new BusinessException("productIds can not be null", NO_PARAM);
        }
        if (CollectionUtils.isEmpty(product.getUserIds())) {
            throw new BusinessException("userIds can not be null", NO_PARAM);
        }
        product.getProductIds().forEach(productId -> {
            product.getUserIds().forEach(userId -> {
                saveProductPermission(productId, userId, product.getDays());
            });
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 删除可见性
     *
     * @param routingContext context
     */
    private void deleteProductVisible(RoutingContext routingContext) {
        Request<Product> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Product>>() {
        });
        Product product = request.getParams();
        if (CollectionUtils.isEmpty(product.getProductIds())) {
            throw new BusinessException("productIds can not be null", NO_PARAM);
        }
        if (CollectionUtils.isEmpty(product.getUserIds())) {
            throw new BusinessException("userIds can not be null", NO_PARAM);
        }

        product.getProductIds().forEach(productId -> {
            product.getUserIds().forEach(userId -> {
                productPermissionDao.deleteByProductIdAndUserId(productId, userId);
            });
        });


        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }


    /**
     * 保存商品权限
     *
     * @param productId productId
     * @param userId    userId
     */
    public void saveProductPermission(String productId, String userId, Integer agreeDay) {
        if (agreeDay == null) {
            agreeDay = 7;
        }
        ProductPermission pp = new ProductPermission();
        pp.setIsDelete(CommonConstant.DELETE.NO);
        pp.getProduct().setProductId(productId);
        pp.setUserId(userId);
        pp.setPermissionId(StringUtils.uuid());
        pp.setCreateTime(new Date());
        pp.setValidateTime(DateUtils.addDays(pp.getCreateTime(), agreeDay));
        try {
            productPermissionDao.save(pp);
        } catch (DataIntegrityViolationException e) {
        }
    }

    /**
     * 群发商品
     *
     * @param routingContext context
     */
    private void massSendProduct(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");

        Store store = storeService.getStoreByToken(token);

        JsonArray productIds = params.getJsonArray("productIds");

        StringBuilder productIdStr = new StringBuilder();
        productIds.forEach(productId -> {
            productIdStr.append("," + productId);
        });

        SendProduct sp = new SendProduct();
        sp.setStoreId(store.getStoreId());
        sp.setCreateTime(new Date());
        sp.setStatus(CommonConstant.SWITCH_STATUS.ON);
        sp.setProductIds(productIdStr.toString());
        sp.setSendId(StringUtils.uuid());

        sendProductDao.save(sp);

        Map<String, String> map = new HashMap<>();
        map.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=wx158a2d2aa408aa0f&redirect_uri=http://www.mengxue-web.cn/serviceProject/html/commodityList.html?" +
                "sendId=" + sp.getSendId() + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect");

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(map).build()));
    }

    private void configAllVisible(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");

        userService.findUserByToken(token);

        JsonArray productIds = params.getJsonArray("productIds");
        String visible = params.getString("visible");
        if (StringUtils.isBlank(visible) || !visible.equals("1") || !visible.equals("2")) {
            throw new BusinessException(ERROR_PARAM);
        }

        productIds.forEach(productId -> {
            Product productDB = productDao.findOne(productId.toString());
            productDB.setVisible(visible);
            productDao.save(productDB);
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询群发商品
     *
     * @param routingContext context
     */
    private void queryMassSendProduct(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User user = userService.findUserByToken(token);
        String sendId = params.getString("sendId");
        if (StringUtils.isBlank(sendId)) {
            throw new BusinessException("sendId can not be null", ERROR_PARAM);
        }
        SendProduct sendProduct = sendProductDao.findOne(sendId);

        if (sendProduct == null || sendProduct.getStatus() == CommonConstant.SWITCH_STATUS.OFF) {
            throw new BusinessException(DATA_NOT_EXIST);
        }

        String[] productIds = sendProduct.getProductIds().split(",");
        Iterable<Product> iterable = productDao.findAll(Arrays.asList(productIds));
        iterable.forEach(product -> {
            saveProductPermission(product.getProductId(), user.getUserId(), null);
            Iterable<ProductPicture> pp = productPictureDao.findAllByProductId(product.getProductId());
            if (pp.iterator().hasNext()) {
                List<String> pictureUrls = new ArrayList<String>();
                pp.forEach(p -> {
                    pictureUrls.add(p.getPictureUrl());
                });
                product.setPictureUrls(pictureUrls);
            }
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(iterable).build()));

    }


    /**
     * 店铺模块接口
     */
    //@Transactional
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

        if (StringUtils.equals(method, "get.product")) {
            getProduct4Edit(routingContext);
        } else if (StringUtils.equals(method, "query.product")) {
            queryProduct(routingContext);
        } else if (StringUtils.equals(method, "create.product")) {
            createProduct(routingContext);
        } else if (StringUtils.equals(method, "modify.product")) {
            modifyProduct(routingContext);
        } else if (StringUtils.equals(method, "delete.product")) {
            deleteProduct(routingContext);
        } else if (StringUtils.equals(method, "create.category")) {
            createCategory(routingContext);
        } else if (StringUtils.equals(method, "delete.category")) {
            deleteCategory(routingContext);
        } else if (StringUtils.equals(method, "query.category")) {
            queryCategory(routingContext);
        } else if (StringUtils.equals(method, "change.product.category")) {
            changeProductCategory(routingContext);
        } else if (StringUtils.equals(method, "sold.out")) {
            soldOut(routingContext);
        } else if (StringUtils.equals(method, "put.away")) {
            putAway(routingContext);
        } else if (StringUtils.equals(method, "create.color")) {
            createColor(routingContext);
        } else if (StringUtils.equals(method, "query.color")) {
            queryColor(routingContext);
        } else if (StringUtils.equals(method, "create.size")) {
            createSize(routingContext);
        } else if (StringUtils.equals(method, "query.size")) {
            querySize(routingContext);
        } else if (StringUtils.equals(method, "config.product.visible")) {
            configProductVisible(routingContext);
        } else if (StringUtils.equals(method, "config.all.visible")) {
            configAllVisible(routingContext);
        } else if (StringUtils.equals(method, "delete.product.visible")) {
            deleteProductVisible(routingContext);
        } else if (StringUtils.equals(method, "mass.send.product")) {
            massSendProduct(routingContext);
        } else if (StringUtils.equals(method, "query.mass.send.product")) {
            queryMassSendProduct(routingContext);
        } else {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
    }


}
