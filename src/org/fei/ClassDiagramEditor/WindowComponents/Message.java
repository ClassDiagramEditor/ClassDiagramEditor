package org.fei.ClassDiagramEditor.WindowComponents;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 * Zobrazi vyskakovacie "popup" okno. Dobre iba pri ladeni.
 * Ale nechal som vypisovat aj niektore vynimky takymto sposobom. No netreba prehanat.
 * Uzivatela to otravuje.
 * 
 * @author Tomas
 */
public class Message {

    public static class InputParams {
    
        public Object option;
        public String answer;
    }
    
    /*
     * @param msg sprava ktora sa ma zobrazit
     */
    public static void showMessage(String msg) {

        NotifyDescriptor message = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(message);
    }
    
    public static InputParams showInputLine(String text, String title) {
    
        Message.InputParams params = new Message.InputParams();
        NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(text, title, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);
        params.option = DialogDisplayer.getDefault().notify(input);
        params.answer = input.getInputText();

        return params;
    }
}