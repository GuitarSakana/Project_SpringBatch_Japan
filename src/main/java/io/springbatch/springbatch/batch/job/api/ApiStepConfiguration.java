package io.springbatch.springbatch.batch.job.api;

import io.springbatch.springbatch.batch.chunk.proccessor.ApiItemProcessor1;
import io.springbatch.springbatch.batch.chunk.proccessor.ApiItemProcessor2;
import io.springbatch.springbatch.batch.chunk.proccessor.ApiItemProcessor3;
import io.springbatch.springbatch.batch.classifier.ProcessorClassifier;
import io.springbatch.springbatch.batch.domain.ApiRequestVO;
import io.springbatch.springbatch.batch.domain.Product;
import io.springbatch.springbatch.batch.domain.ProductVO;
import io.springbatch.springbatch.batch.partition.ProductPartitioner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApiStepConfiguration {

    private final DataSource dataSource;

    private int chunkSize = 10;

    @SneakyThrows
    @Bean
    public Step apiMasterStep(JobRepository jobRepository
            , PlatformTransactionManager platformTransactionManager
            , ItemReader itemReader) throws Exception{

        return new StepBuilder("apiMasterStep",jobRepository)
                .partitioner(apiSlaveStep(jobRepository,platformTransactionManager,itemReader).getName(),partitioner())// Partitioner를 설정하여 데이터를 분할 처리
                .step(apiSlaveStep(jobRepository,platformTransactionManager,itemReader))// 분할된 각 파티션에서 실행될 실제 Step 설정
                .gridSize(3)// 데이터 파티션의 개수 지정 (3개의 파티션 생성)
                .taskExecutor(taskExecutor())// 멀티스레딩 처리를 위한 TaskExecutor 설정
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        // ThreadPoolTaskExecutor: 스레드풀 기반으로 작업을 처리하는 TaskExecutor 구현체
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // setCorePoolSize: 기본적으로 유지되는 스레드 수를 설정 (스레드가 부족하지 않으면 추가 생성하지 않음)
        taskExecutor.setCorePoolSize(3);
        // 요청이 많아 스레드가 부족할 경우, corePoolSize를 초과하여 maxPoolSize까지 생성
        taskExecutor.setMaxPoolSize(6);
        // 스레드 이름이 "api-thread-1", "api-thread-2"와 같이 설정됨
        taskExecutor.setThreadNamePrefix("api-thread-");
        return taskExecutor;
    }

    @Bean
    public Step apiSlaveStep(JobRepository jobRepository
            , PlatformTransactionManager platformTransactionManager
            , ItemReader itemReader){
        return new StepBuilder("apiSlaveStep",jobRepository)
                .<ProductVO, ProductVO>chunk(chunkSize,platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ProductPartitioner partitioner(){
        ProductPartitioner productPartitioner = new ProductPartitioner();

        productPartitioner.setDataSource(dataSource);
        return productPartitioner;
    }


    @Bean
    @StepScope
    public ItemReader<ProductVO> itemReader(
            @Value("#{stepExecutionContext['product']}")ProductVO productVO)throws Exception{
        // @StepScope와 @Value를 사용하여,
        // 실행 중인 step의 ExecutionContext에서 'product' 값을 읽어와 ProductVO 객체를 전달합니다.

        // JdbcPagingItemReader는 데이터베이스에서 데이터를 페이지 단위로 읽어오는 구현체입니다.
        JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();

        // 데이터 소스를 설정합니다. 여기서 dataSource는 데이터베이스 연결을 관리하는 객체입니다.
        reader.setDataSource(dataSource);
        // 한 페이지에서 읽을 데이터의 양을 설정합니다.
        reader.setPageSize(chunkSize);
        // 쿼리 결과를 ProductVO 객체로 매핑합니다.
        // BeanPropertyRowMapper는 ResultSet의 각 행을 주어진 클래스 타입(ProductVO)으로 변환합니다.
        reader.setRowMapper(new BeanPropertyRowMapper(ProductVO.class));


        // MySqlPagingQueryProvider는 MySQL 데이터베이스에 특화된 페이지네이션 쿼리를 생성하는 클래스입니다.
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("where type =:type");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id",Order.DESCENDING);
        queryProvider.setSortKeys(sortKeys);

        // 쿼리 파라미터 값을 설정합니다. `QueryGenerator.getParameterForQuery`는
        // `type` 필드에 해당하는 값을 `productVO.getType()`에서 읽어와 설정합니다.
        // 여기서 'productVO.getType()'은 StepExecutionContext에서 전달된 값을 사용합니다.
        reader.setParameterValues(QueryGenerator.getParameterForQuery("type",productVO.getType()));
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();

        return reader;
    }

    @Bean
    public ItemProcessor itemProcessor(){

        ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO> processor
                = new ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO>();

        ProcessorClassifier<ProductVO,ItemProcessor<?,? extends ApiRequestVO>> classifier
                = new ProcessorClassifier();

        Map<String, ItemProcessor<ProductVO,ApiRequestVO>> processorMap = new HashMap<>();
        processorMap.put("1", new ApiItemProcessor1());
        processorMap.put("2", new ApiItemProcessor2());
        processorMap.put("3", new ApiItemProcessor3());

        classifier.setProcessorMap(processorMap);

        processor.setClassifier(classifier);
        return processor;
    }

}
