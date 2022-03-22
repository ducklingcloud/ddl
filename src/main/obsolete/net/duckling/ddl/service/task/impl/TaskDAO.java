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
package net.duckling.ddl.service.task.impl;

import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskItem;
import net.duckling.ddl.service.task.TaskItemShare;
import net.duckling.ddl.service.task.TaskTaker;
import net.duckling.ddl.service.task.UserProcess;



/**
 * @author lvly
 * @since 2012-6-8
 * 对任务进行持久化操作的基本DAO
 * */
public interface TaskDAO {

    /**查询条件*/
    String IN_TASK="t";
    String IN_ITEM="i";
    String IN_TAKER="ta";
    String IN_SHARE="si";
    String IN_REF="r";
    String BY_NOT_INVALID= " and valid!='"+Task.INVALID+"'";
    String BY_VALID_OR_OVER=" and (valid='"+Task.VALID+"' or valid='"+Task.OVER+"')";
    String BY_VALID="and valid='"+Task.VALID+"'";
    String BY_INVALID=" and valid='"+Task.INVALID+"'";
    String BY_OVER=" and valid='"+Task.OVER+"'";
    String BY_DUSTBIN=" and valid='"+Task.DUSTBIN+"'";

    String BY_TASK_TYPE=" and ${table}.task_type=?";
    String BY_CREATOR=" and ${table}.creator=?";
    String BY_TEAM_ID=" and ${table}.team_id=?";
    String BY_TASK_ID=" and ${table}.task_id=?";
    String BY_USER_ID=" and ${table}.user_id?=";
    String BY_LIKE_USER_ID=" and ${table}.user_id like ?";
    String BY_TAKER_ID=" and ${table}.taker_id=?";
    String BY_ITEM_ID=" and ${table}.item_id=?";
    String BY_STATUS=" and ${table}.status=?";
    String BY_CREATE_TIME=" and ${table}.create_time between '${from}' and '${to}'";

    /**排序条件*/
    String ORDER_BY_READ_STATUS=" ${table}.read_status desc";
    String ORDER_BY_CREATE_TIME=" ${table}.create_time desc";
    /**查询我创建的任务列表
     * @param currentUID 当前用户ID
     * @param teamCode   团队ID
     * */
    List<Task> getTasksByUID(String currentUID, String teamCode);

    /**查询我接受到的任务列表
     *
     * @param currentUID 当前用户ID
     * @param teamCode   团队ID
     * */
    List<Task> getTasksByImTaker(String currentUID, String teamCode);

    /**查看已完成的所有的任务
     * @param teamCode   团队ID
     * @param userId    用户ID
     * @param valid      是否要查有效Task
     * */
    List<Task> getTasksByHistory(String teamCode,String userId);

    /**获得一个任务
     * @param taskId     任务ID
     * */
    Task getTask(String taskId);

    /**获得获得者队列
     * @param taskId 与之关联的任务号
     * @return takers 获得者队列
     * */
    List<TaskTaker> getTakers(String taskId);

    /**获得任务项队列
     * @param taskId 与之关联的任务号
     * @param 是否要查有效item
     * @return takers 任务项队列
     * */
    List<TaskItem> getItems(String taskId);

    /**获得任务项队列附带状态
     * @param taskId 与之关联的任务号
     * @param 是否要查有效item
     * @param currentUID 当前用户ID
     * @return takers 任务项队列
     * */
    List<TaskItem> getItemsWithStatus(String taskId,String currentUId);

    /**更新一个任务
     * @param task      查询用taskId，更改用属性
     * */
    void updateTask(Task task);

    /**删除接受者队列
     * @param ids taker_id队列
     * */
    void deleteTakers(int[] ids);

    /**删除掉item和taker的关联表的数据
     * @param ids 需要删除掉的user_ID
     * @param taskId 关联父任务的ID;
     *
     * */
    void deleteTakerItemRefByUID(String[] ids, int taskId);

    /**新增接受者
     * @param needAdd 需要新增的接受者队列，除了ID其他属性不应缺少
     * */
    void addTakers(List<TaskTaker> needAdd);

    /**增加item和taker 的映射关系，进行笛卡尔积，插入
     * @param needAdd 新加的takers
     * @param itemsId 新增的itemId数组
     * @param taskId taskId()
     * */
    void addTakersItemRef(List<TaskTaker> needAdd, int[] itemsId,
                          int taskId);

    /**增加任务项
     * @param needAddItemContent 需要增加的任务想列表
     * @param i     关联任务ID
     * */
    int[] addItems(String[] needAddItemsContent, String i);

    /**
     *更新任务项
     *@param  ids 需要更新的itemId列表
     *@param  modifyContent 需要更新成的内容
     * */
    void updateItems(String[] ids, String[] modifyContent);

    /**删除任务项，应为设成无效
     * @param ids 需要删除的item的id数组
     * */
    void deleteItems(String[] ids);

    /**
     * 根据itemId把与之相关的taker映射全部干掉
     * @param ids itemId数组
     * */
    void deleteItemRefByItemId(String[] ids);

    /**添加一个任务
     * @param task 需要添加的实例
     * @return id 返回添加成功新生成的id
     * */
    int addTask(Task task);

    /**
     *获得任务进度情况
     * @param taskId 关联任务编号
     * @return map<a,b> a-itemId,b-process[1/3]
     **/
    Map<Integer, String> getProcess(String taskId);

    /**更新REF状态字段
     * @param itemIds  itemId数组
     * @param uid 当前用户
     * @param status 状态
     * */
    boolean updateRefStatus(String[] itemIds, String uid, String status);

    /**获得一个任务下的所有映射关系
     * @param taskId 关联任务号
     * @return List<String> 所有状态
     * */
    List<String> getRefByTaskId(String taskId);

    /**删除任务
     * @param taskId 关联任务号
     * */
    void deleteTask(String taskId);

    /**用任务号删除任务项目
     * @param taskId
     * */
    void deleteItemsByTaskId(String taskId);

    /**获取每个接受者的执行情况
     * @param taskId 任务ID
     * */
    List<UserProcess> getAllUserProcess(String taskId);

    /**----------------------------------以下是share用的方法---------------------------------------**/
    /**
     * 获取share任务的任务项
     * @param taskId 任务Id
     * */
    List<TaskItemShare> getShareItems(String taskId);

    /**
     * 新增share任务项
     * @param needAddItemsContents 任务项描述
     * @param taskId 任务Id
     * */
    void addShareItems(String[] needAddItemsContents, String taskId);

    /**
     * 更新任务项描述
     * @param ids 需更改的id号
     * @param modifyContents 需要改成的内容
     * */
    void updateShareItems(String[] ids, String[] modifyContents);

    /**
     *删除任务项目描述
     *@param ids 需要删除的任务项id
     * */
    void deleteShareItems(String[] ids);

    /**更新share任务项的状态
     * @param ids 需要更新状态的item的id
     * @param status 需要更新成的状态
     * @param userId  当前用户Id
     * @return 执行锁操作的时候，判断
     * */
    boolean updateShareItemStatus(String[] ids, String status, String userId);

    /**根据taskId把所属的item全都设为无效
     * @param taskId 任务ID
     * */
    void deleteShareItemsByTaskId(String taskId);

    /**设置已阅状态，通用
     * @param taskId 任务ID
     * @param userId 当前用户ID
     * */
    void setReadStatus(String taskId, String userId);

    /**判断用户是不是把自己的任务项全部完成了
     * @param taskId 任务ID
     * @param uid 用户ID
     * @return 返回是否已完成？
     * */
    boolean isUserDone(int taskId, String uid);


}
