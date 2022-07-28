package ghostdata.livewithlua.v2.environment.script;

import ghostdata.livewithlua.v2.environment.LuaEnvironment;
import org.dreambot.api.methods.MethodProvider;
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

    public boolean started = false;

    abstract void load();

    abstract void reload(boolean load);

    public void onStart() {
        MethodProvider.log("Live:onStart");
        if (onStart != null) {
            onStart.call();
            started = true;
        }
    }


    public Object onLoop() {
        MethodProvider.log("Live:onLoop");
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

    @Override
    public String toString() {
        return "ScriptObjHandler{" +
                "started=" + started +
                ", onStart=" + (onStart != null) +
                ", onLoop=" + (onLoop != null) +
                ", onExit=" + (onExit != null) +
                ", onPause=" + (onPause != null) +
                ", onResume=" + (onResume != null) +
                ", onPaint=" + (onPaint != null) +
                '}';
    }
}
