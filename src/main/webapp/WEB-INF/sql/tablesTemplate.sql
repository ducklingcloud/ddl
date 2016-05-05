DROP TABLE IF EXISTS `vwb_resource_info`;
CREATE TABLE `vwb_resource_info` (
  `id` int(11) NOT NULL default '0',
  `tid` int(11) NOT NULL default '0',
  `type` varchar(64) default NULL,
  `title` varchar(255) default NULL,
  `trail` int(11) default '0',
  `parent` int(11) default '0',
  `left_menu` int(11) default '0',
  `top_menu` int(11) default '0',
  `footer` int(11) default '0',
  `banner` int(11) default '0',
  `create_time` datetime default NULL,
  `creator` varchar(255) default NULL,
  `acl` int(11) default '0',
  `belong` int(11) default NULL,
  PRIMARY KEY  (`id`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

DROP TABLE IF EXISTS `vwb_dpage_content_info`;
CREATE TABLE `vwb_dpage_content_info` (
  `id` int(11) NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  `title` varchar(255) default NULL,
  `resourceid` int(11) NOT NULL,
  `version` int(11) NOT NULL default '1',
  `change_time` datetime default NULL,
  `change_note` varchar(100) character set utf8 collate utf8_bin default NULL,
  `content` mediumtext character set utf8 collate utf8_bin NOT NULL,
  `change_by` varchar(50) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dpage_content_info_unique_version` (`tid`,`resourceid`,`version`),
  KEY `dpage_content_info_ref_resourcea` (`resourceid`),
  CONSTRAINT `dpage_content_info_ref_resource` FOREIGN KEY (`resourceid`) REFERENCES `vwb_resource_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;


DROP TABLE IF EXISTS `vwb_resource_acl`;
CREATE TABLE `vwb_resource_acl` (
  `id` int(11) NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  `resourceId` int(11) NOT NULL,
  `type` varchar(10) default NULL,
  `eid` varchar(50) NOT NULL,
  `action` varchar(255) NOT NULL,
  `resourceType` varchar(10) NOT NULL,
  PRIMARY KEY  (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `vwb_banner`;
CREATE TABLE `vwb_banner` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(100) character set utf8 collate utf8_bin default NULL,
  `status` tinyint(1) NOT NULL default '0',
  `type` int(11) NOT NULL,
  `creator` varchar(100) character set utf8 collate utf8_bin default NULL,
  `createdTime` datetime default NULL,
  `dirName` varchar(100) character set utf8 collate utf8_bin default NULL,
  `leftName` varchar(100) character set utf8 collate utf8_bin default NULL,
  `rightName` varchar(100) character set utf8 collate utf8_bin default NULL,
  `middleName` varchar(100) character set utf8 collate utf8_bin default NULL,
  `pageId` int(11) NOT NULL,
  `ownedtype` varchar(100) character set utf8 collate utf8_bin default 'system',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

DROP TABLE IF EXISTS `vwb_myspace`;
CREATE TABLE `vwb_myspace` (
  `id` int(11) NOT NULL auto_increment,
  `eid` varchar(50) character set utf8 collate utf8_bin NOT NULL,
  `resourceId` int(11) NOT NULL,
  `name` varchar(255) default NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


DROP TABLE IF EXISTS `vwb_portal_page`;
CREATE TABLE `vwb_portal_page` (
  `resourceId` int(11) NOT NULL default '0',
  `title` varchar(255) default NULL,
  `uri` varchar(255) default NULL,
  `tid` int(11) NOT NULL default '0',
  UNIQUE KEY `index` (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

DROP TABLE IF EXISTS `vwb_portal_portlets`;
CREATE TABLE `vwb_portal_portlets` (
  `id` int(11) NOT NULL auto_increment,
  `resourceId` int(11) default NULL,
  `context` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `resourceId` (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;


DROP TABLE IF EXISTS `vwb_app_info`;
CREATE TABLE `vwb_app_info` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `context` varchar(45) NOT NULL,
  `description` text NOT NULL,
  `issys` tinyint(1) NOT NULL default '0',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;



DROP TABLE IF EXISTS `vwb_app_map`;
CREATE TABLE `vwb_app_map` (
  `resourceId` int(10) unsigned NOT NULL,
  `context` varchar(45) NOT NULL,
  `pageId` varchar(45) NOT NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;


DROP TABLE IF EXISTS `vwb_email_notify`;
CREATE TABLE `vwb_email_notify` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  `subscriber` varchar(45) NOT NULL,
  `receiver` varchar(45) NOT NULL,
  `rec_time` varchar(45) NOT NULL,
  `resourceId` varchar(255) default NULL,
  `page_title` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;



DROP TABLE IF EXISTS `vwb_clb`;
CREATE TABLE `vwb_clb` (
  `RESOURCEID` int(11) NOT NULL,
  `CLBID` int(11) NOT NULL default '0',
  `FILENAME` varchar(100) NOT NULL default '',
  `SUFFIX` varchar(50) default NULL,
  `CHANGE_TIME` datetime default NULL,
  `LENGTH` int(11) default NULL,
  `CHANGE_BY` varchar(50) character set utf8 collate utf8_bin default NULL,
  `VERSION` int(11) NOT NULL,
  `CHANGE_NOTE` varchar(100) character set utf8 collate utf8_bin default NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`tid`,`RESOURCEID`,`CLBID`,`VERSION`),
  KEY `DCT_ATT_CHANGE_TIME_IX` (`CHANGE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `vwb_share_acl`;
CREATE TABLE `vwb_share_acl` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `hash` varchar(45) NOT NULL,
  `URL` varchar(256) NOT NULL,
  `accessTime` bigint(20) unsigned NOT NULL,
  `shareTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `vwb_subscription`;
CREATE TABLE `vwb_subscription` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` varchar(255) NOT NULL default '',
  `publisher` int(10) unsigned NOT NULL default '0',
  `publisher_type` varchar(45) NOT NULL default '',
  `policy` varchar(45) NOT NULL default '',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `$vwb_page`;
CREATE TABLE  `$vwb_page` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `auth_type` int(10) unsigned NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `creator` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `vwb_message`;
CREATE TABLE `vwb_message` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `message_id` int(10) unsigned NOT NULL default '0',
  `user_id` varchar(255) NOT NULL default '',
  `status` int(10) unsigned NOT NULL default '0',
  `publisher` int(10) unsigned NOT NULL default '0',
  `publisher_type` varchar(255) NOT NULL default '',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

DROP TABLE IF EXISTS `vwb_message_body`;
CREATE TABLE `vwb_message_body` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `type` varchar(45) NOT NULL default '',
  `title` varchar(45) NOT NULL default '',
  `digest` text,
  `time` datetime NOT NULL default '0000-00-00 00:00:00',
  `from` varchar(255) NOT NULL default '',
  `page_id` int(11) default NULL,
  `remark` text,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;



DROP TABLE IF EXISTS `vwb_browse_log`;
CREATE TABLE `vwb_browse_log` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  `user_id` varchar(255) NOT NULL default '',
  `page_id` int(11) NOT NULL default '0',
  `browse_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `display_name` varchar(255) default NULL,
  `tracking_id` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `tracking_id` (`tracking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


DROP TABLE IF EXISTS `vwb_blacklist`;
CREATE TABLE `vwb_blacklist` (
  `page_id` int(11) NOT NULL default '0',
  `tid` int(11) default '0',
  PRIMARY KEY  (`page_id`, `tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `$vwb_comment`;
CREATE TABLE  `$vwb_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `receiver` varchar(50) DEFAULT NULL,
  `content` text,
  `create_time` datetime DEFAULT NULL,
  `page_id` int(11) DEFAULT NULL,
  `sender` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `vwb_shortcut`;
CREATE TABLE `vwb_shortcut` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `title` varchar(50) NOT NULL default '',
  `url` varchar(45) NOT NULL default '',
  `sequence` int(10) NOT NULL default '0',
  `collection_id` int(10) unsigned NOT NULL default '0',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

DROP TABLE IF EXISTS `vwb_collection`;
CREATE TABLE `vwb_collection` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `resource_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(45) NOT NULL default '',
  `description` text,
  `sequence` int(10) default NULL,
  `tid` int(11) NOT NULL default '0',
  `default_auth` varchar(45) NOT NULL DEFAULT ''
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


DROP TABLE IF EXISTS `vwb_collection_acl`;
CREATE TABLE `vwb_collection_acl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  `cid` varchar(55) NOT NULL default '0',
  `uid` varchar(55) NOT NULL default '0',
  `auth` varchar(45) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;


DROP TABLE IF EXISTS `vwb_team_acl`;
CREATE TABLE `vwb_team_acl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL default '0',,
  `uid` varchar(50) NOT NULL DEFAULT '0',
  `auth` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `vwb_team_member`;
CREATE TABLE `vwb_team_member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL default '0',,
  `uid` varchar(45) NOT NULL DEFAULT '',
  `title` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `$vwb_user_ext`;
CREATE TABLE  `$vwb_user_ext` (
  `uid` varchar(45) NOT NULL DEFAULT '',
  `name` varchar(45) NOT NULL DEFAULT '',
  `orgnization` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `sex` varchar(45) DEFAULT NULL,
  `telephone` varchar(45) DEFAULT NULL,
  `mobile` varchar(45) DEFAULT NULL,
  `qq` varchar(45) DEFAULT NULL,
  `msn` varchar(45) DEFAULT NULL,
  `address` varchar(45) DEFAULT NULL,
  `photo` varchar(45) DEFAULT NULL,
  `birthday` varchar(45) DEFAULT NULL,
  `weibo` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;