# drop database light;

create database light;

# show databases;

use light;

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
--
-- insert Rooms
--
insert into Room set 
	`name`		= 'Room #1',
    light_state	= '1';
insert into Room set 
	`name`		= 'Room #2',
    light_state	= '0';
insert into Room set 
	`name`		= 'Room #3',
    light_state	= '0';   
insert into Room set 
	`name`		= 'Room #4',
    light_state	= '1';    
insert into Room set 
	`name`		= 'Room #5',
    light_state	= '1'; 
insert into Room set 
	`name`		= 'Room #6 A',
    light_state	= '0'; 
insert into Room set 
	`name`		= 'Room #7 B',
    light_state	= '1';     
    