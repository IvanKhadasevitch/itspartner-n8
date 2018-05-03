# drop database light_test;

create database light_test;

# show databases;

use light_test;

--
-- Комната с лампочкой
--
create table Room (
  id 			bigint not null auto_increment, -- pk
  `name`	 	varchar(30) not null unique, -- название
  light_state 	boolean not null, -- состояния света в комнате: горит (true) или не горит (false)
  primary key (id)
) engine=InnoDB;

describe Room;