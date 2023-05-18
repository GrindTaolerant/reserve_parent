package com.hospital.reserve.user.api;


import com.alibaba.fastjson.JSONObject;
import com.hospital.reserve.common.helper.JwtHelper;
import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.common.result.ResultCodeEnum;
import com.hospital.reserve.common.result.YyghException;
import com.hospital.reserve.model.user.UserInfo;
import com.hospital.reserve.user.service.UserInfoService;
import com.hospital.reserve.user.utils.ConstantWxPropertiesUtil;
import com.hospital.reserve.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;

    //scan weChat and callback

    @GetMapping("callback")
    public String callback(String code, String state){
        //step1 get code
        System.out.println("Wechat redirect。。。。。。");
        System.out.println("state = " + state);
        System.out.println("code = " + code);

        //step2 get access token with code, appid and appscret
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtil.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtil.WX_OPEN_APP_SECRET,
                code);


        try {
            String result = HttpClientUtils.get(accessTokenUrl);
            System.out.println("使用code换取的access_token结果 = " + result);

            //use token info to get access_token and openid
            JSONObject jsonObject = JSONObject.parseObject(result);
            String accessToken = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");


            //check with openid
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openId);

            if(userInfo == null) {

                //use access_token and openid to get user info
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";

                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);

                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo:" + resultInfo);

                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);

                //get nickname
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openId);
                userInfo.setStatus(1);

                userInfoService.save(userInfo);
            }


            Map<String, Object> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            if(StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);

            return "redirect:" + ConstantWxPropertiesUtil.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+"&openid="+map.get("openid")+"&name="+URLEncoder.encode((String)map.get("name"));

        } catch (Exception e) {
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

    }



    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect() throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantWxPropertiesUtil.WX_OPEN_APP_ID);

        String redirectUri = URLEncoder.encode(ConstantWxPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//

        return Result.ok(map);
    }

}
