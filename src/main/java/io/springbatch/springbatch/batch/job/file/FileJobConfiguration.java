package io.springbatch.springbatch.batch.job.file;

import io.springbatch.springbatch.batch.domain.Product;
import io.springbatch.springbatch.batch.domain.ProductVO;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class FileJobConfiguration {

    private final EntityManagerFactory em;

    @Bean
    public Job fileJob(JobRepository jobRepository, Step fileStep){
        return new JobBuilder("fileJob",jobRepository)
                .start(fileStep)
                .build();
    }

    @Bean
    public Step fileStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("fileStep",jobRepository)
        .<ProductVO, Product>chunk(10,platformTransactionManager)
                .reader(fileItemReader())//ProductVO를 읽음
                .processor(fileItemProccessor())//ProductVO를 Product로 변환
                .writer(fileItemWriter())//Product를 가지고 저장,처리
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader <ProductVO>fileItemReader(
            @Value("#{jobParameters['requestDate']}") String requestDate)
    {
        return  new FlatFileItemReaderBuilder<ProductVO>()
                .name("flatFile")
                .resource(new ClassPathResource("product_"+requestDate+".csv"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(ProductVO.class)
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("id","name","price","type")
                .build();
    }

    @Bean
    public ItemProcessor<ProductVO,Product> fileItemProccessor(){
        return new FlatFileItemProccessor()
    }


}
