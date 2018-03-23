use stock_performance;

create table UserInfo( user_id int auto_increment,
					first_name varchar(20) not null,
                    last_name varchar(20) not null,
					username varchar(100) not null,
                    salt varchar(100),
                    hashed_password varchar(100),
                    email varchar(500),
                    budget decimal(13,2),
                    start_date date,
                    end_date date,
					google_user tinyint(1) not null,
                    primary key(user_id));
                    
create table UserInvestment( investment_id int auto_increment,
                    user_id int not null,
                    symbol varchar(10),
                    number_of_shares double,
                    primary key(investment_id),
                    foreign key(user_id) references UserInfo(user_id));
                    
insert into UserInfo(first_name,last_name,username,salt,hashed_password,google_user) values ('phuong','tran','phuong', '$2a$10$YawZPDb7OLQ66FQuMCyW0e','$2a$10$YawZPDb7OLQ66FQuMCyW0e0d2r2Qd1kFLgHLhJqJwaypsQdnYX7fi',0);

-- number of tables(AAPL, MSFT...) increases when more symbols added to Symbols_table                         
create table Symbols( symbol_id int auto_increment not null,
						symbol varchar(10) not null,
                        primary key (symbol_id));



create table AAPL( ticker_id int auto_increment not null,
					price_date date not null,
                    price decimal(13,2) not null,
                    split_ratio double not null,
					symbol_id int not null,
                    primary key (ticker_id),
                    foreign key (symbol_id) references Symbols(symbol_id));
create table MSFT( ticker_id int auto_increment not null,
					price_date date not null,
                    price decimal(13,2) not null,
                    split_ratio double not null,
                    symbol_id int not null,
                    primary key (ticker_id),
                    foreign key (symbol_id) references Symbols(symbol_id));

