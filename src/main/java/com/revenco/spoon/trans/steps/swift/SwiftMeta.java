package com.revenco.spoon.trans.steps.swift;

import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.w3c.dom.Node;
import org.pentaho.metastore.api.IMetaStore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Step(
        id = "Swift",
        i18nPackageName = "com.revenco.spoon.trans.steps.swift",
        name = "Swift.TypeLongDesc.Swift",
        description = "Swift.TypeTooltipDesc.Swift",
        categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Bulk",
        image = "SWIFT.svg"
)
public class SwiftMeta extends BaseStepMeta implements StepMetaInterface {
    private Map<String, String> fields = new HashMap();

    private String serverUrl;
    private String user;
    private String password;
    private String filePath;
    private String folder;

    @Override
    public void setDefault() {

    }

    @Override
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, org.pentaho.metastore.api.IMetaStore metaStore) throws KettleXMLException {
        Node e = XMLHandler.getSubNode(stepnode, "general");

        if(e!=null) {
            this.setUser(XMLHandler.getTagValue(e, "user"));
            this.setServerUrl(XMLHandler.getTagValue(e, "server"));
            this.setFilePath(XMLHandler.getTagValue(e, "file"));
            this.setPassword(XMLHandler.getTagValue(e, "password"));
            this.setFolder(XMLHandler.getTagValue(e, "folder"));
        }
    }

    @Override
    public String getXML() throws KettleException {
        StringBuffer retval = new StringBuffer();
        retval.append("<general>");
        retval.append(XMLHandler.addTagValue("user", this.getUser()));
        retval.append(XMLHandler.addTagValue("server", this.getServerUrl()));
        retval.append(XMLHandler.addTagValue("file", this.getFilePath()));
        retval.append(XMLHandler.addTagValue("password", this.getPassword()));
        retval.append(XMLHandler.addTagValue("folder", this.getFolder()));
        retval.append("</general>");
        return retval.toString();
    }

    @Override
    public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException {
        this.setUser(rep.getStepAttributeString(id_step, "user"));
        this.setServerUrl(rep.getStepAttributeString(id_step, "server"));
        this.setFilePath(rep.getStepAttributeString(id_step, "file"));
        this.setPassword(rep.getStepAttributeString(id_step, "password"));
        this.setFolder(rep.getStepAttributeString(id_step, "folder"));
    }

    @Override
    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException {
        rep.saveStepAttribute(id_transformation, id_step, "user", this.getUser());
        rep.saveStepAttribute(id_transformation, id_step, "server", this.getServerUrl());
        rep.saveStepAttribute(id_transformation, id_step, "file", this.getFilePath());
        rep.saveStepAttribute(id_transformation, id_step, "password", this.getPassword());
        rep.saveStepAttribute(id_transformation, id_step, "folder", this.getFolder());
    }

    @Override
    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int i, TransMeta transMeta, Trans trans) {
        return new Swift(stepMeta, stepDataInterface, i, transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData() {
        return new SwiftData();
    }


    public Map<String, String> getFields() {
        return this.fields;
    }

    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
        inputRowMeta.clear();
        inputRowMeta.addValueMeta(new ValueMetaString("fileName"));
        inputRowMeta.addValueMeta(new ValueMetaString("metaKey"));
        inputRowMeta.addValueMeta(new ValueMetaString("metaValue"));
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public void clearFields() {
        this.fields.clear();
    }

    public void addField(String inputName, String nameInJson) {
        this.fields.put(inputName, StringUtils.isBlank(nameInJson)?inputName:nameInJson);
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getUser(){
       return user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
