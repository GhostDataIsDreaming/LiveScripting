package ghostdata.livewithlua.environment.wrappers;

import ghostdata.livewithlua.environment.script.LiveScript;
import org.dreambot.api.script.ScriptManager;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ScriptWrapper extends LuaTable {
    private LiveScript script;

    public ScriptWrapper(LiveScript script) {
        this.script = script;

        set("kill", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                script.killed = true;

                return LuaValue.NIL;
            }
        });

        set("stop", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                ScriptManager.getScriptManager().stop();
                return LuaValue.NIL;
            }
        });

        set("onStart", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.setOnStart(arg.checkfunction());
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });

        set("onLoop", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.setOnLoop(arg.checkfunction());
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });

        set("onExit", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.setOnExit(arg.checkfunction());
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });

        set("onPause", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.setOnPause(arg.checkfunction());
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });

        set("onResume", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.setOnResume(arg.checkfunction());
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });

        set("onPaint", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.setOnPaint(arg.checkfunction());
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });
    }
}
