package ghostdata.livewithlua.v1;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.script.ScriptManager;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class LiveScriptGUI {
    protected JButton loadFromFileButton;
    protected JButton saveToFileButton;
    protected JRadioButton pauseExecutingRadioButton;
    protected JTextField executeIntervalTextField;
    protected JTextArea luaCodeTextArea;
    protected JTextField recentErrorTextField;
    protected JPanel rootPanel;
    protected JRadioButton printErrorInConsoleRadioButton;

    public LiveScriptGUI() {
        pauseExecutingRadioButton.setSelected(true); //Start not running
        printErrorInConsoleRadioButton.setSelected(false);

        executeIntervalTextField.addKeyListener(new KeyAdapter() {
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

        loadFromFileButton.addActionListener(event -> {
            LiveScriptGUI.this.pauseExecutingRadioButton.setSelected(true);

            File chosen = null;
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            int returnValue = fileChooser.showOpenDialog(LiveScriptGUI.this.rootPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                chosen = fileChooser.getSelectedFile();
            }

            if (chosen == null) {
                return;
            }

            StringBuilder luaScript = new StringBuilder();
            try {
                Files.lines(chosen.toPath()).collect(Collectors.toList()).forEach((line) -> {
                    luaScript.append(line);
                    luaScript.append("\n");
                });
            } catch (Exception e) {
                MethodProvider.logError(e);
                luaCodeTextArea.setText("Error Loading from file. Check Console.");
                return;
            }

            luaCodeTextArea.setText(luaScript.toString());
        });

        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                LiveScriptGUI.this.pauseExecutingRadioButton.setSelected(true);

                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
                int returnValue = fileChooser.showSaveDialog(LiveScriptGUI.this.rootPanel);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    try {
                        Files.write(fileChooser.getSelectedFile().toPath(), LiveScriptGUI.this.luaCodeTextArea.getText().getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        MethodProvider.logError(e);
                        return;
                    }
                }
            }
        });
        pauseExecutingRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (LiveScriptGUI.this.pauseExecutingRadioButton.isSelected()) {
                    ScriptManager.getScriptManager().pause();
                } else {
                    ScriptManager.getScriptManager().resume();
                }
            }
        });
    }
}
