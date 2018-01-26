use stock_performance;
create table users( user_id int auto_increment,
					first_name varchar(20) not null,
                    last_name varchar(20) not null,
					username varchar(100) not null,
                    salt varchar(100),
                    hashed_password varchar(100),
                    email varchar(500),
                    investment decimal(13,2),
                    start_date date,
                    end_date date,
                    primary key(user_id));
                    
create table stocks( stock_id int auto_increment,
					symbol varchar(10) not null,
                    user_id int not null,
                    number_of_shares double,
                    primary key(stock_id),
                    foreign key(user_id) references users(user_id));
                    
insert into users(first_name,last_name,username,salt,hashed_password) values ('phuong','tran','phuong', '$2a$10$YawZPDb7OLQ66FQuMCyW0e','$2a$10$YawZPDb7OLQ66FQuMCyW0e0d2r2Qd1kFLgHLhJqJwaypsQdnYX7fi');
