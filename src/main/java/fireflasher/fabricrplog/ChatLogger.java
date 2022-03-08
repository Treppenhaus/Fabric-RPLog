package fireflasher.fabricrplog;

import fireflasher.fabricrplog.client.FabricrplogClient;
import fireflasher.fabricrplog.config.DefaultConfig;
import fireflasher.fabricrplog.config.json.ServerConfig;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Logger;
import org.checkerframework.common.value.qual.MatchesRegex;

import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fireflasher.fabricrplog.client.FabricrplogClient.CONFIG;

public class ChatLogger {

    public static Logger LOGGER = Fabricrplog.LOGGER;
    private static String serverIP = "";
    private static String serverName = "";
    public static final DateTimeFormatter DATE  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME  = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static File log;
    private static List<String> channellist = new ArrayList<>();
    private static String timedmessage = "";
    private static boolean error;


    public static void servercheck(){
        String address = MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress().toString();
        String ip = address.split("/")[1];
        ip = ip.split(":")[0];



        ServerConfig serverConfig = CONFIG.getServerObject(ip);

        if( serverConfig != null){
            channellist = serverConfig.getServerDetails().getServerKeywords();
            if(!address.split("/")[0].contains(serverName) || serverName.equals("")) {
                serverName = getServerNameShortener(serverConfig.getServerDetails().getServerNames());
            }
        }
        else channellist = CONFIG.getKeywords();
        serverIP = ip;
    }


    public static void chatFilter(String chat){

        // TODO: Debug
        /*
        for(String debug: channellist){
            LOGGER.info(debug + " chatFilter");
        }
         */
        if( MinecraftClient.getInstance().getNetworkHandler() != null && !MinecraftClient.getInstance().getNetworkHandler().getConnection().isLocal()) servercheck();
        else channellist = DefaultConfig.defaultKeywords;

        for(String Channel: channellist){
            if(!chat.contains(Channel)){
                continue;
            }
            addMessage(chat);
        }



    }

    public void setup() {

        for(ServerConfig serverList: CONFIG.getList()){

            String server_name = getServerNameShortener(serverList.getServerDetails().getServerNames());
            String Path = FabricrplogClient.getFolder() + "/RPLogs/" + server_name;
            log = new File(Path ,LocalDateTime.now().format(DATE) + ".txt");
            File[] files = new File(Path).listFiles();
            if(files == null){}
            else {
                for (File textfile : files) {
                    if (textfile.toString().endsWith(".txt") && textfile.compareTo(log) != 0 ) {
                        try {
                            String filename  = textfile.toString().replaceFirst(".txt", ".zip");

                            FileOutputStream fos = new FileOutputStream(filename);
                            ZipOutputStream zipOut = new ZipOutputStream(fos);

                            File fileToZip = new File(textfile.toString());
                            FileInputStream fis = new FileInputStream(fileToZip);

                            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                            zipOut.putNextEntry(zipEntry);

                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                            zipOut.close();
                            fis.close();
                            fos.close();

                            if(new File(filename).exists()) fileToZip.delete();
                        }
                        catch (IOException e){
                            LOGGER.warn("RPLOG Datei konnte nicht verpackt werden");
                        }
                    }
                }
            }
        }
    }

    private static void addMessage(String chat){
        String Path = FabricrplogClient.getFolder() + "/RPLogs" + "/" + serverName;
        if(!log.toString().contains(LocalDateTime.now().format(DATE)) || !log.getPath().equalsIgnoreCase(Path)) {
            LocalDateTime today = LocalDateTime.now();
            String date = today.format(DATE);
            String Filename = date + ".txt";
            log = new File(Path, Filename);
            if(error)log = new File(FabricrplogClient.getFolder() + "/RPLogs", date + "-error.txt");
            if (!log.exists()) {
                try {
                    File path = new File(Path);
                    path.mkdir();
                    log.createNewFile();
                } catch (IOException e) {
                    LOGGER.warn("RPLOG Datei " + log.toString() + " konnte nicht erstellt werden");
                    error = true;
                }
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(log, true));
            BufferedReader br = new BufferedReader(new FileReader(log));
            LocalDateTime date = LocalDateTime.now();

            String time = "[" + date.format(TIME) + "] ";
            String message = time + chat;

            String collect = br.lines().collect(Collectors.joining(""));
            if(collect.isEmpty()) bw.append(message);
            else if (!timedmessage.equalsIgnoreCase(chat))bw.append("\n" + message);
            bw.close();

            timedmessage = chat;

        } catch (IOException e) {
            LOGGER.warn("RPLog konnte nicht in " + log.toString() + " schreiben");
        }
    }

    public static String getServerNameShortener(List<String> namelist){
        int[] lenght = new int[2];
        lenght[0] = namelist.get(0).length();
        if(namelist.size() != 1){
            for(String name:namelist){
                if(lenght[0] > name.length()){
                    lenght[0] = name.length();
                    lenght[1] = namelist.indexOf(name);
                }
            }
        }
        String name = namelist.get(lenght[1]);
        Pattern pattern = Pattern.compile("\\.");
        Matcher match = pattern.matcher(name);
        int count = 0;
        while( match.find()){
            count++;
        }
        if(count > 1) name = name.split("\\.",2)[1];
        name = name.split("\\.")[0];
        return name;
    }
}