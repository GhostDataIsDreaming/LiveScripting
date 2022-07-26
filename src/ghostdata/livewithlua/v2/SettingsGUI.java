package ghostdata.livewithlua.v2;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SettingsGUI {
    private JButton seeBindingsButton;
    private JButton addDefaultBindingButton;
    public JTextField executingIntervalTextField;
    public JRadioButton showRecentErrorInRadioButton;
    public JRadioButton printErrorInConsoleRadioButton;
    private JList plannedAdditionsList;
    public  JRadioButton highlightErrorLineRadioButton;
    public JPanel rootPanel;

    public SettingsGUI() {
        executingIntervalTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                // https://stackhowto.com/how-to-make-jtextfield-accept-only-numbers/
                char c = event.getKeyChar();
                if ( ((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                    event.consume();  // if it's not a number, ignore the event
                }

                super.keyTyped(event);
            }
        });
    }
}