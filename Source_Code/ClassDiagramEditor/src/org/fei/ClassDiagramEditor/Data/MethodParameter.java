package org.fei.ClassDiagramEditor.Data;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.fei.ClassDiagramEditor.JavaParser.MethodAndConstructorParameterParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;
import org.fei.ClassDiagramEditor.XMI.XmiTypePlaceHolder;

/**
 *
 * @author Tomas
 */
public class MethodParameter{

    private MethodAndConstructorParameterParser param;
    private String descriptor;
    
    private String name;
    private String returnType;
    private String xmiid;

    public MethodParameter(MethodAndConstructorParameterParser param) {

        this.name = param.getName();
        this.returnType = param.getType();
        this.param = param;
        String type = param.getType();
        descriptor = param.getDescriptor();        
        this.xmiid = XmiIdFactory.getId();
    }
    
    public MethodParameter(String name, String returnType) {
        
        this.name = name;
        this.returnType = returnType;      
        descriptor = returnType + " " + name;
        param = null;
        this.xmiid = XmiIdFactory.getId();
    }

    public MethodAndConstructorParameterParser getParameter() {
        return param;
    }
    
    public String getDescriptor() {
        return descriptor;
    }

    public Element xmiCreateParameterElement(Document document) {
    
        Element type = document.createElement("UML:Parameter.type");
        Element dataTypeElement = document.createElement("UML:DataType");
        Element parameter = document.createElement("UML:Parameter");
        
        parameter.setAttribute("xmi.id", this.xmiid);
        parameter.setAttribute("name", this.name);
        parameter.setAttribute("isSpecification", "false");
        parameter.setAttribute("kind", "in");

        dataTypeElement.setAttribute("xmi.idref", XmiTypePlaceHolder.getTypeId(this.returnType));
        
        type.appendChild(dataTypeElement);
        
        parameter.appendChild(type);
        
        return parameter;
    }

    public MethodAndConstructorParameterParser getParam() {
        return param;
    }

    public void setParam(MethodAndConstructorParameterParser param) {
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getXmiid() {
        return xmiid;
    }

    public void setXmiid(String xmiid) {
        this.xmiid = xmiid;
    }
    
    
}
