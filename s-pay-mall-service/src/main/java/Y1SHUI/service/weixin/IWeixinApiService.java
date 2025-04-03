package Y1SHUI.service.weixin;

import Y1SHUI.domain.req.WeixinQrCodeReq;
import Y1SHUI.domain.res.WeixinQrCodeRes;
import Y1SHUI.domain.res.WeixinTokenRes;
import Y1SHUI.domain.po.WeixinTemplateMessageVO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 这个接口用来调用微信Api
 * 封装了GET和POST
 */
public interface IWeixinApiService {

    /*
    调用API需要获取token，其中appid和secret是微信分配给我的，拿着这个去找微信要token
    有了token后才能调用微信的API
    cgi-bin/token这个路由是微信指定的
     */
    @GET("cgi-bin/token")
    Call<WeixinTokenRes> getToken(@Query("grant_type") String grantType,
                                  @Query("appid") String appId,
                                  @Query("secret") String appSecret);

    /*
    * 用于调用微信的 cgi-bin/qrcode/create API，生成二维码
    * 需要提交的东西有：token和请求体(WeixinQrCodeReq)
    * */
    @POST("cgi-bin/qrcode/create")
    Call<WeixinQrCodeRes> createQrCode(@Query("access_token") String accessToken, @Body WeixinQrCodeReq weixinQrCodeReq);

    /*
    * 调用微信的消息发送接口，同样带上token，请求体是weixinTemplateMessageVO，里面封装了和消息有关的一切
    * */
    @POST("cgi-bin/message/template/send")
    Call<Void> sendMessage(@Query("access_token") String accessToken, @Body WeixinTemplateMessageVO weixinTemplateMessageVO);

}
