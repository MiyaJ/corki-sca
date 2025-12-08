package com.corki.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志记录表实体
 *
 * @author Corki
 * @since 2024-12-09
 */
@Data
@TableName("operation_log")
public class OperationLog {

    /**
     * 日志主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块标题
     */
    @TableField("title")
    private String title;

    /**
     * 业务类型（0-其他，1-新增，2-修改，3-删除）
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 方法名称
     */
    @TableField("method")
    private String method;

    /**
     * 请求方式
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 操作类别（0-其他，1-后台用户，2-手机端用户）
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @TableField("operator_name")
    private String operatorName;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 请求URL
     */
    @TableField("oper_url")
    private String operUrl;

    /**
     * 主机地址
     */
    @TableField("oper_ip")
    private String operIp;

    /**
     * 操作地点
     */
    @TableField("oper_location")
    private String operLocation;

    /**
     * 请求参数
     */
    @TableField("oper_param")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField("json_result")
    private String jsonResult;

    /**
     * 操作状态（0-正常，1-异常）
     */
    @TableField("status")
    private Integer status;

    /**
     * 错误消息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField("oper_time")
    private LocalDateTime operTime;

    /**
     * 请求开始时间
     */
    @TableField(exist = false)
    private Long startTime;

    /**
     * 请求结束时间
     */
    @TableField(exist = false)
    private Long endTime;

    /**
     * 执行时长（毫秒）
     */
    @TableField(exist = false)
    private Long executionTime;
}