----------------------------------------------
--a1_userinterest 存储用户感兴趣的用户
----------------------------------------------
CREATE TABLE `a1_userinterest` (
  `id` int(11) NOT NULL auto_increment,
  `uid` varchar(255) NOT NULL,
  `interest` varchar(255) NOT NULL,
  `score` int(11) default '0',
  `time` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31197 DEFAULT CHARSET=utf8
----------------------------------------------
--表 a1_searchlog，a1_searchedlog，a1_search_docweight添加tid字段
----------------------------------------------
alter table a1_searchlog add column `tid` int(11) not null default '0';
alter table a1_searchedlog add column `tid` int(11) not null default '0';
----------------------------------------------
--a1_search_docweight存放dlog提取出的最原始的日志数据
----------------------------------------------
CREATE TABLE `a1_searchlog` (
  `id` int(11) NOT NULL auto_increment,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `item_type` varchar(255) default NULL,
  `item_id` int(11) default '0',
  `sequence` int(11) default '0',
  `opername` varchar(255) default NULL,
  `time` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-------------------------------------------
--a1_searched_docweight存放dlog处理后的日志数据
-------------------------------------------
CREATE TABLE `a1_searchedlog` (
  `id` int(11) NOT NULL auto_increment,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `item_type` varchar(255) default NULL,
  `item_id` int(11) default '0',
  `sequence` int(11) default '0',
  `opername` varchar(255) default NULL,
  `time` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
----------------------------------------
--a1_search_docweight存放经过分析后新的文档权重
----------------------------------------
CREATE TABLE `a1_search_docweight` (
  `id` int(11) NOT NULL auto_increment,
  `uid` varchar(255) NOT NULL,
  `keyword` varchar(255) NOT NULL,
  `rid` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  `time` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;