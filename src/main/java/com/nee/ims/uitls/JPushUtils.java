package com.nee.ims.uitls;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 *
 */
public class JPushUtils {
    private static Logger logger = LoggerFactory.getLogger(JPushUtils.class);

    private static final String APP_KEY = "db957da1c5736ef1581e8441";
    private static final String MASTER_SECRET = "28f5e295ca039489ca220fca";
    private static final String PUSH_TITLE = "点尚";

    private static JPushClient client;

    private String title;
    private String subTitle;
    private String content;
    private String clientData;
    private String image;
    private String platType;

    public static class Builder {
        private String title;
        private String subTitle;
        private String content;
        private String clientData;
        private String image;
        private String platType;


        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setClientData(String clientData) {
            this.clientData = clientData;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Builder setPlatType(String platType) {
            this.platType = platType;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public JPushUtils builder() {
            return new JPushUtils(this);
        }
    }

    private JPushUtils(Builder builder) {
        this.clientData = builder.clientData;
        this.content = builder.content;
        this.image = builder.image;
        this.platType = builder.platType;
        this.title = builder.title;
        this.subTitle = builder.subTitle;
    }

    /**
     * 指定用户
     *
     * @return
     * @throws Exception
     */
    public PushResult execute(List<String> pushTokens) throws Exception {

        if (pushTokens == null || pushTokens.size() <= 0) {
            throw new RuntimeException("pushTokens 不能为空");
        }
        PushPayload pa = buildPa(pushTokens);

        PushResult rs = getJpushClient().sendPush(pa);

        if (logger.isDebugEnabled()) {
            logger.debug(rs.toString());
        }
        return rs;
    }

    /**
     * 推送全部
     *
     * @return
     * @throws Exception
     */
    public PushResult execute() throws Exception {

        PushPayload pa = buildPa(null);

        PushResult rs = getJpushClient().sendPush(pa);

        if (logger.isDebugEnabled()) {
            logger.debug(rs.toString());
        }
        return rs;
    }

    /**
     * @param registrationIds push token
     * @return PushPayload
     */
    private PushPayload buildPa(List<String> registrationIds) {

        return PushPayload
                .newBuilder()
                .setPlatform(createPlatform(platType))
                .setAudience(createAudience(registrationIds))
                .setNotification(createNotification(content, clientData, image))
                .setOptions(
                        Options.newBuilder().setApnsProduction(true).build())
                .build();
    }

    private Platform createPlatform(String platType) {
        if (StringUtils.isEmpty(platType)) {
            return Platform.all();
        } else if (platType.equals("ios")) {
            return Platform.ios();
        } else if (platType.equals("android")) {
            return Platform.android();
        } else {
            return Platform.all();
        }
    }

    /**
     * 创建推送对象
     *
     * @param registrationIds
     * @return
     */
    private Audience createAudience(List<String> registrationIds) {
        Audience audience = null;
        if (registrationIds != null && registrationIds.size() > 0) {
            audience = Audience.alias(registrationIds);
        } else {
            audience = Audience.all();
        }
        return audience;
    }

    /**
     * 创建Notification
     *
     * @param alert
     * @param clientData
     * @param image
     * @return
     */
    private Notification createNotification(String alert, String clientData, String image) {

        return Notification
                .newBuilder()
                .addPlatformNotification(
                        IosNotification.newBuilder()
                                .setMutableContent(true)
                                .setAlert(alert).setBadge(1)
                                .setSound("happy.caf")
                                .addExtra("subTitle", "生活小提示")
                                .addExtra("from", "Nothing")
                                .addExtra("image", image)
                                .addExtra("data", clientData).build())
                .addPlatformNotification(
                        AndroidNotification.newBuilder()
                                .setAlert(alert)
                                .setTitle(PUSH_TITLE)
                                .setBuilderId(1)
                                //.extra(Constants.EXTRA_PARAM_SOUND_URI, "android.resource://com.juejian.nothing/raw/notification  ")
                                .addExtra("from", "Nothing")
                                .addExtra("data", clientData).build())
                .build();
    }

    private JPushClient getJpushClient() {
        if (client == null) {
            synchronized (JPushUtils.class) {
                if (client == null) {
                    client = new JPushClient(MASTER_SECRET, APP_KEY);
                }
            }
        }
        return client;
    }


    public static void main(String[] args) throws Exception {
        List<String> pushTokens = new ArrayList<String>();
        pushTokens.add("67e77c96a5d84530aaadd2ab595d5b86");
        /*pushTokens.add("161a3797c80b76449c6");
        pushTokens.add("191e35f7e04a9362429");
        pushTokens.add("101d85590949f5c41fc");
        pushTokens.add("18171adc0304d274d2f");
        pushTokens.add("01110828d18");*/
        //pushTokens.add("101d8559094bcc7600c");


        new JPushUtils.Builder()
                .setPlatType("all")
                .setImage("http://7xljza.com2.z0.glb.qiniucdn.com/qggvdgdsd552")
                .setClientData("{\"type\": 8, \"url\": \"http://bit.ly/2jj6ok9\"}")
                .setContent("亲爱的点尚用户，祝您生活愉快～")
                .builder()
                .execute(pushTokens);

    }
}
