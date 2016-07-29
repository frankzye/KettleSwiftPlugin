package com.revenco.spoon.trans.steps.swift;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Created by frank on 2016/7/26.
 */
public class Swift extends BaseStep implements StepInterface {
    private SwiftMeta meta;
    private SwiftData data;

    private static Class<?> PKG = SwiftMeta.class;

    public Swift(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
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
            return upload(filePath);
        }
    }

    private boolean upload(String fileName){
        if(!createBulket())
            return false;

        HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            File file = new File(fileName);
            FileInputStream fs = new FileInputStream(file);
            HttpEntity entity = new InputStreamEntity(fs);

            HttpPut put = new HttpPut(this.meta.getServerUrl()+"/v1/AUTH_"+ this.meta.getUser().split(":")[0]+ "/" + this.meta.getFolder()+"/"+ URLEncoder.encode(file.getName(), "UTF-8"));
            put.addHeader(new BasicHeader("X-Auth-Token", getToken()));
            put.setEntity(entity);

            HttpResponse response = httpclient.execute(put);
            if (entity != null) {
                return response.getStatusLine().getStatusCode() == 201;
            }

        } catch (Exception ex) {
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
            this.logError(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        return false;
    }

    private String getToken(){
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpgets = new HttpGet(this.meta.getServerUrl()+"/auth/v1.0/");
        httpgets.addHeader(new BasicHeader("X-Auth-User", this.meta.getUser()));
        httpgets.addHeader(new BasicHeader("X-Auth-Key", this.meta.getPassword()));
        try {
            HttpResponse response = httpclient.execute(httpgets);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return response.getHeaders("X-Auth-Token")[0].getValue();
            }

        } catch (Exception ex) {
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
