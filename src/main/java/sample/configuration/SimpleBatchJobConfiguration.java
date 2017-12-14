package sample.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import sample.batch.RecordFieldSetMapper;
import sample.batch.domain.SimpleBatchJobSampleObject;

@Configuration
@PropertySource("classpath:application.properties")
public class SimpleBatchJobConfiguration {
	@Autowired JobBuilderFactory jobBuilderFactory;
	@Autowired StepBuilderFactory stepBuilderFactory;
	
	@Value("classpath:input/input.csv")
	private Resource inputCsv;
	
	@Value("file:${output-directory}/output.xml")
	private Resource outputXml;
	
	
	@Bean @Qualifier("simpleBatchJob") public Job simpeBatchJob() {
		return jobBuilderFactory.get("simpeBatchJob").start(simpleBatchJobStep()).build();
	}
	
	
	@Bean public ItemReader<SimpleBatchJobSampleObject> simpleJobItemReader(){
		FlatFileItemReader<SimpleBatchJobSampleObject> reader = new FlatFileItemReader<SimpleBatchJobSampleObject>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		String[] tokens = {"username", "userid"};
		tokenizer.setNames(tokens);
		reader.setResource(inputCsv);
		DefaultLineMapper<SimpleBatchJobSampleObject> lineMapper = new DefaultLineMapper<SimpleBatchJobSampleObject>();
		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new RecordFieldSetMapper());
		reader.setLineMapper(lineMapper);
		return reader;
	}
	
	@Bean Marshaller marshaller() {
		Jaxb2Marshaller bean = new Jaxb2Marshaller();
		bean.setClassesToBeBound(SimpleBatchJobSampleObject.class);
		return bean;
		
	}
	
	
	@Bean public ItemWriter<SimpleBatchJobSampleObject> simpleJobItemWriter(){
		StaxEventItemWriter<SimpleBatchJobSampleObject> bean = new StaxEventItemWriter<SimpleBatchJobSampleObject>();
		bean.setMarshaller(marshaller());
		bean.setRootTagName("users");
		bean.setResource(outputXml);
		bean.setOverwriteOutput(true);
		return bean;
	}
	
	@Bean public Step simpleBatchJobStep() {
		StepBuilder stepBuilder = stepBuilderFactory.get("simpleBatchJobStep");
		SimpleStepBuilder<SimpleBatchJobSampleObject, SimpleBatchJobSampleObject> simpleStepBuilder = stepBuilder.chunk(10);
		simpleStepBuilder.reader(simpleJobItemReader()).writer(simpleJobItemWriter()).chunk(10);
		return simpleStepBuilder.build();
		
	}
}
