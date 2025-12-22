package com.ruoyi.biz.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报名记录对象 biz_registration
 * 
 * @author ruoyi
 * @date 2025-12-17
 */
public class BizRegistration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报名ID */
    private Long regId;

    /** 活动ID */
    @Excel(name = "活动ID")
    private Long activityId;

    /** 学生ID(关联sys_user) */
    @Excel(name = "学生ID(关联sys_user)")
    private Long userId;

    /** 学生姓名(冗余字段方便查询) */
    @Excel(name = "学生姓名(冗余字段方便查询)")
    private String userName;

    /** 学号 */
    @Excel(name = "学号")
    private String studentNo;

    /** 所属学院 */
    @Excel(name = "所属学院")
    private String deptName;

    /** 报名状态（0已报名 1已签到 2已取消） */
    @Excel(name = "报名状态", readConverterExp = "0=已报名,1=已签到,2=已取消")
    private String status;

    /** 签到时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签到时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkinTime;

    public void setRegId(Long regId) 
    {
        this.regId = regId;
    }

    public Long getRegId() 
    {
        return regId;
    }

    public void setActivityId(Long activityId) 
    {
        this.activityId = activityId;
    }

    public Long getActivityId() 
    {
        return activityId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setStudentNo(String studentNo) 
    {
        this.studentNo = studentNo;
    }

    public String getStudentNo() 
    {
        return studentNo;
    }

    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setCheckinTime(Date checkinTime) 
    {
        this.checkinTime = checkinTime;
    }

    public Date getCheckinTime() 
    {
        return checkinTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("regId", getRegId())
            .append("activityId", getActivityId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("studentNo", getStudentNo())
            .append("deptName", getDeptName())
            .append("status", getStatus())
            .append("checkinTime", getCheckinTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
