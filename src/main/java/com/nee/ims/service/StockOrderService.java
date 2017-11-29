package com.nee.ims.service;

import static com.nee.ims.common.constant.ErrorCodeEnum.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.nee.ims.uitls.JPushUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.web.RoutingContext;

/**
 */
@Component
public class StockOrderService {
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private StockOrderDao stockOrderDao;
    @Autowired
    private StockOrderItemDao stockOrderItemDao;
    @Autowired
    private StockOrderItemDetailDao stockOrderItemDetailDao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProductPictureDao productPictureDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AsyncSQLClient mysqlClient;

    /**
     * 创建进货单
     */
    private void createStockOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String productId = params.getString("productId");
        //String price = params.getString("price");
        JsonArray colorSizeInfo = params.getJsonArray("colorSizeInfo");
        User user = userService.findUserByToken(token);

        if (StringUtils.isBlank(productId)) {
            throw new BusinessException("productId can not be null", ERROR_PARAM);
        }
        if (colorSizeInfo.isEmpty()) {
            throw new BusinessException("colorSizeInfo can not be null", ERROR_PARAM);
        }
        Product product = productDao.findOne(productId);
        Store store = storeDao.findOne(product.getStoreId());
        if (store == null) {
            throw new BusinessException("store not found", DATA_NOT_EXIST);
        }
      /*  if (product == null) {
            throw new BusinessException("product not found", DATA_NOT_EXIST);
        }*/
        final String itemId = saveStockOrderItem(product, store, user.getUserId(), null);
        colorSizeInfo.forEach(single -> {
            saveStockOrderItemDetail((JsonObject) single, itemId);
        });

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询我的进货单
     */
    private void queryMyStockOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        Integer pageSize = params.getInteger("pageSize") == null? 9 : params.getInteger("pageSize");
        Integer pageNo = params.getInteger("pageNo") == null? 0 : params.getInteger("pageNo");
        User user = userService.findUserByToken(token);
        //Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "createTime");
        Iterable<Object> iterable = stockOrderItemDao.findAll(user.getUserId());
        List<Object> list = new ArrayList<>();
        iterable.forEach(single -> {
            Map<String, String> map = new HashMap<String, String>();
            Object[] cells = (Object[]) single;
            map.put("storeId", String.valueOf(cells[0]));
            map.put("storeName", String.valueOf(cells[1]));
            map.put("storeLogo", String.valueOf(cells[2]));
            list.add(map);
        });


        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(list).build()));
    }

    /**
     * 保存收货地址
     */
    private void createReceivingAddress(RoutingContext routingContext) {
        Request<Address> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Address>>() {});
        Address address = request.getParams();
        User user = userService.findUserByToken(address.getToken());
        address.setUserId(user.getUserId());
        address.setAddressId(StringUtils.uuid());
        address.setIsDelete(CommonConstant.DELETE.NO);

        if (address.getIsDefault() == CommonConstant.DEFAULT.YES) {
            addressDao.updateByUserId(user.getUserId());
        }
        addressDao.save(address);
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 修改收货地址
     */
    private void modifyReceivingAddress(RoutingContext routingContext) {
        Request<Address> request = A0Json.decodeValue(routingContext.getBodyAsString(), new TypeReference<Request<Address>>() {});
        Address address = request.getParams();
        User user = userService.findUserByToken(address.getToken());
        address.setUserId(user.getUserId());
        if (StringUtils.isBlank(address.getAddressId())) {
            throw new BusinessException("addressId can not be null", ERROR_PARAM);
        }
        if (address.getIsDefault() == CommonConstant.DEFAULT.YES) {
            addressDao.updateByUserId(user.getUserId());
        }
        address.setIsDelete(CommonConstant.DELETE.NO);
        addressDao.save(address);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 查询收货地址
     */
    private void queryReceivingAddress(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User user = userService.findUserByToken(token);

        Iterable<Address> iterable = addressDao.findAllByUserId(user.getUserId(), new Sort(Sort.Direction.DESC, "createTime"));

        Map<String, Object> map = new HashMap<>();
        map.put("addressList", iterable);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(map).build()));
    }

    /**
     * 删除收获地址
     */
    private void deleteReceivingAddress(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User user = userService.findUserByToken(token);
        String addressId = params.getString("addressId");
        if (StringUtils.isBlank(addressId)) {
            throw new BusinessException("addressId can not be null", NO_PARAM);
        }
        addressDao.delete(addressId);
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }


    /**
     * 保存订单元素详情
     */
    private void saveStockOrderItemDetail(JsonObject object, String itemId) {
        String colorId = object.getString("colorId");
        String colorName = object.getString("colorName");
        String sizeId = object.getString("sizeId");
        String sizeName = object.getString("sizeName");
        Integer num = object.getInteger("num");
        if (StringUtils.isBlank(colorName) || StringUtils.isBlank(sizeName) || num == null) {
            throw new BusinessException("colorName、sizeName and num can not be null", ERROR_PARAM);
        }
        StockOrderItemDetail detail = new StockOrderItemDetail();
        detail.setDetailId(StringUtils.uuid());
        detail.setItemId(itemId);
        detail.setColorId(colorId);
        detail.setColorName(colorName);
        detail.setSizeId(sizeId);
        detail.setSizeName(sizeName);
        detail.setNum(num);
        detail.setStatus(CommonConstant.SWITCH_STATUS.ON);
        detail.setIsDelete(CommonConstant.DELETE.NO);
        stockOrderItemDetailDao.save(detail);
    }

    /**
     *
     * 保存订单元素
     *
     */
    private String saveStockOrderItem(Product product, Store store, String userId, String orderId) {
        StockOrderItem orderItem = new StockOrderItem();
        orderItem.setItemId(StringUtils.uuid());
        if (store != null) {
            orderItem.setStoreId(store.getStoreId());
            orderItem.setStoreLogo(store.getLogo());
            orderItem.setStoreName(store.getStoreName());
        }
        orderItem.setOrderId(orderId);
        orderItem.setUserId(userId);
        orderItem.setProductId(product.getProductId());
        orderItem.setProductName(product.getProductName());
        orderItem.setProductCode(product.getProductCode());
        orderItem.setPrice(product.getReferencePrice());
        orderItem.setStatus(CommonConstant.SWITCH_STATUS.ON);
        orderItem.setIsDelete(CommonConstant.DELETE.NO);
        stockOrderItemDao.save(orderItem);
        return orderItem.getItemId();
    }

    /**
     *
     * 获取店铺详情
     */
    private void getMyStockOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        String storeId = params.getString("storeId");
        if (StringUtils.isBlank(storeId)) {
            throw new BusinessException("storeId can not be null", ERROR_PARAM);
        }
        User user = userService.findUserByToken(token);

        List<JsonObject> objects = new ArrayList<>();
        Iterable<StockOrderItem> iterable = stockOrderItemDao.findAllByStoreIdAndUserId(storeId, user.getUserId());
        getColorSizeInfo(iterable, objects);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(objects).build()));
    }

    private void getColorSizeInfo(Iterable<StockOrderItem> iterable, List<JsonObject> objects) {
        iterable.forEach(single -> {

            JsonObject object = new JsonObject();
            object.put("itemId", single.getItemId());
            object.put("productId", single.getProductId());
            object.put("productCode", single.getProductCode());
            object.put("productName", single.getProductName());
            object.put("delay", single.getDelay());
            object.put("price", single.getPrice());

            Iterable<ProductPicture> pics = productPictureDao.findAllByProductId(single.getProductId());
            List<String> pictureUls = new ArrayList<String>();
            pics.forEach(pp -> {
                pictureUls.add(pp.getPictureUrl());
            });
            object.put("pictureUrl", pictureUls);
            Iterable<StockOrderItemDetail> details =  stockOrderItemDetailDao.findByItemId(single.getItemId());
            JsonObject colorSizeInfo = new JsonObject();
            details.forEach(detail -> {
                if (colorSizeInfo.getJsonArray(detail.getColorName()) == null) {
                    colorSizeInfo.put(detail.getColorName(), new JsonArray());
                }
                JsonObject colorSize = new JsonObject();
                colorSize.put("detailId", detail.getDetailId());
                colorSize.put("sizeName", detail.getSizeName());
                colorSize.put("num", detail.getNum());
                colorSizeInfo.getJsonArray(detail.getColorName()).add(colorSize);
            });
            List<JsonObject> colorSizes = new ArrayList<JsonObject>();
            colorSizeInfo.forEach(colorSize -> {
                JsonObject cs = new JsonObject();
                String colorName = colorSize.getKey();
                cs.put("color", colorName);
                cs.put("size", colorSizeInfo.getJsonArray(colorName));
                colorSizes.add(cs);
            });
            object.put("colorSizeInfo", colorSizes);

            objects.add(object);

        });
    }

    /**
     * 查询订单数量
     */
    private void queryOrderNum(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        Integer location = params.getInteger("location") == null? 1 : params.getInteger("location");
        User user = userService.findUserByToken(token);
        JsonObject object = new JsonObject();
        if (user.getUserType() < 3) {
            if (user.getUserType() == 1 || location == 2) {
                object.put("ordered", stockOrderDao.countByUserIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.ORDERED));
                object.put("unPay", stockOrderDao.countByUserIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.UN_PAY));
                object.put("unDeliver", stockOrderDao.countByUserIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.UN_DELIVER));
                object.put("unReceiving", stockOrderDao.countByUserIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.UN_RECEIVING));
                object.put("finish", stockOrderDao.countByUserIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.FINISH));
                object.put("cancel", stockOrderDao.countByUserIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.CANCEL));
            } else {
                customerOrderNum(object, user);
            }
        } else {

            customerOrderNum(object, user);
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(object).build()));
    }

    private void customerOrderNum(JsonObject object, User user) {
        object.put("ordered", stockOrderDao.countByCustomerIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.ORDERED));
        object.put("unPay", stockOrderDao.countByCustomerIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.UN_PAY));
        object.put("unDeliver", stockOrderDao.countByCustomerIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.UN_DELIVER));
        object.put("unReceiving", stockOrderDao.countByCustomerIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.UN_RECEIVING));
        object.put("finish", stockOrderDao.countByCustomerIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.FINISH));
        object.put("cancel", stockOrderDao.countByCustomerIdAndStatus(user.getUserId(), CommonConstant.ORDER_STATUS.CANCEL));
    }


    private void createOrder(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User customer = userService.findUserByToken(token);

        JsonArray products = params.getJsonArray("products");
        products.forEach(single -> {
            String orderId = null;
            JsonObject object = (JsonObject) single;
            String productId = object.getString("productId");
            //String price = params.getString("price");
            JsonArray colorSizeInfo = object.getJsonArray("colorSizeInfo");


            if (StringUtils.isBlank(productId)) {
                throw new BusinessException("productId can not be null", ERROR_PARAM);
            }
            if (colorSizeInfo.isEmpty()) {
                throw new BusinessException("colorSizeInfo can not be null", ERROR_PARAM);
            }
            Product product = productDao.findOne(productId);

            if (orderId == null) {
                Store store = storeDao.findOne(product.getStoreId());
                params.put("storeId", product.getStoreId());
                params.put("storeName", store.getStoreName());
                params.put("userId", store.getUserId());
                params.put("customerId", customer.getUserId());
                params.put("customerName", customer.getUserName());

                User user = userDao.findOne(store.getUserId());
                params.put("userName", user.getUserName());
                orderId = saveStockOrder(params);
            }

            final String itemId = saveStockOrderItem(product, null, customer.getUserId(), orderId);
            colorSizeInfo.forEach(single2 -> {
                saveStockOrderItemDetail((JsonObject) single2, itemId);
            });
        });

        // 删除进货单
        JsonArray stockOrderIds = params.getJsonArray("stockOrderIds");
        if (stockOrderIds != null) {
            stockOrderIds.stream().forEach(stockOrderId -> {
                try {
                    stockOrderItemDao.delete(String.valueOf(stockOrderId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private String saveStockOrder(JsonObject params) {
        StockOrder order = new StockOrder();
        order.setOrderId(StringUtils.uuid());
        order.setOrderNo("D" + GenerateCodeUtils.datetimeAndRandomNumberCode("yyyyMMddHHmmss", 6));
        order.setOrderType(2);
        order.setStoreId(params.getString("storeId"));
        order.setStoreName(params.getString("storeName"));
        order.setFreight(params.getString("freight"));
        order.setStatus(1);
        order.setUserId(params.getString("userId"));
        order.setUserName(params.getString("userName"));
        order.setCustomerId(params.getString("customerId"));
        order.setCustomerName(params.getString("customerName"));
        order.setAddressId(params.getString("addressId"));
        order.setOrderTime(new Date());
        order.setIsDelete(CommonConstant.DELETE.NO);
        stockOrderDao.save(order);

        return order.getOrderId();
    }

    /**
     * 查询我的订单
     */
    private void queryMyOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        Integer location = params.getInteger("location") == null?
                1 : params.getInteger("location");
        User customer = userService.findUserByToken(token);
        Integer status = params.getInteger("status");
        if (status == null) {
            status = CommonConstant.ORDER_STATUS.ORDERED;
        }
        Integer pageSize = params.getInteger("pageSize") == null? 9 : params.getInteger("pageSize");
        Integer pageNo = params.getInteger("pageNo") == null? 0 : params.getInteger("pageNo");

        Pageable pageable = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "updateTime");
        Page<StockOrder> orders = null;
        if (customer.getUserType() < 3) {
            if (customer.getUserType() == 1 || location == 2) {
                orders = stockOrderDao.findAllByUserIdAndStatus(customer.getUserId(), status, pageable);
            } else {
                orders = stockOrderDao.findAllByCustomerIdAndStatus(customer.getUserId(), status, pageable);
            }
        } else {
            orders = stockOrderDao.findAllByCustomerIdAndStatus(customer.getUserId(), status, pageable);
        }

        queryOrderSomeInfo(orders);
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().setData(orders).build()));

    }

    /**
     * 查询我下过的订单
     */
    private void queryMySinceOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User customer = userService.findUserByToken(token);
        String storeId = params.getString("storeId");

        Integer pageSize = params.getInteger("pageSize") == null? 9 : params.getInteger("pageSize");
        Integer pageNo = params.getInteger("pageNo") == null? 0 : params.getInteger("pageNo");


        JsonObject result = new JsonObject();
        result.put("size", pageSize);
        result.put("number", pageNo);

        CompletableFuture<JsonObject> allFuture = new CompletableFuture<JsonObject>();
        mysqlClient.getConnection(connect -> {
            if (connect.failed())
                allFuture.completeExceptionally(connect.cause());
            else {
                connect.result().queryWithParams("SELECT count(i.product_id) num\n" +
                                "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id\n" +
                                "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                                "WHERE o.customer_id = '" + customer.getUserId() + "' GROUP BY i.product_id", new JsonArray(),
                        res -> {
                            if (res.failed()) {
                                allFuture.completeExceptionally(res.cause());
                            } else {
                                result.put("totalElements", res.result().getRows().get(0).getLong("num"));
                                allFuture.complete(result);
                            }
                            connect.result().close();
                        });
            }
        });
        allFuture.thenCompose(futureRes -> {
            CompletableFuture<List<JsonObject>> future = new CompletableFuture<List<JsonObject>>();
            if (futureRes.getLong("totalElements") <= 0) {
                return future;
            }
            mysqlClient.getConnection(connect -> {
                if (connect.failed())
                    future.completeExceptionally(connect.cause());
                else {
                    connect.result().queryWithParams("SELECT i.product_id\n" +
                                    "FROM stock_order o INNER JOIN stock_order_item i ON i.order_id = o.order_id\n" +
                                    "  INNER JOIN stock_order_item_detail d ON d.item_id = i.item_id\n" +
                                    "WHERE o.customer_id = '" + customer.getUserId() + "' GROUP BY i.product_id " +
                                    "order by o.update_time desc limit " + pageNo * pageSize + ", " + pageSize, new JsonArray(),
                            res -> {
                                if (res.failed()) future.completeExceptionally(res.cause());
                                else
                                    future.complete(res.result().getRows());
                                connect.result().close();
                            });
                }
            });

            return future;
        }).thenCompose(futureRes -> {
            CompletableFuture future = new CompletableFuture();

            List<Product> products = new ArrayList<Product>();
            futureRes.forEach(single -> {
                String productId = single.getString("product_id");
                Product product = productDao.findOne(productId);
                if (product != null) {
                    Iterable<ProductPicture> productPictures = productPictureDao.findAllByProductId(productId);
                    product.setPictureUrls(new ArrayList<>());
                    productPictures.forEach(productPicture -> {
                        product.getPictureUrls().add(productPicture.getPictureUrl());
                    });

                    products.add(product);
                }
            });
            result.put("content", products);
            future.complete(products);
            return future;

        }).whenComplete((res, ex) -> {
            if (ex != null) {
                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .end(A0Json.encode(new Result.Builder().setCode(SYSTEM_ERROR.getCode()+"")
                                .setMessage(ex.toString()).build()));
            }
            routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                    .end(A0Json.encode(new Result.Builder().setData(result).build()));
        });

    }

    private void queryOrderSomeInfo(Page<StockOrder> orders) {
        orders.forEach(stockOrder -> {
            List<JsonObject> objects = new ArrayList<>();
            Iterable<StockOrderItem> iterable = stockOrderItemDao.findAllByOrderId(stockOrder.getOrderId());
            getColorSizeInfo(iterable, objects);
            stockOrder.setItems(objects);
            if (StringUtils.isNotBlank(stockOrder.getAddressId())) {
                stockOrder.setAddress(addressDao.findOne(stockOrder.getAddressId()));
            }
        });
    }

    /**
     * 取消
     */
    private void cancelOrder(RoutingContext routingContext) {
        StockOrder order = validateOrder(routingContext);
        if (order.getStatus() != CommonConstant.ORDER_STATUS.ORDERED &&
                order.getStatus() != CommonConstant.ORDER_STATUS.UN_PAY) {
            throw new BusinessException("this order can not be cancel", ERROR_PARAM);
        }

        order.setStatus(CommonConstant.ORDER_STATUS.CANCEL);

        stockOrderDao.save(order);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 确认订单
     */
    private void confirmOrder(RoutingContext routingContext) {
        StockOrder order = validateOrder(routingContext);

        if (order.getStatus() != CommonConstant.ORDER_STATUS.UN_RECEIVING) {
            throw new BusinessException("this order can not be confirm", ERROR_PARAM);
        }

        order.setStatus(CommonConstant.ORDER_STATUS.FINISH);
        order.setConfirmTime(new Date());

        stockOrderDao.save(order);


        try {
            new JPushUtils.Builder()
                    .setPlatType("all")
                    //.setImage("http://7xljza.com2.z0.glb.qiniucdn.com/qggvdgdsd552")
                    //.setClientData("{\"type\": 8, \"url\": \"http://bit.ly/2jj6ok9\"}")
                    .setContent("亲爱的点尚用户，您的订单已经被客户确认，请查看～")
                    .builder()
                    .execute(Arrays.asList(order.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));

    }

    /**
     * 支付
     */
    private void payOrder(RoutingContext routingContext) {
        StockOrder order = validateOrder(routingContext);
        if ( order.getStatus() != CommonConstant.ORDER_STATUS.UN_PAY) {
            throw new BusinessException("this order can not be pay", ERROR_PARAM);
        }

        order.setStatus(CommonConstant.ORDER_STATUS.UN_DELIVER);
        order.setPayTime(new Date());
        stockOrderDao.save(order);

        try {
            new JPushUtils.Builder()
                    .setPlatType("all")
                    //.setImage("http://7xljza.com2.z0.glb.qiniucdn.com/qggvdgdsd552")
                    //.setClientData("{\"type\": 8, \"url\": \"http://bit.ly/2jj6ok9\"}")
                    .setContent("亲爱的点尚用户，您的订单已经被客户支付，请查看～")
                    .builder()
                    .execute(Arrays.asList(order.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    private StockOrder validateOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User customer = userService.findUserByToken(token);
        String orderId = params.getString("orderId");
        StockOrder order = stockOrderDao.findOne(orderId);
        if (order == null || !customer.getUserId().equals(order.getCustomerId())) {
            throw new BusinessException("order not found", DATA_NOT_EXIST);
        }

        return order;
    }


    /**
     * 删除进货单中的单品
     */
    private void deleteStockOrderProduct(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User customer = userService.findUserByToken(token);
        String itemId = params.getString("itemId");
        if (StringUtils.isBlank(itemId)) {
            throw new BusinessException("itemId can not be null", ERROR_PARAM);
        }
        stockOrderItemDao.delete(itemId);

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));

    }

    /**
     * 发货
     *
     * @param routingContext
     */
    private void deliverOrder(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User user = userService.findUserByToken(token);

        Integer logisticsWay = params.getInteger("logisticsWay");
        String logisticsCompany = params.getString("logisticsCompany");
        String logisticsNo = params.getString("logisticsNo");

        String orderId = params.getString("orderId");
        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException("orderId can not be null", NO_PARAM);
        }

        StockOrder orderDB = stockOrderDao.findOne(orderId);
        if (!orderDB.getUserId().equals(user.getUserId())) {
            throw new BusinessException("order not found!", DATA_NOT_EXIST);
        }
        if (orderDB.getStatus() != CommonConstant.ORDER_STATUS.UN_DELIVER) {
            throw new BusinessException("order status error!", DATA_NOT_EXIST);
        }
        orderDB.setLogisticsNo(logisticsNo);
        orderDB.setLogisticsCompany(logisticsCompany);
        orderDB.setLogisticsWay(logisticsWay);

        orderDB.setStatus(CommonConstant.ORDER_STATUS.UN_RECEIVING);

        orderDB.setDeliverTime(new Date());

        stockOrderDao.save(orderDB);

        try {
            new JPushUtils.Builder()
                    .setPlatType("all")
                    //.setImage("http://7xljza.com2.z0.glb.qiniucdn.com/qggvdgdsd552")
                    //.setClientData("{\"type\": 8, \"url\": \"http://bit.ly/2jj6ok9\"}")
                    .setContent("亲爱的点尚用户，您的订单已经发货，请查看～")
                    .builder()
                    .execute(Arrays.asList(orderDB.getCustomerId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 卖家确认订单
     *
     * @param routingContext
     */
    private void confirmOrder4Seller(RoutingContext routingContext) {
        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User user = userService.findUserByToken(token);

        String orderId = params.getString("orderId");
        String freight = params.getString("freight");



        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException("orderId can not be null", NO_PARAM);
        }

        StockOrder orderDB = stockOrderDao.findOne(orderId);
        if (!orderDB.getUserId().equals(user.getUserId())) {
            throw new BusinessException("order not found!", DATA_NOT_EXIST);
        }
        if (orderDB.getStatus() != CommonConstant.ORDER_STATUS.ORDERED) {
            throw new BusinessException("order status error!", DATA_NOT_EXIST);
        }

        if (StringUtils.isNotBlank(freight)) {
            orderDB.setFreight(freight);
        }
        orderDB.setStatus(CommonConstant.ORDER_STATUS.UN_PAY);

        stockOrderDao.save(orderDB);

        JsonArray items = params.getJsonArray("items");
        items.forEach(single -> {
            JsonObject item = (JsonObject) single;
            String itemId = item.getString("itemId");
            if (StringUtils.isNotBlank(itemId)) {
                StockOrderItem orderItem = stockOrderItemDao.findOne(itemId);
                if (orderItem == null) {
                    return;
                }
                String price = item.getString("price");
                Object delay = item.getValue("delay");
                if (StringUtils.isNotBlank(price)) {
                    orderItem.setPrice(price);
                }
                if (delay != null) {
                    orderItem.setDelay(Integer.valueOf(delay.toString()));
                }
                stockOrderItemDao.save(orderItem);
                JsonArray details = item.getJsonArray("details");
                details.forEach(s -> {
                    JsonObject detail = (JsonObject) s;
                    String detailId = detail.getString("detailId");
                    if (StringUtils.isNotBlank(detailId)) {
                        StockOrderItemDetail orderItemDetail = stockOrderItemDetailDao.findOne(detailId);
                        if (orderItemDetail == null) {
                            throw new BusinessException(DATA_NOT_EXIST);
                        }
                        Integer num = detail.getInteger("num");
                        if (num != null) {
                            orderItemDetail.setNum(num);
                            stockOrderItemDetailDao.save(orderItemDetail);
                        }
                    }
                });
            }
        });

        try {
            new JPushUtils.Builder()
                    .setPlatType("all")
                    //.setImage("http://7xljza.com2.z0.glb.qiniucdn.com/qggvdgdsd552")
                    //.setClientData("{\"type\": 8, \"url\": \"http://bit.ly/2jj6ok9\"}")
                    .setContent("亲爱的点尚用户，您的订单已经被卖家确认，请查看～")
                    .builder()
                    .execute(Arrays.asList(orderDB.getCustomerId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 删除订单
     *
     * @param routingContext context
     */
    private void deleteStockOrder(RoutingContext routingContext) {

        JsonObject params = routingContext.getBodyAsJson().getJsonObject("params");
        String token = params.getString("token");
        User user = userService.findUserByToken(token);

        String orderId = params.getString("orderId");
        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException("orderId can not be null", NO_PARAM);
        }

        StockOrder order = stockOrderDao.findOne(orderId);

        if (order == null) {
            throw new BusinessException("order not found", DATA_NOT_EXIST);
        }
        if (user.getUserId().equals(order.getUserId())) {
            throw new BusinessException("this order can not be delete", ERROR_LOGIC);
        }

        stockOrderDao.delete(orderId);
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(A0Json.encode(new Result.Builder().build()));
    }

    /**
     * 订单模块接口
     */
    public void execute (RoutingContext routingContext) {
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

        if (StringUtils.equals(method, "create.stock.order")) {
            createStockOrder(routingContext);
        } else if (StringUtils.equals(method, "delete.stock.order")) {
            deleteStockOrder(routingContext);
        } else if (StringUtils.equals(method, "query.my.stock.order")) {
            queryMyStockOrder(routingContext);
        } else if (StringUtils.equals(method, "get.my.stock.order")) {
            getMyStockOrder(routingContext);
        } else if (StringUtils.equals(method, "delete.stock.order.product")) {
            deleteStockOrderProduct(routingContext);
        } else if (StringUtils.equals(method, "create.receiving.address")) {
            createReceivingAddress(routingContext);
        } else if (StringUtils.equals(method, "query.receiving.address")) {
            queryReceivingAddress(routingContext);
        } else if (StringUtils.equals(method, "delete.receiving.address")) {
            deleteReceivingAddress(routingContext);
        } else if (StringUtils.equals(method, "modify.receiving.address")) {
            modifyReceivingAddress(routingContext);
        } else if (StringUtils.equals(method, "create.order")) {
            createOrder(routingContext);
        } else if (StringUtils.equals(method, "query.order.num")) {
            queryOrderNum(routingContext);
        } else if (StringUtils.equals(method, "query.my.order")) {
            queryMyOrder(routingContext);
        } else if (StringUtils.equals(method, "query.my.since.order")) {
            queryMySinceOrder(routingContext);
        } else if (StringUtils.equals(method, "cancel.order")) {
            cancelOrder(routingContext);
        } else if (StringUtils.equals(method, "confirm.order")) {
            confirmOrder(routingContext);
        } else if (StringUtils.equals(method, "confirm.order.4.seller")) {
            confirmOrder4Seller(routingContext);
        } else if (StringUtils.equals(method, "deliver.order")) {
            deliverOrder(routingContext);
        } else if (StringUtils.equals(method, "pay.order")) {
            payOrder(routingContext);
        } else {
            throw new BusinessException(ErrorCodeEnum.REQUEST_ERROR);
        }
    }




}
