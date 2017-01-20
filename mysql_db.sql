use db;

create table user_data(
    user_id int(11) primary key,
    username varchar(20),
    password varchar(20)
     );

create table user_saved_stock(
    user_id int(11),
    stocksymbol varchar(10),
    stockprice decimal(10,2),
    stockquantity int(5),
    FOREIGN KEY(user_id) REFERENCES user_data(user_id),
    FOREIGN KEY(stocksymbol) REFERENCES stock_for_cap(stocksymbol)
    );
    
create table stock_for_Cap(
    stocksymbol varchar(10) primary key,
    marketcap varchar(20)
    );
          
