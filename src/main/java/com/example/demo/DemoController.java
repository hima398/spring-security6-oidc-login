package com.example.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class DemoController {

    private final OAuth2AuthorizedClientService service;

    public DemoController(OAuth2AuthorizedClientService service) {
        this.service=service;
    }

    @GetMapping("/")
    public ModelAndView index() {
        var modelAndView = new ModelAndView("index");
        return modelAndView;
    }

    @GetMapping("userdetail")
    public ModelAndView userDetail(OAuth2AuthenticationToken token) {

        var user = getUser(token);
        var modelAndView = new ModelAndView("userdetail");
        modelAndView.addObject("attributes", user);
        return modelAndView;
    }

    private Map getUser(OAuth2AuthenticationToken token) {
        var client = this.service.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        var uri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

        var restClient = RestClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+client.getAccessToken().getTokenValue())
                .build();
        return  restClient.get().retrieve().toEntity(Map.class).getBody();
    }

}
