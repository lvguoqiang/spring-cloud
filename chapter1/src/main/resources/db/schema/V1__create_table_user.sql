create table user
(
  `id` bigint auto_increment primary key comment '主键',
  `username` VARCHAR(50) null comment '账户名',
  `name` VARCHAR(20) null comment '用户名',
  `age` int(3) null comment '年龄',
  `balance` DECIMAL(10,2) comment '体重',
  `object_version_number` BIGINT DEFAULT '1' NULL COMMENT '版本号',
  `gmt_create` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户表';

