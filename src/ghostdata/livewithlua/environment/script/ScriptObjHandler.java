package ghostdata.livewithlua.environment.script;

import ghostdata.livewithlua.environment.LuaEnvironment;
import org.dreambot.api.methods.MethodProvider;
import org.luaj.vm2.Globals;
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
    public boolean killed = false;

    abstract void load();

    abstract void reload(boolean load);

    public void setOnStart(LuaFunction onStart) {
        this.onStart = onStart;
    }

    public void setOnLoop(LuaFunction onLoop) {
        this.onLoop = onLoop;
    }

    public void setOnExit(LuaFunction onExit) {
        this.onExit = onExit;
    }

    public void setOnPause(LuaFunction onPause) {
        this.onPause = onPause;
    }

    public void setOnResume(LuaFunction onResume) {
        this.onResume = onResume;
    }

    public void setOnPaint(LuaFunction onPaint) {
        this.onPaint = onPaint;
    }

    public void onStart() {
        if (onStart != null) {
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
