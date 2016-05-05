--  ----------------------------------------------
--  任务主表
-- -----------------------------------------------
CREATE TABLE `vwb_task` (
  `task_id` int(11) NOT NULL auto_increment,
  `title` varchar(255) default NULL,
  `creator` varchar(255) default NULL,
  `create_time` varchar(255) default NULL,
  `valid` varchar(255) default NULL,
  `task_type` varchar(255) default NULL,
  `team_id` varchar(255) default NULL,
  PRIMARY KEY  (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--  ----------------------------------------------
--  独立任务的任务内容表，跟任务表是一对多关系
-- -----------------------------------------------
CREATE TABLE `vwb_task_item` (
  `item_id` int(11) NOT NULL auto_increment,
  `content` varchar(255) default NULL,
  `task_id` int(11) default NULL,
  `valid` varchar(255) default NULL,
  PRIMARY KEY  (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--  ----------------------------------------------
--   独立任务内容和接受者 的笛卡尔积，用来维护独立任务项的状态用
-- -----------------------------------------------
CREATE TABLE `vwb_task_ref` (
  `ref_id` int(11) NOT NULL auto_increment,
  `user_id` varchar(255) default NULL,
  `task_id` int(11) default NULL,
  `item_id` int(11) default NULL,
  `status` varchar(255) default NULL,
  PRIMARY KEY  (`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--  ----------------------------------------------
--   共享任务项表，跟任务表是一对多关系
-- -----------------------------------------------
CREATE TABLE `vwb_task_share_item` (
  `item_id` int(11) NOT NULL auto_increment,
  `content` varchar(255) default NULL,
  `task_id` int(11) default NULL,
  `valid` varchar(255) default NULL,
  `edit_time` varchar(255) default NULL,
  `user_id` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  PRIMARY KEY  (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--  ----------------------------------------------
--   接受者队列，跟任务表是一对多关系
-- -----------------------------------------------
CREATE TABLE `vwb_task_taker` (
  `taker_id` int(11) NOT NULL auto_increment,
  `user_id` varchar(255) default NULL,
  `task_id` int(255) default NULL,
  `read_status` varchar(255) default NULL,
  PRIMARY KEY  (`taker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;