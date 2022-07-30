package ghostdata.livewithlua.environment.script;

import ghostdata.livewithlua.LiveScriptingWithLuaV2;
import ghostdata.livewithlua.environment.LuaEnvironment;
import org.dreambot.api.methods.MethodProvider;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class LiveScript extends ScriptObjHandler {

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

    public boolean edited = false;
    public boolean started = false;

    public File file;
    public String lines;

    public LiveScript() {
        this.file = new File(System.getProperty("user.dir"), "LuaScripts/sample.lua");
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

    public void setContent(String content, boolean load, boolean save) {
        this.lines = content;

        if (save) {
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
                MethodProvider.logError(e);
            }
        }

        reload(load);
    }

    @Override
    public void reload(boolean load) {
        this.globals = LuaEnvironment.getNewGlobals(this);
        this.script = globals.load(getLinesAsString(), file.getName());

        if (load) {
            load();
        }
    }

    public void load() {
        script.call();
    }

    public void save() throws IOException {
        if (file == null || file.getName().equals("sample.lua")) {
            JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
            int returnValue = chooser.showSaveDialog(LiveScriptingWithLuaV2.instance().loadLuaScriptGUI.rootPanel);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
        }

        if (!file.exists()) {
            file.mkdirs();
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
        Files.lines(chosen.toPath()).forEach((line) -> content.append(line + "\n"));
        luaScriptFile.setContent(content.toString(), true, false);

        return luaScriptFile;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
