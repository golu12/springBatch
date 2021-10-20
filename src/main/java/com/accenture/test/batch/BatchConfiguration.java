package com.accenture.test.batch;

import com.accenture.test.Repository.CountryCodeRepository;
import com.accenture.test.Writer.FlatFileWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import com.accenture.test.Reader.FlatFileReader;
import com.accenture.test.Processor.FileProcessor;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class BatchConfiguration  {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public CountryCodeRepository codeRepository;

    @Bean
    @StepScope
    public  FlatFileReader reader(){
        return new FlatFileReader();
    }

    @Bean
    @StepScope
    public FileProcessor processor(){
        return new FileProcessor();
    }

    @Bean
    @StepScope
    public FlatFileWriter writer(){
        return new FlatFileWriter(codeRepository);
    }
    @Bean
    public Job processJob(@Qualifier("FlatFileStep") Step FlatFileStep) {
        return jobBuilderFactory.get("processJob")
                .incrementer(new RunIdIncrementer())
                .flow(FlatFileStep).end().build();
    }

    @Bean
    public Step FlatFileStep() {
        return stepBuilderFactory.get("FlatFileStep").chunk(60000)
                .reader(new FlatFileReader()).processor(new FileProcessor())
                .writer(new FlatFileWriter(codeRepository)).build();
    }



}