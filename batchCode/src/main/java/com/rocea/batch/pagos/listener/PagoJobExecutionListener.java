package com.rocea.batch.pagos.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class PagoJobExecutionListener implements JobExecutionListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
        logger.info("Iniciando Pago Job");
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionListener.super.afterJob(jobExecution);
        logger.info("Finaliza Pago Job");
    }


}
