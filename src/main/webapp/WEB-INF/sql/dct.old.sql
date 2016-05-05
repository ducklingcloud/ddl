CREATE TABLE `dmldata_form` (
  `id` int(11) NOT NULL auto_increment,
  `formid` varchar(255) default NULL,
  `dmldesc` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

CREATE TABLE `dmldata_input` (
  `id` int(11) NOT NULL auto_increment,
  `inputname` varchar(255) default NULL,
  `inputtype` varchar(255) default NULL,
  `formid` varchar(255) default NULL,
  `dmldesc` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_properties` (
  `id` int(11) NOT NULL auto_increment,
  `strName` varchar(255) default NULL,
  `strValue` varchar(255) default NULL,
  `iSiteNum` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `vwb_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `state` varchar(255) DEFAULT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `display_name` varchar(255) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `creator` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_resource_info` (
  `id` int(11) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  `type` varchar(64) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `belong` int(11) DEFAULT NULL,
  `sphinx_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`sphinx_id`),
  UNIQUE KEY `id` (`tid`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

CREATE TABLE  `vwb_dpage_draft` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `pid` int(10) unsigned NOT NULL DEFAULT '0',
  `uid` varchar(45) NOT NULL DEFAULT '',
  `type` varchar(45) NOT NULL DEFAULT '',
  `content` mediumtext,
  `modify_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `title` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  UNIQUE KEY `dpage_content_info_unique_version` (`tid`,`resourceid`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

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

CREATE TABLE `vwb_myspace` (
  `id` int(11) NOT NULL auto_increment,
  `eid` varchar(50) character set utf8 collate utf8_bin NOT NULL,
  `resourceId` int(11) NOT NULL,
  `name` varchar(255) default NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


CREATE TABLE `vwb_portal_page` (
  `resourceId` int(11) NOT NULL default '0',
  `title` varchar(255) default NULL,
  `uri` varchar(255) default NULL,
  `tid` int(11) NOT NULL default '0',
  UNIQUE KEY `index` (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

CREATE TABLE `vwb_portal_portlets` (
  `id` int(11) NOT NULL auto_increment,
  `resourceId` int(11) default NULL,
  `context` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `resourceId` (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

CREATE TABLE `vwb_app_info` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `context` varchar(45) NOT NULL,
  `description` text NOT NULL,
  `issys` tinyint(1) NOT NULL default '0',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

CREATE TABLE `vwb_app_map` (
  `resourceId` int(10) unsigned NOT NULL,
  `context` varchar(45) NOT NULL,
  `pageId` varchar(45) NOT NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`resourceId`,`tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

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

CREATE TABLE `vwb_clb` (
  `RESOURCEID` int(11) NOT NULL,
  `CLBID` int(11) NOT NULL default '0',
  `FILENAME` varchar(100) NOT NULL default '',
  `SUFFIX` varchar(50) default NULL,
  `CHANGE_TIME` datetime default NULL,
  `LENGTH` int(11) default NULL,
  `change_by` varchar(255) default NULL,
  `VERSION` int(11) NOT NULL,
  `CHANGE_NOTE` varchar(100) character set utf8 collate utf8_bin default NULL,
  `ID` int(10) unsigned NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`ID`),
  KEY `DCT_ATT_CHANGE_TIME_IX` (`CHANGE_TIME`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


DROP TABLE IF EXISTS `vwb_share_acl`;
CREATE TABLE `vwb_share_acl` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `hash` varchar(45) NOT NULL,
  `URL` varchar(256) NOT NULL,
  `accessTime` bigint(20) unsigned NOT NULL,
  `shareTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_subscription` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` varchar(255) NOT NULL default '',
  `publisher` int(10) unsigned NOT NULL default '0',
  `publisher_type` varchar(45) NOT NULL default '',
  `policy` varchar(45) NOT NULL default '',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

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

CREATE TABLE `vwb_message_body` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `type` varchar(45) NOT NULL default '',
  `title` varchar(255) NOT NULL default '',
  `digest` text,
  `time` datetime NOT NULL default '0000-00-00 00:00:00',
  `from` varchar(255) NOT NULL default '',
  `page_id` int(11) default NULL,
  `remark` text,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

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

CREATE TABLE `vwb_blacklist` (
  `page_id` int(11) NOT NULL default '0',
  `tid` int(11) default '0',
  PRIMARY KEY  (`page_id`, `tid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE  `vwb_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `receiver` varchar(50) DEFAULT NULL,
  `content` text,
  `create_time` datetime DEFAULT NULL,
  `page_id` int(11) DEFAULT NULL,
  `sender` varchar(50) DEFAULT NULL,
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_shortcut` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `title` varchar(255) NOT NULL default '',
  `url` varchar(45) NOT NULL default '',
  `sequence` int(10) NOT NULL default '0',
  `collection_id` int(10) unsigned NOT NULL default '0',
  `tid` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;

CREATE TABLE `vwb_collection` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `resource_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `description` text,
  `sequence` int(10) default NULL,
  `tid` int(11) NOT NULL default '0',
  `default_auth` varchar(45) NOT NULL DEFAULT '',
  `grid_column` int(10) NOT NULL default 3, 
  `home_mode` varchar(45) default 'list',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `vwb_grid` (
  `id` int(11) NOT NULL auto_increment,
  `tid` int(11) default NULL,
  `cid` int(11) default NULL,
  `sequence` int(11) default NULL,
  `title` varchar(256) default NULL,
  `type` varchar(45) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_grid_item` (
  `id` int(11) NOT NULL auto_increment,
  `tid` int(11) default NULL,
  `gid` int(11) default NULL,
  `resource_type` varchar(45) default NULL,
  `resource_id` int(11) default NULL,
  `sequence` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_collection_acl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `tid` int(11) NOT NULL default '0',
  `cid` varchar(55) NOT NULL default '0',
  `uid` varchar(55) NOT NULL default '0',
  `auth` varchar(45) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

CREATE TABLE `vwb_team_acl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL default '0',
  `uid` varchar(50) NOT NULL DEFAULT '0',
  `auth` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_team_member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL default '0',
  `uid` varchar(45) NOT NULL DEFAULT '',
  `title` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_user_ext` (
  `uid` varchar(45) NOT NULL default '',
  `name` varchar(45) NOT NULL default '',
  `orgnization` varchar(255) default NULL,
  `department` varchar(255) default NULL,
  `sex` varchar(45) default NULL,
  `telephone` varchar(45) default NULL,
  `mobile` varchar(45) default NULL,
  `qq` varchar(45) default NULL,
  `msn` varchar(45) default NULL,
  `address` varchar(45) default NULL,
  `photo` varchar(45) default NULL,
  `birthday` varchar(45) default NULL,
  `weibo` varchar(45) default NULL,
  `pinyin` varchar(55) default NULL,
  `id` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`id`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE  `vwb_activation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `encode` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_invitation` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `encode` varchar(255) NOT NULL default '',
  `inviter` varchar(255) NOT NULL default '',
  `invitee` varchar(255) NOT NULL default '',
  `team` int(10) unsigned NOT NULL default '0',
  `accept_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `invite_time` timestamp NOT NULL default '0000-00-00 00:00:00',
  `status` varchar(45) NOT NULL default '',
  `message` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE  `vwb_dfile` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(100) NOT NULL DEFAULT '',
  `cid` int(10) unsigned NOT NULL DEFAULT '0',
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `clb_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `vwb_dfile_ref` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `pid` int(10) unsigned NOT NULL DEFAULT '0',
  `fid` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `vwb_dfile_version` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fid` int(10) unsigned NOT NULL DEFAULT '0',
  `version` int(10) unsigned NOT NULL DEFAULT '0',
  `size` int(10) unsigned NOT NULL DEFAULT '0',
  `change_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `change_by` varchar(100) NOT NULL DEFAULT '',
  `title` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE  `vwb_collection_element` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_user_feedback` (
  `id` int(11) NOT NULL auto_increment,
  `email` varchar(50) NOT NULL,
  `message` text NOT NULL,
  `send_time` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


DROP VIEW IF EXISTS `vwb_file_last_version`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `vwb_file_last_version` AS select `f`.`tid` AS `tid`,`v`.`fid` AS `fid`,max(`v`.`version`) AS `last_version` from (`vwb_dfile` `f` join `vwb_dfile_version` `v`) where (`v`.`fid` = `f`.`id`) group by `v`.`fid`;


DELIMITER $$
DROP PROCEDURE IF EXISTS `insert_dfile_version_and_ref` $$
CREATE PROCEDURE `insert_dfile_version_and_ref`(in uid varchar(255) CHARACTER SET utf8 ,in version int(11),in fid int(11),in pid int(11),
in cid int(11),in tid int(11),in title varchar(255) CHARACTER SET utf8,in size int(11),in curr_time timestamp )
BEGIN
declare v_fid int(11);
declare v_version int(11);
select version into v_version;
select fid into v_fid;
insert into vwb_dfile_version(fid,title,version,size,change_by,change_time) VALUES(v_fid,title,version,size,uid,curr_time);
if (pid!=0) then
insert into vwb_dfile_ref(fid,pid,tid) VALUES(v_fid,pid,tid);
end if;
insert into vwb_collection_element(tid,cid,resource_type,title,creator,create_time,modifier,modify_time,resource_id,version) values(
tid,cid,'DFile',title,uid,curr_time,uid,curr_time,v_fid,version);
END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS `update_dfile_version` $$
CREATE PROCEDURE `update_dfile_version`(in uid varchar(255) CHARACTER SET utf8,in v_version int(11),in v_fid int(11), in v_title varchar(255) CHARACTER SET utf8,in size int(11),in curr_time timestamp,in v_tid int(11) )
BEGIN
insert into vwb_dfile_version(fid,title,version,size,change_by,change_time) VALUES(v_fid,v_title,v_version,size,uid,curr_time);
update vwb_collection_element set title=v_title,modifier=uid,modify_time=curr_time,version=v_version where resource_id=v_fid and resource_type='DFile' and tid=v_tid;
END $$

DELIMITER ;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;