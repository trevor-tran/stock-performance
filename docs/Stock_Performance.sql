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
                    
-- number of tables(AAPL, MSFT...) increases when more symbols added to Symbols_table                         
create table Symbols( symbol_id int auto_increment not null,
						symbol varchar(10) not null,
                        primary key (symbol_id));

-- create procedure to get the date prior to the date in first row if startDate before firstDate
-- e.g. firstDate="2000-1-3" and startDate="1999-1-1". endDate would be "2000-1-3"
delimiter $$
drop procedure if exists END_DATE_OF_QUANDL_REQUEST $$
create procedure END_DATE_OF_QUANDL_REQUEST(in ticker varchar(10), in startDate date)
begin
	-- get first date in the table
	 set @query = concat('select date_as_id from ',ticker,' limit 1 into @firstDate');
     prepare stmt from @query;
     execute stmt;
     deallocate prepare stmt;
    -- the numbers of days from startDate to @firstDate
    set @diff = datediff(startDate, @firstDate);
    -- return the date prior to @firstDate if startDate before @firstDate
    set @endDate = if(@diff<0, subdate(@firstDate,1), "");
    select @endDate;
end;$$
delimiter ;



insert into UserInfo(first_name,last_name,username,salt,hashed_password,google_user) values ('phuong','tran','phuong', '$2a$10$YawZPDb7OLQ66FQuMCyW0e','$2a$10$YawZPDb7OLQ66FQuMCyW0e0d2r2Qd1kFLgHLhJqJwaypsQdnYX7fi',0);
insert into symbols(symbol) values('msft');
create table MSFT( 
					date_as_id date not null,
                    price decimal(13,2) not null,
                    split_ratio double not null,
					symbol_id int not null,
                    primary key (date_as_id),
                    foreign key (symbol_id) references Symbols(symbol_id));


select * from msft;



