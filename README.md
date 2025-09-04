#### 一，目的

解决配置资方及资方要素，配置产品定价和资方定价所使用的定价编号，定价镜像编号，费项，公式编号以及费项公式相关属性，不用再手动书写SQL语句

#### 二，处理步奏

##### 1，新建模版Excel

"费项"页签中费项名称和费项编码做个二级关联;更新"费项说明"页签,费项名称会实时更新,防止手写错误;</br>
<font style="color:#DF2A3F;"> 注意：</font><font style="color:#117CEE;">"费项说明"页签费项只需要新增,不要做修改和删除</font></br>
[C5024.xlsx](https://www.yuque.com/attachments/yuque/0/2025/xlsx/56924506/1756366555881-31ac3520-5174-498e-8d31-b076217a933c.xlsx)</br>
[C5048.xlsx](https://www.yuque.com/attachments/yuque/0/2025/xlsx/56924506/1756366571683-b7b6793b-12d2-42ac-a7e0-0286f8004d8c.xlsx)</br>
[C5056.xlsx](https://www.yuque.com/attachments/yuque/0/2025/xlsx/56924506/1756366571701-ee65c81d-42c9-4f15-b02d-e5f94356c841.xlsx)</br>
[C5059-合并数据.xlsx](https://www.yuque.com/attachments/yuque/0/2025/xlsx/56924506/1756971336866-b5ec6b43-9988-4d98-9518-bc3a4ba23196.xlsx)</br>

![](https://cdn.nlark.com/yuque/0/2025/png/56924506/1756359238688-95151c9f-d937-47fa-bff0-0b9ec5bdea4d.png)

##### 2，新建两张表

###### 2.1，费项模版表

```sql
-- 基于费项和费项公式建立费项模版
DROP TABLE IF EXISTS t_pec_fee_template;
create table  t_pec_fee_template
(
  f_id            bigint auto_increment comment '主键'
  primary key,
  f_version       tinyint     default 0                 not null comment '版本号',
  f_template_no   char(32)    default ''                not null comment '模版编号',
  f_template_name char(128)   default ''                not null comment '模版名称',
  f_fee_code      varchar(64) default ''                not null comment '费用代码',
  f_fee_name      varchar(64) default ''                not null comment '费用名称',
  f_formula_no    char(32)    default ''                not null comment '公式编号',
  f_formula_name  varchar(64) default ''                not null comment '公式名称',
  f_created_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
  f_modified_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
  f_operater_name char(32)    default ''                not null comment '操作人',
  constraint uk_template_no_template_name_fee_code
  unique (f_template_no,f_template_name, f_fee_code)
)
    comment '费项模版表';
```

###### 2.2，资方模版表

```sql
-- 基于资方和代偿方建立资方模版
DROP TABLE IF EXISTS t_pec_capital_template;
create table  t_pec_capital_template
(
  f_id                          bigint auto_increment comment '主键'
  primary key,
  f_version                     tinyint      default 0                 not null comment '版本号',
  f_capital_type                tinyint      default -1                not null comment '类型 1-资方 2-代偿方',
  f_capital_code                varchar(32)  default ''                not null comment '资方/代偿方代码',
  f_match_target_code           varchar(64)  default ''                not null comment '匹配资方/代偿方代码,匹配多个资方/代偿方用逗号隔开',
  f_match_target_rank_level     char(20)     default ''                not null comment '匹配风险等级,匹配多个风险等级用逗号隔开',
  f_repayment_formula_no        char(32)     default ''                not null comment '本息计算公式编号',
  f_repayment_formula_name      varchar(64)  default ''                not null comment '本息计算公式名称',
  f_clearing_formula_no         char(32)     default ''                not null comment '资方/代偿方清分公式编号',
  f_clearing_formula_name       varchar(64)  default ''                not null comment '资方/代偿方清分公式名称',
  f_is_support_accelerate       char(1)      default '0'               not null comment '是否支持加速到期 0-否 1-是',
  f_repurchase_calculation_mode char(128)    default ''                not null comment '回购时重算 BY_DAY_ENTIRELY-合并后未还金额*日费率*实际占用天数 REPURCHASE_STAGE_ONLY-只包含回购期次费用 OVER_MUST_PAY_DATE_STAGE-已出账单日期次费用',
  f_fee_template_no             char(32)     default ''                not null comment '费项模版编号',
  f_fee_code                    varchar(128) default ''                not null comment '资方/代偿方费用代码,多个费用用逗号隔开',
  f_created_time                datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
  f_modified_time               datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
  f_operater_name               char(32)     default ''                not null comment '操作人',
  constraint uk_capital_code_match_target_code_clearing_formula_no
  unique (f_capital_code,f_match_target_code,f_clearing_formula_no)
)
    comment '资方模版表';
```

###### 2.3，两张表关系说明

资方模版表的f_fee_template_no(费项模版编号)关联费项模版表的f_template_no(模版编号);</br>
同一个费项模版关联多个资方模版

![](https://cdn.nlark.com/yuque/0/2025/png/56924506/1756353025144-16080411-3243-4ac9-a419-01ebe5b3be0b.png)

###### 2.4，先插入费项模版表,再插入资方模版表

```sql
INSERT INTO t_pec_fee_template
(f_template_no, f_template_name, f_fee_code, f_fee_name, f_formula_no, f_formula_name, f_operater_name) 
VALUES
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'GUARANTEE_FEE', '担保费', '9f8fac353dc14e7cb141522bcc90ff74', '联合收担保费计算公式2', 'PEC'),
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'SERVICE_FEE', '服务费', '750a032c443945e4a9892b48f8abdf65', '联合贷服务费公式2', 'PEC'),

('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'GUARANTEE_FEE_ONE', '担保费1', 'd62874f5e54c440282b01866f94c2409', '担保费计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'GUARANTEE_FEE_TWO', '担保费2', 'd62874f5e54c440282b01866f94c2409', '担保费计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'OVERDUE_LIQUIDATED_DAMAGES_ONE', '逾期违约金1', 'd653d0f56dad41aa8bc3072038135ab1', '蓝海逾期违约金1计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'OVERDUE_LIQUIDATED_DAMAGES_TWO', '逾期违约金2', '233f31f862914dd7be8a40024b5868d9', '蓝海逾期违约金2计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'SERVICE_FEE_ONE', '服务费1', '0f0d83705dec49f9af169d5a52b5e016', '服务费1计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'SERVICE_FEE_TWO', '服务费2', 'cc50725fe34e4a5296bfb3b359dfab32', '服务费2计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'ASSET_MANAGE_FEE_ONE', '资产管理费1', 'd62ce247c2454794b07b8f0e79fbb965', '资产管理费1', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'ASSET_MANAGE_FEE_TWO', '资产管理费2', '0329872cf33d47a598ffb739f2cc2559', '资产管理费2', 'PEC'),

('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'ASSET_MANAGE_FEE', '资产管理费', '21f21083a8b7451baade18f445b6b2ff', '资产管理费', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'GUARANTEE_FEE', '担保费', 'a242757aa7684cd1a3e8236c77daae35', '费项金额为0的公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'SERVICE_FEE', '服务费', '6a2cda7b6f3b45fc9426e0aa7f96acc6', '综合兜底费项公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'SERVICE_FEE_SETTLEMENT', '服务费结算', '', '服务费结算', 'PEC')
;
```

模版编号UUID生成,同一个模版下根据需求配置多个费项<font style="color:#DF2A3F;">(注意:不用配置本息计算费项即:f_fee_code='')</font>,每个费项对应公式编号和公式名称由FCC提供的

```sql
INSERT INTO t_pec_capital_template
(f_capital_type, f_capital_code, f_match_target_code, f_match_target_rank_level ,f_repayment_formula_no, f_repayment_formula_name, f_clearing_formula_no, f_clearing_formula_name, f_is_support_accelerate, f_repurchase_calculation_mode, f_fee_template_no, f_fee_code, f_operater_name)
VALUES
(1, 'C5024', 'G0012', 'A,B,C,D', '766bdbe4294c42a2bee62d06d15bc746', '西安债务债权公式', '766bdbe4294c42a2bee62d06d15bc746', '联合收清分公式', 1, 'REPURCHASE_STAGE_ONLY', '0d2f904761f24362bb7d0acba4a22351', 'GUARANTEE_FEE', 'PEC'),
(2, 'G0012', 'C5024', 'A,B,C,D', '766bdbe4294c42a2bee62d06d15bc746', '西安债务债权公式', '39002f22bf7e41fd9f4e30aa949d1d96', '联合收代偿清分公式', 1,'',  '0d2f904761f24362bb7d0acba4a22351', 'OVERDUE_LIQUIDATED_DAMAGES,PENALTY_INTEREST', 'PEC'),

(1, 'C5048', 'G0009', 'A', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', '766bdbe4294c42a2bee62d06d15bc746', '联合收清分公式', 1, '' ,'0d2f904761f24362bb7d0acba4a22351', 'GUARANTEE_FEE', 'PEC'),
(2, 'G0009', 'C5048', 'A', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', '6dc07c6c6c7241609302cdc3e6c03eb5', '实担模式代偿清分公式', 1, '' ,'0d2f904761f24362bb7d0acba4a22351', 'OVERDUE_LIQUIDATED_DAMAGES', 'PEC'),
(1, 'C5048', 'G0009,G0012', 'B,C,D', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', 'e2a3170b238b42cbbcc0f0fca9351215', '双代偿资方清分公式', 1, '' ,'7fb28bcde98540f4bfa1d4b11b031546', 'GUARANTEE_FEE_ONE,GUARANTEE_FEE_TWO', 'PEC'),
(2, 'G0009', 'C5048', 'B,C,D', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', 'dcf7e938b45c479489b279fa0c78b7de', '双代偿双融担代偿清分公式', 1,'' ,'7fb28bcde98540f4bfa1d4b11b031546', '', 'PEC'),
(2, 'G0012', 'C5048', 'B,C,D', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', 'dcf7e938b45c479489b279fa0c78b7de', '双代偿双融担代偿清分公式', 1,'' ,'7fb28bcde98540f4bfa1d4b11b031546', '', 'PEC'),

(1, 'C5056', 'G0012', 'A', 'db1f52bdd6064a92ab7c2fd23714f4a8', '京东云工厂(中原消金)债务债权计算公式', '766bdbe4294c42a2bee62d06d15bc746', '联合收清分公式', 0, '' ,'9549749e60bf42ba86d3bf0da5ec41ab', 'PENALTY_INTEREST', 'PEC'),
(2, 'G0012', 'C5056', 'A', 'db1f52bdd6064a92ab7c2fd23714f4a8', '京东云工厂(中原消金)债务债权计算公式', '39002f22bf7e41fd9f4e30aa949d1d96', '联合收代偿清分公式', 0,'' ,'9549749e60bf42ba86d3bf0da5ec41ab', 'OVERDUE_LIQUIDATED_DAMAGES', 'PEC')
;
```

1,资方的<font style="color:#DF2A3F;">f_match_target_code</font>(匹配编码)是代偿方的编码,而代偿方的f_match_target_code                是资方编码,这样就可以知道资方需要配置那些代偿方;

2,一个资方有<font style="color:#DF2A3F;">多个代偿方</font>就需要在f_match_target_code按<font style="color:#117CEE;">风险等级</font><font style="color:#DF2A3F;">(f_match_target_rank_level)</font>分类用逗号隔开; 而<font style="color:#DF2A3F;">多个代偿方</font>也要按照<font style="color:#117CEE;">风险等级</font>一对一配置对应f_match_target_code匹配的资方编码; 参考<font style="color:#DF2A3F;">C5048</font><font style="color:#000000;">;</font>

其中<font style="color:#DF2A3F;">f_repayment_formula_no</font>(本息计算公式编号),<font style="color:#DF2A3F;">f_clearing_formula_no</font>(资方/代偿方清分公式编号)也是FCC提供; <font style="color:#DF2A3F;">注意:</font><font style="color:#117CEE;">f_clearing_formula_no</font><font style="color:#DF2A3F;">区分资方和代偿方值是不同的</font>

3,如若资方需要支持<font style="color:#DF2A3F;">加速到期</font>则<font style="color:#117CEE;">f_is_support_accelerate</font>(是否支持加速到期)配置为1即可;

4,还有<font style="color:#DF2A3F;">特别注意:</font><font style="color:#117CEE;">f_repurchase_calculation_mode</font>(回购时重算),这个是回购时债权合并成一期有些费用要重新计算,就需要配置该字段,目前只有<font style="color:#DF2A3F;">担保费,逾期担保费,固收担保费1,固收担保费2</font>使用;如果<font style="color:#DF2A3F;">有别的费项，则修改</font><font style="color:#117CEE;">FeeFormulaParamMapping</font><font style="color:#DF2A3F;">类中的费项添加</font><font style="color:#117CEE;">calculationMode</font><font style="color:#DF2A3F;">属性即可;</font>

5,而f_fee_template_no不用多说就对应费项模版表的模版编号;f_fee_code配置的是资方或代偿方的需要费项,多个费项用逗号隔开<font style="color:#DF2A3F;">(注意:不用配置本息计算费项即:f_fee_code='');</font>

##### 三，代码逻辑

![](https://cdn.nlark.com/yuque/0/2025/png/56924506/1756429661425-4f82e921-3c68-4ae8-8c0a-b0857c5a0d5b.png)
