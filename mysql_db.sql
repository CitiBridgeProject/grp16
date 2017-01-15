use db;

create table login(
    user_id int(11) primary key,
    username varchar(20),
    password varchar(20)
     );

create table user_saved_stock(
    user_id int(11),
    stockname varchar(100),
    stocksymbol varchar(10),
    stockprice decimal(10,2),
    stockquantity int(5),
    FOREIGN KEY(user_id) REFERENCES login(user_id)
    );
    
    insert into login values(1,"mohit","citi");
    insert into login values(2,"munir","citi");
    insert into login values(3,"tejal","citi");
    insert into login values(4,"sneha","citi");
    insert into login values(5,"apoorva","citi");
    insert into login values(6,"z","citi");
    
    select * from login;
    
    select * from user_saved_stock;
    
