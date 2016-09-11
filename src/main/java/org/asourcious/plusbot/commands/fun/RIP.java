package org.asourcious.plusbot.commands.fun;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.asourcious.plusbot.PlusBot;
import org.asourcious.plusbot.commands.Argument;
import org.asourcious.plusbot.commands.Command;
import org.asourcious.plusbot.commands.CommandDescription;
import org.asourcious.plusbot.commands.PermissionLevel;
import org.asourcious.plusbot.utils.CommandUtils;
import org.asourcious.plusbot.utils.FormatUtils;
import org.asourcious.plusbot.utils.MiscUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RIP extends Command {

    private CommandDescription description = new CommandDescription(
            "RIP",
            "Generates an image of a tombstone with the specified text or user on it.",
            "rip me, rip @person",
            new Argument[] { new Argument("Text", false) },
            PermissionLevel.EVERYONE
    );

    @Override
    public String checkArgs(String[] args) {
        if (args.length > 1)
            return "The RIP command takes up to 1 argument";

        return null;
    }

    @Override
    public void execute(PlusBot plusBot, String[] args, MessageReceivedEvent event) {
        if (args.length == 0 && !event.getMessage().getMentionedUsers().stream().anyMatch(user -> !user.equals(event.getJDA().getSelfInfo()))) {
            event.getChannel().sendMessageAsync(FormatUtils.error("If no arguments are supplied, you must mention a user!"), null);
            return;
        }

        List<User> mentionedUsers = new ArrayList<>(event.getMessage().getMentionedUsers());
        if (CommandUtils.getPrefixForMessage(plusBot, event.getMessage()).equals(event.getJDA().getSelfInfo().getAsMention()))
            mentionedUsers.remove(event.getJDA().getSelfInfo());

        if (mentionedUsers.size() > 1) {
            event.getChannel().sendMessageAsync("You can only mention one user!", null);
            return;
        }

        String text = args.length == 0
                ? event.getGuild().getEffectiveNameForUser(mentionedUsers.get(0))
                : args[0];

        try {
            BufferedImage image = ImageIO.read(new File("media/tombstone.png"));

            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(scaleFont(text, new Rectangle(70, 460, 450, 100), g2d));
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, 70, 470);

            File file = new File("media/tempGrave.png");
            for (int i = 0; !file.createNewFile(); i++) {
                file = new File("media/tempGrave" + i + ".png");
                if (i > 1000)
                    PlusBot.LOG.warn("More than 1000 tmp grave images!");
            }

            try {
                BufferedImage avatar = null;
                if (!mentionedUsers.isEmpty()) {
                    InputStream stream = MiscUtils.getDataStream(event.getJDA(), mentionedUsers.get(0).getAvatarUrl());
                    if(stream != null) {
                        avatar = ImageIO.read(stream);
                    }

                    g2d.drawImage(avatar, 225, 510, 125, 125, Color.white, null);
                }
            } catch (IOException ex) {
                PlusBot.LOG.warn(mentionedUsers.get(0).getAvatarUrl());
                PlusBot.LOG.log(ex);
            }

            ImageIO.write(image, "png", file);

            final File toDelete = file;
            event.getChannel().sendFileAsync(file, null, msg -> toDelete.delete());
        } catch (IOException e) {
            event.getChannel().sendMessageAsync(FormatUtils.error("Error generating tombstone!"), null);
        }
    }

    @Override
    public CommandDescription getDescription() {
        return description;
    }

    private Font scaleFont(String text, Rectangle rect, Graphics g) {
        float fontSize = 10.0f;

        Font font = g.getFont().deriveFont(fontSize);
        int width = g.getFontMetrics(font).stringWidth(text);
        fontSize = (rect.width / width ) * fontSize;
        font = g.getFont().deriveFont(fontSize);
        if (font.getSize() > 160)
            font = font.deriveFont(160.0f);
        return font;
    }
}
