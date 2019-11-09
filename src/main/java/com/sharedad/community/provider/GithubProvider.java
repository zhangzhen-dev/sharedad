package com.sharedad.community.provider;

import com.alibaba.fastjson.JSON;
import com.sharedad.community.dto.AccessTokenDTO;
import com.sharedad.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO) throws IOException {

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO) );
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token?client_id=e8b197592e11d1711778&client_secret=fb20bc700124c620b024072a1259cdda44a3f634&state=1")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String[] sqlit = string.split("&");
            String tokenstr =sqlit[0];
           String token= tokenstr.split("=")[1];
            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser =JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e){
        }
        return null;
    }

}
