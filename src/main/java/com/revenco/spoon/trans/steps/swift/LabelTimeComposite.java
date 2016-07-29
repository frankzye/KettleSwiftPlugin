package com.revenco.spoon.trans.steps.swift;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.ui.core.PropsUI;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by frank on 2016/7/27.
 */
public class LabelTimeComposite extends Composite {
    private static final PropsUI props = PropsUI.getInstance();
    private Label wLabel;
    private Text wText;
    private CCombo wTimeUnit;
    private String lastValidValue = "";

    public LabelTimeComposite(Composite composite, String labelText, String toolTipText) {
        super(composite, 0);
        props.setLook(this);
        int middle = props.getMiddlePct();
        int threeQuarters = (middle + 100) / 2;
        byte margin = 4;
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = 0;
        formLayout.marginHeight = 0;
        formLayout.marginTop = 0;
        formLayout.marginBottom = 0;
        this.setLayout(formLayout);
        this.wText = new Text(this, 18436);
        FormData fdText = new FormData();
        fdText.left = new FormAttachment(middle, margin);
        fdText.right = new FormAttachment(threeQuarters, 0);
        this.wText.setLayoutData(fdText);
        this.wText.setToolTipText(toolTipText);
        this.wTimeUnit = new CCombo(this, 18436);
        FormData fdCombo = new FormData();
        fdCombo.left = new FormAttachment(threeQuarters, margin);
        fdCombo.right = new FormAttachment(100, 0);
        this.wTimeUnit.setEditable(false);
        this.wTimeUnit.setLayoutData(fdCombo);
        this.wTimeUnit.setItems(this.getTimeUnits());
        this.wTimeUnit.setToolTipText(toolTipText);
        this.wLabel = new Label(this, 131072);
        props.setLook(this.wLabel);
        this.wLabel.setText(labelText);
        FormData fdLabel = new FormData();
        fdLabel.left = new FormAttachment(0, 0);
        fdLabel.right = new FormAttachment(middle, 0);
        fdLabel.top = new FormAttachment(this.wText, 0, 16777216);
        this.wLabel.setLayoutData(fdLabel);
        this.wLabel.setToolTipText(toolTipText);
        this.wText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if(!StringUtils.isNumeric(LabelTimeComposite.this.wText.getText())) {
                    LabelTimeComposite.this.wText.setText(LabelTimeComposite.this.lastValidValue);
                } else {
                    LabelTimeComposite.this.lastValidValue = LabelTimeComposite.this.wText.getText();
                }

            }
        });
    }

    private String[] getTimeUnits() {
        ArrayList timeUnits = new ArrayList();
        TimeUnit[] arr$ = TimeUnit.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            TimeUnit timeUnit = arr$[i$];
            timeUnits.add(timeUnit.toString());
        }

        return (String[])timeUnits.toArray(new String[timeUnits.size()]);
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.valueOf(this.wTimeUnit.getItem(this.wTimeUnit.getSelectionIndex()));
    }

    public void setTimeUnit(TimeUnit tu) {
        for(int i = 0; i < this.wTimeUnit.getItemCount(); ++i) {
            if(tu.toString().equals(this.wTimeUnit.getItem(i))) {
                this.wTimeUnit.select(i);
                break;
            }
        }

    }

    public void addModifyListener(ModifyListener lsMod) {
        this.wText.addModifyListener(lsMod);
    }

    public void addSelectionListener(SelectionAdapter lsDef) {
        this.wText.addSelectionListener(lsDef);
    }

    public void setText(String name) {
        this.wText.setText(name);
    }

    public String getText() {
        return this.wText.getText();
    }

    public void setEchoChar(char c) {
        this.wText.setEchoChar(c);
    }

    public void setEnabled(boolean flag) {
        this.wText.setEnabled(flag);
        this.wLabel.setEnabled(flag);
    }

    public boolean setFocus() {
        return this.wText.setFocus();
    }

    public void addTraverseListener(TraverseListener tl) {
        this.wText.addTraverseListener(tl);
    }

    public Text getTextWidget() {
        return this.wText;
    }

    public Label getLabelWidget() {
        return this.wLabel;
    }
}
