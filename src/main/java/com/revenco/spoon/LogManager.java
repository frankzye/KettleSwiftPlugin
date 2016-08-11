package com.revenco.spoon;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by frank on 2016/8/10.
 */
public class LogManager {
    private SessionFactory ourSessionFactory;
    int planId, jobId;

    public Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public LogManager(int planId, int jobId) {
        this.planId = planId;
        this.jobId = jobId;

        try {
            Thread.currentThread().setContextClassLoader(null);

            Configuration configuration = new Configuration();
            configuration.configure(this.getClass().getResource("/hibernate.cfg.xml"));

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
           ex.printStackTrace();
        }
    }

    public void Log(String stepName, String msg) {
        this.Log(stepName, msg, false);
    }

    public void Log(String stepName, String msg, boolean error) {
        final Session session = getSession();
        synchronized(this) {
            Transaction tx = session.beginTransaction();
            try {
                JobJobLogDetailEntity logDetail = new JobJobLogDetailEntity();
                logDetail.setJobPlanId(planId);
                logDetail.setJobLogId(jobId);
                logDetail.setMessage(msg);
                logDetail.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                logDetail.setStepName(stepName);

                session.save(logDetail);

                if (error) {
                    JobJobLogEntity jobLog = (JobJobLogEntity) session.load(JobJobLogEntity.class, (long) jobId);
                    jobLog.setJobStatus((short) -1);

                    session.update(jobLog);
                }

                tx.commit();

            }
            catch (Exception ex){
                ex.printStackTrace();
                tx.rollback();
            }
            finally {
                session.close();
            }
        }
    }
}
