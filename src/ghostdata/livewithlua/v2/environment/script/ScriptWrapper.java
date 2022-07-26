package ghostdata.livewithlua.v2.environment.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
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
    }
}
