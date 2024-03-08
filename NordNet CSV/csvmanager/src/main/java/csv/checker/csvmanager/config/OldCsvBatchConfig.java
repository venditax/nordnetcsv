package csv.checker.csvmanager.config;

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

import csv.checker.csvmanager.entity.OldCsv;
import csv.checker.csvmanager.processor.OldCsvProcessor;
import csv.checker.csvmanager.repository.OldCsvRepo;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class OldCsvBatchConfig {
	private OldCsvRepo oldCsvRepo;


    @Bean
    public FlatFileItemReader<OldCsv> reader() {
	 	FlatFileItemReader<OldCsv> itemReader = new FlatFileItemReader<>();
	    itemReader.setResource(new FileSystemResource("src/main/resources/old.csv"));
	    itemReader.setName("csvReader");
	    itemReader.setLinesToSkip(1);
	    itemReader.setLineMapper(lineMapper());
	    itemReader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
	    return itemReader;
    }

    private LineMapper<OldCsv> lineMapper() {
        DefaultLineMapper<OldCsv> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(";");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[]{"csvId","idFantoir","numero","rep","nomVoie","codePostal","codeInsee","nomCommune","codeInseeAncienneCommune", "nomAncienneCommune","x","y","lon","lat","typePosition","csvAlias","nomLd","libelleAcheminement","nomAfnor","sourcePosition","sourceNomVoie","certificationCo","itemNo"});
        
      
        BeanWrapperFieldSetMapper<OldCsv> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(OldCsv.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public OldCsvProcessor processor() {
        return new OldCsvProcessor();
    }

    @Bean
    public RepositoryItemWriter<OldCsv> writer() {
        RepositoryItemWriter<OldCsv> writer = new RepositoryItemWriter<>();
        writer.setRepository(oldCsvRepo);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step",jobRepository).
                <OldCsv, OldCsv>chunk(10000,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository,PlatformTransactionManager transactionManager) {
        return new JobBuilder("importCustomers",jobRepository)
                .flow(step1(jobRepository,transactionManager)).end().build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(64);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(64);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }
}
