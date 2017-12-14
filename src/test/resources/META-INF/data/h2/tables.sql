
CREATE TABLE BATCH_SAMPLE (
	 id bigint auto_increment not null  primary key , 
	 content VARCHAR(100) not null,
	 version INT not null
); 

CREATE TABLE BATCH_SAMPLE_OUTPUT (
	 id bigint auto_increment not null  primary key , 
	 content VARCHAR(100) not null,
	 run_id VARCHAR(100) not null,
	 version INT not null default 0
); 


COMMIT;