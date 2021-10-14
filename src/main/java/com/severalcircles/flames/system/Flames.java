
package com.severalcircles.flames.system;

import com.bugsnag.Bugsnag;
import com.severalcircles.flames.command.DebugCommand;
import com.severalcircles.flames.command.FlamesCommand;
import com.severalcircles.flames.command.HelpCommand;
import com.severalcircles.flames.command.TestCommand;
import com.severalcircles.flames.command.connections.ArtistCommand;
import com.severalcircles.flames.command.data.GlobalDataCommand;
import com.severalcircles.flames.command.data.HiCommand;
import com.severalcircles.flames.command.data.MyDataCommand;
import com.severalcircles.flames.command.data.TodayCommand;
import com.severalcircles.flames.data.base.FlamesDataManager;
import com.severalcircles.flames.events.ButtonEvent;
import com.severalcircles.flames.events.CommandEvent;
import com.severalcircles.flames.events.MessageEvent;
import com.severalcircles.flames.features.external.spotify.ReconnectRunnable;
import com.severalcircles.flames.features.external.spotify.SpotifyConnection;
import com.severalcircles.flames.features.today.ResetTodayRunnable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for Flames. Sets up everything you could ever hope for.
 */
public class Flames {
    public static final Map<String, FlamesCommand> commandMap = new HashMap<>();
    public static JDA api;
    /**
     * Global Spotify Connection referenced throughout Flames
     */
    public static SpotifyConnection spotifyConnection;
    /**
     * Bugsnag instance used to report bugs
     */
    public static Bugsnag bugsnag;
    private static int fatalErrorCounter;
    static {
        try {
            spotifyConnection = new SpotifyConnection();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Failed to connect to Spotify.");
        }
    }

    /**
     * Bootloader function
     * @param args any arguments passed to Flames via the command line
     */
    public static void main(String[] args) {
        // --- Initial Preparations ---
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);
        if(now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);
        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds();
        bugsnag = new Bugsnag("4db7c7d93598a437149f27b877cc6a93");
        FlamesDataManager.prepare();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new ReconnectRunnable(), 1, 1, TimeUnit.HOURS);
//        scheduler.scheduleAtFixedRate(new FlushHistoricalData(), 1, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(new ResetTodayRunnable(), initalDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        // --- Connecting to the API and Logging in to Discord ---
        try {
            Logger.getGlobal().log(Level.INFO, "Token is " + System.getenv("FlamesToken"));
            api = JDABuilder.createDefault(System.getenv("FlamesToken")).build();
            api.awaitReady();
        } catch (LoginException e) {
            Logger.getGlobal().log(Level.SEVERE, "Yeah that's not funny");
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // --- Commands ---
        commandMap.put("based", new TestCommand());
        commandMap.put("mydata", new MyDataCommand());
        commandMap.put("globaldata", new GlobalDataCommand());
        commandMap.put("artist", new ArtistCommand());
        commandMap.put("hi", new HiCommand());
        commandMap.put("help", new HelpCommand());
        commandMap.put("debug", new DebugCommand());
        commandMap.put("today", new TodayCommand());
        //noinspection ResultOfMethodCallIgnored
        api.updateCommands();
        // --- Events ---
        new CommandEvent().register(api);
        new MessageEvent().register(api);
        new ButtonEvent().register(api);
    }

    /**
     * Increases the counter of the number of times a fatal error has occurred. If this number gets too high, Flames will exit.
     */
    public static void incrementErrorCount() {
        fatalErrorCounter++;
        if (fatalErrorCounter > 10) {
            Logger.getGlobal().log(Level.SEVERE, "Flames has detected a recurring fatal problem. To protect Flames' data, it will now exit. There may be stack traces above with more information.");
            bugsnag.notify(new FlamesProtectException("Fatal error counter went over 5"));
            System.exit(2);
        }
    }
}