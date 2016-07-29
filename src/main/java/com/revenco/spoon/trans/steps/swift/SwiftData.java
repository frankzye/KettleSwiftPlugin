package com.revenco.spoon.trans.steps.swift;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Created by frank on 2016/7/26.
 */
public class SwiftData  extends BaseStepData implements StepDataInterface {
    public RowMetaInterface inputRowMeta;
    public RowMetaInterface outputRowMeta;
    public int nextBufferRowIdx = 0;
    public Object[][] inputRowBuffer;

    public  SwiftData(){

    }
}
