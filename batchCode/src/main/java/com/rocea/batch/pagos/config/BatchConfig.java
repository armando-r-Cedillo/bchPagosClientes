package com.rocea.batch.pagos.config;



import com.rocea.batch.pagos.listener.PagoJobExecutionListener;
import com.rocea.batch.pagos.model.Pago;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Paths;

@EnableBatchProcessing
@Configuration
public class BatchConfig {



    @Bean
    @StepScope
    public FlatFileItemReader<Pago> pagoReader(  @Value("#{jobParameters['inputFile']}") String pathFile ) {
        return new FlatFileItemReaderBuilder<Pago>()
                .name("pagoReader")
                .resource(new FileSystemResource(pathFile))
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names("idPago", "cuenta", "importe", "fechaPago", "estatus")
                .targetType(Pago.class)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Pago> pagoWriter(@Value("#{jobParameters['outputFile']}") String pathFile) {
        return new FlatFileItemWriterBuilder<Pago>()
                .name("pagoWriter")
                .resource(new FileSystemResource(pathFile))
                .delimited()
                .delimiter(";")
                .names("idPago", "cuenta", "importe", "fechaPago", "estatus")
                .build();
    }



    @Bean
    public Step pagoStep(
            PlatformTransactionManager transactionManager,
            JobRepository jobRepository,
            ItemReader<Pago> pagoReader,
            ItemProcessor<Pago, Pago> pagoProcessor,
            ItemWriter<Pago> pagoClassifierWriter
    ) {
        return new StepBuilder("pagoStep", jobRepository)
                .<Pago, Pago>chunk(3).transactionManager(transactionManager)
                .reader(pagoReader)
                .processor(pagoProcessor)
                .writer(pagoClassifierWriter)
                .build();
    }

    @Bean
    public Job pagoJob(JobRepository jobRepository, Step pagoStep
            , PagoJobExecutionListener pagoJobExecutionListener) {
        return new JobBuilder("pagoJob", jobRepository)
                // .incrementer(new RunIdIncrementer())
                .start(pagoStep)
                .listener(pagoJobExecutionListener)
                .build();
    }
}

