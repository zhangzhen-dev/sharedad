package com.sharedad.community.controller;

import com.sharedad.community.dto.AccessTokenDTO;
import com.sharedad.community.dto.GithubUser;
import com.sharedad.community.mapper.UserMapper;
import com.sharedad.community.model.User;
import com.sharedad.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String secret;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.getClient_id(clientId);
        accessTokenDTO.getClient_secret(secret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.getRedirect_uri("http://localhost:8080/callback");
        accessTokenDTO.getState(state);

        try {
            String accessToken =githubProvider.getAccessToken(accessTokenDTO);
            GithubUser githubUser = githubProvider.getUser(accessToken);
            if(githubUser != null){
                //登录成功
                User user = new User();
                final String token = UUID.randomUUID().toString();
                user.setToken(token);
                user.setName(githubUser.getName());
                user.setAccountId(String.valueOf(githubUser.getId()));
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModified(user.getGmtCreate());
                userMapper.insert(user);
                // 登录成功
                response.addCookie(new Cookie("token",token));
                return "redirect:/";
            } else {
                //登录失败
                return "redirect:/";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }
}
