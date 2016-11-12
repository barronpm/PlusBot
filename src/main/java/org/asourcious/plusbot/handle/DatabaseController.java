package org.asourcious.plusbot.handle;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.ISnowflake;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.asourcious.plusbot.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class DatabaseController {

    public static final SimpleLog LOG = SimpleLog.getLog("Database");

    private ISnowflake entity;

    private TableHandler blacklist;
    private TableHandler disabledCommands;
    private TableHandler prefixes;

    public DatabaseController(Connection connection, ISnowflake entity) throws SQLException {
        this.entity = entity;

        this.blacklist = new TableHandler(connection, Constants.BLACKLIST);
        this.disabledCommands = new TableHandler(connection,
                entity instanceof Guild
                        ? Constants.GUILD_DISABLED_COMMANDS
                        : Constants.CHANNEL_DISABLED_COMMANDS);
        this.prefixes = new TableHandler(connection, Constants.PREFIXES);
    }

    public Set<String> loadBlacklist() {
        return blacklist.loadTable(entity.getId());
    }

    public void addUserToBlacklist(String userID) {
        blacklist.addEntry(entity.getId(), userID);
    }

    public void removeUserFromBlacklist(String userID) {
        blacklist.removeEntry(entity.getId(), userID);
    }

    public void clearBlacklist() {
        blacklist.clearEntries(entity.getId());
    }

    public Set<String> loadGuildDisabledCommands() {
        return disabledCommands.loadTable(entity.getId());
    }

    public void addDisabledCommand(String command) {
        disabledCommands.addEntry(entity.getId(), command);
    }

    public void removeDisabledCommand(String command) {
        disabledCommands.removeEntry(entity.getId(), command);
    }

    public void clearDisabledCommands() {
        disabledCommands.clearEntries(entity.getId());
    }

    public Set<String> loadPrefixes() {
        return prefixes.loadTable(entity.getId());
    }

    public void addPrefix(String prefix) {
        prefixes.addEntry(entity.getId(), prefix);
    }

    public void removePrefix(String prefix) {
        prefixes.removeEntry(entity.getId(), prefix);
    }

    public void clearPrefixes() {
        prefixes.clearEntries(entity.getId());
    }
}