package org.fei.ClassDiagramEditor.ViewData;

import japa.parser.ast.body.ModifierSet;

/**
 * Abstraktna trieda od ktorej dalej dedia triedy, ktore reprezentuju vzhlad atributov tried.
 * Datovych casti a metod.
 * 
 * @author Tomas
 */
public abstract class Data {
    // ak datovy clen je asociacia oznaci sa na true, aby sa nevykreslila v triede
    private boolean isAsotiation = false;
    // predstavuje retazcovu reprezentaciu atributu triedy
    protected String represent;
    protected static String newline = System.getProperty("line.separator");
   /* public Data(String s) {
        represent = s;
    }*/

    public String getRepresent() {
        return represent;
    }
    
    public String accessToString(int access) {

        if (ModifierSet.isPublic(access))
            return "+";
        if (ModifierSet.isPrivate(access))
            return "-";
        if (ModifierSet.isProtected(access))
            return "#";
                
        return "~";
    }

    /*
    public void setRepresent(String represent) {
        this.represent = represent;
    }*/

    public boolean isIsAsotiation() {
        return isAsotiation;
    }

    public void setIsAsotiation(boolean isAsotiation) {
        this.isAsotiation = isAsotiation;
    }
}