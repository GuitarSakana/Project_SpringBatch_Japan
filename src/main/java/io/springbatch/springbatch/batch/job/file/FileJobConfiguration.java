package io.springbatch.springbatch.batch.job.file;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FileJobConfiguration {

    private final EntityManagerFactory entityManagerFactory;


}
