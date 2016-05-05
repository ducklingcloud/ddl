CREATE TABLE `a1_auth_code` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL,
  `access_token` varchar(45) NOT NULL,
  `uid` varchar(45) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `client_id` varchar(45) DEFAULT NULL,
  `status` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;



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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_device_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `device_token` varchar(255) NOT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
  PRIMARY KEY (`id`),
  KEY `file_version_index` (`tid`,`version`),
  KEY `file_rid_index` (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_grid_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `uid` varchar(255) CHARACTER SET latin1 NOT NULL,
  `last_edit_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `grid_map` mediumblob NOT NULL,
  PRIMARY KEY (`id`),
  KEY `grid_group_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_mobile_version` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `version` varchar(45) NOT NULL,
  `creator` varchar(45) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `a1_navbar_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `tid` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `url` text,
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `navbar_index` (`uid`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` varchar(50) DEFAULT NULL,
  `key` varchar(50) DEFAULT NULL,
  `value` varchar(50) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



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
  PRIMARY KEY (`rid`),
  KEY `resource_tid_bid_index` (`tid`,`bid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_search_docweight` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `rid` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  `time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_starmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rid` int(11) NOT NULL,
  `tid` int(11) NOT NULL,
  `uid` varchar(255) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `stark_mark_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_tag_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_group_index` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_tag_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL,
  `tgid` int(11) NOT NULL,
  `rid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_item_index` (`tid`,`tgid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_team_space_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` varchar(45) NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_uid` varchar(45) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_user_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `max_create_team` int(10) unsigned NOT NULL,
  `config_uid` varchar(45) DEFAULT NULL,
  `config_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `a1_user_copy_count` (
  `uid` varchar(45) NOT NULL,
  `copy_date` varchar(20) NOT NULL,
  `count` varchar(45) NOT NULL,
  PRIMARY KEY (`uid`,`copy_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `a1_user_guide` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `module` varchar(20) NOT NULL,
  `step` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_guide_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_user_preferences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `refresh_team_mode` varchar(255) NOT NULL,
  `default_team` int(11) NOT NULL,
  `access_home_mode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_pre_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_user_sim` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `simuid` varchar(255) NOT NULL,
  `score` int(11) DEFAULT '0',
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `a1_userinterest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) NOT NULL,
  `interest` varchar(255) NOT NULL,
  `score` int(11) DEFAULT '0',
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `ddl_folder_path` (
  `tid` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  `ancestor_rid` int(10) unsigned NOT NULL,
  `length` int(10) unsigned NOT NULL,
  PRIMARY KEY (`tid`,`rid`,`ancestor_rid`) USING BTREE,
  KEY `ancestor_rid_tid_index` (`tid`,`ancestor_rid`),
  KEY `rid_ancestorrid_index` (`rid`,`ancestor_rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `ddl_item_mapping` (
  `tid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `item_type` varchar(45) COLLATE utf8_bin NOT NULL,
  `item_id` int(10) unsigned NOT NULL,
  `rid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`tid`,`item_type`,`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE `ddl_page_lock` (
  `rid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `uid` varchar(45) NOT NULL,
  `last_access` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `max_version` int(10) unsigned NOT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `jobs` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `job_name` (`job_name`)
) ENGINE=InnoDB AUTO_INCREMENT=211262 DEFAULT CHARSET=utf8;


CREATE TABLE `sphinx_counter` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `max_doc_id` int(10) unsigned NOT NULL,
  `counter` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_activation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `encode` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(45) NOT NULL DEFAULT '',
  `tname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_app_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `context` varchar(45) NOT NULL,
  `description` text NOT NULL,
  `issys` tinyint(1) NOT NULL DEFAULT '0',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44597 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;










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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_dfile` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `owner` varchar(100) NOT NULL DEFAULT '',
  `cid` int(10) unsigned NOT NULL DEFAULT '0',
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `clb_id` int(11) NOT NULL DEFAULT '0',
  `status` varchar(45) DEFAULT 'available',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_dfile_ref` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL DEFAULT '0',
  `page_rid` int(10) unsigned NOT NULL,
  `file_rid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `file_ref_index` (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_dfile_version` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fid` int(10) unsigned NOT NULL DEFAULT '0',
  `version` int(10) unsigned NOT NULL DEFAULT '0',
  `size` int(10) unsigned NOT NULL DEFAULT '0',
  `change_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `change_by` varchar(100) NOT NULL DEFAULT '',
  `title` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_grid` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) DEFAULT NULL,
  `cid` int(11) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `title` varchar(256) DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_grid_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tid` int(11) DEFAULT NULL,
  `gid` int(11) DEFAULT NULL,
  `resource_type` varchar(45) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_message` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `message_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `publisher` int(10) unsigned NOT NULL DEFAULT '0',
  `publisher_type` varchar(255) NOT NULL DEFAULT '',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;



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
  KEY `notice_index` (`tid`,`notice_type`,`recipient`)
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;




CREATE TABLE `vwb_properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `strName` varchar(255) DEFAULT NULL,
  `strValue` varchar(255) DEFAULT NULL,
  `iSiteNum` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;




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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_share_acl` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `hash` varchar(45) NOT NULL,
  `URL` varchar(256) NOT NULL,
  `accessTime` bigint(20) unsigned NOT NULL,
  `shareTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `vwb_subscription` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL DEFAULT '',
  `publisher` int(10) unsigned NOT NULL DEFAULT '0',
  `publisher_type` varchar(45) NOT NULL DEFAULT '',
  `policy` varchar(45) NOT NULL DEFAULT '',
  `tid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `subscription_inde` (`user_id`,`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=REDUNDANT;

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_team_acl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(11) NOT NULL DEFAULT '0',
  `uid` varchar(50) NOT NULL DEFAULT '0',
  `auth` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `team_acl_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_team_applicant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(45) NOT NULL,
  `tid` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `apply_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `i_know` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_team_create_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tid` int(10) unsigned NOT NULL,
  `param_key` varchar(45) NOT NULL,
  `param_value` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


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
  KEY `team_member_index` (`tid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;




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
  PRIMARY KEY (`id`,`uid`),
  KEY `pinyin_tree_index` (`pinyin`) USING BTREE,
  KEY `uid_tree_index` (`uid`(10)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `vwb_user_feedback` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `message` text NOT NULL,
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
