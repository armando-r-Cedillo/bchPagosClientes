package com.rocea.batch.pagosclientes.config;

import com.rocea.batch.pagosclientes.listener.PagoJobExecutionListener;
import com.rocea.batch.pagosclientes.model.Pago;
import com.rocea.batch.pagosclientes.model.PagoProcesado;
import com.rocea.batch.pagosclientes.processor.PagoProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
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

    private Log logger = LogFactory.getLog(getClass());

    @Bean
    @StepScope
    public FlatFileItemReader<Pago> PagoReader(   @Value("#{jobParameters['inputFile']}") String pathFile) {
        return new FlatFileItemReaderBuilder<Pago>()
                .name("PagoReader")
                .resource(new FileSystemResource(pathFile))
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names("idPago", "cuenta", "importe","fechaPago","estatus")
                .targetType(Pago.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<PagoProcesado> pagosCorrectosWriter() {
        return new FlatFileItemWriterBuilder<PagoProcesado>()
                .name("pagosCorrectosWriter")
                .resource(new FileSystemResource("output/pagos_correctos.csv"))
                .delimited()
                .delimiter(";")
                .names("idPago", "cuenta", "importe","fechaPago","estatus","esFormatoCorrecto","detalleError")
                .build();
    }

    @Bean
    public FlatFileItemWriter<PagoProcesado> pagosIncorrectosWriter() {

        return new FlatFileItemWriterBuilder<PagoProcesado>()
                .name("pagosIncorrectosWriter")
                .resource(new FileSystemResource("output/pagos_incorrectos.csv"))
                .delimited()
                .delimiter(";")
                .names("idPago", "cuenta", "importe","fechaPago","estatus","esFormatoCorrecto","detalleError")
                .build();

    }

    @Bean
    public JdbcBatchItemWriter<PagoProcesado> pagosCorrectosJDBCWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<PagoProcesado>()
                .dataSource(dataSource)
                .sql("""
                 INSERT INTO pagos (
                     cuenta,
                     importe,
                     fecha_pago,
                     estatus
                 )
                 VALUES (
                     :cuenta,
                     :importe,
                     :fechaPago,
                     :estatus
                 )
                 """)
                .beanMapped()
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<PagoProcesado> pagosIncorrectosJDBCWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<PagoProcesado>()
                .dataSource(dataSource)
                .sql("""
                 INSERT INTO pagos_error (
                     cuenta,
                     importe, 
                     fecha_pago,
                     estatus,
                     esFormatoCorrecto,
                     detalleError
                 )
                 VALUES (
                     :cuenta,
                     :importe,
                     :fechaPago,
                     :estatus,
                     :esFormatoCorrecto,
                     :detalleError
                 )
                 """)
                .beanMapped()
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<PagoProcesado> pagoClassifierWriter(
            //&FlatFileItemWriter<PagoProcesado> pagosIncorrectosWriter,
            JdbcBatchItemWriter<PagoProcesado> pagosCorrectosJDBCWriter ,
            JdbcBatchItemWriter<PagoProcesado> pagosIncorrectosJDBCWriter
    ) {
        ClassifierCompositeItemWriter<PagoProcesado> writer =
                new ClassifierCompositeItemWriter<>();

        writer.setClassifier(pagoProcesado -> {
            if (pagoProcesado.isEsFormatoCorrecto()) {
                return pagosCorrectosJDBCWriter;
            }

            return pagosIncorrectosJDBCWriter;
        });

        return writer;
    }

    @Bean
    public Step pagoStep(
            PlatformTransactionManager transactionManager,
            JobRepository jobRepository,
            FlatFileItemReader<Pago> pagoReader,
            PagoProcessor pagoProcessor,
            ClassifierCompositeItemWriter<PagoProcesado> pagoClassifierWriter//,
           // FlatFileItemWriter<PagoProcesado> pagosIncorrectosWriter
    ) {
        return new StepBuilder("PagoClientesStep", jobRepository)
                .<Pago, PagoProcesado>chunk(3).transactionManager(transactionManager)
                .reader(pagoReader)
                .processor(pagoProcessor)
                .writer(pagoClassifierWriter)
               // .stream(pagosIncorrectosWriter)
                .build();
    }

    @Bean
    public Job PagoJob(
            JobRepository jobRepository,
            Step PagoStep,
            PagoJobExecutionListener pagoJobExecutionListener
    ) {
        return new JobBuilder("PagoClientesJob", jobRepository)
                //.incrementer(new RunIdIncrementer())
                .start(PagoStep)
                //.next(PagoStep)
                .listener(pagoJobExecutionListener)
                .build();
    }

    public ClassPathResource getPagoClassPath() {

        ClassPathResource path = new ClassPathResource("Pagos.csv");

        System.out.println(path.getPath());
        return path;
    }


    public FileSystemResource getPagoRelativePath(){
        FileSystemResource path = new FileSystemResource(Paths.get("/CLIENTES/FILES/INPUT/Pagos.csv"));
        logger.info(path.getPath());
        return path;
    }

}