package org.asourcious.plusbot.commands.maintenance;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.asourcious.plusbot.BootLoader;
import org.asourcious.plusbot.Constants;
import org.asourcious.plusbot.PlusBot;
import org.asourcious.plusbot.commands.NoArgumentCommand;
import org.asourcious.plusbot.commands.PermissionLevel;

public class Restart extends NoArgumentCommand {

    public Restart(PlusBot plusBot) {
        super(plusBot);
        this.help = "Restarts " + Constants.NAME + ". Only available to the bot owner.";
        this.permissionLevel = PermissionLevel.OWNER;
    }

    @Override
    public void execute(String stripped, Message message, User author, TextChannel channel, Guild guild) {
        channel.sendMessage("Restarting...").queue();
        BootLoader.restart();
    }
}
