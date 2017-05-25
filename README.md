# Simple DBMS

2016-2 데이타베이스 강좌의 프로젝트로 개발했던 Simple DBMS입니다.


## Usage

프로그램을 실행하려면 Simple_DBMS.jar이 있는 경로에서 `java -jar Simple_DBMS.jar` 명령어를 입력한다. 이 때 같은 경로에 db라는 이름의 폴더가 반드시 있어야 한다.


## Supported SQL Syntax

* 모든 SQL문은 세미콜론(';')으로 끝나야 한다.

### DDL (Data Definition Language)

#### CREATE TABLE

```SQL
create table table_name (
  column_name data_type [not null],
  ...
  primary key(column_name1, column_name2, ...),
  [foreign key(column_name3) references table_name1(column_name4),]
  [foreign key(column_name5) references table_name2( column_name6)]
  ...
);
```
* table_name, column_name은 alphabet과 '_'으로만 이루어져야 한다.

**example:**

```sql
create table account (
  account_number int not null,
  branch_name char(15),
  primary key(account_number)
);
```



#### DROP TABLE

```sql
drop table table_name;
```

**example:**

```sql
drop table account;
```



#### DESC

```sql
desc table_name;
```

**example:**

```sql
desc account;
```



#### SHOW TABLES

```sql
show tables;
```

**example:**

```sql
show tables;
```



### DML (Data Manipulation Language)

#### INSERT

```sql
insert into table_name [(col_name1, col_name2, … )] values(value1, value2, …);
```

**example:**

```sql
insert into account values(9732, 'Perryridge');
```



#### DELETE

```sql
delete from table_name [where clause];
```

**example:**

```sql
delete from account where branch_name = 'Perryridge';
```



#### SELECT

```sql
select [table_name.]column_name [as name], ...
from table_name [as name], ...
[where clause];
```

**example:**

```sql
select customer_name, borrower.loan_number, amount
from borrower, loan
where borrower.loan_number = loan.loan_number and branch_name = 'Perryridge';
```

