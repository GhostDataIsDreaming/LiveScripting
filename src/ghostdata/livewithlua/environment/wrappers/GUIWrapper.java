package ghostdata.livewithlua.environment.wrappers;

import ghostdata.livewithlua.environment.LuaEnvironment;
import ghostdata.livewithlua.environment.script.LiveScript;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GUIWrapper extends LuaTable {

    private LiveScript script;

    public GUIWrapper(LiveScript script) {
        this.script = script;

        set("newFrame", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JFrame());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    return CoerceJavaToLua.coerce(new JFrame(arg.checkjstring()));
                }

                return LuaValue.NIL;
            }
        });

        set("newPanel", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JPanel());
            }
        });

        set("newButton", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JButton());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    return CoerceJavaToLua.coerce(new JButton(arg.checkjstring()));
                }

                return LuaValue.NIL;
            }
        });


        set("newScrollPane", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JScrollPane());
            }
        });

        set("newRadioButton", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JRadioButton());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    return CoerceJavaToLua.coerce(new JRadioButton(arg.checkjstring()));
                }

                return LuaValue.NIL;
            }
        });
        set("newCheckBox", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JCheckBox());
            }
        });
        set("newLabel", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JLabel());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    return CoerceJavaToLua.coerce(new JLabel(arg.checkjstring()));
                }

                return LuaValue.NIL;
            }
        });
        set("newTextField", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JTextField());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    return CoerceJavaToLua.coerce(new JTextField(arg.checkjstring()));
                }

                return LuaValue.NIL;
            }
        });
        set("newTextArea", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JTextArea());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    return CoerceJavaToLua.coerce(new JTextArea(arg.checkjstring()));
                } else if (arg.istable()) {
                    LuaTable table = arg.checktable();
                    String lines = new String();

                    for (LuaValue val : table.keys()) {
                        lines += LuaEnvironment.getObjectFromLuavalue(val) + "\n";
                    }

                    return CoerceJavaToLua.coerce(new JTextArea(lines));
                }

                return LuaValue.NIL;
            }
        });
        set("newTextPane", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JTextPane());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    JTextPane pane = new JTextPane();
                    pane.setText(arg.checkjstring());
                    return CoerceJavaToLua.coerce(pane);
                } else if (arg.istable()) {
                    LuaTable table = arg.checktable();
                    String lines = new String();

                    for (LuaValue val : table.keys()) {
                        lines += LuaEnvironment.getObjectFromLuavalue(val) + "\n";
                    }

                    JTextPane pane = new JTextPane();
                    pane.setText(lines);
                    return CoerceJavaToLua.coerce(pane);
                }

                return LuaValue.NIL;
            }
        });

        set("newList", new VarArgFunction() {
            @Override
            public LuaValue call() {
                return CoerceJavaToLua.coerce(new JList());
            }

            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isstring()) {
                    JList list = new JList();
                    list.setListData(new Object[] { arg.checkjstring() });
                    return CoerceJavaToLua.coerce(list);
                } else if (arg.istable()) {
                    LuaTable table = arg.checktable();
                    List objList = new ArrayList<>();

                    for (LuaValue val : table.keys()) {
                        objList.add(LuaEnvironment.getObjectFromLuavalue(val));
                    }

                    JList list = new JList();
                    list.setListData(objList.toArray(new Object[objList.size()]));
                    return CoerceJavaToLua.coerce(list);
                }

                return LuaValue.NIL;
            }
        });

//        set("newPasswordField");
//        set("newFormattedTextField");
//        set("newEditorPane");
//        set("newComboBox");
//        set("newTable");
//        set("newTabbedPane");
//        set("newSplitPane");
//        set("newSpinner");
//        set("newSlider");
//        set("newSeparator");
//        set("newProgressBar");
//        set("newToolBar");
//        set("newToolBarSeparator");
//        set("newScrollBar");
    }
}
