package ghostdata.livewithlua.v2.environment.script;

import ghostdata.livewithlua.v2.LiveScriptingWithLuaV2;
import ghostdata.livewithlua.v2.environment.LuaEnvironment;
import org.dreambot.api.methods.MethodProvider;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LiveScript {

    protected static String[] DEFAULT_SCRIPT = {
        "local MethodProvider = luajava.bindClass(\"org.dreambot.api.methods.MethodProvider\")\n",
        "\n",
        "script.onStart(function()\n",
        "   -- normal onStart functions\n",
        "   MethodProvider:log(\"onStart\")\n",
        "end)\n",
        "\n",
        "script.onLoop(function()\n",
        "   -- normal scripting goes here\n",
        "   -- return an int here to override Executing Interval\n",
        "   MethodProvider:log(\"onLoop\")\n",
        "end)\n",
    };

    public Globals globals;
    public LuaValue main;
    public LuaFunction onStart;
    public LuaFunction onLoop;

    public boolean edited = false;
    public boolean started = false;

    public File file;
    public String lines;

    public LiveScript() {
        this.file = new File(System.getProperty("user.dir") + "/LuaScripts/sample.lua");
        this.globals = LuaEnvironment.getNewGlobals(this);

        StringBuilder def = new StringBuilder();
        for (String line : DEFAULT_SCRIPT) {
            def.append(line);
        }

        setContent(def.toString(), false, false);
    }

    public File getFile() {
        return this.file;
    }

    public String getLinesAsString() {
        return this.lines;
    }

    public void setContent(String content) {
        setContent(content, false, false);
    }

    public void setContent(String content, boolean call, boolean save) {
        this.lines = content;

        if (save) {
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
                MethodProvider.logError(e);
            }
        }

        reloadScript(call);
    }

    public void reloadScript(boolean call) {
        this.main = globals.load(getLinesAsString(), file.getName());

        if (call) {
            call();
        }
    }

    public LiveScript call() {
        main.call();

        MethodProvider.log("Found Script:" + file.getName() + ":onStart? " + (onStart != null));
        MethodProvider.log("Found Script:" + file.getName() + ":onLoopt? " + (onLoop != null));
        return this;
    }

    public void onStart() {
        if (onStart != null && !started) {
            onStart.call();
            started = true;
        }
    }

    public Object onLoop() {
        if (onLoop != null) {
            return LuaEnvironment.getObjectFromLuavalue(onLoop.call());
        }

        return null;
    }

    public void save() throws IOException {
        if (file == null || file.getName().equals("sample.lua")) {
            JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
            int returnValue = chooser.showSaveDialog(LiveScriptingWithLuaV2.instance().loadLuaScriptGUI.rootPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
        }

        file.mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }

        Files.write(file.toPath(), getLinesAsString().getBytes(StandardCharsets.UTF_8));
    }

    public static LiveScript loadFileWithFileChooser() throws IOException {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        int returnValue = chooser.showOpenDialog(LiveScriptingWithLuaV2.instance().loadLuaScriptGUI.rootPanel);

        File chosen = null;

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            chosen = chooser.getSelectedFile();
        }

        if (chosen == null) return null;

        LiveScript luaScriptFile = new LiveScript();
        luaScriptFile.file = chosen;
        StringBuilder content = new StringBuilder();
        Files.lines(chosen.toPath()).forEach(content::append);
        luaScriptFile.setContent(content.toString(), true, false);

        return luaScriptFile;
    }
}
