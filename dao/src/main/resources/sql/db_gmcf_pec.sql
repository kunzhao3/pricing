DROP DATABASE IF EXISTS db_gmcf_pec;
CREATE DATABASE db_gmcf_pec CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
USE db_gmcf_pec;
DROP TABLE IF EXISTS t_pec_template;
create table t_pec_template
(
    f_id                   bigint auto_increment comment '主键'
        primary key,
    f_version              tinyint      default 0                 not null comment '版本号',
    f_source_type          tinyint      default -1                not null comment '来源类型 1-产品模板 2-资方模板 3-进件模板 4-担保方模板',
    f_template_status      tinyint      default -1                not null comment '模板状态 0-不可用 1-可用',
    f_template_no          char(32)     default ''                not null comment '模板编号',
    f_current_mirror_no    char(32)     default ''                not null comment '模板镜像编号',
    f_template_name        varchar(64)  default ''                not null comment '模板名称',
    f_template_description varchar(256) default ''                not null comment '模板描述',
    f_created_time         datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time        datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    f_operater_name        char(32)     default ''                not null comment '操作人',
    constraint uk_source_name
        unique (f_source_type, f_template_name),
    constraint uk_template_no
        unique (f_template_no)
)
    comment '模板表';

INSERT INTO t_pec_template
(f_source_type, f_template_status, f_template_no, f_current_mirror_no, f_template_name, f_template_description,f_operater_name)
VALUES
(1, 1, '8a29677055a541419879d43648d10584', 'aff3ddd952a44985bdb083f2e483636d', '通用产品模板', '通用产品模板信息', 'PEC'),
(2, 1, '4271ccf7c7e64cfe877ba58d3bb93096', 'ef38790ebfc04543b4ee97f814d97c57', '通用资金方模板', '通用资金方模板信息', 'PEC'),
(2, 1, '9bad40d7ce1d41ff912be2d09830b96f', 'ccd84989de7f4c17a101350468bc1afd', '兜底资金方模板', '兜底资金方模板信息', 'PEC')
;
DROP TABLE IF EXISTS t_pec_template_element;
create table t_pec_template_element
(
    f_id                       bigint auto_increment comment '主键'
        primary key,
    f_version                  tinyint(11)  default 0                 not null comment '版本号',
    f_template_mirror_no       char(32)     default ''                not null comment '模板镜像编号',
    f_source_type              tinyint      default -1                not null comment '来源类型 1-产品模板 2-资方模板 3-进件模板 4-担保方模板',
    f_element_type             tinyint      default -1                not null comment '要素类型 1-必须要素 2-可选要素',
    f_element_code             varchar(64)  default ''                not null comment '要素代码',
    f_element_name             varchar(64)  default ''                not null comment '要素名称(中文)',
    f_element_description      varchar(256) default ''                not null comment '要素描述',
    f_input_type               tinyint      default -1                not null comment '要素输入类型 0-默认值 1-单行文本输入 2-多行文本输入 3-数字输入 4-百分比数字输入 5-日期输入 6-时间输入 7-日期时间输入 8-下拉选择 9-单选 10-复选',
    f_default_value            varchar(256) default ''                not null comment '通用默认值',
    f_suggest_value_type       tinyint      default -1                not null comment '建议值类型 1-本地读取 2-远程读取',
    f_local_suggest_value_json json                                   not null comment '本地建议值JSON',
    f_is_required              tinyint      default 0                 not null comment '是否必填 0-选填 1-必填',
    f_display_rule             varchar(512) default ''                not null comment '展示规则',
    f_valid_rule               varchar(512) default ''                not null comment '校验规则',
    f_valid_tips               varchar(512) default ''                not null comment '校验不通过提示信息',
    f_sort_order               int(4)       default 0                 not null comment '排序次序',
    f_created_time             datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time            datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uk_template_code
        unique (f_template_mirror_no, f_element_code)
)
    comment '模板要素表';
-- 产品模板要素
INSERT INTO t_pec_template_element
(f_template_mirror_no, f_source_type, f_element_type, f_element_code, f_element_name, f_element_description, f_input_type, f_default_value, f_suggest_value_type, f_local_suggest_value_json, f_is_required, f_display_rule, f_valid_rule, f_valid_tips, f_sort_order)
VALUES
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'BUSINESS_GROUP_CODE', '业务线代码', '', 8, '', 2, '[]', 1, '', '', '', 995),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_CHANNEL_CODE', '产品支持渠道', '', 1, '', 0, '[]', 0, '', '', '', 994),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_CODE', '产品代码', '', 1, '', 0, '[]', 1, '', '', '', 997),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_CURRENCY_CODE', '币种代码', '', 8, 'CNY', 1, '[{"key": "CNY", "value": "人民币"}]', 1, '', '', '', 976),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_DERATE_ALLOCATION_RULE', '减免顺序', '', 0, '7361c772b03311e896f8529269fb1459', 2, '[]', 1, '', '', '', 971),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_DESCRIPTION', '产品描述', '', 2, '', 0, '[]', 1, '', '', '', 998),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_END_DATETIME', '产品截止时间', '', 7, '', 0, '[]', 1, '', '', '', 992),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_GRACE_DAYS', '宽限期天数', '', 3, '3', 0, '[]', 1, '', '$value >=0 && $value <=30', '', 978),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_LOAN_TYPE', '贷款类型', '', 8, '1', 1, '[{"key": 1, "value": "现金贷"}, {"key": 2, "value": "商品贷"}]', 1, '', '', '', 996),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_MAX_LOAN_AMOUNT', '单笔最大可贷金额', '', 3, '50000', 0, '[]', 1, '', '$value >0', '', 983),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_MIN_LOAN_AMOUNT', '单笔最小可贷金额', '', 3, '10000', 0, '[]', 1, '', '$value >0', '', 982),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_NAME', '产品名称', '', 1, '', 0, '[]', 1, '', '', '', 999),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_OVERDUE_CONTINUOUS_STAGES', '逾期持续批扣期数', '', 3, '3', 0, '[]', 1, '', '$value >=0', '', 984),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_PAYOUT_TYPE', '放款方式', '', 8, '1', 1, '[{"key": 1, "value": "线上代付"}, {"key": 2, "value": "线下转账"}]', 1, '', '', '', 979),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_PRE_SETTLE', '是否支持提前结清', '', 9, '0', 1, '[{"key": "0", "value": "否"}, {"key": "1", "value": "是"}]', 1, '', '', '', 974),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_PRE_SETTLE_TYPE', '提前结清方式', '', 8, '2', 1, '[{"key": "1", "value": "不合并债务，按全部应收收取"}, {"key": "2", "value": "合并债务，只收本金和手续费"}, {"key": "3", "value": "合并债务，当期应还总额，剩余本金和手续费"}]', 1, '$this[''PRODUCT_PRE_SETTLE''] == 1', '', '', 973),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_QUOTA_PRODUCT', '是否额度类产品', '', 9, '0', 1, '[{"key": "0", "value": "否"}, {"key": "1", "value": "是"}]', 1, '', '', '', 971),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_QUOTA_PRODUCT_CODE', '额度产品代码', '', 1, '', 0, '[]', 1, '$this["PRODUCT_QUOTA_PRODUCT"] == 1', '', '', 972),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_REPAYMENT_ALLOCATION_RULE', '还款顺序', '', 0, 'e33b46a06204485090d7b0862083462b', 2, '[]', 1, '', '', '', 972),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_REPAYMENT_DATE_TYPE', '还款日定义', '每期还款日定义方式', 8, '2', 1, '[{"key": 1, "value": "每期最后一日"}, {"key": 2, "value": "放款日当天"}, {"key": 3, "value": "固定还款日"}, {"key": 4, "value": "账单日"}]', 1, '', '', '', 987),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_REPAYMENT_NOTIFY_DAYS', '还款提醒提前天数', '', 3, '3', 0, '[]', 1, '', '$value >0 && $value <=30', '', 977),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_REPAYMENT_REGULAR_DAY', '固定还款日', '每期固定还款日日期', 3, '1', 0, '[]', 0, '$this[''PRODUCT_REPAYMENT_DATE_TYPE''] == 3', '$value >0 && $value <=30', '', 986),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_STAGE_CYCLE_UNIT', '分期周期单位', '', 8, '2', 1, '[{"key": 1, "value": "日"}, {"key": 2, "value": "月"}]', 1, '', '', '', 990),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_STAGE_CYCLE_VALUE', '分期周期数值', '', 3, '', 0, '[]', 1, '', '', '', 991),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_STAGE_MIN_DAYS', '首期最短分期天数', '', 3, '15', 0, '[]', 0, '', '$value >0 && $value <=30', '', 989),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_START_DATETIME', '产品开始时间', '', 7, '', 0, '[]', 1, '', '', '', 993),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_STEP_AMOUNT', '递增金额', '', 3, '1000.00', 0, '[]', 1, '', '$value >0', '', 981),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_VALUE_DATE_DAY', '起息日定义', '放款后多少天开始计息', 3, '0', 0, '[]', 1, '', '', '', 988),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_WITHHOLD_CONTINUED_DAYS', '扣款持续补扣天数', '', 3, '5', 0, '[]', 1, '', '$value >0', '', 985),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUCT_YEAR_DAYS', '产品年天数', '', 3, '360', 0, '[]', 1, '', '$value >0 && $value <=365', '', 980),
('aff3ddd952a44985bdb083f2e483636d', 1, 1, 'PRODUT_ONLY_NEWER', '是否仅限新人', '', 9, '1', 1, '[{"key": "0", "value": "否"}, {"key": "1", "value": "是"}]', 1, '', '', '', 975)
;
-- 资方模板要素
INSERT INTO t_pec_template_element
(f_template_mirror_no, f_source_type, f_element_type, f_element_code, f_element_name, f_element_description, f_input_type, f_default_value, f_suggest_value_type, f_local_suggest_value_json, f_is_required, f_display_rule, f_valid_rule, f_valid_tips, f_sort_order)
VALUES
-- 资方
('ef38790ebfc04543b4ee97f814d97c57', 2, 1, 'CAPITAL_CODE', '资金代码', '', 1, '', 0, '[]', 1, '', '', '', 992),
('ef38790ebfc04543b4ee97f814d97c57', 2, 1, 'CAPITAL_SHORT_NAME', '机构简称', '', 1, '', 0, '[]', 1, '', '', '', 991),
('ef38790ebfc04543b4ee97f814d97c57', 2, 1, 'CAPITAL_MECHANISM_CODE', '机构代码', '', 1, '', 0, '[]', 1, '', '', '', 990),
('ef38790ebfc04543b4ee97f814d97c57', 2, 1, 'CAPITAL_FULL_NAME', '机构全称', '', 1, '', 0, '[]', 1, '', '', '', 989),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_GRACE_DAYS', '宽限期天数', '', 3, '0', 0, '[]', 1, '', '$value >=0 && $value < 30', '', 988),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_COMPENSATION_TYPE', '代偿方式', '', 9, '0', 1, '[{"key": "0", "value": "不代偿"}, {"key": "1", "value": "逾期代偿"}]', 1, '', '', '', 987),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_OVERDUE_COMPENSATION_DAYS', '逾期代偿天数', '', 3, '0', 0, '[]', 0, '$this[''CAPITAL_COMPENSATION_TYPE''] == 1', '', '', 986),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_PRE_SETTLE', '是否支持提前结清', '', 9, '0', 1, '[{"key": "0", "value": "否"}, {"key": "1", "value": "是"}]', 1, '', '', '', 985),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_PRE_SETTLE_TYPE', '提前结清方式', '', 8, '1', 1, '[{"key": "1", "value": "合并债权，收取剩余本金和剩余本金按日计息"}, {"key": "2", "value": "合并债权，只收本金和手续费"}]', 0, '$this[''CAPITAL_PRE_SETTLE''] == 1', '', '', 984),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_HAVE_ENTRY_INTERFACE', '是否有进件接口', '', 9, '0', 1, '[{"key": "0", "value": "无"}, {"key": "1", "value": "有"}]', 1, '', '', '', 983),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_ENTRY_INTERFACE_TYPE', '进件方式', '', 8, '1', 1, '[{"key": "1", "value": "接口调用"}, {"key": "2", "value": "H5"}]', 1, '', '', '', 982),
('ef38790ebfc04543b4ee97f814d97c57', 2, 1, 'CAPITAL_YEAR_DAYS', '资金年天数', '', 3, '360', 0, '[]', 1, '', '', '', 981),
('ef38790ebfc04543b4ee97f814d97c57', 2, 2, 'CAPITAL_MILLI_AND_FOURPERCENT_PRE_SETTLE_RATE', '提前结清千一/百四', 'milliPreSettleRate：代表千一；fourPercentPreSettleRate：代表百四；name：产品-等级(多个等级逗号隔开)；value：费项', 1, '[{"milliPreSettleRate":[{"name":"5551-A","value":"SERVICE_FEE"}]},{"fourPercentPreSettleRate":[{"name":"5551-B,C,D","value":"SERVICE_FEE_TWO"}]}]', 0, '[]', 1, '', '', '', 980),
-- 兜底方
('ccd84989de7f4c17a101350468bc1afd', 2, 1, 'CAPITAL_CODE', '资金代码', '', 1, '', 0, '[]', 1, '', '', '', 999),
('ccd84989de7f4c17a101350468bc1afd', 2, 1, 'CAPITAL_FULL_NAME', '机构全称', '', 1, '', 0, '[]', 1, '', '', '', 996),
('ccd84989de7f4c17a101350468bc1afd', 2, 1, 'CAPITAL_MECHANISM_CODE', '机构代码', '', 1, '', 0, '[]', 1, '', '', '', 998),
('ccd84989de7f4c17a101350468bc1afd', 2, 1, 'CAPITAL_SHORT_NAME', '机构简称', '', 1, '', 0, '[]', 1, '', '', '', 997),
('ccd84989de7f4c17a101350468bc1afd', 2, 1, 'CAPITAL_YEAR_DAYS', '资金年天数', '', 3, '360', 0, '[]', 1, '', '', '', 990);
;

DROP TABLE IF EXISTS t_pec_product;
create table t_pec_product
(
    f_id                     bigint auto_increment comment '主键'
        primary key,
    f_version                tinyint      default 0                     not null comment '版本号',
    f_product_no             char(32)     default ''                    not null comment '产品编号',
    f_current_mirror_no      char(32)     default ''                    not null comment '当前镜像编号',
    f_template_no            char(32)     default ''                    not null comment '关联模板编号',
    f_template_mirror_no     char(32)     default ''                    not null comment '关联模板镜像编号',
    f_loan_type              tinyint      default 1                     not null comment '贷款类型 1-现金贷 2-商品贷',
    f_product_code           char(10)     default ''                    not null comment '产品代码',
    f_product_name           varchar(64)  default ''                    not null comment '产品名称',
    f_business_group_code    char(10)     default ''                    not null comment '业务线代码',
    f_product_channel_code   varchar(128) default ''                    not null comment '产品支持渠道 逗号分隔',
    f_product_start_datetime datetime     default CURRENT_TIMESTAMP     not null comment '产品开始时间',
    f_product_end_datetime   datetime     default CURRENT_TIMESTAMP     not null comment '产品截止时间',
    f_product_status         tinyint      default -1                    not null comment '产品状态 0-不可用 1-可用',
    f_created_time           datetime     default CURRENT_TIMESTAMP     not null comment '创建时间',
    f_modified_time          datetime     default CURRENT_TIMESTAMP     not null on update CURRENT_TIMESTAMP comment '修改时间',
    f_operater_name          char(32)     default ''                    not null comment '操作人',
    constraint uk_product_code
        unique (f_product_code),
    constraint uk_product_name
        unique (f_product_name),
    constraint uk_product_no
        unique (f_product_no)
)
    comment '产品表';

create index idx_business_group_code
    on t_pec_product (f_business_group_code);

INSERT INTO t_pec_product
(f_product_no, f_current_mirror_no, f_template_no, f_template_mirror_no, f_loan_type, f_product_code, f_product_name, f_business_group_code, f_product_channel_code, f_product_start_datetime, f_product_end_datetime, f_product_status,  f_operater_name)
VALUES
('2fc3d776c1b142fe81230f62a6f924da', '24f665685702485b9b86e08992583609', '8a29677055a541419879d43648d10584', 'aff3ddd952a44985bdb083f2e483636d', 1, '5561', '国美易卡-自有公积金贷款', 'MJ', '10000000', '2018-08-01 00:00:00', '2099-11-22 00:00:00', 1, 'PEC'),
('fc14a503eedd417d9d4e2bee16fb6171', '0883d8cdafde1d56103d62c8d5b20407', '8a29677055a541419879d43648d10584', 'aff3ddd952a44985bdb083f2e483636d', 1, '5551', '国美易卡-额度产品', 'MJ', '10000000', '2018-08-01 00:00:00', '2099-12-26 00:00:00', 1, 'PEC')
;

DROP TABLE IF EXISTS t_pec_capital;
create table t_pec_capital
(
    f_id                 bigint auto_increment comment '主键'
        primary key,
    f_version            tinyint     default 0                 not null comment '版本号',
    f_capital_no         char(32)    default ''                not null comment '资金方编号',
    f_current_mirror_no  char(32)    default ''                not null comment '当前镜像编号',
    f_template_no        char(32)    default ''                not null comment '关联模板编号',
    f_template_mirror_no char(32)    default ''                not null comment '关联模板镜像编号',
    f_capital_code       varchar(32) default ''                not null comment '资金方代码',
    f_capital_name       varchar(64) default ''                not null comment '资金方名称',
    f_capital_member_no  char(36)    default ''                not null comment '资金方开户的用户编号',
    f_capital_status     tinyint     default -1                not null comment '资方状态 0-不可用 1-可用',
    f_created_time       datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time      datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    f_operater_name      char(32)    default ''                not null comment '操作人',
    constraint uk_capital_code
        unique (f_capital_code),
    constraint uk_capital_name
        unique (f_capital_name),
    constraint uk_capital_no
        unique (f_capital_no)
)
    comment '资金方表';

DROP TABLE IF EXISTS t_pec_element_data;
create table t_pec_element_data
(
    f_id             bigint auto_increment comment '主键'
        primary key,
    f_version        tinyint       default 0                 not null comment '版本号',
    f_data_type      tinyint       default -1                not null comment '来源数据类型 1-产品要素 2-资方要素 3-进件要素 4-担保方要素',
    f_data_no        char(32)      default ''                not null comment '来源数据编号',
    f_data_mirror_no char(32)      default ''                not null comment '来源数据镜像编号',
    f_element_code   varchar(64)   default ''                not null comment '要素代码',
    f_element_data   varchar(4096) default ''                not null comment '要素代码数据',
    f_created_time   datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time  datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uk_mirror_no_element_code
        unique (f_data_mirror_no, f_element_code)
)
    comment '要素数据表';

DROP TABLE IF EXISTS t_pec_capital_pricing;
create table t_pec_capital_pricing
(
    f_id                        bigint auto_increment comment '主键'
        primary key,
    f_version                   tinyint        default 0                 not null comment '版本号',
    f_capital_no                char(32)       default ''                not null comment '资金方编号',
    f_capital_code              varchar(32)    default ''                not null comment '资金方代码',
    f_pricing_no                char(32)       default ''                not null comment '资金方定价编号',
    f_current_mirror_no         char(32)       default ''                not null comment '当前镜像编号',
    f_match_type                tinyint        default -1                not null comment '匹配类型 0-所有产品 1-业务线匹配 2-产品匹配 3-定价匹配',
    f_match_target_no           char(32)       default ''                not null comment '匹配目标编号',
    f_repayment_formula_no      char(32)       default ''                not null comment '本息计算公式编号',
    f_repayment_formula_name    varchar(64)    default ''                not null comment '本息计算公式名称',
    f_year_rate                 decimal(10, 6) default 0.000000          not null comment '年利率',
    f_month_rate                decimal(10, 6) default 0.000000          not null comment '月利率',
    f_day_rate                  decimal(10, 6) default 0.000000          not null comment '日利率',
    f_clearing_formula_no       char(32)       default ''                not null comment '清分公式编号',
    f_clearing_formula_name     varchar(64)    default ''                not null comment '清分公式名称',
    f_profit_distribution_no    char(32)       default ''                not null comment '分润公式编号',
    f_profit_distribution_name  varchar(64)    default ''                not null comment '分润公式名称',
    f_remain_capital_code       varchar(32)    default ''                not null comment '兜底资金方代码',
    f_remain_capital_name       varchar(64)    default ''                not null comment '兜底资金方名称',
    f_remain_capital_member_no  char(36)       default ''                not null comment '兜底资金方的用户编号',
    f_compensatory_capital_code varchar(32)    default ''                not null comment '代偿资方代码',
    f_compensatory_capital_name varchar(64)    default ''                not null comment '代偿资方名称',
    f_compensatory_member_no    char(36)       default ''                not null comment '代偿资方编号',
    f_compensatory_expand_info  json                                     null comment '代偿方扩展信息',
    f_created_time              datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time             datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uk_pricing_no
        unique (f_pricing_no)
)
    comment '资金方定价表';

create index uk_match_target_no
    on t_pec_capital_pricing (f_match_target_no);


DROP TABLE IF EXISTS t_pec_product_pricing;
create table t_pec_product_pricing
(
    f_id                      bigint auto_increment comment '主键'
        primary key,
    f_version                 tinyint        default 0                 not null comment '版本号',
    f_product_price_status    char           default '0'               not null comment '产品状态 0:启用，1:禁用',
    f_product_no              char(32)       default ''                not null comment '产品编号',
    f_product_code            char(10)       default ''                not null comment '产品代码',
    f_pricing_no              char(32)       default ''                not null comment '产品定价编号',
    f_current_mirror_no       char(32)       default ''                not null comment '当前镜像编号',
    f_total_stage             int(4)         default 0                 not null comment '总期数',
    f_rank_level              char(2)                                  null,
    f_min_rank                int(4)         default 0                 not null comment '最小风险等级',
    f_max_rank                int(4)         default 0                 not null comment '最大风险等级',
    f_repayment_formula_no    char(32)       default ''                not null comment '本息计算公式编号',
    f_repayment_formula_name  varchar(64)    default ''                not null comment '本息计算公式名称',
    f_year_rate               decimal(10, 6) default 0.000000          not null comment '年利率',
    f_month_rate              decimal(10, 6) default 0.000000          not null comment '月利率',
    f_day_rate                decimal(10, 6) default 0.000000          not null comment '日利率',
    f_rate_flag               char           default '1'               not null comment '提前结清计息方式 0-按日计息 1-按月计息',
    f_annualized_rate         decimal(10, 6) default 0.000000          not null comment '年化费率',
    f_service_annualized_rate decimal(10, 6) default 0.000000          not null comment '服务年化费率',
    f_apr_year_rate           decimal(10, 6) default 0.000000          not null comment '展示年利率',
    f_apr_month_rate          decimal(10, 6) default 0.000000          not null comment '展示月利率',
    f_apr_day_rate            decimal(10, 6) default 0.000000          not null comment '展示日利率',
    f_capital_code            varchar(50)    default ''                null comment '资方编码',
    f_consumer_label          varchar(50)    default ''                null comment '用户标签',
    f_repayment_way           varchar(50)    default ''                null comment '还款方式(1-等本等息,2-等额本息,3-等额本金,4-先息后本,5-一次性还本付息)',
    f_channel_type            varchar(50)    default ''                null comment '渠道ID',
    f_activity_code           varchar(60)    default ''                not null comment '活动编码',
    f_remark                  varchar(255)   default ''                null comment '备注',
    f_operator_name           varchar(32)    default ''                not null comment '操作人',
    f_created_time            datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time           datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uk_pricing_no
        unique (f_pricing_no)
)
    comment '产品定价表';

create index index_c_t_r
    on t_pec_product_pricing (f_capital_code, f_total_stage, f_rank_level);

DROP TABLE IF EXISTS t_pec_pricing_fee;
create table t_pec_pricing_fee
(
    f_id                         bigint auto_increment comment '主键'
        primary key,
    f_version                    tinyint      default 0                 not null comment '版本号',
    f_pricing_type               tinyint      default -1                not null comment '定价类型 1-产品定价 2-资金方定价',
    f_pricing_no                 char(32)     default ''                not null comment '定价编号',
    f_pricing_mirror_no          char(32)     default ''                not null comment '定价镜像编号',
    f_fee_code                   varchar(64)  default ''                not null comment '费用代码',
    f_fee_name                   varchar(64)  default ''                not null comment '费用名称',
    f_fee_description            varchar(256) default ''                not null comment '费用描述',
    f_calc_period                tinyint      default -1                not null comment '计算周期 1-按期计算 2-按日计算',
    f_formula_type               tinyint      default -1                not null comment '公式类型 1-本息公式 2-费项公式',
    f_formula_no                 char(32)     default ''                not null comment '公式编号',
    f_formula_name               varchar(64)  default ''                not null comment '公式名称',
    f_formula_params_json        json                                   not null comment '公式参数',
    f_created_time               datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time              datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    f_formula_description        varchar(256) default ''                not null comment '公式描述',
    f_formula_carbon_name        varchar(64)  default ''                not null comment '公式副本名称',
    f_formula_carbon_no          char(32)     default ''                not null comment '公式副本编号',
    f_formula_carbon_description varchar(256) default ''                not null comment '公式副本描述',
    f_operater_name              char(32)     default ''                not null comment '操作人',
    constraint uk_mirror_no_fee_code
        unique (f_pricing_mirror_no, f_fee_code)
)
    comment '定价费项表';


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
INSERT INTO t_pec_fee_template
(f_template_no, f_template_name, f_fee_code, f_fee_name, f_formula_no, f_formula_name, f_operater_name) 
VALUES
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'GUARANTEE_FEE', '担保费', '9f8fac353dc14e7cb141522bcc90ff74', '联合收担保费计算公式2', 'PEC'),
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('0d2f904761f24362bb7d0acba4a22351', '联合清分模版', 'SERVICE_FEE', '服务费', '750a032c443945e4a9892b48f8abdf65', '联合贷服务费公式2', 'PEC'),

('f0d709cad24c46f988300d7cad15e2ad', '资产管理费拆分模版', 'ASSET_MANAGE_FEE', '资产管理费', 'd62ce247c2454794b07b8f0e79fbb965', '资产管理费', 'PEC'),
('f0d709cad24c46f988300d7cad15e2ad', '资产管理费拆分模版', 'GUARANTEE_FEE', '担保费', '9f8fac353dc14e7cb141522bcc90ff74', '联合收担保费计算公式2', 'PEC'),
('f0d709cad24c46f988300d7cad15e2ad', '资产管理费拆分模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('f0d709cad24c46f988300d7cad15e2ad', '资产管理费拆分模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('f0d709cad24c46f988300d7cad15e2ad', '资产管理费拆分模版', 'SERVICE_FEE', '服务费', '750a032c443945e4a9892b48f8abdf65', '联合贷服务费公式2', 'PEC'),

('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'GUARANTEE_FEE_ONE', '担保费1', 'd62874f5e54c440282b01866f94c2409', '担保费计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'GUARANTEE_FEE_TWO', '担保费2', 'd62874f5e54c440282b01866f94c2409', '担保费计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'OVERDUE_LIQUIDATED_DAMAGES_ONE', '逾期违约金1', 'd653d0f56dad41aa8bc3072038135ab1', '蓝海逾期违约金1计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'OVERDUE_LIQUIDATED_DAMAGES_TWO', '逾期违约金2', '233f31f862914dd7be8a40024b5868d9', '蓝海逾期违约金2计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'SERVICE_FEE_ONE', '服务费1', '0f0d83705dec49f9af169d5a52b5e016', '服务费1计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'SERVICE_FEE_TWO', '服务费2', 'cc50725fe34e4a5296bfb3b359dfab32', '服务费2计算公式', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'ASSET_MANAGE_FEE_ONE', '资产管理费1', 'd62ce247c2454794b07b8f0e79fbb965', '资产管理费1', 'PEC'),
('7fb28bcde98540f4bfa1d4b11b031546', '双代偿实担模版', 'ASSET_MANAGE_FEE_TWO', '资产管理费2', '0329872cf33d47a598ffb739f2cc2559', '资产管理费2', 'PEC'),

('152b1b5202cf46eb9a323030f9a6243b', '资产管理费同服务费模版', 'ASSET_MANAGE_FEE', '资产管理费', '6a2cda7b6f3b45fc9426e0aa7f96acc6', '综合兜底费项公式', 'PEC'),
('152b1b5202cf46eb9a323030f9a6243b', '资产管理费同服务费模版', 'GUARANTEE_FEE', '担保费', '9f8fac353dc14e7cb141522bcc90ff74', '联合收担保费计算公式2', 'PEC'),
('152b1b5202cf46eb9a323030f9a6243b', '资产管理费同服务费模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('152b1b5202cf46eb9a323030f9a6243b', '资产管理费同服务费模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('152b1b5202cf46eb9a323030f9a6243b', '资产管理费同服务费模版', 'SERVICE_FEE', '服务费', '750a032c443945e4a9892b48f8abdf65', '联合贷服务费公式2', 'PEC'),

('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'ASSET_MANAGE_FEE', '资产管理费', '21f21083a8b7451baade18f445b6b2ff', '资产管理费', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'GUARANTEE_FEE', '担保费', 'a242757aa7684cd1a3e8236c77daae35', '费项金额为0的公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'PENALTY_INTEREST', '罚息', 'f58468b3e1564d44b026e994c2ba0528', '罚息计算公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'SERVICE_FEE', '服务费', '6a2cda7b6f3b45fc9426e0aa7f96acc6', '综合兜底费项公式', 'PEC'),
('9549749e60bf42ba86d3bf0da5ec41ab', '资产管理费模版', 'SERVICE_FEE_SETTLEMENT', '服务费结算', '', '服务费结算', 'PEC'),

('5cf3f23abbda444cbd538f36d9fae2a8', '分润模版', 'ASSET_MANAGE_FEE', '资产管理费', '6a2cda7b6f3b45fc9426e0aa7f96acc6', '综合兜底费项公式', 'PEC'),
('5cf3f23abbda444cbd538f36d9fae2a8', '分润模版', 'GUARANTEE_FEE', '担保费', '9f8fac353dc14e7cb141522bcc90ff74', '联合收担保费计算公式2', 'PEC'),
('5cf3f23abbda444cbd538f36d9fae2a8', '分润模版', 'OVERDUE_LIQUIDATED_DAMAGES', '逾期违约金', '5f110648dc3a4b34a9e20c12a685a776', '逾期违约金计算公式', 'PEC'),
('5cf3f23abbda444cbd538f36d9fae2a8', '分润模版', 'PENALTY_INTEREST', '罚息', '52bd9a018a0e4732b5770e11180f9153', '罚息计算公式', 'PEC'),
('5cf3f23abbda444cbd538f36d9fae2a8', '分润模版', 'SERVICE_FEE', '服务费', 'a242757aa7684cd1a3e8236c77daae35', '服务费计算公式', 'PEC')

;

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
    f_fee_code                    varchar(128) default ''                not null comment '资方/代偿方费用代码,多个费项用逗号隔开',
    f_created_time                datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    f_modified_time               datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    f_operater_name               char(32)     default ''                not null comment '操作人',
    constraint uk_capital_code_match_target_code_clearing_formula_no
        unique (f_capital_code,f_match_target_code,f_clearing_formula_no)
)
    comment '资方模版表';
INSERT INTO t_pec_capital_template
(f_capital_type, f_capital_code, f_match_target_code, f_match_target_rank_level ,f_repayment_formula_no, f_repayment_formula_name, f_clearing_formula_no, f_clearing_formula_name, f_is_support_accelerate, f_repurchase_calculation_mode, f_fee_template_no, f_fee_code, f_operater_name)
VALUES
(1, 'C5024', 'G0012', 'A,B,C,D', '766bdbe4294c42a2bee62d06d15bc746', '西安债务债权公式', '766bdbe4294c42a2bee62d06d15bc746', '联合收清分公式', 1, 'REPURCHASE_STAGE_ONLY', 'f0d709cad24c46f988300d7cad15e2ad', 'GUARANTEE_FEE', 'PEC'),
(2, 'G0012', 'C5024', 'A,B,C,D', '766bdbe4294c42a2bee62d06d15bc746', '西安债务债权公式', '39002f22bf7e41fd9f4e30aa949d1d96', '联合收代偿清分公式', 1,'',  'f0d709cad24c46f988300d7cad15e2ad', 'OVERDUE_LIQUIDATED_DAMAGES,PENALTY_INTEREST', 'PEC'),
(1, 'C5048', 'G0009', 'A', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', '766bdbe4294c42a2bee62d06d15bc746', '联合收清分公式', 1, '' ,'0d2f904761f24362bb7d0acba4a22351', 'GUARANTEE_FEE', 'PEC'),
(2, 'G0009', 'C5048', 'A', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', '6dc07c6c6c7241609302cdc3e6c03eb5', '实担模式代偿清分公式', 1, '' ,'0d2f904761f24362bb7d0acba4a22351', 'OVERDUE_LIQUIDATED_DAMAGES', 'PEC'),
(1, 'C5048', 'G0009,G0012', 'B,C,D', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', 'e2a3170b238b42cbbcc0f0fca9351215', '双代偿资方清分公式', 1, '' ,'7fb28bcde98540f4bfa1d4b11b031546', 'GUARANTEE_FEE_ONE,GUARANTEE_FEE_TWO', 'PEC'),
(2, 'G0009', 'C5048', 'B,C,D', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', 'dcf7e938b45c479489b279fa0c78b7de', '双代偿双融担代偿清分公式', 1,'' ,'7fb28bcde98540f4bfa1d4b11b031546', '', 'PEC'),
(2, 'G0012', 'C5048', 'B,C,D', '077dfd23594f45d9a675038e4461b610', '蓝海银行（实担）债务债权公式', 'dcf7e938b45c479489b279fa0c78b7de', '双代偿双融担代偿清分公式', 1,'' ,'7fb28bcde98540f4bfa1d4b11b031546', '', 'PEC'),
(1, 'C5056', 'G0012', 'A', 'db1f52bdd6064a92ab7c2fd23714f4a8', '京东云工厂(中原消金)债务债权计算公式', '766bdbe4294c42a2bee62d06d15bc746', '联合收清分公式', 0, '' ,'9549749e60bf42ba86d3bf0da5ec41ab', 'PENALTY_INTEREST', 'PEC'),
(2, 'G0012', 'C5056', 'A', 'db1f52bdd6064a92ab7c2fd23714f4a8', '京东云工厂(中原消金)债务债权计算公式', '39002f22bf7e41fd9f4e30aa949d1d96', '联合收代偿清分公式', 0,'' ,'9549749e60bf42ba86d3bf0da5ec41ab', 'OVERDUE_LIQUIDATED_DAMAGES', 'PEC'),
(1, 'C5059', '', 'A', 'db1f52bdd6064a92ab7c2fd23714f4a8', '小米消金(分润)放款债务债权计算公式', 'db2d3f0b67234d0fbeaacb7f28fddc31', '清分公式', 0, '' ,'5cf3f23abbda444cbd538f36d9fae2a8', 'PENALTY_INTEREST', 'PEC'),
(1, 'C5059', 'G0012', 'B,C,D', 'db1f52bdd6064a92ab7c2fd23714f4a8', '小米消金(分润)放款债务债权计算公式', 'db2d3f0b67234d0fbeaacb7f28fddc31', '清分公式', 0,'' ,'5cf3f23abbda444cbd538f36d9fae2a8', 'PENALTY_INTEREST', 'PEC'),
(2, 'G0012', 'C5059', 'B,C,D', 'db1f52bdd6064a92ab7c2fd23714f4a8', '小米消金(分润)放款债务债权计算公式', 'db3e734a1b7e40518c7832f5e75a68b9', '代偿清分公式', 0,'' ,'5cf3f23abbda444cbd538f36d9fae2a8', 'OVERDUE_LIQUIDATED_DAMAGES', 'PEC')
;




