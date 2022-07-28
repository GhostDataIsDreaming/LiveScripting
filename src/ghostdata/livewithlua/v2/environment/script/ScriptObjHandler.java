package ghostdata.livewithlua.v2.environment.script;

import ghostdata.livewithlua.v2.environment.LuaEnvironment;
import org.luaj.vm2.Globals;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.awt.*;

abstract class ScriptObjHandler {

    Globals globals;
    LuaValue script;

    LuaFunction onStart;
    LuaFunction onLoop;
    LuaFunction onExit;
    LuaFunction onPause;
    LuaFunction onResume;
    LuaFunction onPaint;

    public boolean started;

    abstract void load();

    abstract void reload(boolean load);

    public void onStart() {
        if (onStart != null && !started) {
            onStart.call();
            started = true;
        }
    }


    public Object onLoop() {
        if (onLoop != null) {
            return LuaEnvironment.getObjectFromLuavalue(onLoop.call());
        } else {
            script.call();
        }

        return null;
    }

    public void onExit() {
        if (onExit != null) {
            onExit.call();
        }
    }

    public void onPause() {
        if (onPause != null) {
            onPause.call();
        }
    }

    public void onResume() {
        if (onResume != null) {
            onResume.call();
        }
    }

    public void onPaint(Graphics graphics) {
        if (onPaint != null) {
            onPaint.call(CoerceJavaToLua.coerce(graphics));
        }
    }
}
