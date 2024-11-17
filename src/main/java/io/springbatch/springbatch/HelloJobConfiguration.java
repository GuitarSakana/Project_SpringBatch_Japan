package io.springbatch.springbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class HelloJobConfiguration {
    //Spring Boot 3.0이상 버전 부터는 JobBuilderFactory랑 StepBuilderFactory가 필요가 없다.

    @Bean
    public Job helloJob(JobRepository jobRepository, Step helloStep, Step helloStep2) {
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep)
                .next(helloStep2)
                .build();
    }
    @Bean
    public Step helloStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("helloStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("====================================");
                    System.out.println(" hello Spring Batch!! ");
                    System.out.println("====================================");
                    return RepeatStatus.FINISHED;
                }
                , platformTransactionManager)
                .build();
    }
    @Bean
    public Step helloStep2(JobRepository jobRepository,PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("helloStep2",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("====================================");
                    System.out.println(" hello Spring Batch2 was Excute!! ");
                    System.out.println("====================================");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

//    @Bean
//    public Tasklet testTasklet(){
//        return ((contribution, chunkContext) -> {
//            log.info(">>>>> This is Step1");
//            return RepeatStatus.FINISHED;
//        });
//    }

}
