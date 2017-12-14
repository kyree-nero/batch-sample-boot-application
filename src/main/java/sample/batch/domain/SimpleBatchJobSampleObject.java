package sample.batch.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user")
public class SimpleBatchJobSampleObject {
	private String username;
	private String userid;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
}
