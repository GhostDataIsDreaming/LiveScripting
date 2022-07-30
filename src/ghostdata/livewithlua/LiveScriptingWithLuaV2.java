package ghostdata.livewithlua;

import ghostdata.livewithlua.environment.script.LiveScript;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@ScriptManifest(
        name = "Live Scripting",
        description = "Write and edit scripts live while your bot is running.",
        author = "GhostData",
        version = 2.4,
        category = Category.MISC
)
public class LiveScriptingWithLuaV2 extends AbstractScript {

    private static LiveScriptingWithLuaV2 instance;

    public LoadLuaScriptGUI loadLuaScriptGUI;
    public LuaScriptEditor luaScriptEditor;
    public SettingsGUI settingsGUI;

    public LiveScript currentScript = new LiveScript();

    public JFrame _loadFrame;
    public JFrame _editorFrame;
    public JFrame _settingsFrame;

    public boolean ready = false;

    public static LiveScriptingWithLuaV2 instance() {
        return instance;
    }

    @Override
    public void onStart() {
        LiveScriptingWithLuaV2.instance = this;

        SwingUtilities.invokeLater(() -> {
            this.loadLuaScriptGUI = new LoadLuaScriptGUI();
            this.luaScriptEditor = new LuaScriptEditor();
            this.settingsGUI = new SettingsGUI();

            _loadFrame = new JFrame("LiveScript File Loader");
            _editorFrame = new JFrame("LiveScript Editor");
            _settingsFrame = new JFrame("LiveScript Settings");

            _loadFrame.setContentPane(loadLuaScriptGUI.rootPanel);
            _editorFrame.setContentPane(luaScriptEditor.rootPanel);
            _settingsFrame.setContentPane(settingsGUI.rootPanel);

            _loadFrame.pack();
            _editorFrame.pack();
            _settingsFrame.pack();

            _editorFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            _settingsFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            _loadFrame.setVisible(true);

            _loadFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ScriptManager.getScriptManager().stop();
                }
            });
            _editorFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(_editorFrame,
                        "Stop Script & Choose Another?\nMake sure to save your script.",
                        "Editor Closing Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    _loadFrame.setVisible(true);
                    _editorFrame.setVisible(false);
                    _settingsFrame.setVisible(false);
                    instance().ready = false;
                } else {
                    SwingUtilities.invokeLater(() ->{
                        _editorFrame.setVisible(true);
                    });
                }
                }
            });
            _settingsFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                _settingsFrame.setVisible(false);
                }
            });
        });
    }

    @Override
    public int onLoop() {
        if (!ready) return 1000;

        // Poll Settings
        int nextInterval = Integer.valueOf(settingsGUI.executingIntervalTextField.getText());
//        boolean showRecentError = settingsGUI.showRecentErrorInRadioButton.isSelected();
        boolean showFullError = settingsGUI.printErrorInConsoleRadioButton.isSelected();
//        boolean highlightErrorLine = settingsGUI.highlightErrorLineRadioButton.isSelected();
        // End Poll

        //Apply Settings
//        luaScriptEditor.recentErrorPanel.setVisible(showRecentError);

        Throwable error = null;
        try {
            if (currentScript.killed) return 1000; // Never Run Script again

            // Check for Changes - If any load them and wait till next onLoop
            if (currentScript.edited) {
                currentScript.setContent(luaScriptEditor.luaEditorTextPane.getText(), true, false);
                currentScript.started = false;
                currentScript.setEdited(false);

                return 1000;
            }

            // Run Script
            if (!currentScript.started) {
                currentScript.onStart();
            }

            Object value = currentScript.onLoop();

            if (value != null) {
                nextInterval = Integer.valueOf(value.toString());
            }
        } catch (Exception e) {
            error = e;
            MethodProvider.logError(e);
        }
        //End Script

        // Display Any Errors
        if (error != null) {
            if (showFullError) {
//                error.printStackTrace();
                MethodProvider.logError(error);
            }

            luaScriptEditor.recentErrorTextField.setText(error.getMessage());

//            if (highlightErrorLine) {
//                //Attempt to parse simple message to find the error line
//                String message = error.getMessage();
//                String lineNumberStr = message.split(":")[1];
//
//                try {
//                    int lineNumber = Integer.valueOf(lineNumberStr);
//                    MethodProvider.log("Detected Line number: " + lineNumberStr);
//                    LuaScriptEditor.highlightLine(luaScriptEditor.luaEditorTextPane, lineNumber, Color.RED);
//                } catch (Exception e) {
//                    MethodProvider.logError(e);
//                }
//            }
        }

        return nextInterval;
    }

    @Override
    public void onExit() {
        _editorFrame.setVisible(false);
        _loadFrame.setVisible(false);
        _settingsFrame.setVisible(false);

        if (!ready || currentScript == null) return;
        currentScript.onExit();
    }

    @Override
    public void onPause() {
        if (!ready || currentScript == null) return;
        currentScript.onPause();
    }

    @Override
    public void onResume() {
        if (!ready || currentScript == null) return;
        currentScript.onResume();
    }

    @Override
    public void onPaint(Graphics graphics) {
        if (!ready || currentScript == null) return;
        currentScript.onPaint(graphics);
    }
}
