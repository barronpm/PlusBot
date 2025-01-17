package org.asourcious.plusbot.commands.maintenance;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.asourcious.plusbot.PlusBot;
import org.asourcious.plusbot.commands.NoArgumentCommand;
import org.asourcious.plusbot.commands.PermissionLevel;
import org.asourcious.plusbot.util.DiscordUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Clean extends NoArgumentCommand {

    public Clean(PlusBot plusBot) {
        super(plusBot);
        this.help = "Removes all commands and responses in the last 100 messages.";
        this.requiredPermissions = new Permission[] { Permission.MESSAGE_MANAGE };
        this.permissionLevel = PermissionLevel.SERVER_MODERATOR;
    }

    @Override
    public void execute(String stripped, Message message, User author, TextChannel channel, Guild guild) {
        try {
            List<Message> messages = channel.getHistory().retrievePast(100).block()
                    .parallelStream()
                    .filter(msg -> msg.getAuthor().equals(msg.getJDA().getSelfUser()) || DiscordUtils.isCommand(plusBot, msg))
                    .collect(Collectors.toList());
            channel.deleteMessages(messages).queue();
            channel.sendMessage("Deleted **" + messages.size() + "** messages").queue();
        } catch (RateLimitedException ignored) {}
    }
}
