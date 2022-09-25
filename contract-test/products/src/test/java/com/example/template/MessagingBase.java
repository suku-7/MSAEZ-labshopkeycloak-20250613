package com.example.template;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)    // junit4 와 springboot 를 연결해준다.
// 통합테스트
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class MessagingBase {

    //remove::start[]
    @Autowired
    MessageVerifier messaging;
    //remove::end[]

    @Before
    public void setup() {
        // let's clear any remaining messages
        // output == destination or channel name
        //remove::start[]
        this.messaging.receive("eventTopic", 100, TimeUnit.MILLISECONDS);
        //remove::end[]
    }

    public void productChanged() {

        Product product = new Product();
        product.setId(1L);
        product.setName("TEST");
        product.setPrice(10000);
        product.setStock(10);
        product.setImageUrl("/test.jpg");

        ProductChanged productChanged = new ProductChanged(product);
        String json = productChanged.toJson();

        this.messaging.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build(), "eventTopic");
    }
}
