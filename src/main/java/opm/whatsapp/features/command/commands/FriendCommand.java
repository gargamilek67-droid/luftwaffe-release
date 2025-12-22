package opm.whatsapp.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.client.CPacketChatMessage;
import opm.whatsapp.WhatsApp;
import opm.whatsapp.features.command.Command;
import opm.whatsapp.api.manager.FriendManager;
import opm.whatsapp.features.modules.HUD.HUD;

public class FriendCommand
        extends Command {
    public FriendCommand() {
        super("friend", new String[]{"<add/del/name/clear>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            if (WhatsApp.friendManager.getFriends().isEmpty()) {
                FriendCommand.sendMessage("Friend list empty D:.");
            } else {
                String f = "Friends: ";
                for (FriendManager.Friend friend : WhatsApp.friendManager.getFriends()) {
                    try {
                        f = f + friend.getUsername() + ", ";
                    } catch (Exception exception) {
                    }
                }
                FriendCommand.sendMessage(f);
            }
            return;
        }
        if (commands.length == 2) {
            switch (commands[0]) {
                case "reset": {
                    WhatsApp.friendManager.onLoad();
                    FriendCommand.sendMessage("Friends got reset.");
                    return;
                }
            }
            FriendCommand.sendMessage(commands[0] + (WhatsApp.friendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
            return;
        }
        if (commands.length >= 2) {
            switch (commands[0]) {
                case "add": {
                    WhatsApp.friendManager.addFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.GREEN + commands[1] + " has been friended");
                    if (HUD.getInstance().friendMsg.getValue().booleanValue()) {
                        FriendCommand.mc.player.connection.sendPacket(new CPacketChatMessage("/w " + commands[1] + " I just have you added thanks to WhatsApp.cz!"));
                    }
                    return;
                }
                case "del": {
                    WhatsApp.friendManager.removeFriend(commands[1]);
                    FriendCommand.sendMessage(ChatFormatting.RED + commands[1] + " has been unfriended");
                    return;
                }
            }
            FriendCommand.sendMessage("Unknown Command, try friend add/del (name)");
        }
    }
}

