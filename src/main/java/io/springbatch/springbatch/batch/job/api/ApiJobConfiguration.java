package io.springbatch.springbatch.batch.job.api;

import io.springbatch.springbatch.batch.tasklet.ApiEndTasklet;
import io.springbatch.springbatch.batch.tasklet.ApiStartTasklet;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApiJobConfiguration {

    private final EntityManagerFactory entityManagerFactory;
    private final ApiStartTasklet apiStartTasklet;  //시작 로그 남김
    private final ApiEndTasklet apiEndTasklet;      //끝나는 로그 남김
    private final Step jobStep;


}
