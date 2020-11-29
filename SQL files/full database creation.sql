drop database if exists gps;
create database if not exists gps;
use gps;

drop user if exists 'userman';
create user 'userman'@'%' identified by 'random secure paswword';
grant all privileges on gps.* to 'userman';

drop table if exists Channel_User;
drop table if exists Message;
drop table if exists Channel;
drop table if exists User;

-- User ----------------------------------
create table if not exists User (
	id  int not null primary key auto_increment,
	username varchar(25) not null unique key,
	password_hash char(64) not null
);
-- Channel ----------------------------------
create table if not exists Channel (
    id int not null primary key auto_increment,
    creator_id int not null,
    name varchar(25) not null unique,

    foreign key (creator_id) references User(id) 
);
-- Channel_User ----------------------------------
create table if not exists Channel_User (
	channel_id int not null,
    user_id int not null,
    
    primary key (channel_id,user_id),
	foreign key (channel_id) references Channel(id) on delete cascade,
	foreign key (user_id) references User(id) on delete cascade
);
-- Message ----------------------------------
create table if not exists Message (
	id int not null primary key,
    sender_id int not null,
    channel_id int not null,
    moment_sent datetime not null default current_timestamp,
    type enum( 'text' , 'file' ) not null,
    content varchar(512) not null,
    
    foreign key (sender_id) references User(id),
    foreign key (channel_id) references Channel(id)
);
