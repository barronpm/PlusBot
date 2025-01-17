package org.asourcious.plusbot.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.math3.util.Pair;
import org.asourcious.plusbot.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class DataSource<T> {

    public static final Logger LOG = LoggerFactory.getLogger("Database");

    protected HikariDataSource connectionPool;
    private ExecutorService executorService;

    protected String table;
    protected Map<String, Set<T>> cache;

    protected String add;
    protected String remove;
    protected String clear;

    public DataSource(HikariDataSource connectionPool, ExecutorService executorService, String table) throws SQLException {
        this.connectionPool = connectionPool;
        this.executorService = executorService;
        this.table = table;
    }

    public void load() {
        cache = new ConcurrentHashMap<>();

        try (Connection connection = connectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table);

            while (resultSet.next()) {
                Pair<String, T> entry = deserializeRow(toArray(resultSet));

                if (!cache.containsKey(entry.getKey()))
                    cache.put(entry.getKey(), ConcurrentHashMap.newKeySet());

                cache.get(entry.getKey()).add(entry.getValue());
            }
        } catch (SQLException ex) {
            LOG.error("An exception occurred", ex);
            System.exit(Constants.DATABASE_ERROR);
        }
    }

    public boolean has(String container, T entry) {
        return cache.containsKey(container) && cache.get(container).contains(entry);
    }

    public Set<T> get(String container) {
        if (!cache.containsKey(container))
            return Collections.emptySet();

        return Collections.unmodifiableSet(cache.get(container));
    }

    public void add(String container, T entry) {
        if (!cache.containsKey(container))
            cache.put(container, ConcurrentHashMap.newKeySet());

        cache.get(container).add(entry);
        executeStatement(add, serializeRow(container, entry));
    }

    public void remove(String container, T entry) {
        cache.get(container).remove(entry);
        executeStatement(remove, serializeRow(container, entry));
    }

    public void clear(String container) {
        cache.get(container).clear();
        executeStatement(clear, container);
    }

    protected abstract Pair<String, T> deserializeRow(String[] columns);
    protected abstract String[] serializeRow(String container, T entry);

    private void executeStatement(String query, String... args) {
        executorService.execute(() -> {
            try (Connection connection = connectionPool.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);

                for (int i = 0; i < args.length; i++) {
                    statement.setString(i + 1, args[i]);
                }
                statement.execute();
            } catch (SQLException ex) {
                LOG.error("An exception occurred", ex);
            }
        });
    }

    private String[] toArray(ResultSet results) throws SQLException {
        String[] result = new String[results.getMetaData().getColumnCount()];

        for (int i = 0; i < result.length; i++) {
            result[i] = results.getString(i + 1);
        }

        return result;
    }
}
