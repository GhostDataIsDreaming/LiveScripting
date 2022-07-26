package ghostdata.livewithlua.v2;

import ghostdata.livewithlua.v2.environment.script.LiveScript;
import org.dreambot.api.methods.MethodProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoadLuaScriptGUI {
    public JPanel rootPanel;
    private JButton loadScriptFromSampleButton;
    private JButton loadScriptFromFileButton;
    private JButton newEmptyScriptButton;

    public LoadLuaScriptGUI() {
        loadScriptFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LiveScriptingWithLuaV2.instance().currentScript = LiveScript.loadFileWithFileChooser();
                    LiveScriptingWithLuaV2.instance()._loadFrame.setVisible(false);

                    LiveScriptingWithLuaV2.instance().luaScriptEditor.luaEditorTextPane.setText(LiveScriptingWithLuaV2.instance().currentScript.getLinesAsString());
                    LiveScriptingWithLuaV2.instance()._editorFrame.setVisible(true);

                    LiveScriptingWithLuaV2.instance().ready = true;
                } catch (IOException ex) {
                    MethodProvider.logError(ex);
                    throw new RuntimeException(ex);
                }
            }
        });

        newEmptyScriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LiveScriptingWithLuaV2.instance()._editorFrame.setVisible(true);
                LiveScriptingWithLuaV2.instance()._loadFrame.setVisible(false);

                LiveScriptingWithLuaV2.instance().luaScriptEditor.luaEditorTextPane.setText(LiveScriptingWithLuaV2.instance().currentScript.getLinesAsString());
                LiveScriptingWithLuaV2.instance().ready = true;
            }
        });
    }
}
