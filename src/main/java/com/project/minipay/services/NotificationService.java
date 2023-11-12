package com.project.minipay.services;

import com.project.minipay.dtos.NotificationDto;
import com.project.minipay.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(UserModel user, String msg) throws Exception {
        String email = user.getEmail();

        NotificationDto request = new NotificationDto(email, msg);

        ResponseEntity<String> responseEntity =  restTemplate.postForEntity("https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6", request, String.class);

        if(! (responseEntity.getStatusCode() == HttpStatus.OK) ){
            throw new Exception("Service unavaliable");
        }


    }


}
