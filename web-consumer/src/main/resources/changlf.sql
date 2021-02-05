-- 用户表 -2021/2/5 9:32 by chang
/*
 Navicat Premium Data Transfer

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : localhost:3306
 Source Schema         : changlf

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 05/02/2021 09:34:41
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `ID`          varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `PARAM_KEY`   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'key',
    `PARAM_VALUE` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'value',
    `STATUS`      tinyint(4) NULL DEFAULT 1 COMMENT '状态   0：隐藏   1：显示',
    `REMARK`      varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`ID`) USING BTREE,
    UNIQUE INDEX `PARAM_KEY`(`PARAM_KEY`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统配置信息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`
(
    `ID`       varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `GROUP_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属分组ID',
    `NAME`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典名称',
    `VALUE`    varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典值',
    `SORT`     int(11) NULL DEFAULT NULL COMMENT '排序号',
    `STATUS`   int(11) NULL DEFAULT NULL COMMENT '状态码',
    `REMARK`   text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dict_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_group`;
CREATE TABLE `sys_dict_group`
(
    `ID`          varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `CODE`        varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分组编码',
    `NAME`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分组名称',
    `CREATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    `REMARK`      text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注',
    PRIMARY KEY (`ID`, `CODE`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据字典分组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `MENU_ID`   varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `PARENT_ID` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
    `NAME`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
    `URL`       varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单URL',
    `PERMS`     varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
    `TYPE`      int(11) NULL DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
    `ICON`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
    `ORDER_NUM` int(11) NULL DEFAULT NULL COMMENT '排序',
    PRIMARY KEY (`MENU_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu`
VALUES ('02', '0', '官网运营', NULL, NULL, 0, 'kufang', 4);
INSERT INTO `sys_menu`
VALUES ('10', '0', '系统管理', NULL, NULL, 0, 'system', 10);
INSERT INTO `sys_menu`
VALUES ('1001', '10', '菜单管理', 'sys/menu', 'sys:menu:list,sys:menu:info', 1, 'menu', 1);
INSERT INTO `sys_menu`
VALUES ('100101', '1001', '新增', NULL, 'sys:menu:save,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('100102', '1001', '修改', NULL, 'sys:menu:update,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('100103', '1001', '删除', NULL, 'sys:menu:delete', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('1003', '10', '系统参数', 'sys/config', 'sys:config:list,sys:config:info', 1, 'xitongpeizhi', 3);
INSERT INTO `sys_menu`
VALUES ('100301', '1003', '新增', NULL, 'sys:config:save', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('100302', '1003', '修改', NULL, 'sys:config:update', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('100303', '1003', '删除', NULL, 'sys:config:delete', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('1006', '10', '系统日志', 'sys/log', 'sys:log:list', 1, 'log', 6);
INSERT INTO `sys_menu`
VALUES ('11', '0', '权限管理', NULL, NULL, 0, 'auth', 5);
INSERT INTO `sys_menu`
VALUES ('1101', '11', '管理员列表', 'sys/user', 'sys:user:list,sys:user:info', 1, 'admin', 1);
INSERT INTO `sys_menu`
VALUES ('110101', '1101', '重置密码', NULL, 'sys:user:resetPw', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('110102', '1101', '新增', NULL, 'sys:user:save,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('110103', '1101', '修改', NULL, 'sys:user:update,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('110104', '1101', '删除', NULL, 'sys:user:delete', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('1102', '11', '角色管理', 'sys/role', 'sys:role:list,sys:role:info', 1, 'role', 2);
INSERT INTO `sys_menu`
VALUES ('110201', '1102', '新增', NULL, 'sys:role:save,sys:menu:list', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('110202', '1102', '修改', NULL, 'sys:role:update,sys:menu:list', 2, NULL, 0);
INSERT INTO `sys_menu`
VALUES ('110203', '1102', '删除', NULL, 'sys:role:delete', 2, NULL, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `ROLE_ID`            varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `ROLE_NAME`          varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
    `REMARK`             varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `CREATE_USER_ID`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
    `CREATE_USER_ORG_NO` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者所属机构',
    `CREATE_TIME`        datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`ROLE_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES ('1148925097465786370', '开发', NULL, '1', '01', '2019-07-10 20:01:04');
INSERT INTO `sys_role`
VALUES ('1149238166091939841', '项目', NULL, '1', '01', '2019-07-11 16:45:05');
INSERT INTO `sys_role`
VALUES ('1154218311295868930', '測試', NULL, '1', '01', '2019-07-25 10:34:24');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `ID`      varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `ROLE_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID',
    `MENU_ID` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单ID',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色与菜单对应关系' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `USER_ID`            varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `USER_NAME`          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
    `REAL_NAME`          varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `SEX`                tinyint(4) NOT NULL,
    `ORG_NO`             varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机构编码',
    `SALT`               varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐',
    `EMAIL_HOST`         varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮件服务器地址',
    `EMAIL`              varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `EMAIL_PW`           varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户邮箱密码',
    `MOBILE`             varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
    `STATUS`             tinyint(4) NULL DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
    `PASSWORD`           varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `CREATE_USER_ID`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者ID',
    `CREATE_USER_ORG_NO` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人所属机构',
    `CREATE_TIME`        datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`USER_ID`) USING BTREE,
    UNIQUE INDEX `USERNAME`(`USER_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统用户' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `ID`      varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `USER_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
    `ROLE_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户与角色对应关系' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sys_user_token
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_token`;
CREATE TABLE `sys_user_token`
(
    `USER_ID`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL,
    `TOKEN`       varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'TOKEN',
    `EXPIRE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
    `UPDATE_TIME` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`USER_ID`) USING BTREE,
    UNIQUE INDEX `TOKEN`(`TOKEN`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统用户Token' ROW_FORMAT = Dynamic;

CREATE TABLE `user`
(
    `id`          char(32) NOT NULL,
    `username`    varchar(255) DEFAULT NULL,
    `rear_name`   varchar(255) DEFAULT NULL,
    `password`    varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP,
    `is_delete`   tinyint(1) DEFAULT '1',
    PRIMARY KEY (`id`),
    KEY           `idx_username` (`username`) USING BTREE,
    KEY           `联合索引` (`username`,`rear_name`,`password`,`create_time`) USING BTREE,
    KEY           `idx_rear_name` (`rear_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `system_log`
(
    `id`          varchar(32) NOT NULL DEFAULT '' COMMENT 'id',
    `module`      varchar(255)         DEFAULT NULL COMMENT '模块',
    `user_name`   varchar(32)          DEFAULT NULL COMMENT '操作人昵称',
    `user_id`     varchar(32)          DEFAULT NULL COMMENT '操作人ID',
    `method`      varchar(255)         DEFAULT NULL COMMENT '方法',
    `operation`   varchar(255)         DEFAULT NULL COMMENT '操作',
    `args`        varchar(5000)        DEFAULT NULL COMMENT '参数',
    `time`        int(11) unsigned DEFAULT '0' COMMENT '执行时长(毫秒)',
    `ip`          varchar(32)          DEFAULT NULL COMMENT 'IP',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';

SET
FOREIGN_KEY_CHECKS = 1;