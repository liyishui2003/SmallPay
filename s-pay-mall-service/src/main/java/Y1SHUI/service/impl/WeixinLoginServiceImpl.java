package Y1SHUI.service.impl;

import Y1SHUI.domain.req.WeixinQrCodeReq;
import Y1SHUI.domain.res.WeixinQrCodeRes;
import Y1SHUI.domain.res.WeixinTokenRes;
import Y1SHUI.domain.po.WeixinTemplateMessageVO;
import Y1SHUI.service.ILoginService;
import Y1SHUI.service.weixin.IWeixinApiService;
import com.google.common.cache.Cache;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Service
public class WeixinLoginServiceImpl implements ILoginService {
    /**
     * 注解：Value / Resource
     * Value通过读取配置文件的方式进行注解，防止重要的密码等硬编码在代码里
     */
    @Value("${weixin.config.app-id}")
    private String appid;

    @Value("${weixin.config.app-secret}")
    private String appSecret;

    @Value("${weixin.config.template_id}")
    private String template_id;

    @Resource
    private Cache<String,String> weixinAccessToken;

    @Resource
    private IWeixinApiService weixinApiService;

    @Resource
    private Cache<String,String> openidToken;

    public String createQrCodeTicket() throws Exception{
        // 1.获取accessToken
        //读取appid，这个是写在配置里的
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if(null == accessToken){
            //发起retrofit请求并获得响应
            Call<WeixinTokenRes> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenRes weixinTokenRes = call.execute().body();
            assert weixinTokenRes != null;
            //获取token并更新
            accessToken = weixinTokenRes.getAccess_token();
            weixinAccessToken.put(appid,accessToken);
        }

        // 2.生成ticket，这里用lombok提供的build方法，直接链式调用
        WeixinQrCodeReq weixinQrCodeReq = new WeixinQrCodeReq().builder()
                .expire_seconds(2592000)
                .action_name(WeixinQrCodeReq.ActionNameTypeVO.QR_SCENE.getCode())
                .action_info(WeixinQrCodeReq.ActionInfo.builder()
                        .scene(WeixinQrCodeReq.ActionInfo.Scene.builder()
                                .scene_id(100601)
                                .build())
                        .build()
                ).build();

        Call<WeixinQrCodeRes> call = weixinApiService.createQrCode(accessToken,weixinQrCodeReq);
        WeixinQrCodeRes weixinQrCodeRes = call.execute().body();
        assert weixinQrCodeRes != null;
        return weixinQrCodeRes.getTicket();
    }

    @Override
    public String checkLogin(String ticket){
        return openidToken.getIfPresent(ticket);
    }

    public void saveLoginState(String ticket,String openid) throws IOException{
        //0.这里ticket和二维码对应，用户扫码后ticket就和openid(微信分配给用户的id，每个公众号下唯一)
        //绑定了
        openidToken.put(ticket, openid);

        //1.获取accessToken
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null == accessToken){
            Call<WeixinTokenRes> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenRes weixinTokenRes = call.execute().body();
            assert weixinTokenRes != null;
            accessToken = weixinTokenRes.getAccess_token();
            weixinAccessToken.put(appid, accessToken);
        }

        //2.发送模板消息
        Map<String,Map<String,String>> data = Maps.newHashMap();
        //这里data是外层的map，内层调用了枚举类里的USER获得"user"作为key值，openid作为value值
        WeixinTemplateMessageVO.put(data,WeixinTemplateMessageVO.TemplateKey.USER,openid);

        WeixinTemplateMessageVO templateMessageDTO = new WeixinTemplateMessageVO(openid,template_id);
        templateMessageDTO.setData(data);
        templateMessageDTO.setUrl("https://gaga.plus");

        Call<Void> call = weixinApiService.sendMessage(accessToken,templateMessageDTO);
        call.execute();
    }

}
