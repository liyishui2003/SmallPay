package Y1SHUI.domain.req;

import lombok.*;


/**
 * !获取微信登录二维码请求对象!
 */


/**
 * Data/Builder/AllArgsConstructor/NoArgsConstructor
 * 这些都是lombok注解，自动生成对应方法和构造函数
 * 比如下面声明了这些信息，但我可以不用写构造函数，可以直接这么调用：
 * WeixinQrCodeReq req = WeixinQrCodeReq.builder()
 *     .action_name("QR_LIMIT_STR_SCENE") // 永久二维码，字符串场景值
 *     .action_info(ActionInfo.builder()
 *         .scene(Scene.builder()
 *             .scene_str("login_20231001") // 场景字符串为 "login_20231001"
 *             .build())
 *         .build())
 *     .build();
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeixinQrCodeReq {

    private int expire_seconds;//二维码有效期
    private String action_name;//类型，临时的还是永久的
    private ActionInfo action_info;//存储二维码所在的场景有关信息

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActionInfo {
        Scene scene;//指定二维码的具体场景信息。

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Scene {
            //描述了一个场景：id/str
            int scene_id;
            String scene_str;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    /**
     * 二维码有不同的场景类型需求
     * 比如固定的支付功能就可以用整型
     * 动态生成的登录需求如"123456_2022_0202_login"就要用字符串
     * 下面的枚举类就是用来做这个的
     */
    public enum ActionNameTypeVO {
        QR_SCENE("QR_SCENE", "临时的整型参数值"),
        QR_STR_SCENE("QR_STR_SCENE", "临时的字符串参数值"),
        QR_LIMIT_SCENE("QR_LIMIT_SCENE", "永久的整型参数值"),
        QR_LIMIT_STR_SCENE("QR_LIMIT_STR_SCENE", "永久的字符串参数值");

        private String code;
        private String info;
    }

}
