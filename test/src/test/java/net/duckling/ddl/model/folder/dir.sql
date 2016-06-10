/*
 Navicat Premium Data Transfer

 Source Server         : mac
 Source Server Type    : MySQL
 Source Server Version : 50515
 Source Host           : localhost
 Source Database       : dir

 Target Server Type    : MySQL
 Target Server Version : 50515
 File Encoding         : utf-8

 Date: 09/11/2013 11:29:13 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `file_node`
-- ----------------------------
DROP TABLE IF EXISTS `file_node`;
CREATE TABLE `file_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(20) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=561 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `ddl_folder_path`
-- ----------------------------
DROP TABLE IF EXISTS `ddl_folder_path`;
CREATE TABLE  `ddl_folder_path` (
  `tid` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  `ancestor_rid` int(10) unsigned NOT NULL,
  `length` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`rid`,`ancestor_rid`,`length`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

