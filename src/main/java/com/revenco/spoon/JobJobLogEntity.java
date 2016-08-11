package com.revenco.spoon;

import java.sql.Timestamp;

/**
 * Created by frank on 2016/8/10.
 */
public class JobJobLogEntity {
    private long jobLogId;
    private int jobPlanId;
    private int jobServerId;
    private String serverInfo;
    private Timestamp startTime;
    private Timestamp endTime;
    private short jobStatus;
    private String contentText;

    public long getJobLogId() {
        return jobLogId;
    }

    public void setJobLogId(long jobLogId) {
        this.jobLogId = jobLogId;
    }

    public int getJobPlanId() {
        return jobPlanId;
    }

    public void setJobPlanId(int jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    public int getJobServerId() {
        return jobServerId;
    }

    public void setJobServerId(int jobServerId) {
        this.jobServerId = jobServerId;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public short getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(short jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobJobLogEntity that = (JobJobLogEntity) o;

        if (jobLogId != that.jobLogId) return false;
        if (jobPlanId != that.jobPlanId) return false;
        if (jobServerId != that.jobServerId) return false;
        if (jobStatus != that.jobStatus) return false;
        if (serverInfo != null ? !serverInfo.equals(that.serverInfo) : that.serverInfo != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (contentText != null ? !contentText.equals(that.contentText) : that.contentText != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (jobLogId ^ (jobLogId >>> 32));
        result = 31 * result + jobPlanId;
        result = 31 * result + jobServerId;
        result = 31 * result + (serverInfo != null ? serverInfo.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (int) jobStatus;
        result = 31 * result + (contentText != null ? contentText.hashCode() : 0);
        return result;
    }
}
