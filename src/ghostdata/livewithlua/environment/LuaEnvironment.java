package ghostdata.livewithlua.environment;

import ghostdata.livewithlua.LiveScriptingWithLuaV2;
import ghostdata.livewithlua.environment.script.LiveScript;
import ghostdata.livewithlua.environment.wrappers.GUIWrapper;
import ghostdata.livewithlua.environment.wrappers.ScriptWrapper;
import org.dreambot.api.methods.MethodProvider;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    path = path.replace("{dreambot}", "org.dreambot.api");

                    return CoerceJavaToLua.coerce(Class.forName(path));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return LuaValue.NIL;
            }
        });

        globals.set("require_local", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                try {
                    String path = arg.checkjstring();

                    if (path.startsWith("/")) path = path.substring(1);
                    if (!path.endsWith(".lua")) path += ".lua"; // I think += on string is funny

                    LuaValue loaded = globals.get("__livescripts__").checktable().get(path);

                    if (loaded == null) {
                        Stream<String> lineSet = Files.lines(Path.of(new File(System.getProperty("user.dir"), "LuaScripts").toURI()));
                        String lines = lineSet.collect(Collectors.joining("\n"));

                        loaded = globals.load(lines, path.replace("/", ".")).call();
                        globals.get("__livescripts__").checktable().set(path.replace("/", "."), loaded);
                    }

                    return loaded == null ? LuaValue.NIL : loaded;
                } catch (Exception e) {
                    LiveScriptingWithLuaV2.instance().luaScriptEditor.recentErrorTextField.setText(e.getMessage());
                    MethodProvider.logError(e);
                }

                return LuaValue.NIL;
            }
        });

        globals.set("newInstance", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                String path = args.checkjstring(1);
                LuaValue vargs = args.optvalue(2, LuaValue.NIL);

                path = path.replace("{dreambot}", "org.dreambot.api");
                Class clazz = null;

                try {
                    clazz = Class.forName(path);
                } catch (Exception e) {
                    LiveScriptingWithLuaV2.instance().luaScriptEditor.recentErrorTextField.setText(e.getMessage());
                    MethodProvider.logError(e);
                    return LuaValue.NIL;
                }

                LuaString luaPath = LuaValue.valueOf(path);
                LuaValue instanceMethod = globals.get("luajava").get("newInstance");

                switch (vargs.type()) {
                    case TNIL -> {
                        return instanceMethod.invoke(luaPath).checkvalue(1);
                    }
                    case TTABLE -> {
                        LuaTable table = vargs.checktable();
                        LuaValue[] vargsArray = new LuaValue[table.length() + 1];
                        vargsArray[0] = luaPath;

                        for (int i = 1; i < vargsArray.length; i++) {
                            vargsArray[i] = table.get(i);
                        }

                        return instanceMethod.invoke(vargsArray).checkvalue(1);
                    }
                    default -> {
                        String errorMessage = "newInstance has wrong values, requires table not " + vargs.typename();
                        LiveScriptingWithLuaV2.instance().luaScriptEditor.recentErrorTextField.setText(errorMessage);
                        MethodProvider.logError(errorMessage);
                    }
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
