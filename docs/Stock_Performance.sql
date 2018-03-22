use stock_performance;

-- https://stackoverflow.com/questions/7296846/how-to-implement-one-to-one-one-to-many-and-many-to-many-relationships-while-de
create table StockSymbol( symbol_id int auto_increment not null,
						symbol varchar(10) not null,
                        primary key (symbol_id));
                        
create table StockPrice( price_id int auto_increment not null,
						price_date date not null,
                        price decimal(13,2) not null,
                        split_ratio double not null,
                        primary key (price_id));
                        
create table StockSymbolStockPrice( symbol_price_id int auto_increment not null,
							price_id int not null,
                            symbol_id int not null,
                            primary key (symbol_price_id),
                            foreign key ( price_id) references StockPrice(price_id),
                            foreign key ( symbol_id) references StockSymbol(symbol_id));

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



                            