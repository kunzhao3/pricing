DROP DATABASE IF EXISTS db_gmcf_csc;
CREATE DATABASE db_gmcf_csc CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
USE db_gmcf_csc;
DROP TABLE IF EXISTS t_csc_merchant_member;
create table t_csc_merchant_member
(
    f_id               bigint auto_increment comment 'ID主键'
        primary key,
    f_merchant_type    tinyint                               not null comment '商户类型(1=三方支付/2=资金方/3=平台方/4=担保方/5=保险/6=渠道方/7=反担保方/9=虚拟资方编码(支付用于配置渠道权益提供商支付通道))',
    f_merchant_code    varchar(64) default ''                not null comment '商户code',
    f_merchant_no      varchar(256)                          not null comment '机构编号',
    f_member_name      varchar(64)                           not null comment '会员名称',
    f_member_type      tinyint                               not null comment '会员类型(1=个人/2=机构/3=平台)',
    f_member_no        char(32)                              not null comment '会员编号',
    f_created_time     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time    datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    f_allow_pre_settle char        default '0'               null comment '是否支持提前结清标志 0-未知(默认) 1-支持 2-不支持',
    f_member_fullname  varchar(64) default ''                not null comment '会员名全称',
    f_created_user     varchar(128)                          null comment '创建人',
    f_modified_user    varchar(128)                          null comment '更新人',
    constraint f_member_no
        unique (f_member_no)
)
    comment '商户会员表';

create index idx_merchanttype_merchantcode
    on t_csc_merchant_member (f_merchant_type, f_merchant_code);

create index idx_merchanttype_merchantno
    on t_csc_merchant_member (f_merchant_type, f_merchant_no);

INSERT INTO t_csc_merchant_member
(f_merchant_type, f_merchant_code, f_merchant_no, f_member_name, f_member_type, f_member_no, f_allow_pre_settle, f_member_fullname, f_created_user, f_modified_user)
VALUES
(2, 'C5024', '56a062937eb84ddd8a2f58807d4f667b', '西安银行', 2, '41a753f0a8ce4b678642707375a340a4',  '0', '西安银行股份有限公司', '系统', '系统'),
(2, 'C5048', '09ea4c9129a846229bac68bc33af193f', '蓝海银行(实担)', 2, '1734677d581d4aec9143927af2db22d6',  '1', '威海蓝海银行股份有限公司', '系统', '系统'),
(2, 'C5056', 'd2e24155aaa04ba5b5e7adf8264592fd', '京东云工厂(中原消金)', 2, '30b7fae3c58d4108a9ad40dd4769438c', '2', '际晖信息服务有限公司', '系统', '系统'),
(2, 'C5059', 'f7c252a070a748ea805cb5f08c97fc56', '小米消金(分润)', 2, 'eabcd627cae947b5bd6765d6e3642f68',  '0', '重庆小米消费金融有限公司', '系统', '系统'),
(4, 'G0009', '8f4e2e5e57ac4d40a7babbdebd505791', '博盛安融担保', 2, '08476f844d354af9ae965f9f19b142d0',  '0', '', '系统', '系统'),
(7, 'G0012', '648dd10d80604aacb60d2cca5feae1c4', '青岛鑫鑫向荣资产管理有限公司', 2, '840f0dd139aa4ad4922a6c751aa80a86',  '0', '', '系统', '系统')
;
