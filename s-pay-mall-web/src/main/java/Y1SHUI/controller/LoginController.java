package Y1SHUI.controller;

import Y1SHUI.common.constants.Constants;
import Y1SHUI.common.response.Response;
import Y1SHUI.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/login/")

public class LoginController {

    @Resource
    private ILoginService loginService;

    @RequestMapping(value = "weixin_qrcode_ticket",method = RequestMethod.GET)

    public Response<String> weixinQrCodeTicket(){
        try {
            String qrCodeTicket = loginService.createQrCodeTicket();
            log.info(("生成微信扫码登录 ticket:{}",qrCodeTicket);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(qrCodeTicket)
                    .build();
        } catch (Exception e){

        }
    }


}
