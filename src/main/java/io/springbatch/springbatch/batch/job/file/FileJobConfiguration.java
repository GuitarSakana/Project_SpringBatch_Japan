package io.springbatch.springbatch.batch.job.file;

import io.springbatch.springbatch.batch.chunk.proccessor.FileItemProcessor;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
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
                .processor(fileItemProcessor())//ProductVO를 Product로 변환
                .writer(fileItemWriter())//Product를 가지고 저장,처리
                .build();
    }

    //1. reader 동작 메소드 파일에 데이터들을 읽어 온다
    @Bean
    @StepScope
    public FlatFileItemReader <ProductVO>fileItemReader(
            @Value("#{jobParameters['requestDate']}") String requestDate)
    {
        return  new FlatFileItemReaderBuilder<ProductVO>()
                .name("flatFile")//Reader이름 지정
                .resource(new ClassPathResource("product_"+requestDate+".csv"))//읽을 파일 경로
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())//각행을 Java객체에 맵핑
                .targetType(ProductVO.class)//읽은 데이터를 ProductVO 타입으로 변환
                .linesToSkip(1)// 첫번째 라인은 헤더이므로 생략
                .delimited().delimiter(",")// 데이터가 구분자 형태고 쉼표로 구분한다는 의미
                .names("id","name","price","type")// 각열을 해당 이름으로 맵핑
                .build();
    }

    //2. processor 동작 메소드: 비즈니스 로직/데이터 형태 변환
    @Bean
    public ItemProcessor<ProductVO,Product> fileItemProcessor(){
        return new FileItemProcessor(); //새로운 클래스를 사용함
    }

    //3. writer 동작 메소드: 처리한 데이터를 저장, 또는 전송
    @Bean
    public ItemWriter<Product> fileItemWriter(){
        return new JpaItemWriterBuilder<Product>() //JPA 엔티티를 사용해서 데이터를 DB에 저장하는 용도
                .entityManagerFactory(em)
                .usePersist(true)
                .build();
    }


}
