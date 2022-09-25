package com.example.template;

import com.example.template.config.kafka.KafkaProcessor;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.NotificationListener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
// provide the [group-id]:[artifact-id]
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "com.example:boot-camp-products")
//@AutoConfigureStubRunner(
//        repositoryRoot="http://34.85.54.161:8081/repository/maven-snapshots/",
//        ids = "com.example:boot-camp-products:+:stubs:8090",
//        stubsMode = StubRunnerProperties.StubsMode.REMOTE
//)
@AutoConfigureWireMock(port = 0)
public class ProductChangedContactTest {

    @Autowired
    StubFinder stubFinder;

    @Autowired
    private KafkaProcessor processor;

    @Autowired
    private MessageCollector messageCollector;


//    @Autowired
//    ConsumerTemplate consumerTemplate;

    @Test
    public void testOnMessageReceived() {
        // event start
        stubFinder.trigger("productChanged");

        Message<String> received = (Message<String>) messageCollector.forChannel(processor.outboundTopic()).poll();

        System.out.println("=======================================================");
        System.out.println(received.getPayload());
        System.out.println("=======================================================");

        DocumentContext parsedJson = JsonPath.parse(received.getPayload());

        // 넘어 오는 값에 대하여 validation 을 한다. 만약 productName 컬럼이 변경되었다면 에러가 발생한다.
        assertThat(parsedJson.read("$.eventType", String.class)).isEqualTo("ProductChanged");
        assertThat(parsedJson.read("$.productId", String.class)).matches("[\\S\\s]+");
        assertThat(parsedJson.read("$.productName", String.class)).matches("[\\S\\s]+");
        assertThat(parsedJson.read("$.productPrice", String.class)).matches("[\\S\\s]+");
        assertThat(parsedJson.read("$.productStock", String.class)).matches("[\\S\\s]+");
        assertThat(parsedJson.read("$.imageUrl", String.class)).matches("[\\S\\s]+");

    }
}
