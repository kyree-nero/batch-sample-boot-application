package sample;

import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class SimpleBatchJobIT extends AbstractBatchIT{
	@Autowired @Qualifier("simpleBatchJob") Job simpleBatchJob;
	
	@Test public void test() throws Exception {
		JobExecution jobExecution = jobLauncher.run(simpleBatchJob, new JobParameters());
		Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		
		int userCount = 0;
		XMLInputFactory f = XMLInputFactory.newInstance();
		XMLStreamReader  streamReader= f.createXMLStreamReader(new FileReader("./target/output.xml"));
		while(streamReader.hasNext()) {
			 int eventType = streamReader.next();

			    if(eventType == XMLStreamReader.START_ELEMENT){
			    	if(streamReader.getLocalName().equals("user")){
			    		userCount++;
			    	}
			       
			    }

		   
		}
		
		Assert.assertEquals(14, userCount);
	}
}