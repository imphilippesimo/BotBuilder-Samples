package com.microsoft.bot.sample.echo.services;

import com.microsoft.bot.sample.echo.conf.AIEndpointConf;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApiService {
    private final RestTemplate restTemplate;
    private final AIEndpointConf conf;

    public ApiService(AIEndpointConf conf) {
        this.conf = conf;
        this.restTemplate = new RestTemplate();
    }

    public String consumeApi(String message) {

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + conf.getKey());
        headers.set("azureml-model-deployment", conf.getDeployment());

        // Body
        Map<String, Object> map = new HashMap<>();
        map.put("question", message);
        map.put("chat_history", Collections.emptyList());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        String unknown = "Sorry, I don't understand.";
        try {
            Response response = restTemplate.postForObject(conf.getUrl(), entity, Response.class);
            return response != null ? response.getAnswer() : unknown;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unknown;
    }

    static class Response {
        String answer;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

}
