package Y1SHUI.config;

import Y1SHUI.service.weixin.IWeixinApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Retrofit接口不归springboot管，框架扫描时扫不到
 * 也就是说用诸如"
 * @GET("cgi-bin/token")
 *     Call<WeixinTokenRes> getToken(@Query("grant_type") String grantType,
 *                                   @Query("appid") String appId,
 *                                   @Query("secret") String appSecret);
 * "
 * 这样的语句都得单独写配置文件
 */
@Slf4j
@Configuration
public class Retrofit2Config {

    private static final String BASE_URL = "https://api.weixin.qq.com/";

    @Bean
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create()).build();
    }

    @Bean
    public IWeixinApiService weixinApiService(Retrofit retrofit) {
        return retrofit.create(IWeixinApiService.class);
    }

}
