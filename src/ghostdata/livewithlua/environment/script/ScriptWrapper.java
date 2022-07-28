package ghostdata.livewithlua.environment.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ScriptWrapper extends LuaTable {
    private LiveScript script;

    public ScriptWrapper(LiveScript script) {
        this.script = script;

        set("onStart", new VarArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (arg.isfunction()) {
                    script.onStart = arg.checkfunction();
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
                    script.onLoop = arg.checkfunction();
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
                    script.onExit = arg.checkfunction();
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
                    script.onExit = arg.checkfunction();
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
                    script.onResume = arg.checkfunction();
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
                    script.onPaint = arg.checkfunction();
                } else {
                    throw new RuntimeException("onStart does not contain function");
                }

                return LuaValue.NIL;
            }
        });
    }
}
