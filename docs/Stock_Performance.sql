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




-- get date_as_id field from the first row in table, "ticker" is the name of table
delimiter $$
drop procedure if exists FIRST_DATE_IN_TABLE $$
create procedure FIRST_DATE_IN_TABLE(ticker varchar(10),inout firstDate date)
begin
	-- get first date in the table
	set @query = concat('select date_as_id into @firstDate from ', ticker, ' limit 1');
    prepare stmt from @query;
	execute stmt;
	deallocate prepare stmt;
    set firstDate = @firstDate;
end;$$
delimiter ;



-- the number of days from dateInput to the date of first row in the table("ticker" is the name of table).
-- e.g. dateInput:2000-1-1, firstDate:2000-1-10 => diff is -9
delimiter $$
drop procedure if exists DIFF_FROM_FIRST_DATE $$
create procedure DIFF_FROM_FIRST_DATE(ticker varchar(10), dateInput date, inout diff int)
begin
	-- get first date in the table
	call FIRST_DATE_IN_TABLE(ticker,@firstDate);
	set diff = datediff(dateInput, @firstDate);
end;$$
delimiter ;


-- if dateInput prior to first date in table, get the date before first date.
-- if not get null  
delimiter $$
drop procedure if exists DATE_BEFORE_FIRST_DATE $$
create procedure DATE_BEFORE_FIRST_DATE(ticker varchar(10), dateInput date)
begin
	call DIFF_FROM_FIRST_DATE(ticker,dateInput,@diff);
    
    if @diff<0 then
		begin
			call FIRST_DATE_IN_TABLE(ticker,@firstDate);
			set @dateBefore = subdate(@firstDate,1);
		end;
    else 
		set @dateBefore = null;
    end if;

    select @dateBeforeFirst;
end;$$
delimiter ;

call DATE_BEFORE_FIRST_DATE('msft','1993-01-01');









CALL END_DATE_OF_QUANDL_REQUEST('msft', '2017-1-1');


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



