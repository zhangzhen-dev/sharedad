package com.sharedad.community.controller;

import com.sharedad.community.dto.AccessTokenDTO;
import com.sharedad.community.dto.GithubUser;
import com.sharedad.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String secret;



    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.getClient_id(clientId);
        accessTokenDTO.getClient_secret(secret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.getRedirect_uri("http://localhost:8080/callback");
        accessTokenDTO.getState(state);

        try {
            String accessToken =githubProvider.getAccessToken(accessTokenDTO);
            GithubUser user = githubProvider.getUser(accessToken);
            System.out.println(user.getId());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }
}
