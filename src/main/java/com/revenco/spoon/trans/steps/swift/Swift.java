package com.revenco.spoon.trans.steps.swift;

import com.revenco.spoon.LogManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * Created by frank on 2016/7/26.
 */
public class Swift extends BaseStep implements StepInterface {
    private SwiftMeta meta;
    private SwiftData data;
    private LogManager logger;

    private static Class<?> PKG = SwiftMeta.class;

    public Swift(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);

        try {
            logger = new LogManager(Integer.parseInt(trans.getVariable("jobplanid")), Integer.parseInt(trans.getVariable("jobid")));
        }catch (Exception ex){}

        try {
            String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            path = java.net.URLDecoder.decode(path, "UTF-8");
            int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
            int lastIndex = path.lastIndexOf(File.separator) + 1;
            path = path.substring(firstIndex, lastIndex);
            getFileMetas(path+"/db.config");
        }catch (Exception ex){}
    }

    private void writeLogs(String stepName, String msg, boolean error) {
        if (logger != null) {
            logger.Log(stepName, msg, error);
        }
    }

    private void writeLogs(String stepName, Exception ex, boolean error) {
        if (logger != null) {
            String msg = ex.getMessage();
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\r\n" + e;
            }
            logger.Log(stepName, msg, error);
        }
    }

    @Override
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        this.meta = (SwiftMeta)smi;
        this.data = (SwiftData)sdi;
        if(super.init(smi, sdi)) {
            try {
                return true;
            } catch (Exception var4) {
                this.logError(BaseMessages.getString(PKG, "Swift.Log.ErrorOccurredDuringStepInitialize", new String[0]) + var4.getMessage());
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
        Object[] rowData = this.getRow();
        if (rowData == null) {
            return false;
        } else {
            this.data.nextBufferRowIdx = 0;
            this.data.inputRowMeta = this.getInputRowMeta().clone();
            this.data.outputRowMeta = this.data.inputRowMeta.clone();
            this.meta.getFields(this.data.outputRowMeta, this.getStepname(), (RowMetaInterface[]) null, (StepMeta) null, this, this.repository, this.metaStore);

            String filePath = "" + rowData[getFieldIdx(this.data.inputRowMeta, this.environmentSubstitute(this.meta.getFilePath()))];
            String descFilePath = filePath + ".desc";
            HashMap<String, String> map = getFileMetas(descFilePath);


            upload(filePath, map);


            try {
                BasicFileAttributes attributes = Files.getFileAttributeView(new File(filePath).toPath(), BasicFileAttributeView.class).readAttributes();
                this.putRow(this.data.outputRowMeta,new Object[]{ filePath, "lastmodified", attributes.lastModifiedTime() });
                this.putRow(this.data.outputRowMeta,new Object[]{ filePath, "contentlength", attributes.size() });
                for(String key : map.keySet()) {
                    this.putRow(this.data.outputRowMeta, new Object[]{filePath, key, map.get(key)});
                }
            } catch (Exception ex) {
                writeLogs(this.meta.getName(), "no description file", false);
            }

            this.setOutputDone();
            return true;
        }
    }


    private HashMap<String, String> getFileMetas(String path) {
        File file = new File(path);
        HashMap<String, String> map = new HashMap<>();

        try {
            FileInputStream fs = new FileInputStream(file);
            InputStreamReader is = new InputStreamReader(fs);
            BufferedReader br = new BufferedReader(is);
            String line;
            while ((line = br.readLine()) != null) {
                String[] keyValues = line.split("\\t");
                map.put(keyValues[0], keyValues[1]);
            }
            is.close();
            fs.close();
        } catch (Exception ex) {

        }
        return map;
    }

    private boolean upload(String fileName, HashMap<String, String> map){
        writeLogs(this.meta.getName(), "start upload file"+fileName, false);

        if(!createBulket()) {
            writeLogs(this.meta.getName(), "create bulket faild", false);
            return false;
        }

        HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            File file = new File(fileName);
            FileInputStream fs = new FileInputStream(file);
            HttpEntity entity = new InputStreamEntity(fs);

            HttpPut put = new HttpPut(this.meta.getServerUrl()+"/v1/AUTH_"+ this.meta.getUser().split(":")[0]+ "/" + this.meta.getFolder()+"/"+ URLEncoder.encode(file.getName(), "UTF-8"));
            put.addHeader(new BasicHeader("X-Auth-Token", getToken()));
            for(String key : map.keySet()) {
                put.addHeader(new BasicHeader("X-Object-Meta-" + key, map.get(key)));
            }

            put.setEntity(entity);

            HttpResponse response = httpclient.execute(put);
            if (entity != null) {
                writeLogs(this.meta.getName(), "end upload file"+fileName, false);
                return response.getStatusLine().getStatusCode() == 201;
            }

        } catch (Exception ex) {
            writeLogs(this.meta.getName(), ex,  true);
            this.logError(ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return false;
    }

    private boolean createBulket(){
        HttpClient httpclient = HttpClientBuilder.create().build();

        HttpPut put = new HttpPut(this.meta.getServerUrl()+"/v1/AUTH_"+ this.meta.getUser().split(":")[0] + "/" + this.meta.getFolder());
        put.addHeader(new BasicHeader("X-Auth-Token", getToken()));
        try {
            HttpResponse response = httpclient.execute(put);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                int code = response.getStatusLine().getStatusCode();
                return code == 201 || code == 202;
            }

        } catch (Exception ex) {
            writeLogs(this.meta.getName(), ex, true);
            this.logError(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        return false;
    }

    private String getToken() {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpgets = new HttpGet(this.meta.getServerUrl() + "/auth/v1.0/");
        httpgets.addHeader(new BasicHeader("X-Auth-User", this.meta.getUser()));
        httpgets.addHeader(new BasicHeader("X-Auth-Key", this.meta.getPassword()));
        try {
            HttpResponse response = httpclient.execute(httpgets);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String token = response.getHeaders("X-Auth-Token")[0].getValue();
                writeLogs(this.meta.getName(), "get token back:" + token, true);
                return token;
            }
        } catch (Exception ex) {
            writeLogs(this.meta.getName(), ex, true);
            this.logError(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        return "";
    }

    private static Integer getFieldIdx(RowMetaInterface rowMeta, String fieldName) {
        if(fieldName == null) {
            return null;
        } else {
            for(int i = 0; i < rowMeta.size(); ++i) {
                String name = rowMeta.getValueMeta(i).getName();
                if(fieldName.equals(name)) {
                    return Integer.valueOf(i);
                }
            }

            return null;
        }
    }


    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        this.meta = (SwiftMeta)smi;
        this.data = (SwiftData)sdi;
        super.dispose(smi, sdi);
    }
}
