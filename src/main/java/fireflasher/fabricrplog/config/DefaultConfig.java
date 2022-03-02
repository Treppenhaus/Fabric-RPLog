package fireflasher.fabricrplog.config;

import fireflasher.fabricrplog.client.FabricrplogClient;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DefaultConfig {

    private File ConfigFile;
    private List<String> Keywords = new ArrayList<>();
    private final String ModsDir = FabricrplogClient.getModsFolder();

    private static final Logger LOGGER = LogManager.getLogger("FabricRPLog Config");


    public void reloadConfig() {
        if (this.ConfigFile == null || !this.ConfigFile.exists()) {
            this.ConfigFile = new File(ModsDir, "config.yml");
            try {
                ConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Scanner sc = new Scanner(ConfigFile);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Keywords.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (Keywords.isEmpty()) {
            Keywords.add("[Flüstern]");
            Keywords.add("[Leise]");
            Keywords.add("[Reden]");
            Keywords.add("[Rufen]");
            Keywords.add("[PRufen]");
            Keywords.add("[Schreien]");
        }
    }

    public List<String> getList() {
        if (Keywords.isEmpty()) {
            reloadConfig();
        }
        return this.Keywords;
    }

    public void saveConfig() {
        if (this.ConfigFile == null) {
            return;
        }
        try {
            for (String write : this.Keywords) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(ConfigFile, true));
                BufferedReader br = new BufferedReader(new FileReader(ConfigFile));

                br.read();
                if (br.lines().toList().isEmpty()) bw.append(write);
                else bw.append("\n" + write);
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void setup() {
        new File(ModsDir).mkdir();
        this.ConfigFile = new File(ModsDir + "config.yml");
        if (ConfigFile.exists()) {
            LOGGER.info("[RPLog] Config erfolgreich geladen");
            reloadConfig();
        } else {
            reloadConfig();
            saveConfig();
        }
    }

    protected void openConfigFile() {Util.getOperatingSystem().open(ConfigFile);}

    private void Userchanges(List<String> keywordstemp){
        int sizetemp = keywordstemp.size();
        int size = Keywords.size();
        if(size != sizetemp){
            Keywords.clear();
            Keywords = keywordstemp;
        }
        for (String keywords: keywordstemp){
            keywords.equalsIgnoreCase(Keywords.get(1));
        }
    }
}
