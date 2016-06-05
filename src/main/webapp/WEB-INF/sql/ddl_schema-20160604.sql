-- MySQL dump 10.13  Distrib 5.1.69, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: ddldb
-- ------------------------------------------------------
-- Server version	5.1.69-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `a1_browse_log`
--

DROP TABLE IF EXISTS `a1_browse_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_browse_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `rid` int(11) DEFAULT NULL,
  `item_type` varchar(10) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `display_name` varchar(255) DEFAULT NULL,
  `tracking_id` varchar(255) DEFAULT NULL,
  `browse_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `tracking_id` (`tracking_id`),
  KEY `browse_log_index` (`rid`,`browse_time`)
) ENGINE=InnoDB AUTO_INCREMENT=8644699 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_bundle`
--

DROP TABLE IF EXISTS `a1_bundle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_bundle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bid` int(11) NOT NULL DEFAULT '0',
  `status` varchar(25) DEFAULT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `creator` varchar(255) DEFAULT NULL,
  `last_editor` varchar(255) DEFAULT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_version` int(11) NOT NULL DEFAULT '1',
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bundle_index` (`bid`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=4356 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_bundle_item`
--

DROP TABLE IF EXISTS `a1_bundle_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_bundle_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `bid` int(11) NOT NULL,
  `rid` int(11) NOT NULL,
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `b_item_index` (`tid`,`bid`)
) ENGINE=InnoDB AUTO_INCREMENT=53011 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_copy_log`
--

DROP TABLE IF EXISTS `a1_copy_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_copy_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_version` int(11) DEFAULT NULL,
  `from_rid` int(11) DEFAULT NULL,
  `from_tid` int(11) DEFAULT NULL,
  `to_version` int(11) DEFAULT NULL,
  `to_rid` int(11) DEFAULT NULL,
  `to_tid` int(11) DEFAULT NULL,
  `copy_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `uid` varchar(255) DEFAULT NULL,
  `ancestry_rid` int(11) DEFAULT '0',
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=739 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_device_token`
--

DROP TABLE IF EXISTS `a1_device_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_device_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `device_token` varchar(255) NOT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2343 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_emailattach`
--

DROP TABLE IF EXISTS `a1_emailattach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_emailattach` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  `item_type` varchar(45) DEFAULT NULL,
  `creator` varchar(255) NOT NULL,
  `mid` varchar(128) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `email_mid` (`mid`),
  KEY `emailattach_tid_creator` (`tid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2445481 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_file`
--

DROP TABLE IF EXISTS `a1_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `clb_id` int(11) NOT NULL,
  `status` varchar(25) DEFAULT NULL,
  `fid` int(11) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `creator` varchar(255) DEFAULT NULL,
  `last_editor` varchar(255) DEFAULT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_version` int(11) NOT NULL DEFAULT '1',
  `clb_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_index` (`fid`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=99913 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_file_version`
--

DROP TABLE IF EXISTS `a1_file_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_file_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rid` int(10) unsigned NOT NULL,
  `tid` int(11) NOT NULL,
  `version` int(11) NOT NULL DEFAULT '1',
  `clb_id` int(11) NOT NULL,
  `size` bigint(20) DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `editor` varchar(255) DEFAULT NULL,
  `edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` varchar(10) DEFAULT NULL,
  `clb_version` int(11) DEFAULT NULL,
  `checksum` varchar(255) DEFAULT NULL COMMENT '文件校验和',
  `device` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `file_version_index` (`tid`,`version`),
  KEY `file_rid_index` (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=3695068 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_grid_group`
--

DROP TABLE IF EXISTS `a1_grid_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_grid_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `uid` varchar(255) CHARACTER SET latin1 NOT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `grid_map` mediumblob NOT NULL,
  PRIMARY KEY (`id`),
  KEY `grid_group_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=170056 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_mobile_version`
--

DROP TABLE IF EXISTS `a1_mobile_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_mobile_version` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `version` varchar(45) NOT NULL,
  `creator` varchar(45) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_navbar_item`
--

DROP TABLE IF EXISTS `a1_navbar_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_navbar_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `tid` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` text,
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `navbar_index` (`uid`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_page`
--

DROP TABLE IF EXISTS `a1_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_page` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  `status` varchar(25) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `creator` varchar(255) DEFAULT NULL,
  `creator_name` varchar(255) DEFAULT NULL,
  `last_editor` varchar(255) DEFAULT NULL,
  `last_editor_name` varchar(255) DEFAULT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `page_index` (`pid`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=55964 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_page_version`
--

DROP TABLE IF EXISTS `a1_page_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_page_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  `version` int(11) NOT NULL DEFAULT '1',
  `title` varchar(255) DEFAULT NULL,
  `editor` varchar(255) DEFAULT NULL,
  `edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `content` mediumtext,
  `status` varchar(10) DEFAULT NULL,
  `size` bigint(20) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `page_version_index` (`tid`,`version`),
  KEY `page_rid_index` (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=1974308 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_page_view`
--

DROP TABLE IF EXISTS `a1_page_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_page_view` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `rid` int(11) NOT NULL DEFAULT '0',
  `item_type` varchar(10) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `display_name` varchar(255) DEFAULT NULL,
  `browse_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tracking_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `rid` (`rid`),
  KEY `tid` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=2978057 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_pan_space_application`
--

DROP TABLE IF EXISTS `a1_pan_space_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_pan_space_application` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `application_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `application_type` varchar(10) NOT NULL,
  `approve_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `new_size` bigint(20) unsigned NOT NULL,
  `original_size` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `uid_tree_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=90796 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_param`
--

DROP TABLE IF EXISTS `a1_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` varchar(50) DEFAULT NULL,
  `key` varchar(50) DEFAULT NULL,
  `value` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7740 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_picture`
--

DROP TABLE IF EXISTS `a1_picture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_picture` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_clb_version` int(11) DEFAULT NULL,
  `file_clb_id` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `clb_id` int(11) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `picture_index` (`file_clb_version`,`file_clb_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17110 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_resource`
--

DROP TABLE IF EXISTS `a1_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_resource` (
  `rid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `item_type` varchar(25) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_editor` varchar(255) DEFAULT NULL,
  `last_editor_name` varchar(255) DEFAULT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_version` int(11) NOT NULL DEFAULT '1',
  `tags` text NOT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `marked_users` blob NOT NULL,
  `bid` int(11) NOT NULL,
  `status` varchar(10) NOT NULL,
  `order_type` int(10) unsigned NOT NULL,
  `description` text,
  `size` bigint(20) unsigned DEFAULT '0',
  `shared` int(11) DEFAULT '0' COMMENT '文件是否分享 0未分享 1已分享',
  PRIMARY KEY (`rid`),
  KEY `resource_tid_bid_index` (`tid`,`bid`)
) ENGINE=InnoDB AUTO_INCREMENT=5799761 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_search_docweight`
--

DROP TABLE IF EXISTS `a1_search_docweight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_search_docweight` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `rid` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  `time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=188113 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_searchedlog`
--

DROP TABLE IF EXISTS `a1_searchedlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_searchedlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `item_type` varchar(255) DEFAULT NULL,
  `item_id` int(11) DEFAULT '0',
  `sequence` int(11) DEFAULT '0',
  `opername` varchar(255) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124382 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_searchlog`
--

DROP TABLE IF EXISTS `a1_searchlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_searchlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `item_type` varchar(255) DEFAULT NULL,
  `item_id` int(11) DEFAULT '0',
  `sequence` int(11) DEFAULT '0',
  `opername` varchar(255) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11817 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_shortcut`
--

DROP TABLE IF EXISTS `a1_shortcut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_shortcut` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sequence` int(11) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  `tgid` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  `color` varchar(45) DEFAULT NULL,
  `creator` varchar(45) DEFAULT NULL,
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=222 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_starmark`
--

DROP TABLE IF EXISTS `a1_starmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_starmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rid` int(11) NOT NULL,
  `tid` int(11) NOT NULL,
  `uid` varchar(255) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `stark_mark_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=8060 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_tag`
--

DROP TABLE IF EXISTS `a1_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `count` int(11) NOT NULL DEFAULT '0',
  `group_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `pinyin` varchar(255) DEFAULT NULL,
  `sequence` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `tag_index` (`tid`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=244601 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_tag_group`
--

DROP TABLE IF EXISTS `a1_tag_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_tag_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_group_index` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=17242 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_tag_item`
--

DROP TABLE IF EXISTS `a1_tag_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_tag_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `tgid` int(11) NOT NULL,
  `rid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_item_index` (`tid`,`tgid`)
) ENGINE=InnoDB AUTO_INCREMENT=2950654 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_team_space_application`
--

DROP TABLE IF EXISTS `a1_team_space_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_team_space_application` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `uid` varchar(45) NOT NULL,
  `application_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `application_type` varchar(10) NOT NULL,
  `approve_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `new_size` bigint(20) unsigned NOT NULL,
  `original_size` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tid_tree_index` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=1894 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_team_space_config`
--

DROP TABLE IF EXISTS `a1_team_space_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_team_space_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` varchar(45) NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_uid` varchar(45) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1460 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_user_config`
--

DROP TABLE IF EXISTS `a1_user_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_user_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `max_create_team` int(10) unsigned NOT NULL,
  `config_uid` varchar(45) DEFAULT NULL,
  `config_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_user_copy_count`
--

DROP TABLE IF EXISTS `a1_user_copy_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_user_copy_count` (
  `uid` varchar(45) NOT NULL,
  `copy_date` varchar(20) NOT NULL,
  `count` varchar(45) NOT NULL,
  PRIMARY KEY (`uid`,`copy_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_user_guide`
--

DROP TABLE IF EXISTS `a1_user_guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_user_guide` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `module` varchar(20) NOT NULL,
  `step` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_guide_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=103292 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_user_preferences`
--

DROP TABLE IF EXISTS `a1_user_preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_user_preferences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `refresh_team_mode` varchar(255) NOT NULL,
  `default_team` int(11) NOT NULL,
  `access_home_mode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_pre_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=7502 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_user_sim`
--

DROP TABLE IF EXISTS `a1_user_sim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_user_sim` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `simuid` varchar(255) NOT NULL,
  `score` int(11) DEFAULT '0',
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_user_sort_preference`
--

DROP TABLE IF EXISTS `a1_user_sort_preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_user_sort_preference` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `sort_type` varchar(45) NOT NULL,
  `last_modify` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3830 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `a1_userinterest`
--

DROP TABLE IF EXISTS `a1_userinterest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `a1_userinterest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `interest` varchar(255) NOT NULL,
  `score` int(11) DEFAULT '0',
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `apn_user`
--

DROP TABLE IF EXISTS `apn_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apn_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `username` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_activity`
--

DROP TABLE IF EXISTS `ddl_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '活动名称',
  `remark` varchar(1000) DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0新建,1进行中,2已结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_folder_path`
--

DROP TABLE IF EXISTS `ddl_folder_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_folder_path` (
  `tid` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  `ancestor_rid` int(10) unsigned NOT NULL,
  `length` int(10) unsigned NOT NULL,
  PRIMARY KEY (`tid`,`rid`,`ancestor_rid`) USING BTREE,
  KEY `ancestor_rid_tid_index` (`tid`,`ancestor_rid`),
  KEY `rid_ancestorrid_index` (`rid`,`ancestor_rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_item_mapping`
--

DROP TABLE IF EXISTS `ddl_item_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_item_mapping` (
  `tid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `item_type` varchar(45) COLLATE utf8_bin NOT NULL,
  `item_id` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`tid`,`item_type`,`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27607 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_jounal`
--

DROP TABLE IF EXISTS `ddl_jounal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_jounal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tid` int(11) DEFAULT NULL,
  `jid` int(11) DEFAULT NULL,
  `device` varchar(300) DEFAULT NULL,
  `operation` varchar(50) DEFAULT NULL,
  `fid` int(11) DEFAULT NULL,
  `fver` int(11) DEFAULT NULL,
  `occur_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_dir` bit(1) DEFAULT NULL,
  `path` varchar(2000) DEFAULT NULL,
  `to_path` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `jid_index` (`tid`,`jid`)
) ENGINE=InnoDB AUTO_INCREMENT=370664 DEFAULT CHARSET=utf8 COMMENT='同步盘操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_lottery`
--

DROP TABLE IF EXISTS `ddl_lottery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_lottery` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(100) NOT NULL,
  `date` varchar(15) NOT NULL,
  `drawed_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gift_level` int(11) NOT NULL,
  `lottery_name` varchar(256) NOT NULL,
  `gift_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_date_idx` (`user`,`date`),
  KEY `user_idx` (`user`)
) ENGINE=InnoDB AUTO_INCREMENT=70191 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_lottery_delivery`
--

DROP TABLE IF EXISTS `ddl_lottery_delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_lottery_delivery` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(256) NOT NULL,
  `real_name` varchar(50) NOT NULL,
  `user_address` text NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  `gift_content` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_delivery_user` (`user`(255))
) ENGINE=InnoDB AUTO_INCREMENT=578 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_page_lock`
--

DROP TABLE IF EXISTS `ddl_page_lock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_page_lock` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `uid` varchar(45) NOT NULL,
  `last_access` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `max_version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=5799719 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ddl_space_gained`
--

DROP TABLE IF EXISTS `ddl_space_gained`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ddl_space_gained` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) DEFAULT NULL COMMENT '创建者',
  `obj_id` int(11) DEFAULT NULL COMMENT '活动等关联的ID',
  `obj_type` int(11) DEFAULT NULL COMMENT '1代表活动',
  `space_type` int(11) DEFAULT NULL COMMENT '1可分配团队空间 2自动分配的盘空间 ',
  `size` bigint(20) DEFAULT NULL COMMENT '空间大小',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uor` (`uid`,`obj_id`,`remark`)
) ENGINE=InnoDB AUTO_INCREMENT=103141 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dmldata_form`
--

DROP TABLE IF EXISTS `dmldata_form`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dmldata_form` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `formid` varchar(255) DEFAULT NULL,
  `dmldesc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dmldata_input`
--

DROP TABLE IF EXISTS `dmldata_input`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dmldata_input` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `inputname` varchar(255) DEFAULT NULL,
  `inputtype` varchar(255) DEFAULT NULL,
  `formid` varchar(255) DEFAULT NULL,
  `dmldesc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pan_share_resource`
--

DROP TABLE IF EXISTS `pan_share_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pan_share_resource` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `share_uid` varchar(45) NOT NULL,
  `share_path` text NOT NULL,
  `pan_share_id` varchar(45) NOT NULL,
  `password` varchar(10) DEFAULT NULL,
  `share_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire_millis` bigint(20) unsigned DEFAULT NULL,
  `download_count` int(10) unsigned DEFAULT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001338 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `share_resource`
--

DROP TABLE IF EXISTS `share_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `share_resource` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `share_uid` varchar(45) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_editor` varchar(45) NOT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `password` varchar(45) DEFAULT NULL,
  `download_count` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=5799473 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sphinx_counter`
--

DROP TABLE IF EXISTS `sphinx_counter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sphinx_counter` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `max_doc_id` int(10) unsigned NOT NULL,
  `counter` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_activation`
--

DROP TABLE IF EXISTS `vwb_activation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_activation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `encode` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(45) NOT NULL DEFAULT '',
  `tname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11138 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_app_info`
--

DROP TABLE IF EXISTS `vwb_app_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_app_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `context` varchar(45) NOT NULL,
  `description` text NOT NULL,
  `issys` tinyint(1) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44597 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_app_map`
--

DROP TABLE IF EXISTS `vwb_app_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_app_map` (
  `resourceId` int(10) unsigned NOT NULL,
  `context` varchar(45) NOT NULL,
  `pageId` varchar(45) NOT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_banner`
--

DROP TABLE IF EXISTS `vwb_banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_banner` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL,
  `creator` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `createdTime` datetime DEFAULT NULL,
  `dirName` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `leftName` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `rightName` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `middleName` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `pageId` int(11) NOT NULL,
  `ownedtype` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'system',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_blacklist`
--

DROP TABLE IF EXISTS `vwb_blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_blacklist` (
  `page_id` int(11) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`page_id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_browse_log`
--

DROP TABLE IF EXISTS `vwb_browse_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_browse_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `item_id` int(11) DEFAULT NULL,
  `browse_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `display_name` varchar(255) DEFAULT NULL,
  `tracking_id` varchar(255) DEFAULT NULL,
  `item_type` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tracking_id` (`tracking_id`)
) ENGINE=InnoDB AUTO_INCREMENT=260373 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_clb`
--

DROP TABLE IF EXISTS `vwb_clb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_clb` (
  `RESOURCEID` int(11) NOT NULL,
  `CLBID` int(11) NOT NULL DEFAULT '0',
  `FILENAME` varchar(100) NOT NULL DEFAULT '',
  `SUFFIX` varchar(50) DEFAULT NULL,
  `CHANGE_TIME` datetime DEFAULT NULL,
  `LENGTH` int(11) DEFAULT NULL,
  `change_by` varchar(255) DEFAULT NULL,
  `VERSION` int(11) NOT NULL,
  `CHANGE_NOTE` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `DCT_ATT_CHANGE_TIME_IX` (`CHANGE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=420 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_client_invite`
--

DROP TABLE IF EXISTS `vwb_client_invite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_client_invite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `invitor` varchar(255) NOT NULL DEFAULT '',
  `invitee` varchar(255) NOT NULL DEFAULT '',
  `invitetime` timestamp NULL DEFAULT NULL,
  `accepttime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `invitee` (`invitee`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_client_message`
--

DROP TABLE IF EXISTS `vwb_client_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_client_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL DEFAULT '',
  `readed` tinyint(3) NOT NULL DEFAULT '0',
  `createtime` timestamp NULL DEFAULT NULL,
  `readtime` timestamp NULL DEFAULT NULL,
  `message` text,
  PRIMARY KEY (`id`),
  KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_collection`
--

DROP TABLE IF EXISTS `vwb_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_collection` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `resource_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  `sequence` int(10) DEFAULT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  `default_auth` varchar(45) NOT NULL DEFAULT '',
  `grid_column` int(10) DEFAULT NULL,
  `home_mode` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28436 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_collection_acl`
--

DROP TABLE IF EXISTS `vwb_collection_acl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_collection_acl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `cid` varchar(55) NOT NULL DEFAULT '0',
  `uid` varchar(55) NOT NULL DEFAULT '0',
  `auth` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1597 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_collection_element`
--

DROP TABLE IF EXISTS `vwb_collection_element`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_collection_element` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `cid` int(10) unsigned NOT NULL DEFAULT '0',
  `resource_type` varchar(45) NOT NULL DEFAULT '',
  `title` varchar(255) NOT NULL DEFAULT '',
  `creator` varchar(45) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modifier` varchar(45) NOT NULL DEFAULT '',
  `modify_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `resource_id` int(10) unsigned NOT NULL DEFAULT '0',
  `version` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8671 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_comment`
--

DROP TABLE IF EXISTS `vwb_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `receiver` varchar(50) DEFAULT NULL,
  `content` text,
  `create_time` datetime DEFAULT NULL,
  `sender` varchar(50) DEFAULT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  `rid` int(10) unsigned NOT NULL,
  `item_type` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `comment_index` (`tid`,`item_type`)
) ENGINE=InnoDB AUTO_INCREMENT=27903 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_dfile`
--

DROP TABLE IF EXISTS `vwb_dfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_dfile` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(100) NOT NULL DEFAULT '',
  `cid` int(10) unsigned NOT NULL DEFAULT '0',
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `clb_id` int(11) NOT NULL DEFAULT '0',
  `status` varchar(45) DEFAULT 'available',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5753 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_dfile_ref`
--

DROP TABLE IF EXISTS `vwb_dfile_ref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_dfile_ref` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `page_rid` int(10) unsigned NOT NULL,
  `file_rid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `file_ref_index` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=12094 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_dfile_version`
--

DROP TABLE IF EXISTS `vwb_dfile_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_dfile_version` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fid` int(10) unsigned NOT NULL DEFAULT '0',
  `version` int(10) unsigned NOT NULL DEFAULT '0',
  `size` int(10) unsigned NOT NULL DEFAULT '0',
  `change_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `change_by` varchar(100) NOT NULL DEFAULT '',
  `title` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5781 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_dpage_content_info`
--

DROP TABLE IF EXISTS `vwb_dpage_content_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_dpage_content_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `resourceid` int(11) NOT NULL,
  `version` int(11) NOT NULL DEFAULT '1',
  `change_time` datetime DEFAULT NULL,
  `change_note` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `content` mediumtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `change_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `dpage_content_info_unique_version` (`tid`,`resourceid`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=5644 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_dpage_draft`
--

DROP TABLE IF EXISTS `vwb_dpage_draft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_dpage_draft` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `rid` int(10) unsigned NOT NULL,
  `uid` varchar(45) NOT NULL DEFAULT '',
  `type` varchar(45) NOT NULL DEFAULT '',
  `content` mediumtext,
  `modify_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `title` text,
  PRIMARY KEY (`id`),
  KEY `draft_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=123685 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_email_notify`
--

DROP TABLE IF EXISTS `vwb_email_notify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_email_notify` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `subscriber` varchar(45) NOT NULL,
  `receiver` varchar(45) NOT NULL,
  `rec_time` varchar(45) NOT NULL,
  `resourceId` varchar(255) DEFAULT NULL,
  `page_title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_event`
--

DROP TABLE IF EXISTS `vwb_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `actor` varchar(80) NOT NULL,
  `operation` varchar(80) NOT NULL,
  `target` varchar(80) NOT NULL,
  `target_type` varchar(20) NOT NULL,
  `target_version` int(11) NOT NULL,
  `recipients` text,
  `message` text,
  `occur_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `event_index` (`tid`,`actor`,`target`)
) ENGINE=InnoDB AUTO_INCREMENT=3756089 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `vwb_file_last_version`
--

DROP TABLE IF EXISTS `vwb_file_last_version`;
/*!50001 DROP VIEW IF EXISTS `vwb_file_last_version`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `vwb_file_last_version` (
 `tid` tinyint NOT NULL,
  `fid` tinyint NOT NULL,
  `last_version` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `vwb_grid`
--

DROP TABLE IF EXISTS `vwb_grid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_grid` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) DEFAULT NULL,
  `cid` int(11) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `title` varchar(256) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=367 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_grid_item`
--

DROP TABLE IF EXISTS `vwb_grid_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_grid_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) DEFAULT NULL,
  `gid` int(11) DEFAULT NULL,
  `resource_type` varchar(45) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=523 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_invitation`
--

DROP TABLE IF EXISTS `vwb_invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_invitation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `encode` varchar(255) NOT NULL DEFAULT '',
  `inviter` varchar(255) NOT NULL DEFAULT '',
  `invitee` varchar(255) NOT NULL DEFAULT '',
  `team` int(10) unsigned NOT NULL DEFAULT '0',
  `accept_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `invite_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` varchar(45) NOT NULL DEFAULT '',
  `message` text,
  PRIMARY KEY (`id`),
  KEY `invitation_index` (`invitee`,`team`)
) ENGINE=InnoDB AUTO_INCREMENT=38223 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_message`
--

DROP TABLE IF EXISTS `vwb_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_message` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `message_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `publisher` int(10) unsigned NOT NULL DEFAULT '0',
  `publisher_type` varchar(255) NOT NULL DEFAULT '',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31818 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_message_body`
--

DROP TABLE IF EXISTS `vwb_message_body`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_message_body` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL DEFAULT '',
  `title` varchar(255) DEFAULT '',
  `digest` text,
  `time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `from` varchar(255) NOT NULL DEFAULT '',
  `remark` text,
  `tid` int(11) NOT NULL DEFAULT '0',
  `rid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61994 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_myspace`
--

DROP TABLE IF EXISTS `vwb_myspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_myspace` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `eid` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `resourceId` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_notice`
--

DROP TABLE IF EXISTS `vwb_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `notice_type` varchar(45) NOT NULL,
  `recipient` varchar(255) NOT NULL,
  `reason` varchar(20) NOT NULL,
  `actor_id` varchar(80) NOT NULL,
  `actor_name` varchar(80) NOT NULL,
  `actor_url` varchar(255) DEFAULT NULL,
  `operation` varchar(20) NOT NULL,
  `target_id` varchar(45) NOT NULL,
  `target_type` varchar(20) NOT NULL,
  `target_name` varchar(255) DEFAULT NULL,
  `target_url` varchar(255) DEFAULT NULL,
  `target_version` int(11) NOT NULL,
  `message` text,
  `occur_time` timestamp NOT NULL DEFAULT '2009-12-31 16:00:00',
  `addition` text,
  `relative_id` varchar(80) DEFAULT NULL,
  `relative_name` varchar(255) DEFAULT NULL,
  `relative_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `event_id_index` (`event_id`),
  KEY `notice_index` (`tid`,`notice_type`,`recipient`),
  KEY `target_id_notice_type` (`target_id`,`notice_type`)
) ENGINE=InnoDB AUTO_INCREMENT=7759470 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_oauth_accessor`
--

DROP TABLE IF EXISTS `vwb_oauth_accessor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_oauth_accessor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `consumer_key` varchar(255) NOT NULL DEFAULT '0',
  `request_token` varchar(255) DEFAULT '',
  `token_secret` varchar(255) DEFAULT NULL,
  `request_create_time` timestamp NULL DEFAULT NULL,
  `access_token` varchar(255) DEFAULT NULL,
  `access_create_time` timestamp NULL DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `authorized` tinyint(3) NOT NULL DEFAULT '0',
  `screen_name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2774368 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_oauth_consumer`
--

DROP TABLE IF EXISTS `vwb_oauth_consumer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_oauth_consumer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) NOT NULL DEFAULT '',
  `secret` varchar(255) NOT NULL DEFAULT '',
  `callback_url` varchar(255) DEFAULT NULL,
  `enable` tinyint(3) NOT NULL DEFAULT '1',
  `xauth` tinyint(3) NOT NULL DEFAULT '0',
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_person_contacts`
--

DROP TABLE IF EXISTS `vwb_person_contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_person_contacts` (
  `uid` varchar(45) NOT NULL DEFAULT '',
  `main_email` varchar(45) NOT NULL DEFAULT '',
  `option_email` varchar(45) DEFAULT '',
  `name` varchar(45) NOT NULL DEFAULT '',
  `orgnization` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `sex` varchar(45) DEFAULT NULL,
  `telephone` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `photo` varchar(45) DEFAULT NULL,
  `birthday` varchar(45) DEFAULT NULL,
  `weibo` varchar(45) DEFAULT NULL,
  `pinyin` varchar(55) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=366 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_portal_page`
--

DROP TABLE IF EXISTS `vwb_portal_page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_portal_page` (
  `resourceId` int(11) NOT NULL DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  UNIQUE KEY `index` (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_portal_portlets`
--

DROP TABLE IF EXISTS `vwb_portal_portlets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_portal_portlets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resourceId` int(11) DEFAULT NULL,
  `context` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `resourceId` (`resourceId`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=5128 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_properties`
--

DROP TABLE IF EXISTS `vwb_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `strName` varchar(255) DEFAULT NULL,
  `strValue` varchar(255) DEFAULT NULL,
  `iSiteNum` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=514103 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_push_message`
--

DROP TABLE IF EXISTS `vwb_push_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_push_message` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '',
  `email` varchar(100) DEFAULT '',
  `telephone` varchar(15) DEFAULT NULL,
  `orgnization` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT '',
  `mobile` varchar(15) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `weibo` varchar(45) DEFAULT NULL,
  `sex` varchar(2) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `photo` longblob,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `operation` int(15) DEFAULT NULL,
  `createdby` varchar(100) DEFAULT '',
  `createtime` datetime DEFAULT NULL,
  `modifiedby` varchar(100) DEFAULT '',
  `modifytime` datetime DEFAULT NULL,
  `teamid` int(15) DEFAULT NULL,
  `issystemuser` int(15) DEFAULT NULL,
  `version` int(15) DEFAULT NULL,
  `notificationid` varchar(100) DEFAULT NULL,
  `status` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_request_num`
--

DROP TABLE IF EXISTS `vwb_request_num`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_request_num` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `date` varchar(50) DEFAULT NULL,
  `count` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_resource_acl`
--

DROP TABLE IF EXISTS `vwb_resource_acl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_resource_acl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `resourceId` int(11) NOT NULL,
  `type` varchar(10) DEFAULT NULL,
  `eid` varchar(50) NOT NULL,
  `action` varchar(255) NOT NULL,
  `resourceType` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_resource_info`
--

DROP TABLE IF EXISTS `vwb_resource_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_resource_info` (
  `id` int(11) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  `type` varchar(64) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `trail` int(11) DEFAULT '0',
  `parent` int(11) DEFAULT '0',
  `left_menu` int(11) DEFAULT '0',
  `top_menu` int(11) DEFAULT '0',
  `footer` int(11) DEFAULT '0',
  `banner` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `acl` int(11) DEFAULT '0',
  `cid` int(11) DEFAULT NULL,
  `sphinx_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `last_editor` varchar(255) DEFAULT NULL,
  `last_edit_time` datetime DEFAULT NULL,
  `last_version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`sphinx_id`),
  UNIQUE KEY `id` (`tid`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37384 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_share_access`
--

DROP TABLE IF EXISTS `vwb_share_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_share_access` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `uid` varchar(50) NOT NULL,
  `tid` int(12) NOT NULL,
  `fid` int(12) NOT NULL,
  `password` varchar(24) NOT NULL,
  `create_time` datetime NOT NULL,
  `valid_of_days` int(12) NOT NULL,
  `clb_id` int(12) NOT NULL,
  `fetch_file_code` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `share_access_index` (`tid`,`clb_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2490 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_share_acl`
--

DROP TABLE IF EXISTS `vwb_share_acl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_share_acl` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hash` varchar(45) NOT NULL,
  `URL` varchar(256) NOT NULL,
  `accessTime` bigint(20) unsigned NOT NULL,
  `shareTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_shortcut`
--

DROP TABLE IF EXISTS `vwb_shortcut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_shortcut` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL DEFAULT '',
  `resource_id` varchar(45) DEFAULT NULL,
  `sequence` int(10) NOT NULL DEFAULT '0',
  `collection_id` int(10) unsigned NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  `resource_type` varchar(45) DEFAULT 'DPage',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=601 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_subscription`
--

DROP TABLE IF EXISTS `vwb_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_subscription` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `publisher` int(10) unsigned NOT NULL DEFAULT '0',
  `publisher_type` varchar(45) NOT NULL DEFAULT '',
  `policy` varchar(45) NOT NULL DEFAULT '',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `subscription_inde` (`user_id`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=7230 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_team`
--

DROP TABLE IF EXISTS `vwb_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` varchar(255) DEFAULT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `display_name` varchar(255) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `creator` varchar(45) NOT NULL DEFAULT '',
  `type` varchar(45) DEFAULT 'common',
  `access_type` varchar(20) NOT NULL,
  `default_member_auth` varchar(20) NOT NULL,
  `vmtdn` varchar(255) DEFAULT NULL,
  `team_default_view` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `team_index` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=137452 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_team_acl`
--

DROP TABLE IF EXISTS `vwb_team_acl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_team_acl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `uid` varchar(50) NOT NULL DEFAULT '0',
  `auth` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `team_acl_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=186389 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_team_applicant`
--

DROP TABLE IF EXISTS `vwb_team_applicant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_team_applicant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `tid` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `apply_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `i_know` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8016 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_team_create_info`
--

DROP TABLE IF EXISTS `vwb_team_create_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_team_create_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `param_key` varchar(45) NOT NULL,
  `param_value` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6978 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_team_member`
--

DROP TABLE IF EXISTS `vwb_team_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_team_member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `uid` varchar(45) NOT NULL DEFAULT '',
  `title` varchar(45) NOT NULL DEFAULT '',
  `sequence` int(11) DEFAULT '-1',
  `team_access` timestamp NOT NULL DEFAULT '2009-12-31 16:00:00',
  `person_access` timestamp NOT NULL DEFAULT '2009-12-31 16:00:00',
  `monitor_access` timestamp NOT NULL DEFAULT '2009-12-31 16:00:00',
  `team_notice_count` int(11) NOT NULL DEFAULT '0',
  `person_notice_count` int(11) NOT NULL DEFAULT '0',
  `monitor_notice_count` int(11) NOT NULL DEFAULT '0',
  `team_event_ids` text,
  `person_event_ids` text,
  `monitor_event_ids` text,
  `createdby` varchar(50) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `team_member_index` (`tid`,`uid`),
  KEY `team_member_uid_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=186389 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_team_relations`
--

DROP TABLE IF EXISTS `vwb_team_relations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_team_relations` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `allUserId` varchar(100) DEFAULT '',
  `systemUserId` varchar(100) DEFAULT '',
  `count` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_user_ext`
--

DROP TABLE IF EXISTS `vwb_user_ext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_user_ext` (
  `uid` varchar(45) NOT NULL DEFAULT '',
  `name` varchar(45) NOT NULL DEFAULT '',
  `email` varchar(45) DEFAULT NULL,
  `orgnization` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `sex` varchar(45) DEFAULT NULL,
  `telephone` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `photo` varchar(45) DEFAULT NULL,
  `birthday` varchar(45) DEFAULT NULL,
  `weibo` varchar(255) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pinyin` varchar(255) DEFAULT NULL,
  `regist_time` datetime DEFAULT NULL,
  `frequent` int(15) DEFAULT NULL,
  `operation` int(15) DEFAULT NULL,
  `requestNum` int(15) DEFAULT NULL,
  `version` int(15) DEFAULT NULL,
  `modifytime` datetime DEFAULT NULL,
  `confirm_status` varchar(15) DEFAULT NULL,
  `unallocated_space` bigint(20) DEFAULT '0' COMMENT '未分配的空间',
  PRIMARY KEY (`id`,`uid`),
  KEY `pinyin_tree_index` (`pinyin`) USING BTREE,
  KEY `uid_tree_index` (`uid`(10)) USING BTREE,
  KEY `uid_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=128860 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_user_feedback`
--

DROP TABLE IF EXISTS `vwb_user_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_user_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `message` text NOT NULL,
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=704 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_user_history`
--

DROP TABLE IF EXISTS `vwb_user_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_user_history` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '',
  `email` varchar(100) DEFAULT '',
  `telephone` varchar(15) DEFAULT NULL,
  `orgnization` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT '',
  `mobile` varchar(15) DEFAULT NULL,
  `sex` varchar(2) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `weibo` varchar(45) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `photo` longblob,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `operation` int(15) DEFAULT NULL,
  `createdby` varchar(100) DEFAULT '',
  `createtime` datetime DEFAULT NULL,
  `modifiedby` varchar(100) DEFAULT '',
  `modifytime` datetime DEFAULT NULL,
  `version` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_user_outside`
--

DROP TABLE IF EXISTS `vwb_user_outside`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_user_outside` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '',
  `email` varchar(100) DEFAULT '',
  `telephone` varchar(15) DEFAULT NULL,
  `orgnization` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT '',
  `mobile` varchar(15) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `weibo` varchar(45) DEFAULT NULL,
  `sex` varchar(45) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `photo` longblob,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `operation` int(15) DEFAULT NULL,
  `createdby` varchar(100) DEFAULT '',
  `createtime` datetime DEFAULT NULL,
  `modifiedby` varchar(100) DEFAULT '',
  `modifytime` datetime DEFAULT NULL,
  `version` int(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vwb_user_to_be_processed`
--

DROP TABLE IF EXISTS `vwb_user_to_be_processed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vwb_user_to_be_processed` (
  `id` int(15) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '',
  `email` varchar(100) DEFAULT '',
  `telephone` varchar(15) DEFAULT NULL,
  `orgnization` varchar(255) DEFAULT '',
  `department` varchar(255) DEFAULT '',
  `mobile` varchar(15) DEFAULT NULL,
  `sex` varchar(2) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `weibo` varchar(45) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `photo` longblob,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `operation` int(15) DEFAULT NULL,
  `createdby` varchar(100) DEFAULT '',
  `createtime` timestamp NULL DEFAULT NULL,
  `modifiedby` varchar(100) DEFAULT '',
  `modifytime` timestamp NULL DEFAULT NULL,
  `version` int(15) DEFAULT NULL,
  `issystemuser` int(15) DEFAULT NULL,
  `teamid` int(15) DEFAULT NULL,
  `result` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `vwb_file_last_version`
--

/*!50001 DROP TABLE IF EXISTS `vwb_file_last_version`*/;
/*!50001 DROP VIEW IF EXISTS `vwb_file_last_version`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vwb_file_last_version` AS select `f`.`tid` AS `tid`,`v`.`fid` AS `fid`,max(`v`.`version`) AS `last_version` from (`vwb_dfile` `f` join `vwb_dfile_version` `v`) where (`v`.`fid` = `f`.`id`) group by `v`.`fid` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-06-04 23:39:05
