package org.asourcious.plusbot.commands.config;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.asourcious.plusbot.PlusBot;
import org.asourcious.plusbot.commands.Command;
import org.asourcious.plusbot.commands.CommandContainer;
import org.asourcious.plusbot.commands.NoArgumentCommand;
import org.asourcious.plusbot.commands.PermissionLevel;
import org.asourcious.plusbot.config.source.Blacklists;
import org.asourcious.plusbot.util.DiscordUtils;

import java.util.List;

public class Blacklist extends CommandContainer {

    public Blacklist(PlusBot plusBot) {
        super(plusBot);
        this.help = "Adds and removes users from the server's blacklist.";
        this.children = new Command[] {
                new Add(plusBot),
                new Remove(plusBot),
                new Clear(plusBot)
        };
        this.permissionLevel = PermissionLevel.SERVER_MODERATOR;
    }

    private class Add extends Command {
        Add(PlusBot plusBot) {
            super(plusBot);
        }

        @Override
        public String isValid(Message message, String stripped) {
            if (DiscordUtils.getTrimmedMentions(message).isEmpty())
                return "You must mention at least one user!";
            return null;
        }

        @Override
        public void execute(String stripped, Message message, User author, TextChannel channel, Guild guild) {
            Blacklists blacklist = settings.getBlacklists();
            List<User> targets = DiscordUtils.getTrimmedMentions(message);
            int numUpdated = 0;

            for (User user : targets) {
                if (!blacklist.has(guild.getId(), user.getId())) {
                    if (PermissionLevel.canInteract(guild.getMember(author), guild.getMember(user))) {
                        blacklist.add(guild.getId(), user.getId());
                        numUpdated++;
                    } else {
                        channel.sendMessage("You don't have the necessary permissions to add **" + user.getName() + "** to the blacklist").queue();
                    }
                }
            }
            channel.sendMessage("Successfully added **" + numUpdated + "** users to the blacklist.").queue();
        }
    }

    private class Remove extends Command {
        Remove(PlusBot plusBot) {
            super(plusBot);
        }

        @Override
        public String isValid(Message message, String stripped) {
            if (DiscordUtils.getTrimmedMentions(message).isEmpty())
                return "You must mention at least one user!";
            return null;
        }

        @Override
        public void execute(String stripped, Message message, User author, TextChannel channel, Guild guild) {
            Blacklists blacklist = settings.getBlacklists();
            List<User> targets = DiscordUtils.getTrimmedMentions(message);
            int numUpdated = 0;

            for (User user : targets) {
                if (blacklist.has(guild.getId(), user.getId())) {
                    blacklist.remove(guild.getId(), user.getId());
                    numUpdated++;
                }
            }
            channel.sendMessage("Successfully removed **" + numUpdated + "** users from the blacklist.").queue();
        }
    }

    private class Clear extends NoArgumentCommand {
        Clear(PlusBot plusBot) {
            super(plusBot);
        }

        @Override
        public void execute(String stripped, Message message, User author, TextChannel channel, Guild guild) {
            settings.getBlacklists().clear(guild.getId());
            channel.sendMessage("Successfully cleared blacklist.").queue();
        }
    }
}
