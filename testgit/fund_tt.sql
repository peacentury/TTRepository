create database Jiang;
use Jiang;

create table FUNDS_TT
(
  fund_code               VARCHAR(16),
  fund_name               VARCHAR(128),
  fund_type               VARCHAR(16),
  val_date                VARCHAR(16),
  val            		  DECIMAL(16,4) default 0 ,
  today_day               DECIMAL(16,2) default 0 ,
  lastest_week            DECIMAL(16,2) default 0 ,
  lastest_month           DECIMAL(16,2) default 0 ,
  lastest_3month          DECIMAL(16,2) default 0 ,
  lastest_6month          DECIMAL(16,2) default 0 ,
  lastest_year            DECIMAL(16,2) default 0 ,
  lastest_2year           DECIMAL(16,2) default 0 ,
  lastest_3year           DECIMAL(16,2) default 0 ,
  this_year               DECIMAL(16,2) default 0 ,
  his                     DECIMAL(16,2) default 0 
);
create unique index Index_UQ1 on FUNDS_TT (fund_code,fund_type,val_date);
SELECT *
  FROM FUNDS_TT t
 WHERE 1 = 1
   and t.lastest_3year > t.lastest_2year*1.2
   and t.lastest_2year > t.lastest_year*1.2
   and t.lastest_year > 0
   and t.this_year > 0
   and t.his > t.lastest_3year
   and t.lastest_6month > 0
   and t.lastest_3month > 0
   and t.val > 1
   and t.lastest_3year > 100
   and (t.fund_name like '%易方达%' or t.fund_name like '%富国%'or t.fund_name like '%广发%'or t.fund_name like '%南方%'or t.fund_name like '%华夏%' or t.fund_name like '%汇添富%')
   and val_date='2021-11-25'
 order by t.this_year desc, t.his desc;
  
SELECT *
  FROM FUNDS_TT t
 WHERE 1 = 1
   and t.lastest_3year > t.lastest_2year*1.2
   and t.lastest_2year > t.lastest_year*1.2
   and t.lastest_year > 0
   and t.this_year > 0
   and t.lastest_6month > 0
   and t.lastest_3month > 0
   and t.val > 1
   and t.lastest_3year > 100
   and (t.fund_name like '%易方达%' or t.fund_name like '%富国%'or t.fund_name like '%广发%'or t.fund_name like '%南方%'or t.fund_name like '%华夏%')
   and val_date='2021-10-28'
 order by t.this_year desc, t.his desc;
 
select *  from FUNDS_TT where fund_code in ('005760','110035','161131','000171','110017','506003','001513','001510','001018','000621') and val_date='2021-11-19';
select *  from FUNDS_TT where fund_code in ('000621');
select max(val_date) from FUNDS_TT where fund_code='001510';
select * from FUNDS_TT where fund_code='001510';
delete from FUNDS_TT where val_date='2021-11-23';
SET SQL_SAFE_UPDATES = 0;
select distinct(fund_type) from FUNDS_TT ;


truncate table funds_tt;


drop table shares;
create table shares
(
  fund_code               VARCHAR(16),
  fund_name               VARCHAR(128),
  fund_vol                DECIMAL(16,2) default 0 ,
  current_cost            DECIMAL(16,2) default 0 ,
  market_value			  DECIMAL(16,2) default 0 ,
  update_date             VARCHAR(16),
  is_holding			  VARCHAR(16) default 1 
);
create unique index INDEX_SHARES on shares (fund_code,update_date);

select * from shares where is_holding=1 and update_date='2021-11-18';
select distinct fund_code from shares where is_holding=1;
select update_date,sum(market_value),sum(current_cost),sum(market_value)-sum(current_cost) from shares where is_holding=1 group by update_date;

INSERT INTO `shares`(`fund_code`,`fund_name`,`fund_vol`,`current_cost`,`market_value`,`update_date`)VALUES
('001018','易方达新经济混合',43663.34,173951.35,173951.35,'2021-11-18'),
('110017','易方达增强回报债券A',111078.77,149500,149500,'2021-11-18'),
('000171','易方达裕丰回报债券',37927.77,70000,70000,'2021-11-18'),
('001513','易方达信息产业混合',14634.6,29453.65,20000,'2021-11-18'),
('110035','易方达双债增强债券A',31036.62,50000,50000,'2021-11-18'),
('161131','易方达科润lof',31851.72,32032.68,32032.68,'2021-11-18'),
('001510','新动力灵活配置C',28603.85,110000,110000,'2021-11-18'),
('506003','富国科创板两年定开',46100.47,46651.56,46651.56,'2021-11-18'),
('005760','富国周期优势混合',6541.93,20000,20000,'2021-11-18');

drop table fund_his_val;
create table fund_his_val
(
  fund_code               VARCHAR(16),
  fund_name               VARCHAR(128),
  val_date                VARCHAR(16),
  cell_val                DECIMAL(16,4) default 0,
  acc_val                DECIMAL(16,4) default 0,
  day_val_change         DECIMAL(16,4) default 0,
  buy_status               VARCHAR(128), 
  sell_status              VARCHAR(128)
);
create unique index INDEX_FUND_HIS_VAL on fund_his_val (fund_code,val_date);

select count(1),10000/cell_val from fund_his_val where instr(val_date,'-17')>0 and fund_code='001018';
select sum(10000/cell_val)*4.4420  from fund_his_val where instr(val_date,'-17')>0 and fund_code='001018';
select * from fund_his_val where  fund_code='001018' order by val_date desc;

001018
t  57   154.9  2.718     
t3 183  502.2  2.744
t4 113  307.5  2.721

005760
   29   54.8   1.891
   100  204.1  2.041
   62   126.2  2.035
select count(*) from fund_his_val where instr(val_date,'-18')>0 and fund_code='001018';

select  * from fund_his_val where fund_code='001018' and val_date<'2016-00-00' order by val_date;

truncate table fund_his_val;