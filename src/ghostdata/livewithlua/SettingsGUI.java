package ghostdata.livewithlua;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SettingsGUI {
    private JButton seeBindingsButton;
    private JButton addDefaultBindingButton;
    public JTextField executingIntervalTextField;
    public JRadioButton showRecentErrorInRadioButton;
    public JRadioButton printErrorInConsoleRadioButton;
    public  JRadioButton highlightErrorLineRadioButton;
    public JPanel rootPanel;

    public SettingsGUI() {
        showRecentErrorInRadioButton.setSelected(true);
        highlightErrorLineRadioButton.setSelected(true);
        printErrorInConsoleRadioButton.setSelected(true);

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
