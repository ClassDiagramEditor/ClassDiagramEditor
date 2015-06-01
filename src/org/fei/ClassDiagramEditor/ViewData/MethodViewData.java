package org.fei.ClassDiagramEditor.ViewData;

import java.util.List;
import org.fei.ClassDiagramEditor.Data.ClassData;
import org.fei.ClassDiagramEditor.Data.MethodParameter;

/**
 * Trieda ktora vytvori retazec v spravnom UML formate pre metody tried.
 *
 * @author Tomas
 */
public class MethodViewData extends Data {

    protected org.fei.ClassDiagramEditor.Data.MethodData met;
    private boolean isConstructor;
    private boolean isEnumeration;
    protected ClassData data;
    private String methodPrefix;
    private String methodName;
    private String methodPostfix;

    public MethodViewData(org.fei.ClassDiagramEditor.Data.MethodData method, ClassData classData) {
        
        met = method;
        data = classData;
        //Message.showMessage("metoda " + met.getName() + " " + met.getTypeSignature());
        isConstructor = false;
        isEnumeration = false;

        initMethod();
    }

    @Override
    public String toString() {

        if (met.isConstructor()) {
            isConstructor = true;
            methodName = data.getSimpleName();
        }

        represent = methodPrefix + methodName + methodPostfix;
        return this.represent;
    }

    public void initMethod() {
        this.methodPrefix = met.accessToString() + " ";

        String metName = met.getName();

        if (met.isConstructor()) {
            metName = data.getSimpleName();
            isConstructor = true;
        }

        this.methodName = metName;

        methodPostfix = "(";

        List<MethodParameter> params = met.getParameters();

        for (MethodParameter p : params) {

            //Message.showMessage("parameter: " + p.getName() + " " + p.getDescriptor());
            this.methodPostfix += p.getName() + ": ";
            this.methodPostfix += p.getReturnType();

            if (params.get(params.size() - 1) != p) {
                this.methodPostfix += ", ";
            }
        }
        
        
        

        if (isConstructor) {
            methodPostfix += ")";
        }else {
            methodPostfix += "): " + met.getReturnType();
        }
        
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setIsConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public org.fei.ClassDiagramEditor.Data.MethodData getMethod() {
        return this.met;
    }
    
    
    
}