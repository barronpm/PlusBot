package org.asourcious.plusbot.commands.fun;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.math3.util.Precision;
import org.asourcious.plusbot.PlusBot;
import org.asourcious.plusbot.commands.Command;
import org.asourcious.plusbot.util.ConversionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

public class Weather extends Command {

    public Weather(PlusBot plusBot) {
        super(plusBot);
        this.help = "Gives the weather for the supplied location.";
        this.rateLimit = new RateLimit(6, 60, TimeUnit.SECONDS);
    }

    @Override
    public String isValid(Message message, String stripped) {
        if (stripped.isEmpty())
            return "You must supply a location!";
        return null;
    }

    @Override
    public void execute(String stripped, Message message, User author, TextChannel channel, Guild guild) {
        JSONObject weatherData = plusBot.getWeatherHandler().getWeather(stripped);
        JSONObject main = weatherData.getJSONObject("main");
        JSONArray weather = weatherData.getJSONArray("weather");

        double degreesC = Precision.round(ConversionUtils.kelvinToCelcius(main.getDouble("temp")), 1);
        double degreesF = Precision.round(ConversionUtils.kelvinToFarenheit(main.getDouble("temp")), 1);
        double minC = ConversionUtils.kelvinToCelcius(main.getDouble("temp_min"));
        double minF = ConversionUtils.kelvinToFarenheit(main.getDouble("temp_min"));
        double maxC = ConversionUtils.kelvinToCelcius(main.getDouble("temp_max"));
        double maxF = ConversionUtils.kelvinToFarenheit(main.getDouble("temp_max"));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setThumbnail("https://openweathermap.org/img/w/" + weather.getJSONObject(0).getString("icon") + ".png");
        embedBuilder.setDescription("Weather for " + weatherData.getString("name"));

        embedBuilder.addField("Country", weatherData.getJSONObject("sys").getString("country"), true);
        embedBuilder.addField("Temperature", degreesC + "°C/ " + degreesF + "°F", true);
        embedBuilder.addField("Minimum", minC + "°C/ " + minF + "°F", true);
        embedBuilder.addField("Maximum", maxC + "°C/ " + maxF + "°F", true);


        channel.sendMessage(embedBuilder.build()).queue();
    }
}
