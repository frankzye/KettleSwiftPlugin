package com.revenco.spoon.trans.steps.swift;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.LabelComboVar;
import org.pentaho.di.ui.core.widget.LabelTextVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.ui.trans.step.TableItemInsertListener;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by frank on 2016/7/26.
 */
public class SwiftDialog extends BaseStepDialog implements StepDialogInterface {
    private SwiftMeta model;
    private ModifyListener lsMod;
    private static Class<?> PKG = SwiftMeta.class;
    private CTabFolder wTabFolder;
    private CTabItem wServersTab;
    private Button wTestCl;
    private TableView wServers;
    private FormData fdTabFolder;
    private CTabItem wFieldsTab;
    private TableView wFields;
    private String[] fieldNames;
    private CTabItem wGeneralTab;
    private Composite wGeneralComp;
    private FormData fdGeneralComp;
    private Group wServerGroup;

    private LabelTextVar serverTab;
    private LabelTextVar userTab;
    private LabelTextVar pwdTab;
    private LabelTextVar folderTab;
    private LabelComboVar filePathField;

    public SwiftDialog(org.eclipse.swt.widgets.Shell parent, Object in, TransMeta transMeta, String name) {
        super(parent, (BaseStepMeta)in, transMeta, name);
        this.model = (SwiftMeta)in;
    }

    @Override
    public String open() {
        Shell parent = this.getParent();
        Display display = parent.getDisplay();
        this.shell = new Shell(parent, 3312);
        this.props.setLook(this.shell);
        this.setShellImage(this.shell, this.model);
        this.lsMod = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                SwiftDialog.this.model.setChanged();
            }
        };
        this.changed = this.model.hasChanged();
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = 60;
        formLayout.marginHeight = 50;
        this.shell.setLayout(formLayout);
        this.shell.setText(BaseMessages.getString(PKG, "Swift.TypeLongDesc.Swift", new String[0]));
        int middle = this.props.getMiddlePct();
        byte margin = 4;
        this.wlStepname = new Label(this.shell, 131072);
        this.wlStepname.setText(BaseMessages.getString(PKG, "Swift.TypeLongDesc.Swift", new String[0]));
        this.props.setLook(this.wlStepname);
        this.fdlStepname = new FormData();
        this.fdlStepname.left = new FormAttachment(0, 0);
        this.fdlStepname.top = new FormAttachment(0, margin);
        this.fdlStepname.right = new FormAttachment(middle, -margin);
        this.wlStepname.setLayoutData(this.fdlStepname);
        this.wStepname = new Text(this.shell, 18436);
        this.wStepname.setText(this.stepname);
        this.props.setLook(this.wStepname);
        this.wStepname.addModifyListener(this.lsMod);
        this.fdStepname = new FormData();
        this.fdStepname.left = new FormAttachment(middle, 0);
        this.fdStepname.top = new FormAttachment(0, margin);
        this.fdStepname.right = new FormAttachment(100, 0);
        this.wStepname.setLayoutData(this.fdStepname);
        this.wTabFolder = new CTabFolder(this.shell, 2048);
        this.props.setLook(this.wTabFolder, 5);

        this.addGeneralTab();
        this.addFieldsTab();

        this.wOK = new Button(this.shell, 8);
        this.wOK.setText(BaseMessages.getString(PKG, "System.Button.OK", new String[0]));
        this.wCancel = new Button(this.shell, 8);
        this.wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel", new String[0]));
        this.setButtonPositions(new Button[]{this.wOK, this.wCancel}, margin, (Control)null);
        this.fdTabFolder = new FormData();
        this.fdTabFolder.left = new FormAttachment(0, 0);
        this.fdTabFolder.top = new FormAttachment(this.wStepname, margin);
        this.fdTabFolder.right = new FormAttachment(100, 0);
        this.fdTabFolder.bottom = new FormAttachment(this.wOK, -margin);
        this.wTabFolder.setLayoutData(this.fdTabFolder);
        this.addStandardListeners();
        this.wTabFolder.setSelection(0);
        this.setSize();
        this.getData(this.model);
        this.model.setChanged(this.changed);
        this.shell.open();

        while(!this.shell.isDisposed()) {
            if(!display.readAndDispatch()) {
                display.sleep();
            }
        }

        return this.stepname;
    }

    private void addStandardListeners() {
        this.lsOK = new Listener() {
            public void handleEvent(Event e) {
                SwiftDialog.this.ok();
            }
        };
        this.lsCancel = new Listener() {
            public void handleEvent(Event e) {
                SwiftDialog.this.cancel();
            }
        };
        this.lsMod = new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                SwiftDialog.this.model.setChanged();
            }
        };
        this.wOK.addListener(13, this.lsOK);
        this.wCancel.addListener(13, this.lsCancel);
        this.lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                SwiftDialog.this.ok();
            }
        };
        this.wStepname.addSelectionListener(this.lsDef);
        this.shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                SwiftDialog.this.cancel();
            }
        });
    }

    private void addGeneralTab() {
        this.wGeneralTab = new CTabItem(this.wTabFolder, 0);
        this.wGeneralTab.setText(BaseMessages.getString(PKG, "Swift.Tab.General", new String[0]));
        this.wGeneralComp = new Composite(this.wTabFolder, 0);
        this.props.setLook(this.wGeneralComp);
        FormLayout generalLayout = new FormLayout();
        generalLayout.marginWidth = 3;
        generalLayout.marginHeight = 3;
        this.wGeneralComp.setLayout(generalLayout);
        this.fillServerGroup(this.wGeneralComp);
        this.fdGeneralComp = new FormData();
        this.fdGeneralComp.left = new FormAttachment(0, 0);
        this.fdGeneralComp.top = new FormAttachment(this.wStepname, 4);
        this.fdGeneralComp.right = new FormAttachment(100, 0);
        this.fdGeneralComp.bottom = new FormAttachment(100, 0);
        this.wGeneralComp.setLayoutData(this.fdGeneralComp);
        this.wGeneralComp.layout();
        this.wGeneralTab.setControl(this.wGeneralComp);
    }

    private void fillServerGroup(Composite parentTab) {
        this.wServerGroup = new Group(parentTab, 32);
        this.props.setLook(this.wServerGroup);
        this.wServerGroup.setText(BaseMessages.getString(PKG, "Swift.Tab.General.Server", new String[0]));
        FormLayout indexGroupLayout = new FormLayout();
        indexGroupLayout.marginWidth = 10;
        indexGroupLayout.marginHeight = 10;
        this.wServerGroup.setLayout(indexGroupLayout);
        this.serverTab = new LabelTextVar(this.transMeta, this.wServerGroup, BaseMessages.getString(PKG, "Swift.Tab.General.Server.Server", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Server.Server", new String[0]));
        this.userTab = new LabelTextVar(this.transMeta, this.wServerGroup, BaseMessages.getString(PKG, "Swift.Tab.General.Server.User", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Server.User", new String[0]));
        this.pwdTab = new LabelTextVar(this.transMeta, this.wServerGroup, BaseMessages.getString(PKG, "Swift.Tab.General.Server.Password", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Server.Password", new String[0]));
        this.folderTab = new LabelTextVar(this.transMeta, this.wServerGroup, BaseMessages.getString(PKG, "Swift.Tab.General.Server.Folder", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Server.Folder", new String[0]));

        this.filePathField = new LabelComboVar(this.transMeta, this.wServerGroup, BaseMessages.getString(PKG, "Swift.Tab.General.Server.FileName", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Server.FileName", new String[0]));
        filePathField.getComboWidget().setEditable(true);

        filePathField.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
                SwiftDialog.this.getPreviousFields(filePathField);
            }
        });
        this.getPreviousFields(filePathField);

        serverTab.addModifyListener(this.lsMod);
        userTab.addModifyListener(this.lsMod);
        pwdTab.addModifyListener(this.lsMod);
        filePathField.addModifyListener(this.lsMod);
        folderTab.addModifyListener(this.lsMod);

        Button wTest = new Button(this.wServerGroup, 8);
        wTest.setText(BaseMessages.getString(PKG, "Swift.Tab.General.Btn.Test", new String[0]));
        wTest.setToolTipText(BaseMessages.getString(PKG, "Swift.Tab.General.Btn.Test", new String[0]));
        wTest.addListener(13, new Listener() {
            public void handleEvent(Event arg0) {
                SwiftDialog.this.test(serverTab.getText(), userTab.getText(), pwdTab.getText());
            }
        });

        Control[] connectionControls = new Control[]{serverTab, userTab, pwdTab, folderTab, filePathField};
        this.placeControls(this.wServerGroup, connectionControls);
        BaseStepDialog.positionBottomButtons(this.wServerGroup, new Button[]{wTest}, 4, filePathField);

        FormData fdServerGroup = new FormData();
        fdServerGroup.left = new FormAttachment(0, 4);
        fdServerGroup.top = new FormAttachment(this.wStepname, 4);
        fdServerGroup.right = new FormAttachment(100, -4);
        this.wServerGroup.setLayoutData(fdServerGroup);
    }

    private void test(String server, String user, String pwd) {

        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpGet httpgets = new HttpGet(server + "/auth/v1.0/");
        httpgets.addHeader(new BasicHeader("X-Auth-User", user));
        httpgets.addHeader(new BasicHeader("X-Auth-Key", pwd));
        try {
            HttpResponse response = httpclient.execute(httpgets);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                this.showMessage("connected, Token" + response.getHeaders("X-Auth-Token")[0].getValue());
            }

        } catch (Exception ex) {
            this.showMessage("faield");
            this.logError(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    private void showMessage(String message) {
        MessageBox mb = new MessageBox(this.shell, 34);
        mb.setMessage(message);
        mb.setText(BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Hint", new String[0]));
        mb.open();
    }

    private void placeControls(Group group, Control[] controls) {
        Object previousAbove = group;
        Object previousLeft = group;
        Control[] arr$ = controls;
        int len$ = controls.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Control control = arr$[i$];
            if(control instanceof Label) {
                this.addLabelAfter(control, (Control)previousAbove);
                previousLeft = control;
            } else {
                this.addWidgetAfter(control, (Control)previousAbove, (Control)previousLeft);
                previousAbove = control;
                previousLeft = group;
            }
        }

    }

    private void addWidgetAfter(Control widget, Control widgetAbove, Control widgetLeft) {
        this.props.setLook(widget);
        FormData fData = new FormData();
        fData.left = new FormAttachment(widgetLeft, 4);
        fData.top = new FormAttachment(widgetAbove, 4);
        fData.right = new FormAttachment(100, -4);
        widget.setLayoutData(fData);
    }

    private void addLabelAfter(Control widget, Control widgetAbove) {
        this.props.setLook(widget);
        FormData fData = new FormData();
        fData.top = new FormAttachment(widgetAbove, 4);
        fData.right = new FormAttachment(35, -4);
        widget.setLayoutData(fData);
    }

    private void addFieldsTab() {
        this.wFieldsTab = new CTabItem(this.wTabFolder, 0);
        this.wFieldsTab.setText(BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Field", new String[0]));
        FormLayout fieldsLayout = new FormLayout();
        fieldsLayout.marginWidth = 5;
        fieldsLayout.marginHeight = 5;
        Composite wFieldsComp = new Composite(this.wTabFolder, 0);
        wFieldsComp.setLayout(fieldsLayout);
        this.props.setLook(wFieldsComp);
        this.wGet = new Button(wFieldsComp, 8);
        this.wGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields", new String[0]));
        this.wGet.setToolTipText(BaseMessages.getString(PKG, "System.Tooltip.GetFields", new String[0]));
        this.lsGet = new Listener() {
            public void handleEvent(Event e) {
                SwiftDialog.this.getPreviousFields(SwiftDialog.this.wFields);
            }
        };
        this.wGet.addListener(13, this.lsGet);
        this.setButtonPositions(new Button[]{this.wGet}, 4, (Control)null);
        int fieldsRowCount = this.model.getFields().keySet().size();
        String[] names = this.fieldNames != null?this.fieldNames:new String[]{""};
        ColumnInfo[] columnsMeta = new ColumnInfo[]{new ColumnInfo(BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Field", new String[0]), 2, names, false), new ColumnInfo(BaseMessages.getString(PKG, "Swift.Tab.General.Msg.TargetField", new String[0]), 1, false)};
        this.wFields = new TableView(this.transMeta, wFieldsComp, 67586, columnsMeta, fieldsRowCount, this.lsMod, this.props);
        FormData fdFields = new FormData();
        fdFields.left = new FormAttachment(0, 4);
        fdFields.top = new FormAttachment(0, 4);
        fdFields.right = new FormAttachment(100, -4);
        fdFields.bottom = new FormAttachment(this.wGet, -4);
        this.wFields.setLayoutData(fdFields);
        FormData fdFieldsComp = new FormData();
        fdFieldsComp.left = new FormAttachment(0, 0);
        fdFieldsComp.top = new FormAttachment(0, 0);
        fdFieldsComp.right = new FormAttachment(100, 0);
        fdFieldsComp.bottom = new FormAttachment(100, 0);
        wFieldsComp.setLayoutData(fdFieldsComp);
        wFieldsComp.layout();
        this.wFieldsTab.setControl(wFieldsComp);
    }

    private void getPreviousFields(TableView table) {
        try {
            RowMetaInterface ke = this.transMeta.getPrevStepFields(this.stepname);
            if(ke != null) {
                BaseStepDialog.getFieldsFromPrevious(ke, table, 1, new int[]{1, 2}, (int[])null, 0, 0, (TableItemInsertListener)null);
            }
        } catch (KettleException var3) {
            new ErrorDialog(this.shell, BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Title", new String[0]), BaseMessages.getString(PKG, "System.Dialog.GetFieldsFailed.Message", new String[0]), var3);
        }
    }

    private void getPreviousFields(LabelComboVar combo) {
        String value = combo.getText();
        combo.removeAll();
        combo.setItems(this.getInputFieldNames());
        if(value != null) {
            combo.setText(value);
        }
    }

    private String[] getInputFieldNames() {
        if(this.fieldNames == null) {
            try {
                RowMetaInterface ke = this.transMeta.getPrevStepFields(this.stepname);
                if(ke != null) {
                    this.fieldNames = ke.getFieldNames();
                }
            } catch (KettleException var2) {
                new ErrorDialog(this.shell, BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Error", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Error", new String[0]), var2);
                return new String[0];
            }
        }

        return this.fieldNames;
    }

    private void cancel() {
        this.stepname = null;
        this.model.setChanged(this.changed);
        this.dispose();
    }

    private void ok() {
        try {
            this.toModel(this.model);
        } catch (KettleException var2) {
            new ErrorDialog(this.shell, BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Error", new String[0]), BaseMessages.getString(PKG, "Swift.Tab.General.Msg.Error", new String[0]), var2);
        }

        this.dispose();
    }

    private void toModel(SwiftMeta in) throws KettleException {
        this.stepname = this.wStepname.getText();

        in.setUser(this.userTab.getText());
        in.setPassword(this.pwdTab.getText());
        in.setFilePath(this.filePathField.getText());
        in.setServerUrl(this.serverTab.getText());
        in.setFolder(this.folderTab.getText());

        in.clearFields();
        int i;
        String[] row;

        for(i = 0; i < this.wFields.getItemCount(); ++i) {
            row = this.wFields.getItem(i);
            if(StringUtils.isNotBlank(row[0])) {
                in.addField(row[0], row[1]);
            }
        }
    }

    public void getData(SwiftMeta in) {
        this.userTab.setText(Const.NVL(in.getUser(), ""));
        this.pwdTab.setText(Const.NVL(in.getPassword(),""));
        this.serverTab.setText(Const.NVL(in.getServerUrl(),""));
        this.filePathField.setText(Const.NVL(in.getFilePath(),""));
        this.folderTab.setText(Const.NVL(in.getFolder(),""));
        this.mapToTableView(this.model.getFields(), this.wFields);
        this.wStepname.selectAll();
        this.wStepname.setFocus();
    }

    private void mapToTableView(Map<String, String> map, TableView table) {
        Iterator i$ = map.keySet().iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();
            table.add(new String[]{key, (String)map.get(key)});
        }

        table.removeEmptyRows();
        table.setRowNums();
    }
}
