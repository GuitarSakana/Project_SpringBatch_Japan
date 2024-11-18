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
public class DBJobConfiguration {

    @Bean
    public Job job0(JobRepository jobRepository, Step step1, Step step2){
        return new JobBuilder("job0",jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step1",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step1 was executed");
                    log.info("step1 was executed");
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step2",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 was executed");
                    log.info("step2 was executed");
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

}
