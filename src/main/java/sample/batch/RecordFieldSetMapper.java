package sample.batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import sample.batch.domain.SimpleBatchJobSampleObject;


public class RecordFieldSetMapper implements FieldSetMapper<SimpleBatchJobSampleObject>{

	public SimpleBatchJobSampleObject mapFieldSet(FieldSet fieldSet) throws BindException {
		SimpleBatchJobSampleObject simpleBatchJobSampleObject = new SimpleBatchJobSampleObject();
		simpleBatchJobSampleObject.setUserid(fieldSet.readString(0));
		simpleBatchJobSampleObject.setUsername(fieldSet.readString(1));
		return simpleBatchJobSampleObject;
	}
	
}