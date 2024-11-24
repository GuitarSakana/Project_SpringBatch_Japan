package io.springbatch.springbatch.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution){

    }
    @Override
    public void afterJob(JobExecution jobExecution){
        LocalDateTime startTime = jobExecution.getStartTime();
        LocalDateTime endTime = jobExecution.getEndTime();

        long time = ChronoUnit.MILLIS.between(startTime, endTime);
        System.out.println("총 소요시간 = "+time);

    }
}
