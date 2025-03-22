package Y1SHUI.domain.res;

import lombok.Data;

/**
 * @description 获取微信登录二维码响应对象
 * 用户的登录过程是：我发二维码请求(在req.WeixinQrCodeRea里)给微信，微信返回ticket给我
 * 我的前端拿着这个ticket去找微信要二维码图片
 * 用户扫完后微信再推送ticket给我，告诉我ta扫了
 */
@Data
public class WeixinQrCodeRes {

    private String ticket;
    private Long expire_seconds;
    private String url;

}
