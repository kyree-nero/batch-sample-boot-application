package sample.batch.domain;

public class SimpleRemotePartitioningBatchJobOutputObject {
	private Long id;
	private String content;
	private String runGroupName;
	
	
	public String getRunGroupName() {
		return runGroupName;
	}
	public void setRunGroupName(String runGroupName) {
		this.runGroupName = runGroupName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
