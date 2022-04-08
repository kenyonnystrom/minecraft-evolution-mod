//
// Use this package to send a message to the chat. If the message is already formatted in type Text, use sendText;
// otherwise use sendString. Typical use includes death messages and other notifications.
//

package evo.mod.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ChatExt {
    public static void sendText(Text text) {
        MinecraftClient.getInstance().inGameHud.getChatHud().queueMessage(text);
    }

    public static void sendString(String s) {
        Text text = (new LiteralText(s));
        MinecraftClient.getInstance().inGameHud.getChatHud().queueMessage(text);
    }
}
