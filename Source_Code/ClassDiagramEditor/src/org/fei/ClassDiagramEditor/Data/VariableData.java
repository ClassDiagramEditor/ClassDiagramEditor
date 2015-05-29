/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.Data;


import java.util.Set;
import javax.lang.model.element.Modifier;
import org.fei.ClassDiagramEditor.JavaParser.VariableParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.fei.ClassDiagramEditor.XMI.XmiIdFactory;
import org.fei.ClassDiagramEditor.XMI.XmiTypePlaceHolder;

/**
 *
 * @author Tomas
 */
public class VariableData{
        
    private VariableParser variable;
    private ClassData asotioationType;   // ak ide o asociaciu, a je tohto typu
    
    private boolean isPrivate = false;
    private boolean isPublic = false;
    private boolean isProtected = false;
    private boolean isPackage = false;
    private boolean constructor = false;
    private String enumName; // pre enum
    
    private String name;
    private String type;
    private String xmiid;
    
    private boolean staticMember;
    private boolean abstractMod;
    private String descriptor;
    
    private int access;
    
    public VariableData(VariableParser variable) {
    
        this.name = variable.getVariableName();
        this.type = variable.getVariableType();
        this.variable = variable;
        this.asotioationType = null;
        this.staticMember = variable.isStaticVariable();
        this.abstractMod = false;
        this.access = variable.getAccess();
        
        this.isProtected = variable.isProtectedVariable();
        this.isPublic = variable.isPublicVariable();
        this.isPrivate = variable.isPrivateVariable();
        this.isPackage = variable.isPackageVariable();
        
        this.xmiid = XmiIdFactory.getId();       
    }
    
    public VariableData(String enumVariable, String enumName) {
        this.name = enumVariable;
        this.type = "enum";
        this.enumName = enumName;
        this.xmiid = XmiIdFactory.getId();
    }
    
    public VariableData(String name, String type,Set<Modifier> mods) {
        
        this.name = name;
        this.type = type;
        this.variable = null;
        this.asotioationType = null;
        this.descriptor = new String();
        setByModifiers(mods);
        this.xmiid = XmiIdFactory.getId();
    }

    public int getAccess() {
        return access;
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

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }
    

    public Element xmiCreateAttributeElement(Document document) {
    
        Element type = document.createElement("UML:StructuralFeature.type");
        
        Element attribute = document.createElement("UML:Attribute");
        
        attribute.setAttribute("xmi.id", this.xmiid);
        attribute.setAttribute("name", this.name);
        attribute.setAttribute("visibility", this.accessToFullString());
        attribute.setAttribute("isSpecification", "false");
        attribute.setAttribute("ownerScope", this.isStaticMember() ? "classifier" : "instance");
        
        if (asotioationType != null) {
            Element classElement = document.createElement("UML:Class");
            classElement.setAttribute("xmi.idref",  asotioationType.getXmiid());
            type.appendChild(classElement);
        }
        else {
            Element dataType = document.createElement("UML:DataType");
            dataType.setAttribute("xmi.idref", XmiTypePlaceHolder.getTypeId(this.type));
            type.appendChild(dataType);
        }

        attribute.appendChild(type);
         
        return attribute;
    }
    
    public Element xmiCreateEnumerationLiteralElement(Document document) {
    
        Element enumeration = document.createElement("UML:EnumerationLiteral");
        enumeration.setAttribute("xmi.id", this.xmiid);
        enumeration.setAttribute("name", this.name);
        enumeration.setAttribute("isSpecification", "false");
        
        return enumeration;
    }

    /**
     * @param asotioationType the asotioationType to set
     */
    public void setAsotioationType(ClassData asotioationType) {
        this.asotioationType = asotioationType;
    }

    public VariableParser getVariable() {
        return variable;
    }

    public void setVariable(VariableParser variable) {
        this.variable = variable;
    }

    public ClassData getAsotioationType() {
        return asotioationType;
    }

    public String getType() {
        return type;
    }

    public String getEnumName() {
        return enumName;
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

    public boolean isConstructor() {
        return constructor;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return type;
    }

    public void setReturnType(String returnType) {
        this.type = returnType;
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
