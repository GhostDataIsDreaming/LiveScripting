package ghostdata.livewithlua;

import ghostdata.livewithlua.environment.script.LiveScript;
import org.dreambot.api.methods.MethodProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LoadLuaScriptGUI {
    public JPanel rootPanel;
    private JButton loadScriptFromFileButton;
    private JButton newEmptyScriptButton;
    private JButton loadSelectedSampleButton;
    public JList<String> scriptSamples;

    public LoadLuaScriptGUI() {
        try {
            List<String> samplesFound = new ArrayList<>();
            CodeSource source = LiveScriptingWithLuaV2.class.getProtectionDomain().getCodeSource();
            if (source != null) {
                URL jar = source.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry entry = null;

                while ((entry = zip.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (!entry.isDirectory() && name.startsWith("samples/")) {
                        samplesFound.add(entry.getName().substring("samples/".length()));
                    }
                }
            } else {
                loadSelectedSampleButton.setEnabled(false);
                scriptSamples.setListData(new String[] { "No Samples Found" });
            }

            scriptSamples.setListData(samplesFound.toArray(new String[samplesFound.size()]));
        } catch (Exception e) {
            loadSelectedSampleButton.setEnabled(false);
            scriptSamples.setListData(new String[] { "No Samples Found" });
        }

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

                LiveScriptingWithLuaV2.instance().currentScript = new LiveScript();
                LiveScriptingWithLuaV2.instance().currentScript.load();
                LiveScriptingWithLuaV2.instance().currentScript.setEdited(true);
                LiveScriptingWithLuaV2.instance().luaScriptEditor.luaEditorTextPane.setText(LiveScriptingWithLuaV2.instance().currentScript.getLinesAsString());
                LiveScriptingWithLuaV2.instance().ready = true;
            }
        });
        loadSelectedSampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String selected = scriptSamples.getSelectedValue();

                try {
                    String lines = "";
                    InputStream stream = LiveScriptingWithLuaV2.class.getClassLoader().getResourceAsStream("samples/" + selected);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                        while (reader.ready()) {
                            String line = reader.readLine();
                            lines += line + "\n";
                        }
                    }

                    LiveScriptingWithLuaV2.instance().currentScript = new LiveScript();
                    LiveScriptingWithLuaV2.instance().currentScript.setContent(lines, false, false);
                    LiveScriptingWithLuaV2.instance().currentScript.setEdited(true);
                    LiveScriptingWithLuaV2.instance().currentScript.started = false;
                    LiveScriptingWithLuaV2.instance().luaScriptEditor.luaEditorTextPane.setText(lines);

                    LiveScriptingWithLuaV2.instance()._editorFrame.setVisible(true);
                    LiveScriptingWithLuaV2.instance()._loadFrame.setVisible(false);

                    LiveScriptingWithLuaV2.instance().ready = true;
                } catch (Exception e) {
                    MethodProvider.logError(e);
                }
            }
        });
    }
}
