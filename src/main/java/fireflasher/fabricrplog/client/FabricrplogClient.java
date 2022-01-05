package fireflasher.fabricrplog.client;

import fireflasher.fabricrplog.ChatLogger;
import fireflasher.fabricrplog.DefaultConfig;
import fireflasher.fabricrplog.listener.ChatAccess;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FabricrplogClient implements ClientModInitializer {

    public static DefaultConfig CONFIG = new DefaultConfig();
    public static ChatLogger CHATLOGGER;

    @Override
    public void onInitializeClient() {


        CONFIG.setup();
        CHATLOGGER = new ChatLogger();
        CHATLOGGER.setup();

        chatregister();

    }

    private void chatregister(){
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {


            ((ChatAccess)client.inGameHud).registerChatListener(
                    MessageType.CHAT,
                    (type, message, sender) -> {
                        ChatLogger.chatFilter(message.getString());
                    });
            /*
            ((ChatAccess)client.inGameHud).registerChatListener(
                    MessageType.SYSTEM,
                    (type, message, sender) -> {
                        ChatLogger.chatFilter(message.getString());
                    });

            ((ChatAccess)client.inGameHud).registerChatListener(
                    MessageType.GAME_INFO,
                    (type, message, sender) -> {
                        ChatLogger.chatFilter(message.getString());
                    });

             */
        });
    }

    public static String getFolder(){ return FabricLoader.getInstance().getGameDir().toString();}
    public static String getModsFolder(){ return FabricLoader.getInstance().getConfigDir().toString() + "/RPLog/";}



}
