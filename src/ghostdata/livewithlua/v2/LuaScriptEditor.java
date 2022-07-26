package ghostdata.livewithlua.v2;

import ghostdata.livewithlua.v2.environment.script.LiveScript;
import org.dreambot.api.methods.MethodProvider;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

public class LuaScriptEditor {
    public JPanel recentErrorPanel;
    public JTextField recentErrorTextField;
    private JButton openSettingsButton;
    private JScrollPane luaEditorScrollPane;
    protected JTextPane luaEditorTextPane;
    protected JPanel rootPanel;
    private JPanel topPanel;
    private JButton changeFileButton;
    private JButton saveButton;

    private static HashMap<JTextComponent, DefaultHighlighter.DefaultHighlightPainter> HIGHLIGHTERS = new HashMap<>();

    public LuaScriptEditor() {
        luaEditorTextPane.setText(LiveScriptingWithLuaV2.instance().currentScript.getLinesAsString());

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    LiveScriptingWithLuaV2.instance().currentScript.save();
                } catch (Exception e) {
                    MethodProvider.logError(e);
                    throw new RuntimeException(e);
                }
            }
        });
        changeFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    LiveScript scriptFile = LiveScript.loadFileWithFileChooser();
                    LiveScriptingWithLuaV2.instance().currentScript = scriptFile;
                    LiveScriptingWithLuaV2.instance().luaScriptEditor.luaEditorTextPane.setText(LiveScriptingWithLuaV2.instance().currentScript.getLinesAsString());
                } catch (Exception e) {
                    new RuntimeException(e);
                }
            }
        });
        openSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
//                LiveScriptingWithLuaV2.instance()._editorFrame.setVisible(false);
                LiveScriptingWithLuaV2.instance()._settingsFrame.setVisible(true);
            }
        });
//        luaEditorTextPane.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//                LiveScriptingWithLuaV2.instance().currentScript.edited = true;
//                super.keyTyped(e);
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                LiveScriptingWithLuaV2.instance().currentScript.edited = true;
//                super.keyPressed(e);
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                LiveScriptingWithLuaV2.instance().currentScript.edited = true;
//                super.keyReleased(e);
//            }
//        });
        luaEditorTextPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                LiveScriptingWithLuaV2.instance().currentScript.edited = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                LiveScriptingWithLuaV2.instance().currentScript.edited = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
    }

    public static void highlightLine(JTextComponent textComponent, int lineNumber, Color color) throws BadLocationException {
        int startIndex = textComponent.getDocument().getDefaultRootElement().getElement(lineNumber).getStartOffset();
        int endIndex = textComponent.getDocument().getDefaultRootElement().getElement(lineNumber).getEndOffset();

        DefaultHighlighter.DefaultHighlightPainter highlighter = HIGHLIGHTERS.getOrDefault(textComponent, new DefaultHighlighter.DefaultHighlightPainter(color));

        if (textComponent.getHighlighter().getHighlights().length == 0) {
            textComponent.getHighlighter().addHighlight(startIndex, endIndex, highlighter);
        } else {
            textComponent.getHighlighter().changeHighlight(highlighter, startIndex, endIndex);
        }

        HIGHLIGHTERS.put(textComponent, highlighter);
    }
}
