/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.fei.ClassDiagramEditor.Editor.Parsers.MethodParser;
import org.fei.ClassDiagramEditor.JavaParser.MethodAndConstructorParameterParser;
import org.fei.ClassDiagramEditor.JavaParser.MethodAndConstructorParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;
import org.fei.ClassDiagramEditor.XMI.XmiTypePlaceHolder;

/**
 *
 * @author Tomas
 */
public class MethodData{
    
    private MethodAndConstructorParser method;
    private boolean isPrivate = false;
    private boolean isPublic = false;
    private boolean isProtected = false;
    private boolean isPackage = false;
    private boolean constructor = false;
    private boolean enumeration = false;
    
    private String name;
    private String returnType;
    private String xmiid;
    
    private boolean staticMember;
    private boolean abstractMod;
    private String descriptor;
    
    private int access;
    
    private ArrayList<MethodParameter> parameters = new ArrayList<MethodParameter>();
    
    public MethodData(MethodAndConstructorParser method) {
        
        this.name = method.getMECName();
        this.returnType = method.getMECReturnType();
        this.method = method;
        this.constructor = method.isConstructor();
        this.enumeration = method.isEnumeration();
        this.staticMember = method.isStaticMEC();
        this.abstractMod = false;
        this.descriptor = new String();
        this.access = method.getAccess();
        this.isProtected = method.isProtectedMEC();
        this.isPublic = method.isPublicMEC();
        this.isPrivate = method.isPrivateMEC();
        this.isPackage = method.isPackageMEC();
        
        List<MethodAndConstructorParameterParser> params = method.getParameters();

        for (MethodAndConstructorParameterParser p : params) {
            
            parameters.add(new MethodParameter(p));
        }
        
        abstractMod = method.isAbstractMEC();
        this.xmiid = XmiIdFactory.getId();
    }
    
    public MethodData(String name, String type, Set<Modifier> mods ,ArrayList<MethodParser.ParameterInfo> params) {
    
        this.name = name;
        this.returnType = type;
        this.descriptor = new String();
        
        for(MethodParser.ParameterInfo pi : params)
            parameters.add(new MethodParameter(pi.name, pi.type));
        
        setByModifiers(mods);
        this.xmiid = XmiIdFactory.getId();
    }
    
    public ArrayList<MethodParameter> getParameters() {
        return this.parameters;
    }

    public boolean isConstructor() {
        return constructor;
    }

    public boolean isAbstract() {
        return abstractMod;
    }

    public boolean isEnumeration() {
        return enumeration;
    }

    public int getAccess() {
        return access;
    }
    
    
    
    public void setParameters(ArrayList<MethodParameter> parameters) {
        this.parameters = parameters;
    }
    
    public void setByModifiers(Set<Modifier> mods) {
        
        this.abstractMod = mods.contains(Modifier.ABSTRACT);
        this.staticMember = mods.contains(Modifier.STATIC);
        
        this.isPrivate = false;
        this.isPublic = false;
        this.isProtected = false;
        this.isPackage = false;
        
        if (mods.contains(Modifier.PRIVATE)) {
            this.isPrivate = true;
            return;
        }
        if (mods.contains(Modifier.PROTECTED)) {
            this.isProtected = true;
            return;
        }
        if (mods.contains(Modifier.PUBLIC)){
            this.isPublic = true;
            return;
        }
        if (mods.contains(Modifier.DEFAULT));{
            this.isPackage = true;
            return;
        }
        
    }
    
    public String accessToString() {
            
        if (this.isPublic)
            return "+";
        if (this.isPrivate)
            return "-";
        if (this.isProtected)
            return "#";
                
        //package private
        return "~";
    }
    
    public String accessToFullString() {
        
        if (this.isPublic)
            return "public";
        if (this.isPrivate)
            return "private";
        if (this.isProtected)
            return "protected";
                
        //package private
        return "package";
    }
    
  
    public Element xmiCreateOperationElement(Document document) {
    
        Element type = document.createElement("UML:Parameter.type");
        Element dataType = document.createElement("UML:DataType");
        Element behavioralFeature = document.createElement("UML:BehavioralFeature.parameter");
        Element operation = document.createElement("UML:Operation");
        Element returnParameter = document.createElement("UML:Parameter");
        
        operation.setAttribute("xmi.id", this.xmiid);
        operation.setAttribute("name", this.name);
        operation.setAttribute("visibility", this.accessToFullString());
        operation.setAttribute("isSpecification", "false");
        operation.setAttribute("ownerScope", this.isStaticMember() ? "classifier" : "instance");
        operation.setAttribute("isAbstract", this.isAbstract() ? "true" : "false");
        
        operation.appendChild(behavioralFeature);
        
        returnParameter.setAttribute("xmi.id", XmiIdFactory.getId());
        returnParameter.setAttribute("name", "return");
        returnParameter.setAttribute("isSpecification", "false");
        returnParameter.setAttribute("kind", "return");
        // todo type
        
        dataType.setAttribute("xmi.idref", XmiTypePlaceHolder.getTypeId(this.returnType));
        
        type.appendChild(dataType);
        
        returnParameter.appendChild(type);
        
        behavioralFeature.appendChild(returnParameter);
        
        for (MethodParameter param : this.parameters) {
            behavioralFeature.appendChild(param.xmiCreateParameterElement(document));
        }
        
        return operation;
    }
    

    public MethodAndConstructorParser getMethod() {
        return method;
    }

    public void setMethod(MethodAndConstructorParser method) {
        this.method = method;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isIsProtected() {
        return isProtected;
    }

    public void setIsProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public boolean isIsPackage() {
        return isPackage;
    }

    public void setIsPackage(boolean isPackage) {
        this.isPackage = isPackage;
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

    public boolean isStaticMember() {
        return staticMember;
    }

    public void setStaticMember(boolean staticMember) {
        this.staticMember = staticMember;
    }

    public boolean isAbstractMod() {
        return abstractMod;
    }

    public void setAbstractMod(boolean abstractMod) {
        this.abstractMod = abstractMod;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
    
}
