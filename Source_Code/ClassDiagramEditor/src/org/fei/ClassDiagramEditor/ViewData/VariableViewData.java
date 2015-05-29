package org.fei.ClassDiagramEditor.ViewData;


/**
 * Trieda ktora vytvori retazec v spravnom UML formate pre datove cleny tried.
 * @author Tomas
 */
public class VariableViewData extends Data {
    
    protected org.fei.ClassDiagramEditor.Data.VariableData var;
    
    public VariableViewData(org.fei.ClassDiagramEditor.Data.VariableData variable) {
        var = variable;
        
        this.generateRepresent();
    }
    public void generateRepresent() {
        this.represent = var.accessToString() + " ";
        this.represent += var.getName() + ": ";
        this.represent += var.getType();
                
    }

    @Override
    public String toString() {
        return this.represent;
    }
    
    public String getName() {
        return var.getName();
    }

    public org.fei.ClassDiagramEditor.Data.VariableData getVar() {
        return var;
    }
}