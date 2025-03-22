package Y1SHUI.controller;


import Y1SHUI.common.weixin.MessageTextEntity;
import Y1SHUI.common.weixin.SignatureUtil;
import Y1SHUI.common.weixin.XmlUtil;
import Y1SHUI.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/weixin/portal/")

public class WeixinPortalController {

    @Value("${weixin.config.originalid}")
    private String originalid;

    @Value("${weixin.config.token}")
    private String token;

    @Resource
    private ILoginService loginService;

    @GetMapping(value = "receive", produces = "text/plain;charset=utf-8")
    public String validate(@RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr){

        try {

            log.info("微信公众号验签信息开始 [{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
            if(StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)){
                throw new IllegalArgumentException("请求参数非法，请核实！");
            }
            boolean check = SignatureUtil.check(token,signature,timestamp,nonce);
            log.info("微信公众号验签信息完成 check{} ",check);
            if(check) {
                return echostr;
            } else{
                return null;
            }

        } catch (Exception e) {
            log.info("微信公众号验签信息失败 [{},{},{},{}]",signature, timestamp, nonce, echostr);
            return null;
        }
    }

    @PostMapping(value = "receive",produces =  "application/xml;charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature){

        try {
            log.info("接收微信公众号信息请求{}开始{}",openid,requestBody);
            MessageTextEntity message = XmlUtil.xmlToBean(requestBody, MessageTextEntity.class);
            if("event".equals(message.getMsgType()) && "SCAN".equals(message.getEvent())){
                loginService.saveLoginState(message.getTicket(),openid);
                return buildMessageTextEntity(openid,"登录成功");
            }
            return buildMessageTextEntity(openid,"你好"+message.getContent());
        } catch (Exception e) {
            log.error("接收微信公众号信息请求{}失败{}",openid,requestBody,e);
            return "";
        }
    }

    private String buildMessageTextEntity(String openid,String msg) throws Exception {
        MessageTextEntity message = new MessageTextEntity();
        //公众号分配的ID
        message.setFromUserName(originalid);//发送者，也就是"我"的originalid
        message.setToUserName(openid);
        message.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
        message.setMsgType("text");
        message.setContent(msg);
        return XmlUtil.beanToXml(message);
    }



}
