import javax.swing.SwingUtilities;

import ui.UI;

public class Driver {

    public static void main(String [ ] args){
        SwingUtilities.invokeLater(new UI());
    }
}
