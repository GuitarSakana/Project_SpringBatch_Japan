package io.springbatch.springbatch.practice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class JobConfiguration {

    @Bean
    public Job job2(JobRepository jobRepository,Step stepTest1, Step stepTest2){
        return new JobBuilder("job2",jobRepository)
                .start(stepTest1)
                .next(stepTest2)
                .build();
    }

    @Bean
    public Step stepTest1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step1",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step1 was execute.");
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step stepTest2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step2",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 was execute.");
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

}
