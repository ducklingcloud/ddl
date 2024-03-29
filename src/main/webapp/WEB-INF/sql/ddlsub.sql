--
-- for Derby <2022-03-03 Thu>
--

CREATE TABLE a1_auth_code (
  id int NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 64),
  code varchar(45) NOT NULL,
  access_token varchar(45) NOT NULL,
  uid varchar(45) NOT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  client_id varchar(45) DEFAULT NULL,
  status varchar(15) DEFAULT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX auth_code_index ON a1_auth_code (code);

CREATE TABLE jobs (
  Id int NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 64),
  job_name varchar(255) DEFAULT NULL,
  PRIMARY KEY (Id)
);
CREATE INDEX job_name ON jobs (job_name);

CREATE TABLE vec4user (
  id int NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 64),
  passport varchar(50) NOT NULL,
  uservector varchar(5000) NOT NULL,
  appid int NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX index_uservec ON vec4user (passport,appid);

-- Dump completed on 2016-06-05  7:48:01
