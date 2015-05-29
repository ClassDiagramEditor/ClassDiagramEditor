package org.fei.ClassDiagramEditor;

import org.fei.ClassDiagramEditor.Data.PackageData;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.fei.ClassDiagramEditor.DirecotoryWatch.DirectoryWatch;
import org.fei.ClassDiagramEditor.Editor.Refactoring.RefactoringEvents;
import org.fei.ClassDiagramEditor.WindowComponents.Diagram;
import org.fei.ClassDiagramEditor.WindowComponents.Message;
import org.fei.ClassDiagramEditor.WindowComponents.MessageJPanel;
import org.fei.ClassDiagramEditor.ViewData.*;
import org.fei.ClassDiagramEditor.Element.Class.Class;
import org.fei.ClassDiagramEditor.Editor.WindowComponents.EditPanel;
import org.fei.ClassDiagramEditor.JavaParser.JavaReaderAction;
import org.fei.ClassDiagramEditor.XMI.XmiBuilder;
import org.fei.ClassDiagramEditor.XMI.XmiTypePlaceHolder;
import org.fei.ClassDiagramEditor.drawing.Lines.AsotiationArrow;
import org.fei.ClassDiagramEditor.drawing.Lines.Line;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;

/**
 * Tato trieda dedi od TopComponent, cize pridava novu zalozku do hlavneho okna.
 *
 * @author Tomas
 */
@TopComponent.Description(preferredID = "ClassDiagramView",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
public class ClassDiagramView extends TopComponent {

    private ArrayList<Data> dataList;
    private JTextArea text;
    private Diagram classDiagram;   // diagram tried
    private ArrayList<Class> drawing;
    private JTextField txtFontSize;
    private JButton btnFontSizeLeft;
    private JButton btnFontSizeRight;
    private JCheckBox showAtributes;
    private JCheckBox showAsociations;
    private JCheckBox showAsociationNames;
    private JCheckBox showConstructors;
    private JCheckBox coloredDiagram;
    private JButton btnExport;
    private JButton btnExportXmi;
    private JPanel ClassDiagramPanel;
    private JSplitPane splitPane;   // rozdelenie okna na 2 casti: prava diagram - lava editacia
    private EditPanel editor;       // editacia tried
    // koli filechooserovi
    private final ClassDiagramView parentClass = this;
    // pre zatvorenie posledneho okna
    public static ClassDiagramView lastOpened = null;
    
    /* Modifikacia zdrojovych suborov */
    private DirectoryWatch RP; 

    public ClassDiagramView(JPanel panel, String projectName,DirectoryWatch RP, boolean firstTime) {
         
        if (ClassDiagramView.lastOpened != null){
            ClassDiagramView.lastOpened.close();
            
        }

        this.setLayout(new BorderLayout());
        ClassDiagramPanel = new JPanel();
        ClassDiagramPanel.setLayout(new BorderLayout());

        this.RP = RP;
        
        if (panel instanceof Diagram) { // ked sa zobrazuje diagram
            this.classDiagram = (Diagram) panel;
            this.editor = new EditPanel();
            classDiagram.addClassSelectEventListener(editor);
            classDiagram.addClassSelectEventListener(editor.getClassEditPanel());
            classDiagram.addPackageSelectEventListener(editor);
            classDiagram.addPackageSelectEventListener(editor.getPackageEditPanel());

            this.initComponents();
            this.add(ClassDiagramPanel, BorderLayout.CENTER);
        } else {  // ked sa zobrazuje sprava pre uzivatela
            MessageJPanel message = (MessageJPanel) panel;
            this.add(message, BorderLayout.CENTER);
        }

        this.setName(projectName + " UML diagram");
        this.setVisible(true);
        Image img = Toolkit.getDefaultToolkit().getImage("org/fei/ClassDiagram/ball.png");
        this.setIcon(img);
        this.toFront();
        this.open();
        this.requestActive();
        ClassDiagramView.lastOpened = this;
        //this.removeNotify();
        
    }

    // komponenty class diagramu
    private void initComponents() {
        // rezia pre horny panel
        JPanel frontPanel = new JPanel(new BorderLayout());
        JPanel fontSizePanel = new JPanel(new FlowLayout());
        fontSizePanel.add(new Label("Font size:"));
        this.txtFontSize = new JTextField(2);
        this.txtFontSize.setText(String.valueOf(Class.getFontSize()));
        this.txtFontSize.setEditable(false);
        fontSizePanel.add(txtFontSize);
        this.btnFontSizeLeft = new JButton("-");
        this.btnFontSizeLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Class.getFontSize() > 8) {
                    Class.setFontSize(Class.getFontSize() - 1);
                    Line.setFontSize(Class.getFontSize());
                    txtFontSize.setText(String.valueOf(Class.getFontSize()));
                    classDiagram.repaint();
                }
            }
        });
        fontSizePanel.add(this.btnFontSizeLeft);
        this.btnFontSizeRight = new JButton("+");
        this.btnFontSizeRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Class.getFontSize() < 18) {
                    Class.setFontSize(Class.getFontSize() + 1);
                    Line.setFontSize(Class.getFontSize());
                    txtFontSize.setText(String.valueOf(Class.getFontSize()));
                    classDiagram.repaint();
                }
            }
        });
        fontSizePanel.add(this.btnFontSizeRight);

        frontPanel.add(fontSizePanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout());

        this.btnExportXmi = new JButton("Export XMI");
        this.btnExportXmi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                
                ArrayList<PackageData> packages = new ArrayList<PackageData>();

                for (org.fei.ClassDiagramEditor.Element.Package.Package p : classDiagram.getPackages()) {
                    packages.add(p.getData());
               
                }

                try {
                    FileFilter xmiFilter = new FileNameExtensionFilter("XMI (*.xmi)", "xmi");
                    JFileChooser fc = new JFileChooser();
                    File hint = new File(ProjectScannerFactory.getProjectScanner().getProjectName());
                    
                    fc.addChoosableFileFilter(xmiFilter);
                    fc.setAcceptAllFileFilterUsed(false);
                    fc.setFileFilter(xmiFilter);
                    fc.setSelectedFile(hint);

                    int returnVal = fc.showSaveDialog(parentClass);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        
                        java.io.File selectedFile = fc.getSelectedFile();

                        String path = selectedFile.getAbsolutePath();

                        if (!path.endsWith(".xmi")) {
                            if (fc.getFileFilter().equals(xmiFilter)) {
                                path += ".xmi";
                            }
                        }

                        java.io.File newFile = new java.io.File(path);

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();

                        XmiBuilder builder = new XmiBuilder(packages, classDiagram.getLines());
                        DOMSource source = new DOMSource(builder.getXmiDocument());
                        StreamResult result = new StreamResult(newFile);

                        transformer.transform(source, result);
                    }


                } catch (TransformerConfigurationException ex) {
                    Message.showMessage(ex.getMessage());
                } catch (ParserConfigurationException ex) {
                    Message.showMessage(ex.getMessage());
                } catch (TransformerException ex) {
                    Message.showMessage(ex.getMessage());
                }
            }
        });

        this.btnExport = new JButton("Export image");
        this.btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                FileFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png");
                FileFilter jpegFilter = new FileNameExtensionFilter("JPEG (*.jpg)", "jpg");
                FileFilter bmpFilter = new FileNameExtensionFilter("BMP (*.bmp)", "bmp");
                JFileChooser fc = new JFileChooser();
                File hint = new File(ProjectScannerFactory.getProjectScanner().getProjectName());

                fc.addChoosableFileFilter(pngFilter);
                fc.addChoosableFileFilter(jpegFilter);
                fc.addChoosableFileFilter(bmpFilter);
                fc.setAcceptAllFileFilterUsed(false);
                fc.setFileFilter(pngFilter);
                fc.setSelectedFile(hint);

                int returnVal = fc.showSaveDialog(parentClass);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File savedFile = fc.getSelectedFile();

                    String path = savedFile.getAbsolutePath();

                    if (!(path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".bmp"))) {
                        if (fc.getFileFilter().equals(pngFilter)) {
                            path += ".png";
                        } else if (fc.getFileFilter().equals(jpegFilter)) {
                            path += ".jpg";
                        } else if (fc.getFileFilter().equals(bmpFilter)) {
                            path += ".bmp";
                        }
                    }

                    java.io.File newFile = new java.io.File(path);

                    BufferedImage bi = new BufferedImage(classDiagram.getWidth(), classDiagram.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics g = bi.createGraphics();
                    classDiagram.fireClassUnselectEvent();
                    classDiagram.paint(g);  //this == JComponent
                    g.dispose();
                    try {
                        ImageIO.write(bi, "png", newFile);
                    } catch (Exception ex) {
                        Message.showMessage("ClassDiagramView.java: " + ex.toString());
                    }
                }
            }
        });
        rightPanel.add(btnExportXmi);
        rightPanel.add(btnExport);
        frontPanel.add(rightPanel, BorderLayout.EAST);

        this.showAsociations = new JCheckBox("Show/Hide asociations");
        this.showAsociations.setSelected(true);
        this.showAsociations.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (!showAsociations.isSelected()) {
                    showAtributes.setSelected(true);
                    showAtributes.setEnabled(false);
                    showAsociationNames.setEnabled(false);
                    showAsociationNames.setSelected(true);
                } else {
                    showAtributes.setEnabled(true);
                    showAtributes.setSelected(false);
                }
            }
        });
        this.showAsociations.addItemListener(classDiagram);

        this.showAtributes = new JCheckBox("Show attributes");
        this.showAtributes.setSelected(false);
        this.showAtributes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showAtributes.isSelected()) {
                    showAsociationNames.setEnabled(true);
                } else {
                    showAsociationNames.setSelected(true);
                    showAsociationNames.setEnabled(false);
                }

                Class.setShowAttributes(showAtributes.isSelected());
                classDiagram.repaint();
            }
        });

        this.showAsociationNames = new JCheckBox("Show asociation names");
        this.showAsociationNames.setSelected(true);
        this.showAsociationNames.setEnabled(false);
        this.showAsociationNames.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                AsotiationArrow.setShowAsociationName(showAsociationNames.isSelected());
                classDiagram.repaint();
            }
        });


        this.showConstructors = new JCheckBox("Show/Hide constructors");
        this.showConstructors.setSelected(false);
        this.showConstructors.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Class.setShowConstructors(showConstructors.isSelected());
                classDiagram.repaint();
            }
        });


        this.coloredDiagram = new JCheckBox("Colored diagram");
        this.coloredDiagram.setSelected(true);
        this.coloredDiagram.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Diagram.coloredDiagram = coloredDiagram.isSelected();
                classDiagram.repaint();
            }
        });

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.add(this.showAsociations);
        menuPanel.add(this.showAtributes);
        menuPanel.add(this.showAsociationNames);
        menuPanel.add(this.showConstructors);
        menuPanel.add(this.coloredDiagram);

        frontPanel.add(menuPanel, BorderLayout.CENTER);
        ClassDiagramPanel.add(frontPanel, BorderLayout.NORTH);

        // pridanie hlavnych okien : diagram a editor
        JScrollPane scroll1 = new JScrollPane(classDiagram);
        JScrollPane scroll2 = new JScrollPane(editor);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(1125);

        ClassDiagramPanel.add(splitPane, BorderLayout.CENTER);
    }

    @Override
    protected void componentClosed() {
        
        if(!JavaReaderAction.getRepaint()){
            
            RP.setCancel(true);
            synchronized (RP.getJavaDatas()) {
                JavaReaderAction.setRepaint(true);
                RP.getJavaDatas().notify();
            }
        }


    }

    public Diagram getClassDiagram() {
        return classDiagram;
    }

    public void setClassDiagram(Diagram classDiagram) {
        this.classDiagram = classDiagram;
    }
    public void spusti(){
        this.addNotify();
    }
    
    public void UnselectEditor(){
        editor.ClassUnselect();
    }

    public EditPanel getEditor() {
        return editor;
    }
    
    public void setViewAfterRefactor(JPanel panel){
        
        this.setLayout(new BorderLayout());
        ClassDiagramPanel = new JPanel();
        ClassDiagramPanel.setLayout(new BorderLayout());

        if (panel instanceof Diagram) { // ked sa zobrazuje diagram
            this.classDiagram = (Diagram) panel;
            this.editor = new EditPanel();
            classDiagram.addClassSelectEventListener(editor);
            classDiagram.addClassSelectEventListener(editor.getClassEditPanel());
            classDiagram.addPackageSelectEventListener(editor);
            classDiagram.addPackageSelectEventListener(editor.getPackageEditPanel());
            this.add(ClassDiagramPanel, BorderLayout.CENTER);
        } else {  // ked sa zobrazuje sprava pre uzivatela
            MessageJPanel message = (MessageJPanel) panel;
            this.add(message, BorderLayout.CENTER);
        }

        
    }

    
}