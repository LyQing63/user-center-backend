-- auto-generated definition
create table user
(
    id           bigint unsigned auto_increment comment '主键ID'
        primary key,
    userAccount  varchar(256)                       null comment '登录用户名',
    username     varchar(256)                       null comment '昵称
',
    gender       tinyint  default 0                 null comment '性别',
    userPassword varchar(256)                       null comment '密码
',
    phone        varchar(256)                       null comment '电话',
    email        varchar(256)                       null comment '邮箱',
    userStatus   tinyint  default 0                 null comment '账号状态，0-正常',
    avatarUrl    varchar(256)                       null comment '头像地址',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null,
    isDelete     tinyint  default 0                 null comment '是否被删除',
    userRole     tinyint  default 0                 not null comment '用户角色，0--为普通，1--为管理员',
    planetCode   varchar(512)                       null comment '星球编号'
)
    comment '用户';
