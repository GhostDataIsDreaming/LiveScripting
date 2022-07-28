package ghostdata.livewithlua.v2;

import ghostdata.livewithlua.v2.environment.script.LiveScript;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import javax.swing.*;
import java.awt.*;

@ScriptManifest(
        name = "Live Scripting w/ Lua",
        description = "Script Live directly in DreamBot w/ Lua",
        author = "GhostData",
        version = 2.1,
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

            _settingsFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            _loadFrame.setVisible(true);
        });
    }

    @Override
    public int onLoop() {
        if (!ready) return 1000;

        // Poll Settings
        int nextInterval = Integer.valueOf(settingsGUI.executingIntervalTextField.getText());
//        boolean showRecentError = settingsGUI.showRecentErrorInRadioButton.isSelected();
        boolean showFullError = settingsGUI.printErrorInConsoleRadioButton.isSelected();
        boolean highlightErrorLine = settingsGUI.highlightErrorLineRadioButton.isSelected();
        // End Poll

        //Apply Settings
//        luaScriptEditor.recentErrorPanel.setVisible(showRecentError);

        Throwable error = null;

        // Check for Changes - If any load them and wait till next onLoop
        if (currentScript.edited) {
            currentScript.setContent(luaScriptEditor.luaEditorTextPane.getText(), true, false);
//            currentScript.reloadScript(true);
            currentScript.started = false;
            currentScript.edited = false;

            return 1000;
        }

        // Run Script
        try {
            currentScript.onStart();
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

            if (highlightErrorLine) {
                //Attempt to parse simple message to find the error line
                String message = error.getMessage();
                String lineNumberStr = message.split(":")[1];
//                System.out.println("Detected Line number: " + lineNumberStr);
                int lineNumber = Integer.valueOf(lineNumberStr);

                try {
                    LuaScriptEditor.highlightLine(luaScriptEditor.luaEditorTextPane, lineNumber, Color.RED);
                } catch (Exception e) {
                    MethodProvider.logError(e);
                    throw new RuntimeException(e);
                }
            }
        }

        return nextInterval;
    }

    @Override
    public void onExit() {
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
