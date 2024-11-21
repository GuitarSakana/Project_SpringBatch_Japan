package io.springbatch.springbatch.batch.job.api;

import io.springbatch.springbatch.batch.listener.JobListener;
import io.springbatch.springbatch.batch.tasklet.ApiEndTasklet;
import io.springbatch.springbatch.batch.tasklet.ApiStartTasklet;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ApiJobConfiguration {

    private final EntityManagerFactory entityManagerFactory;
    private final ApiStartTasklet apiStartTasklet;  //시작 로그 남김
    private final ApiEndTasklet apiEndTasklet;      //끝나는 로그 남김
    private final Step jobStep;

    @Bean
    public Job apiJob(JobRepository jobRepository, Step apiStep1, Step apiStep2){
        return new JobBuilder("apiJob",jobRepository)
                .listener(new JobListener())
                .start(apiStep1)
                .next(jobStep)
                .next(apiStep2)
                .build();
    }

    @Bean
    public Step apiStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("apiStep1",jobRepository)
                .tasklet(apiStartTasklet,platformTransactionManager)
                .build();
    }

    @Bean
    public Step apiStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("apiStep2",jobRepository)
                .tasklet(apiEndTasklet,platformTransactionManager)
                .build();
    }


}
