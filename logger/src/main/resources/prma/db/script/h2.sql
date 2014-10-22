
--CREATE SCHEMA IF NOT EXISTS 'PRMA_Log_Event'

-- fully cached
CREATE TABLE IF NOT EXISTS `thread_name` (
  id				    INT4 	    NOT NULL,     -- hash of name
  `value`        VARCHAR(255)  NOT NULL,
  PRIMARY KEY (id)
);

-- fully cached
CREATE TABLE IF NOT EXISTS `logger_name` (
  id				    INT4 	    NOT NULL,    -- hash of value
  `value`        VARCHAR(255)  NOT NULL,
  PRIMARY KEY (id)
);

-- fully cached
CREATE TABLE IF NOT EXISTS `exception_name` (
  id				    INT4 	    NOT NULL,    -- hash of value
  `value`        VARCHAR(255)  NOT NULL,
  PRIMARY KEY (id)
);


-- partial cached
CREATE TABLE IF NOT EXISTS `exception_msg` (
  id				    INT4 	    NOT NULL,    -- hash of value
  `value`        VARCHAR(255)  NOT NULL,
  PRIMARY KEY (id)
);


-- partial cached
CREATE TABLE IF NOT EXISTS `stack_trace` (
  id				    INT4 	        NOT NULL,   -- hash of all caller data
  `file`        VARCHAR(255)  NOT NULL,
  `class`       VARCHAR(255)  NOT NULL,
  `function`    VARCHAR(255)  NOT NULL,
  `line`        VARCHAR(255)  NOT NULL,

  PRIMARY KEY (id)
);

-- partial cached
CREATE TABLE IF NOT EXISTS `exception` (
  id				    INT8          NOT NULL,    -- hash of name
  except_name   INT4          NOT NULL,
  except_msg    INT4          NOT NULL,
  stack_traces  BINARY(255)   NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS `event` (
	id				    IDENTITY 	    NOT NULL,
	time_created 	INT8 		      NOT NULL,
	level 			  TINYINT		    NOT NULL,
	logger_id		  INT4		      NOT NULL,
	thread_id		  INT4		      NOT NULL,
  caller_sk_id  INT4		      NOT NULL, -- caller stack trace
	flag			    TINYINT		    NOT NULL,

	message			  VARCHAR(511),

  PRIMARY KEY (id),
  FOREIGN KEY (logger_id) REFERENCES logger_name(id),
  FOREIGN KEY (thread_id) REFERENCES thread_name(id),
  FOREIGN KEY (caller_id) REFERENCES caller(id)
);


-- partial cached
CREATE TABLE IF NOT EXISTS `property` (
  id				    INT4 	        NOT NULL,      -- hash of key
  `key`         VARCHAR(255)  NOT NULL,
  `value`       BINARY(1073741823) NOT NULL, -- MAX 1 MB,
  PRIMARY KEY (id)
);

-- partial cached
CREATE TABLE IF NOT EXISTS `r_event_prop` (
  event_id    INT8        NOT NULL,
  prop_id     INT4        NOT NULL,
  PRIMARY KEY (event_id, prop_id)
);


CREATE TABLE IF NOT EXISTS `r_event_exception`(
  seq         SMALLINT    NOT NULL,
  event_id    INT8        NOT NULL,
  except_id   INT8        NOT NULL,

  PRIMARY KEY (seq, event_id)
);



-- ALTER TABLE  `event` SET REFERENTIAL_INTEGRITY FALSE;