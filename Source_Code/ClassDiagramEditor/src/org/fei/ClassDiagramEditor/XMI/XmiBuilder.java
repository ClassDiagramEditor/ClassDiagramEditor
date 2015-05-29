/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fei.ClassDiagramEditor.XMI;

import java.util.ArrayList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fei.ClassDiagramEditor.Data.PackageData;
import org.fei.ClassDiagramEditor.ProjectScannerFactory;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tomas
 */
public class XmiBuilder {
    
    private ArrayList<PackageData> packages;
    private ArrayList<Line> relations;
    
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    
    private Document xmiDocument;
    
    private Element rootElement;
    private Element headerElement;
    private Element contentElement;
    private Element modelElement;
    private Element ownedElement;
    
    public XmiBuilder(ArrayList<PackageData> packages, ArrayList<Line> relations) throws ParserConfigurationException {
    
        this.packages = packages;
        this.relations = relations;
        
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
        
        xmiDocument = docBuilder.newDocument();
        
        rootElement = createRootElement();
        xmiDocument.appendChild(rootElement);
        
        headerElement = createHeaderElement();
        rootElement.appendChild(headerElement);
        
        contentElement = xmiDocument.createElement("XMI.content");
        rootElement.appendChild(contentElement);
        
        modelElement = createModelElement();
        contentElement.appendChild(modelElement);
        
        ownedElement = xmiDocument.createElement("UML:Namespace.ownedElement");
        modelElement.appendChild(ownedElement);
        
        for (PackageData pcg : this.packages) {
            if (pcg.isTopLevelPackage()) {
                ownedElement.appendChild(pcg.xmiCreatePackageElement(xmiDocument));
            }
        }
        
        // vztahy
        for (Line l : this.relations) {
            Element relation = l.xmiCreateRelationElement(xmiDocument);   
            if (relation != null)
                ownedElement.appendChild(relation);
        }
        
        // datove typy
        for (Map.Entry<String, String> set : XmiTypePlaceHolder.getTypes().entrySet()) {
            ownedElement.appendChild(createDataTypeElement(set));
        }
    }
    
    // <XMI xmi.version="1.2" xmlns:UML="org.omg/UML/1.4">
    private Element createRootElement() {
        
        Element xmi = getXmiDocument().createElement("XMI");
        xmi.setAttribute("xmi.version", "1.2");
        xmi.setAttribute("xmlns:UML", "org.omg/UML/1.4");
        
        return xmi;
    }
    
    /*
    <XMI.header>
        <XMI.documentation>
            <XMI.exporter>ananas.org stylesheet</XMI.exporter>
        </XMI.documentation>
        <XMI.metamodel xmi.name="UML" xmi.version="1.4"/>
    </XMI.header>
    */
    private Element createHeaderElement() {
    
        Element header;
        Element documentation;
        Element exporter;
        Element metamodel;
        
        exporter = getXmiDocument().createElement("XMI.exporter");
        exporter.appendChild(getXmiDocument().createTextNode("Netbeans module:org.fei.ClassDiagram"));
        
        documentation = getXmiDocument().createElement("XMI.documentation");
        documentation.appendChild(exporter);
        
        metamodel = getXmiDocument().createElement("XMI.metamodel");
        metamodel.setAttribute("xmi.name", "UML");
        metamodel.setAttribute("xmi.version", "1.4");
        
        header = getXmiDocument().createElement("XMI.header");
        header.appendChild(documentation);
        header.appendChild(metamodel);
        
        return header;
    }
    
    private Element createModelElement() {
    
        Element model = getXmiDocument().createElement("UML:Model");
        model.setAttribute("xmi.id", XmiIdFactory.getId());
        model.setAttribute("name", ProjectScannerFactory.getProjectScanner().getProjectName());
        model.setAttribute("isSpecification", "false");
        model.setAttribute("isRoot", "false");
        model.setAttribute("isLeaf", "false");
        model.setAttribute("isAbstract", "false");
        
        return model;
    }

    
    private Element createDataTypeElement(Map.Entry<String, String> typeSet) {
    
        Element type = xmiDocument.createElement("UML:DataType");
        
        type.setAttribute("xmi.id", typeSet.getValue());
        type.setAttribute("name", typeSet.getKey());
        type.setAttribute("isSpecification", "false");
        type.setAttribute("isAbstract", "false");
        
        return type;
    }

    /**
     * @return the xmiDocument
     */
    public Document getXmiDocument() {
        return xmiDocument;
    }
}
