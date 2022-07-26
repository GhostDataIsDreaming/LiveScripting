package ghostdata.livewithlua.v1;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringReader;

//@ScriptManifest(
//        name = "Live Scripting w/ Lua",
//        description = "Script Live directly in DreamBot w/ Lua",
//        author = "GhostData",
//        version = 1.0,
//        category = Category.MISC
//)

public class LiveWithLuaScript extends AbstractScript {

    LiveScriptGUI liveScriptGUI;
    JFrame openFrame;

    public void onStart() {
        SwingUtilities.invokeLater(() -> {
            liveScriptGUI = new LiveScriptGUI();
            openFrame = new JFrame("Live Script w/ Lua");
            openFrame.setContentPane(liveScriptGUI.rootPanel);
            openFrame.pack();
            openFrame.setVisible(true);
            openFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            openFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (JOptionPane.showConfirmDialog(openFrame,
                            "Do you want to close this window? It will stop the script.",
                            "Close & Stop Script",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        ScriptManager.getScriptManager().getCurrentScript().stop();
                        openFrame.setVisible(false);
                    } else {
                        openFrame.setVisible(true);
                    }
                }
            });
        });
    }

    @Override
    public void onExit() {
        openFrame.setVisible(false);
        MethodProvider.log("Stopping Live Scripting w/ Lua.");
    }

    @Override
    public void onResume() {
        liveScriptGUI.pauseExecutingRadioButton.setSelected(false);
        MethodProvider.log("Resuming Live Scripting w/ Lua.");
    }

    @Override
    public int onLoop() {
        if (liveScriptGUI == null || openFrame == null) return 100;

        boolean printError = liveScriptGUI.printErrorInConsoleRadioButton.isSelected();
        boolean pause = liveScriptGUI.pauseExecutingRadioButton.isSelected();
        String luaScript = liveScriptGUI.luaCodeTextArea.getText();
        int interval = Integer.valueOf(liveScriptGUI.executeIntervalTextField.getText());

        if (!pause) {
            if (luaScript != null && !luaScript.isEmpty()) {
                Globals globals = JsePlatform.standardGlobals();
                LuaValue chunk = globals.load(new StringReader(liveScriptGUI.luaCodeTextArea.getText()), "main.lua");

                try {
                    chunk.call();
                } catch (Exception e) {
                    liveScriptGUI.recentErrorTextField.setText(e.getMessage());

                    if (printError) {
                        MethodProvider.logError(e);
                    }
                }
            } else {
                liveScriptGUI.recentErrorTextField.setText("Lua Script is empty or null. Not running.");
            }
        }

        return interval;
    }
}
