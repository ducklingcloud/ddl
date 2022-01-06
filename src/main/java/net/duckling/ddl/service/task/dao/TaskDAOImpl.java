/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 *
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.duckling.ddl.service.task.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.task.SQLThreadLocal;
import net.duckling.ddl.service.task.TakerWrapper;
import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskItem;
import net.duckling.ddl.service.task.TaskItemShare;
import net.duckling.ddl.service.task.TaskTaker;
import net.duckling.ddl.service.task.UserProcess;
import net.duckling.ddl.service.task.impl.TaskDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtil;
import net.duckling.ddl.util.DateUtil;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


import com.mysql.jdbc.Statement;

/**
 * @author lvly
 * @since 2012-6-8 对任务进行持久化操作的基本DAO
 * */
@Repository
public class TaskDAOImpl extends AbstractBaseDAO implements TaskDAO {

    private static final Logger LOGGER = Logger.getLogger(TaskDAOImpl.class);

    /** 基本sql */
    public static final String GET_TASKS = "select * " + "from vwb_task t "
            + "where 1=1";
    public static final String GET_TASKS_I_M_TAKER = "select * "
            + "from vwb_task t,vwb_task_taker ta " + "where 1=1 "
            + "and t.task_id=ta.task_id "
            + "and not exists(select * "
            +"                from a1_param pa "
            +"                where pa.type=? "
            +"                and pa.item_id=? "
            +"                and pa.key=t.task_id) ";
    public static final String GET_TASKS_HISTORY = "select * "
            + "from vwb_task t,vwb_task_taker ta,a1_param pa " + "where 1=1 "
            + "and t.task_id=ta.task_id "
            + "and pa.type='"+ParamConstants.UserTaskType.TYPE+"' "
            + "and pa.key=t.task_id "
            + "and pa.item_id=? ";
    public static final String GET_TAKERS = "select * "
            + "from vwb_task_taker ta " + "where 1=1";
    public static final String GET_ITEMS = "select * "
            + "from vwb_task_item i " + "where 1=1";
    public static final String GET_ITEMS_WITH_STATUS = "select * "
            + "from vwb_task_item i,vwb_task_ref r "
            + "where i.item_id=r.item_id  ";
    public static final String GET_SHARE_ITEMS = "select * "
            + "from vwb_task_share_item si " + "where 1=1";

    public static final String GET_PROCESS = "select r.item_id, "
            + "CONCAT((select count(*) "
            + "                 from vwb_task_ref r "
            + "                 where r.item_id=i.item_id "
            + "                 and r.task_id=? "
            + "                 and r.status='" + TaskTaker.STATUS_FINISH
            + "' ),'/',COUNT(*)) as pro "
            + "from vwb_task_item i,vwb_task_ref r "
            + "where i.item_id=r.item_id " + "and i.task_id=? "
            + "group by r.item_id";
    public static final String GET_ALL_USER_PROCESS = "select user_id , "
            + "(select count(*) from vwb_task_ref r1 where r1.task_id=r.task_id and r1.user_id=r.user_id and r1.`status`='undo')  'undo', "
            + "(select count(*) from vwb_task_ref r1 where r1.task_id=r.task_id and r1.user_id=r.user_id and r1.`status`='doing') 'doing', "
            + "(select count(*) from vwb_task_ref r1 where r1.task_id=r.task_id and r1.user_id=r.user_id and r1.`status`='finish') 'finish' "
            + "from vwb_task_ref r where task_id=? " + "group by user_id";
    public static final String GET_REFS = "select * from vwb_task_ref r "
            + "where 1=1";

    public static final String UPDATE_TASK = "update vwb_task t "
            + "set t.title=?,t.valid=? " + "where 1=1";
    public static final String UPDATE_ITEM = "update vwb_task_item i "
            + "set i.content=? " + "where 1=1";
    public static final String UPDATE_TAKER_READ_STATUS = "update vwb_task_taker ta "
            + "set ta.read_status='"
            + TaskTaker.READ_STATUS_READ
            + "' "
            + "where 1=1";
    public static final String UPDATE_TAKER_ITEM_REF = "update vwb_task_ref r "
            + "set r.status=? " + "where 1=1";
    public static final String UPDATE_SHARE_ITEM = "update vwb_task_share_item si "
            + "set si.content=? " + "where 1=1";
    public static final String UPDATE_SHARE_ITEM_STATUS = "update vwb_task_share_item si "
            + "set si.status=? ,si.user_id=?" + "where 1=1 ";

    public static final String DELETE_TAKERS = "delete from vwb_task_taker "
            + "where 1=1 ";
    public static final String DELETE_TAKER_ITEM_REF = "delete from vwb_task_ref  "
            + "where 1=1 ";
    public static final String DELETE_ITEMS = "update vwb_task_item i "
            + "set i.valid='" + Task.INVALID + "' " + "where 1=1 ";
    public static final String DELETE_TASK = "update vwb_task t "
            + "set t.valid='" + Task.INVALID + "' " + "where 1=1";
    public static final String DELETE_SHARE_ITEMS = "update vwb_task_share_item si "
            + "set si.valid='" + Task.INVALID + "' " + "where 1=1";

    public static final String ADD_TASK = "insert into vwb_task(title,creator,create_time,valid,task_type,team_id) values(?,?,?,?,?,?)";
    public static final String ADD_TAKER = "insert into vwb_task_taker(user_id,task_id,read_status) values(?,?,?)";
    public static final String ADD_TAKER_ITEM_REF = "insert into vwb_task_ref(user_id,task_id,item_id,status) values(?,?,?,?)";
    public static final String ADD_ITEM = "insert into vwb_task_item(content,task_id,valid) values(?,?,?)";
    public static final String ADD_SHARE_ITEM = "insert into vwb_task_share_item(content,task_id,valid,edit_time,status) values(?,?,?,?,?)";

    // 工具方法
    /** 获得order by 条件 */
    private String getOrderBySQL(String... cons) {
        StringBuffer sql = new StringBuffer(" order by ");
        for (String con : cons) {
            sql.append(con).append(",");
        }
        if (sql.indexOf(",") > 0) {
            sql.deleteCharAt(sql.lastIndexOf(","));
        }

        return sql.toString();
    }


    /**
     * 始终取得集合中第一个元素
     *
     * @param list
     *            集合
     * @return 第一个元素，若集合为空，则返回null
     * */
    private <K> K first(List<K> list) {
        return list == null || list.size() == 0 ? null : list.get(0);
    }

    /**
     * 更新语句
     *
     * @param sql
     *            SQL语句
     * @param params
     *            参数列表
     * */
    private int update(String sql, Object[] params) {
        return getJdbcTemplate().update(sql, params);
    }

    /**
     * 查询语句
     *
     * @param sql
     *            SQL语句
     * @param params
     *            参数列表
     * @param mapper
     *            数据注入对象
     *
     * */
    private <K> List<K> query(String sql, Object[] params, RowMapper<K> mapper) {
        return getJdbcTemplate().query(sql, params, mapper);
    }

    /**
     * 每次都转来转去的麻烦死了
     *
     * @param id
     *            String类型的id
     * @return int id
     * */
    private int toInt(String id) {
        return Integer.valueOf(id);
    }

    /**
     * 转换条件里的${table}
     *
     * @param tableName
     *            表名,如果不写,那么就把条件里的替换符删掉
     * @param bys
     *            条件....
     * */
    private String in(String tableName, String... bys) {
        if (CommonUtil.isNullArray(bys)) {
            return tableName.replace("${table}.", "");
        }
        StringBuffer sb = new StringBuffer();
        for (String by : bys) {
            if (by == null) {
                continue;
            }
            sb.append(by.replace("${table}", tableName));
        }
        return sb.toString();
    }

    /**
     * 根据ids更新一个字段
     *
     * @param needUpdateIds
     *            id数组
     * @param needUpdateContents
     *            需要给出的目标字段组
     * @param sql
     *            SQl 语句
     * */
    private void updateLike(final String[] needUpdateIds,
                            final String[] needUpdateContents, String sql) {
        if (CommonUtil.isNullArray(needUpdateIds)) {
            return;
        }
        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public int getBatchSize() {
                    return needUpdateIds.length;
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    int i = 0;
                    ps.setString(++i,
                                 needUpdateContents.length == 1 ? needUpdateContents[0]
                                 : needUpdateContents[index]);
                    ps.setInt(++i, toInt(needUpdateIds[index]));
                }
            });
    }

    /**
     * 根据某一个条件删除一条记录，比如ID
     *
     * @param ids
     *            条件集合，因为要批量
     * @param sql
     *            SQL语句
     * */
    private void deleteFromIds(final String[] ids, String sql) {
        if (CommonUtil.isNullArray(ids)) {
            return;
        }
        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public int getBatchSize() {
                    return ids.length;
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    int i = 0;
                    ps.setInt(++i, toInt(ids[index]));
                }
            });
    }

    // mappers
    private RowMapper<Task> taskMapper = new RowMapper<Task>() {
            public Task mapRow(ResultSet rs, int index) throws SQLException {
                Task task = new Task();
                task.setTaskId(rs.getInt("task_id"));
                task.setCreator(rs.getString("creator"));
                task.setCreateTime(rs.getString("create_time"));
                task.setTaskType(rs.getString("task_type"));
                task.setTeamId(rs.getString("team_id"));
                task.setTitle(rs.getString("title"));
                task.setValid(rs.getString("valid"));
                try {
                    task.setReadStatus(rs.getString("read_status"));
                } catch (Exception e) {
                    LOGGER.debug("%task don't need read_status%");
                }
                return task;
            }
        };
    private RowMapper<TaskTaker> takerMapper = new RowMapper<TaskTaker>() {
            public TaskTaker mapRow(ResultSet rs, int index) throws SQLException {
                TaskTaker taker = new TaskTaker();
                taker.setTakerId(rs.getInt("taker_id"));
                taker.setTaskId(rs.getInt("task_id"));
                taker.setUserId(rs.getString("user_id"));
                return taker;
            }
        };
    private RowMapper<TaskItem> itemMapper = new RowMapper<TaskItem>() {
            public TaskItem mapRow(ResultSet rs, int index) throws SQLException {
                TaskItem item = new TaskItem();
                item.setItemId(rs.getInt("item_id"));
                item.setContent(rs.getString("content"));
                item.setTaskId(rs.getInt("task_id"));
                item.setValid(rs.getString("valid"));
                try {
                    // 并非每次都有偶
                    item.setStatus(rs.getString("status"));
                } catch (Exception e) {
                    LOGGER.debug("%item don't need status%");
                }
                return item;
            }
        };

    private RowMapper<TaskItemShare> shareItemMapper = new RowMapper<TaskItemShare>() {
            public TaskItemShare mapRow(ResultSet rs, int index)
                    throws SQLException {
                TaskItemShare item = new TaskItemShare();
                item.setItemId(rs.getInt("item_id"));
                item.setContent(rs.getString("content"));
                item.setTaskId(rs.getInt("task_id"));
                item.setValid(rs.getString("valid"));
                item.setContent(rs.getString("content"));
                item.setEditTime(rs.getString("edit_time"));
                item.setUserId(rs.getString("user_id"));
                item.setStatus(rs.getString("status"));
                return item;
            }
        };
    private RowMapper<UserProcess> userProcessMapper = new RowMapper<UserProcess>() {
            public UserProcess mapRow(ResultSet rs, int index) throws SQLException {
                UserProcess user = new UserProcess();
                user.setUserId(rs.getString("user_id"));
                user.setDoingCount(rs.getInt("doing"));
                user.setFinishCount(rs.getInt("finish"));
                user.setUndoCount(rs.getInt("undo"));
                return user;
            }
        };

    private RowMapper<TaskRef> taskRefMapper = new RowMapper<TaskRef>() {
            public TaskRef mapRow(ResultSet rs, int index) throws SQLException {
                TaskRef ref = new TaskRef();
                ref.setItemId(rs.getInt("item_id"));
                ref.setRefId(rs.getInt("ref_id"));
                ref.setStatus(rs.getString("status"));
                ref.setTaskId(rs.getInt("task_id"));
                ref.setUserId(rs.getString("user_id"));
                return ref;
            }
        };

    // mappers end

    @Override
    public List<Task> getTasksByUID(String currentUID, String teamCode) {
        String sql = GET_TASKS+ in(IN_TASK, BY_CREATOR, BY_TEAM_ID,BY_NOT_INVALID,
                                   SQLThreadLocal.get());
        String orderCon = getOrderBySQL(in(IN_TASK, ORDER_BY_CREATE_TIME));
        return query(sql + orderCon, new Object[] { currentUID, teamCode },
                     taskMapper);
    }

    @Override
    public List<Task> getTasksByImTaker(String currentUID, String teamCode) {
        String sql = GET_TASKS_I_M_TAKER + in(IN_TAKER, BY_LIKE_USER_ID)
                + in(IN_TASK, BY_TEAM_ID,BY_VALID_OR_OVER, SQLThreadLocal.get());
        String oredrCon = getOrderBySQL(in(IN_TAKER, ORDER_BY_READ_STATUS),
                                        in(IN_TASK, ORDER_BY_CREATE_TIME));
        return query(sql + oredrCon,
                     new Object[] { ParamConstants.UserTaskType.TYPE,currentUID,"%" + currentUID, teamCode }, taskMapper);
    }

    @Override
    public List<Task> getTasksByHistory(String teamCode, String userId) {
        String sql = GET_TASKS_HISTORY
                + in(IN_TASK, BY_TEAM_ID,SQLThreadLocal.get())
                + in(IN_TAKER, BY_LIKE_USER_ID);
        String orderCon = getOrderBySQL(in(IN_TASK, ORDER_BY_CREATE_TIME));
        return query(sql + orderCon, new Object[] { userId,teamCode, "%" + userId },
                     taskMapper);
    }

    @Override
    public Task getTask(String taskId) {
        return first(getJdbcTemplate().query(
            GET_TASKS + in(IN_TASK, BY_TASK_ID), new Object[] { taskId },
            taskMapper));
    }

    @Override
    public List<TaskTaker> getTakers(String taskId) {
        return query(GET_TAKERS + in(IN_TAKER, BY_TASK_ID),
                     new Object[] { taskId }, takerMapper);
    }

    @Override
    public List<TaskItem> getItems(String taskId) {
        return query(GET_ITEMS + in(IN_ITEM, BY_TASK_ID,BY_VALID),
                     new Object[] { taskId }, itemMapper);
    }

    @Override
    public List<TaskItem> getItemsWithStatus(String taskId, String currentUID) {
        return query(GET_ITEMS_WITH_STATUS + in(IN_REF, BY_LIKE_USER_ID)
                     + in(IN_ITEM,BY_VALID, BY_TASK_ID), new Object[] {
                         "%" + currentUID, taskId }, itemMapper);
    }

    @Override
    public void updateTask(Task task) {
        update(UPDATE_TASK + in(IN_TASK, BY_TASK_ID),
               new Object[] { task.getTitle(), task.getValid(),
                   task.getTaskId() });

    }

    @Override
    public void deleteTakers(final int[] ids) {
        if (CommonUtil.isNullArray(ids)) {
            return;
        }
        getJdbcTemplate().batchUpdate(DELETE_TAKERS + in(BY_TAKER_ID),
                                      new BatchPreparedStatementSetter() {

                                          @Override
                                          public int getBatchSize() {
                                              return ids.length;
                                          }

                                          @Override
                                          public void setValues(PreparedStatement ps, int index)
                                                  throws SQLException {
                                              int i = 0;
                                              ps.setInt(++i, ids[index]);
                                          }
                                      });

    }

    @Override
    public void deleteTakerItemRefByUID(final String[] ids, final int taskId) {
        if (CommonUtil.isNullArray(ids)) {
            return;
        }
        getJdbcTemplate().batchUpdate(
            DELETE_TAKER_ITEM_REF + in(BY_TASK_ID) + in(BY_LIKE_USER_ID),
            new BatchPreparedStatementSetter() {

                @Override
                public int getBatchSize() {
                    return ids.length;
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    int i = 0;
                    ps.setInt(++i, taskId);
                    ps.setString(++i, "%" + ids[index]);
                }
            });

    }

    @Override
    public void addTakers(final List<TaskTaker> needAdd) {
        if (CommonUtil.isNullArray(needAdd)) {
            return;
        }
        getJdbcTemplate().batchUpdate(ADD_TAKER,
                                      new BatchPreparedStatementSetter() {

                                          @Override
                                          public int getBatchSize() {
                                              return needAdd.size();
                                          }

                                          @Override
                                          public void setValues(PreparedStatement ps, int index)
                                                  throws SQLException {
                                              int i = 0;
                                              ps.setString(++i, needAdd.get(index).getUserId());
                                              ps.setInt(++i, needAdd.get(index).getTaskId());
                                              ps.setString(++i, needAdd.get(index).getReadStatus());
                                          }
                                      });

    }

    @Override
    public void addTakersItemRef(List<TaskTaker> newAdd, int[] itemsId,
                                 int taskId) {
        if (CommonUtil.isNullArray(newAdd) || CommonUtil.isNullArray(itemsId)) {
            return;
        }
        final int allCount = newAdd.size() * itemsId.length;
        final int countCon = 3;
        final String[][] ref = new String[allCount][countCon];
        int index = 0;
        for (TaskTaker taker : newAdd) {
            for (int id : itemsId) {
                int count = 0;
                ref[index][count++] = taker.getUserId();
                ref[index][count++] = taskId + "";
                ref[index][count++] = id + "";
                index++;
            }
        }
        getJdbcTemplate().batchUpdate(ADD_TAKER_ITEM_REF,
                                      new BatchPreparedStatementSetter() {

                                          @Override
                                          public int getBatchSize() {
                                              return allCount;
                                          }

                                          @Override
                                          public void setValues(PreparedStatement ps, int index)
                                                  throws SQLException {
                                              int i = 0;
                                              ps.setString(i + 1, ref[index][i++]);
                                              ps.setInt(i + 1, toInt(ref[index][i++]));
                                              ps.setInt(i + 1, toInt(ref[index][i++]));
                                              ps.setString(i + 1, Task.STATUS_DOING);

                                          }
                                      });

    }

    @Override
    public int[] addItems(final String[] needAddItemsContent,
                          final String taskId) {
        int[] ids = new int[needAddItemsContent.length];
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getJdbcTemplate().getDataSource().getConnection();
            ps = conn.prepareStatement(ADD_ITEM,
                                       Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < needAddItemsContent.length; i++) {
                int index = 0;
                ps.setString(++index, needAddItemsContent[i]);
                ps.setInt(++index, toInt(taskId));
                ps.setString(++index, Task.VALID);
                ps.addBatch();
            }
            ps.executeBatch();
            rs = ps.getGeneratedKeys();
            int index = 0;
            while (rs.next()) {
                ids[index++] = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("CLOSE_ERROR:", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.error("CLOSE_ERROR:", e);
            }

        }
        return ids;

    }

    @Override
    public void updateItems(String[] ids, String[] modifyContent) {
        updateLike(ids, modifyContent, UPDATE_ITEM + in(IN_ITEM, BY_ITEM_ID));

    }

    @Override
    public void deleteItems(String[] ids) {
        deleteFromIds(ids, DELETE_ITEMS + in(IN_ITEM, BY_ITEM_ID));
    }

    @Override
    public void deleteItemRefByItemId(String[] ids) {
        deleteFromIds(ids, DELETE_TAKER_ITEM_REF + in(BY_ITEM_ID));

    }

    @Override
    public int addTask(final Task task) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update((new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = null;
                    pst = conn.prepareStatement(ADD_TASK,
                                                PreparedStatement.RETURN_GENERATED_KEYS);

                    int i = 0;
                    // title,creator,create_time,valid,task_type,team_id
                    pst.setString(++i, task.getTitle());
                    pst.setString(++i, task.getCreator());
                    pst.setString(++i, task.getCreateTime());
                    pst.setString(++i, Task.VALID);
                    pst.setString(++i, task.getTaskType());
                    pst.setString(++i, task.getTeamId());
                    return pst;
                }
            }), keyHolder);
        return keyHolder.getKey().intValue();

    }

    public Map<Integer, String> getProcess(String taskId) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rsFinisher = null;
        PreparedStatement psFinisher = null;
        try {
            JdbcTemplate template = getJdbcTemplate();
            DataSource dataSource = template.getDataSource();
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(GET_PROCESS);
            int index = 0;
            ps.setInt(++index, toInt(taskId));
            ps.setInt(++index, toInt(taskId));

            rs = ps.executeQuery();
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                final String sql=GET_REFS+in(IN_REF, BY_STATUS, BY_TASK_ID, BY_ITEM_ID);
                psFinisher = conn.prepareStatement(sql);
                index = 0;
                psFinisher.setString(++index, Task.STATUS_FINISH);
                psFinisher.setInt(++index, toInt(taskId));
                psFinisher.setInt(++index, itemId);
                rsFinisher = psFinisher.executeQuery();
                StringBuffer finishers = new StringBuffer();
                while (rsFinisher.next()) {
                    finishers.append(
                        TakerWrapper.getUserName((rsFinisher
                                                  .getString("user_id")))).append(",");
                }
                if (finishers.length() > 0)
                    finishers.deleteCharAt(finishers.lastIndexOf(","));

                map.put(rs.getInt("item_id"), rs.getString("pro") + "["
                        + finishers + "]");
                rsFinisher.close();
                psFinisher.close();
            }

        } catch (SQLException err) {
            LOGGER.error(err);
        } finally {
            try {
                if (rsFinisher != null) {
                    rsFinisher.close();
                }
                if (psFinisher != null) {
                    psFinisher.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                LOGGER.error("CLOSE_ERROR:", e);
            }

        }
        return map;
    }

    @Override

    public boolean updateRefStatus(final String[] itemIds, final String uid,
                                   final String status) {
        if (CommonUtil.isNullArray(itemIds)) {
            return false;
        }
        int taskId=getTaskIdFromRefItemIdAndUid(itemIds[0],uid);
        getTask(taskId+"");

        getJdbcTemplate()
                .batchUpdate(
                    UPDATE_TAKER_ITEM_REF
                    + in(IN_REF, BY_ITEM_ID, BY_LIKE_USER_ID),
                    new BatchPreparedStatementSetter() {

                        @Override
                        public int getBatchSize() {
                            return itemIds.length;
                        }

                        @Override
                        public void setValues(PreparedStatement ps,
                                              int index) throws SQLException {
                            int i = 0;
                            ps.setString(++i, status);
                            ps.setInt(++i, toInt(itemIds[index]));
                            ps.setString(++i, "%" + uid);
                        }
                    });
        return true;
    }
    /**在REF表里面获得任务ID
     * @param itemID itemID
     * @param uid 用户信息
     * @return taskId
     * */
    private int getTaskIdFromRefItemIdAndUid(String itemId,String uid){
        return CommonUtil.first(query(GET_REFS+in(IN_REF, BY_ITEM_ID, BY_LIKE_USER_ID), new Object[]{itemId,"%"+uid}, new RowMapper<Integer>(){

                @Override
                public Integer mapRow(ResultSet rs, int index) throws SQLException {
                    return rs.getInt("task_id");
                }

            }));
    }


    @Override
    public List<String> getRefByTaskId(String taskId) {
        List<String> result = new ArrayList<String>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            JdbcTemplate template = getJdbcTemplate();
            DataSource dataSource = template.getDataSource();
            conn = dataSource.getConnection();
            final String sql=GET_REFS + in(IN_REF, BY_TASK_ID);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, toInt(taskId));
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("status"));
            }

        } catch (Exception err) {
            LOGGER.error("getRefByTaskId()", err);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                LOGGER.error("CLOSE_ERROR:", e);
            }

        }
        return result;
    }

    @Override
    public void deleteItemsByTaskId(String taskId) {
        deleteFromIds(new String[] { taskId },
                      DELETE_ITEMS + in(IN_ITEM, BY_TASK_ID));
    }

    @Override
    public void deleteTask(String taskId) {
        deleteFromIds(new String[] { taskId },
                      DELETE_TASK + in(IN_TASK, BY_TASK_ID));

    }

    @Override
    public List<TaskItemShare> getShareItems(String taskId) {
        return query(GET_SHARE_ITEMS + in(IN_SHARE, BY_TASK_ID,BY_VALID),
                     new String[] { taskId }, shareItemMapper);
    }

    @Override
    public void addShareItems(final String[] contents, final String taskId) {
        if (CommonUtil.isNullArray(contents)) {
            return;
        }
        getJdbcTemplate().batchUpdate(ADD_SHARE_ITEM,
                                      new BatchPreparedStatementSetter() {

                                          @Override
                                          public int getBatchSize() {
                                              return contents.length;
                                          }

                                          @Override
                                          public void setValues(PreparedStatement ps, int index)
                                                  throws SQLException {
                                              int i = 0;
                                              ps.setString(++i, contents[index]);
                                              ps.setInt(++i, toInt(taskId));
                                              ps.setString(++i, Task.VALID);
                                              ps.setString(++i, DateUtil.getCurrentTime());
                                              ps.setString(++i, Task.STATUS_UNDO);
                                          }
                                      });

    }

    @Override
    public void updateShareItems(String[] ids, String[] modifyContents) {
        updateLike(ids, modifyContents,
                   UPDATE_SHARE_ITEM + in(IN_SHARE, BY_TASK_ID));
    }

    @Override
    public void deleteShareItems(String[] ids) {
        deleteFromIds(ids, DELETE_SHARE_ITEMS + in(BY_ITEM_ID));

    }

    @Override
    public boolean updateShareItemStatus(final String[] ids,
                                         final String status, final String userId) {
        if (CommonUtil.isNullArray(ids)) {
            return true;
        }
        List<TaskItemShare> shares = new ArrayList<TaskItemShare>();
        for (int i = 0; i < ids.length; i++) {
            TaskItemShare share = CommonUtil.first(query(
                GET_SHARE_ITEMS + in(IN_SHARE, BY_ITEM_ID),
                new Object[] { ids[i] }, shareItemMapper));
            shares.add(share);
        }
        // 如果锁的时候发现 条目状态并非undo所属人也并非自己那么说明已经手慢了
        if (Task.STATUS_DOING.equals(status)) {
            for (int i = 0; i < shares.size(); i++) {
                TaskItemShare share = shares.get(i);
                if (!Task.STATUS_UNDO.equals(share.getStatus())
                    && !userId.equals(share.getUserId())) {
                    return false;
                }
            }
        }
        getJdbcTemplate().batchUpdate(
            UPDATE_SHARE_ITEM_STATUS + in(IN_SHARE, BY_ITEM_ID),
            new BatchPreparedStatementSetter() {

                @Override
                public int getBatchSize() {
                    return ids.length;
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    int i = 0;
                    ps.setString(++i, status);
                    ps.setString(++i, userId);
                    ps.setInt(++i, toInt(ids[index]));
                }
            });
        return true;
    }

    @Override
    public void deleteShareItemsByTaskId(String taskId) {
        deleteFromIds(new String[] { taskId },
                      DELETE_SHARE_ITEMS + in(IN_SHARE, BY_TASK_ID));
    }
    @Override
    public List<UserProcess> getAllUserProcess(String taskId) {
        return query(GET_ALL_USER_PROCESS, new Object[] { taskId },
                     userProcessMapper);
    }

    @Override
    public void setReadStatus(final String taskId, final String userId) {
        String sql = UPDATE_TAKER_READ_STATUS
                + in(IN_TAKER, BY_LIKE_USER_ID, BY_TASK_ID);
        getJdbcTemplate().update(sql, new PreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    int i = 0;
                    ps.setString(++i, "%" + userId);
                    ps.setInt(++i, toInt(taskId));
                }
            });
    }
    @Override
    public boolean isUserDone(int taskId, String uid) {
        List<TaskRef> refs=query(GET_REFS+in(IN_REF,BY_LIKE_USER_ID,BY_TASK_ID), new Object[]{"%"+uid,taskId}, taskRefMapper);
        if(CommonUtil.isNullArray(refs)){
            return false;
        }
        boolean flag=true;
        for(TaskRef ref:refs){
            if(ref!=null){
                flag&=Task.STATUS_FINISH.equals(ref.getStatus());
            }

        }
        return flag;
    }
}
