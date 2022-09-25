package com.example.template;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.stubrunner.StubRunning;
import org.springframework.cloud.contract.stubrunner.server.HttpStubsController;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
// provide the [group-id]:[artifact-id]
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "com.example:boot-camp-products")
//@AutoConfigureStubRunner(
//        repositoryRoot="https://pkgs.dev.azure.com/eventstorming/_packaging/eventstorming/maven/v1/",
////        repositoryRoot="http://34.85.54.161:8081/repository/maven-snapshots/",
//        ids = "com.example:boot-camp-products:+:stubs:8090",
//        stubsMode = StubRunnerProperties.StubsMode.REMOTE
//)
@AutoConfigureWireMock(port = 0)
public class ProductContractTest {

    @StubRunnerPort("boot-camp-products")
    int mockPort;

//    @Autowired
//    MockMvc mockMvc;

    @MockBean
    RestTemplate restTemplate;

//    @Autowired
//    WebApplicationContext webApplicationContext;

    @Autowired
    StubRunning stubRunning;

    @Before
    public void setup() {
        //remove::start[]
        RestAssuredMockMvc.standaloneSetup(new HttpStubsController(stubRunning));
//        RestAssuredMockMvc.webAppContextSetup(this.webApplicationContext);
        //remove::end[]
    }

    @Test
    public void getProduct_stub_test() throws Exception {

        TestRestTemplate testRestTemplate = new TestRestTemplate();

        testRestTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Content-Type", MediaType.APPLICATION_JSON.toString());
                    return execution.execute(request, body);
                }));
        String response = testRestTemplate
                .getForObject("http://localhost:" + this.mockPort + "/product/1",
                        String.class);

        System.out.println("=======================================================");
        System.out.println(response);
        System.out.println("=======================================================");

        DocumentContext parsedJson = JsonPath.parse(response);
        // and:
        Assertions.assertThat(parsedJson.read("$.id", String.class)).matches("[\\S\\s]+");
        Assertions.assertThat(parsedJson.read("$.name", String.class)).matches("[\\S\\s]+");
        Assertions.assertThat(parsedJson.read("$.price", String.class)).matches("[\\S\\s]+");
        Assertions.assertThat(parsedJson.read("$.stock", String.class)).matches("[\\S\\s]+");
        Assertions.assertThat(parsedJson.read("$.imageUrl", String.class)).matches("[\\S\\s]+");
    }

}