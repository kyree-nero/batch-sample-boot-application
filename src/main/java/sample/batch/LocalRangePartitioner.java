package sample.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class LocalRangePartitioner implements Partitioner{

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
		
		int range = 2;
		int fromId = 0;
		int toId = range;
		
		for(int i = 1; i <=gridSize; i++) {
			ExecutionContext executionContext = new ExecutionContext();
			
			executionContext.putInt("fromId", fromId);
			executionContext.putInt("toId", toId);
			executionContext.putString("groupName", "startsWith" +fromId);
			
			
			
			result.put("partition" + i, executionContext);
			
			System.out.println("partition" + i + ", fromId " + fromId + " toId  " + toId);
			
			fromId = toId + 1;
			toId += range;
		}
		
		return result;
	}

}
