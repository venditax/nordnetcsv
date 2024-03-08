package csv.checker.csvmanager.config;


import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import csv.checker.csvmanager.entity.NewCsv;
import csv.checker.csvmanager.processor.NewCsvProcessor;
import csv.checker.csvmanager.repository.NewCsvRepo;


@Configuration
@AllArgsConstructor
public class NewCsvBatchConfig {

    private NewCsvRepo newCsvRepo;

    @Bean
    public FlatFileItemReader<NewCsv> reader1() {
        FlatFileItemReader<NewCsv> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/new.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper1());
        itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
        return itemReader;
    }

    private LineMapper<NewCsv> lineMapper1() {
        DefaultLineMapper<NewCsv> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(";");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[]{"csvId","idFantoir","numero","rep","nomVoie","codePostal","codeInsee","nomCommune","codeInseeAncienneCommune", "nomAncienneCommune","x","y","lon","lat","typePosition","csvAlias","nomLd","libelleAcheminement","nomAfnor","sourcePosition","sourceNomVoie","certificationCo","itemNo"});
      
        BeanWrapperFieldSetMapper<NewCsv> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(NewCsv.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public NewCsvProcessor processor1() {
        return new NewCsvProcessor();
    }

    @Bean
    public RepositoryItemWriter<NewCsv> writer1() {
        RepositoryItemWriter<NewCsv> writer = new RepositoryItemWriter<>();
        writer.setRepository(newCsvRepo);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step",jobRepository).
                <NewCsv, NewCsv>chunk(10000,transactionManager)
                .reader(reader1())
                .processor(processor1())
                .writer(writer1())
                .taskExecutor(taskExecutor1())
                .build();
    }

    @Bean
    public Job runJob1(JobRepository jobRepository,PlatformTransactionManager transactionManager) {
        return new JobBuilder("newCsvImport",jobRepository)
                .flow(step2(jobRepository,transactionManager)).end().build();
    }

    @Bean
    public TaskExecutor taskExecutor1() {
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(64);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(64);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }

}
