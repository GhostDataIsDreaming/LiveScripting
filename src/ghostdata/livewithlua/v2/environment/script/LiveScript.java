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
        "function onStart()\n",
        "   -- normal onStart functions\n",
        "   MethodProver:log(\"onStart\")\n",
        "end\n",
        "\n",
        "function onLoop()\n",
        "   -- normal scripting goes here\n",
        "   -- return an int here to override Executing Interval\n",
        "   MethodProver:log(\"onLoop\")\n",
        "end\n",
    };

    public Globals globals;
    public LuaValue main;
    public LuaFunction onStart;
    public LuaFunction onLoop;

    public boolean edited = false;
    public boolean started = false;

    public File file;
    public List<String> lines;

    public LiveScript() {
        this.file = new File(System.getProperty("user.dir") + "/LuaScripts/sample.lua");
        this.globals = LuaEnvironment.getNewGlobals(this);

        StringBuilder def = new StringBuilder();
        for (String line : DEFAULT_SCRIPT) {
            def.append(line);
        }

        setContent(def.toString(), false);
    }

    public File getFile() {
        return this.file;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public String getLinesAsString() {
        StringBuilder builder = new StringBuilder();

        lines.forEach((line) -> {
            builder.append(line);
//            builder.append("\n");
        });

        return builder.toString();
    }

    public void setContent(String content) {
        setContent(content, false);
    }

    public void setContent(String content, boolean save) {
        String[] newLines = content.split("\n");

        if (lines == null) {
            lines = new ArrayList();
        }

        lines.clear();
        Arrays.asList(newLines).forEach((line) -> lines.add(line));

        if (save) {
            try {
                save();
            } catch (Exception e) {
                e.printStackTrace();
                MethodProvider.logError(e);
            }
        }

        reloadScript();
    }

    public void reloadScript() {
        this.main = globals.load(getLinesAsString(), file.getName());
    }

    public LiveScript call() {
        main.call();
        return this;
    }

    public boolean onStart() {
        if (started) return false;
        if (onStart != null) onStart.call();
        return true;
    }

    public Object onLoop() {
        if (onLoop != null) {
            return LuaEnvironment.getObjectFromLuavalue(onLoop.call());
        }

        return null;
    }

    public void save() throws IOException {
        if (file == null) {
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
        luaScriptFile.setContent(content.toString(), false);
        luaScriptFile.call();

        return luaScriptFile;
    }
}
