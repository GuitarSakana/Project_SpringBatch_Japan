package io.springbatch.springbatch.service;

import io.springbatch.springbatch.batch.domain.ApiInfo;
import io.springbatch.springbatch.batch.domain.ApiResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ApiService2 extends AbstractApiService {
    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8081/product/1", apiInfo, String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();

        ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCodeValue).message(responseEntity.getBody()).build();

        return apiResponseVO;
    }
}
