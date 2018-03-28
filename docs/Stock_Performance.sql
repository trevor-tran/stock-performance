use stock_performance;

-- ################ TABLES CREATED ##################
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
create table Symbols( symbol_id int auto_increment not null,
						symbol varchar(5) not null,
                        ipo_date date,
                        delisting_date date,
                        primary key (symbol_id));



-- ############### PROCEDURE TO EXECUTE "SELECT" QUERY ################
delimiter $$
drop procedure if exists QUERY_DATA $$
create procedure QUERY_DATA( tableName varchar(5), startDate date, endDate date)
begin
	set @query = concat('SELECT t.date_as_id, s.symbol, t.price, t.split_ratio FROM ',tableName,' AS t 
						INNER JOIN Symbols AS s 
						WHERE t.symbol_id = s.symbol_id AND t.date_as_id BETWEEN ''',startDate, ''' AND ''', endDate,'''');
    prepare stmt from @query;
    execute stmt;
    deallocate prepare stmt;
    -- select @query;
end; $$
delimiter ;



-- ################ THIS PROCEDURE IS TO ADD NEW SYMBOL TO SYMBOLS TABLE ############
-- ################ AND CREATE NEW TABLE NAMED AFTER THE SYMBOL ###############
delimiter $$
drop procedure if exists ADD_TO_SYMBOLS_AND_CREATE_TABLE $$
create procedure ADD_TO_SYMBOLS_AND_CREATE_TABLE( ticker varchar(5))
begin
	-- add symbol to Symbols table
	set @addToSymbols = concat('INSERT INTO Symbols(symbol) VALUES(''', ticker, ''')');
	prepare stmt1 from @addToSymbols;
    execute stmt1;
    deallocate prepare stmt1;
    -- create table. name of the table is value of the ticker param
    set @createTable = concat('create table ',ticker,'( 
					date_as_id date not null,
                    price decimal(13,2) not null,
                    split_ratio double not null,
					symbol_id int not null,
                    primary key (date_as_id),
                    foreign key (symbol_id) references Symbols(symbol_id))');
    prepare stmt2 from @createTable;
    execute stmt2;
    deallocate prepare stmt2;
    
end; $$
delimiter ;




-- ################ FOLLOWING PROCEDURES ARE TO GET THE DATE PRIOR TO THE FIRST DATE IN TABLE##################
-- get date_as_id field from the first row in table, "ticker" is the name of table
delimiter $$
drop procedure if exists FIRST_DATE_IN_TABLE $$
create procedure FIRST_DATE_IN_TABLE(ticker varchar(5),inout firstDate date)
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
drop procedure if exists DIFF_TO_FIRST_DATE $$
create procedure DIFF_TO_FIRST_DATE(ticker varchar(5), dateInput date, inout diff int)
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
create procedure DATE_BEFORE_FIRST_DATE(ticker varchar(5), dateInput date)
begin
	call DIFF_TO_FIRST_DATE(ticker,dateInput,@diff);
    
    if @diff<0 then
		begin
			call FIRST_DATE_IN_TABLE(ticker,@firstDate);
			set @beforeFirstDate = subdate(@firstDate,1);
		end;
    else 
		set @beforeFirstDate = null;
    end if;

    select @beforeFirstDate;
end;$$
delimiter ;




-- ############# FOLLOWING PROCEDURES ARE TO GET THE DATE BEYOND TO THE LAST DATE IN TABLE#####################
-- get date_as_id field from the last row in table, "ticker" is the name of table
delimiter $$
drop procedure if exists LAST_DATE_IN_TABLE $$
create procedure LAST_DATE_IN_TABLE(ticker varchar(5),inout lastDate date)
begin
	-- get first date in the table
    set @query = concat('select date_as_id into @lastDate from ',ticker,' where date_as_id = (select max(date_as_id) from ',ticker,')');
    prepare stmt from @query;
	execute stmt;
	deallocate prepare stmt;
    set lastDate = @lastDate;
end;$$
delimiter ;

-- the number of days from dateInput to the date of last row in the table("ticker" is the name of table)
-- e.g. dateInput:2000-1-1, lastDate:2000-1-10 => diff is -9
delimiter $$
drop procedure if exists DIFF_TO_LAST_DATE $$
create procedure DIFF_TO_LAST_DATE(ticker varchar(5), dateInput date, inout diff int)
begin
	-- get last date in the table
	call LAST_DATE_IN_TABLE(ticker,@lastDate);
	set diff = datediff(dateInput, @lastDate);
end;$$
delimiter ;

-- if dateInput prior to first date in table, get the date before first date.
-- if not get null  
delimiter $$
drop procedure if exists DATE_AFTER_LAST_DATE $$
create procedure DATE_AFTER_LAST_DATE(ticker varchar(5), dateInput date)
begin
	call DIFF_TO_LAST_DATE(ticker,dateInput,@diff);
    
    if @diff>0 then
		begin
			call LAST_DATE_IN_TABLE(ticker,@lastDate);
			set @afterLastDate = adddate(@lastDate,1);
		end;
    else 
		set @afterLastDate = null;
    end if;

    select @afterLastDate;
end;$$
delimiter ;


-- ############ PROCEDURE TO UPDATE IPO AND DELISTING DATES################################
delimiter $$
drop procedure if exists UPDATE_IPO_DELISTING_DATE $$
create procedure UPDATE_IPO_DELISTING_DATE(ticker varchar(5))
begin
	CALL FIRST_DATE_IN_TABLE(ticker,@ipo);
    CALL LAST_DATE_IN_TABLE(ticker,@delisting);
    
    UPDATE symbols SET ipo_date=@ipo, delisting_date=@delisting WHERE symbol=ticker;
    
end;$$
delimiter ;






insert into UserInfo(first_name,last_name,username,salt,hashed_password,google_user) values ('phuong','tran','phuong', '$2a$10$YawZPDb7OLQ66FQuMCyW0e','$2a$10$YawZPDb7OLQ66FQuMCyW0e0d2r2Qd1kFLgHLhJqJwaypsQdnYX7fi',0);



