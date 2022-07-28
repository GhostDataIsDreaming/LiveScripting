package ghostdata.livewithlua.environment;

import ghostdata.livewithlua.environment.script.LiveScript;
import ghostdata.livewithlua.environment.wrappers.GUIWrapper;
import ghostdata.livewithlua.environment.wrappers.ScriptWrapper;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LuaEnvironment {

    public static Globals getNewGlobals(LiveScript script) {
        Globals globals = JsePlatform.standardGlobals();

        globals.set("__livescripts__", new LuaTable());

        globals.set("script", new ScriptWrapper(script));
        globals.set("gui", new GUIWrapper(script));

        globals.set("import", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                try {
                    String path = arg.checkjstring();
                    System.out.println("import: " + path);

                    if (!path.startsWith("org.dreambot.api.")) {
                        path = "org.dreambot.api." + path;
                    }

                    return CoerceJavaToLua.coerce(Class.forName(path));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return LuaValue.NIL;
            }
        });

        return globals;
    }

    public static Object getObjectFromLuavalue(LuaValue value) {
        if (value.istable()) {
            return convertTable(value.checktable());
        } else if (value.isint()) {
            return value.checkint();
        } else if (value.islong()) {
            return value.checklong();
        } else if (value.isnumber()) {
            return value.checkdouble();
        } else if (value.isstring()) {
            return value.checkjstring();
        } else if (value.isboolean()) {
            return value.checkboolean();
        } else if (value.isnil()) {
            return null;
        } else {
            return value.checkuserdata();
        }
    }

    public static boolean isInteger(String str) {
        if (str == null)
            return false;
        int length = str.length();
        if (length == 0)
            return false;
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1)
                return false;
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }

    public static Object convertTable(LuaTable table) {
        HashMap<Object, Object> returnedMap = new HashMap<>();
        boolean isArray = true;
        LuaValue[] keys = table.keys();

        for (LuaValue k : keys) {
            if (!isInteger(k.tojstring()))
                isArray = false;
            returnedMap.put(k.tojstring(), getObjectFromLuavalue(table.get(k)));
        }

        if (isArray) {
            List<Object> list = new ArrayList<>();
            returnedMap.values().forEach(o -> list.add(o));
            return list;
        }
        return returnedMap;
    }
}
