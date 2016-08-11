package com.revenco.spoon;

import java.sql.Timestamp;

/**
 * Created by frank on 2016/8/10.
 */
public class JobJobLogDetailEntity {
    private int jobLogDetailId;
    private int jobPlanId;
    private int jobLogId;
    private String stepName;
    private String message;
    private Timestamp createdDate;

    public int getJobLogDetailId() {
        return jobLogDetailId;
    }

    public void setJobLogDetailId(int jobLogDetailId) {
        this.jobLogDetailId = jobLogDetailId;
    }

    public int getJobPlanId() {
        return jobPlanId;
    }

    public void setJobPlanId(int jobPlanId) {
        this.jobPlanId = jobPlanId;
    }

    public int getJobLogId() {
        return jobLogId;
    }

    public void setJobLogId(int jobLogId) {
        this.jobLogId = jobLogId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobJobLogDetailEntity that = (JobJobLogDetailEntity) o;

        if (jobLogDetailId != that.jobLogDetailId) return false;
        if (jobPlanId != that.jobPlanId) return false;
        if (jobLogId != that.jobLogId) return false;
        if (stepName != null ? !stepName.equals(that.stepName) : that.stepName != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = jobLogDetailId;
        result = 31 * result + jobPlanId;
        result = 31 * result + jobLogId;
        result = 31 * result + (stepName != null ? stepName.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
}
