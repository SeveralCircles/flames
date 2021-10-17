/*
 * Copyright (c) 2021.
 */

package com.severalcircles.flames.data.base;

import com.severalcircles.flames.data.guild.FlamesGuild;
import com.severalcircles.flames.data.guild.NewGuildException;
import com.severalcircles.flames.data.user.FlamesUser;
import com.severalcircles.flames.data.user.UserFunFacts;
import com.severalcircles.flames.data.user.UserStats;
import com.severalcircles.flames.features.rank.Rank;
import com.severalcircles.flames.system.Flames;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for managing Flames data, such as user and guild data.
 */
public class FlamesDataManager {
    /**
     * Data directory
     */
    public static final File flamesDirectory = new File(System.getProperty("user.dir") + "/Flames");
    static final File userDirectory = new File(flamesDirectory.getAbsolutePath() + "/user");
    static final File guildDirectory = new File(flamesDirectory.getAbsolutePath() + "/guild");
    //    static List<File> openFiles = new LinkedList<>();

    /**
     * Checks if data directories exists and creates them if not.
     */
    public static void prepare() {
        Logger.getGlobal().log(Level.INFO, "Preparing Data...");
        //noinspection ResultOfMethodCallIgnored
        flamesDirectory.mkdir();
        //noinspection ResultOfMethodCallIgnored
        userDirectory.mkdir();
        //noinspection ResultOfMethodCallIgnored
        guildDirectory.mkdir();
    }

    /**
     * Checks if a given user exists. If they do, simply returns false. If not, it'll create user data for that user, then return true.
     */
    public static boolean newUser(User user) throws IOException {
//        String name = Flames.api.getUserById(discordId).getName();
        String name = user.getName();
        String discordId = user.getId();
        File udir = new File(userDirectory.getAbsolutePath() + "/" + discordId);
        File userl = new File(udir.getAbsolutePath() + "/user.fl");
        File stats = new File(udir.getAbsolutePath() + "/stats.fl");
        File funfacts = new File(udir.getAbsolutePath() + "/funfacts.fl");
        // If any of the user data files don't exist, we're just going to assume that the data either doesn't exist or is corrupted and start from scratch because it shouldn't ever happen normally.
        if (udir.mkdir() | userl.createNewFile() | stats.createNewFile() | funfacts.createNewFile()) {
            FlamesUser flamesUser = new FlamesUser();
            Logger.getGlobal().log(Level.INFO, "User Data for " + discordId + " does not exist. Creating it now.");
            FileOutputStream os1 = new FileOutputStream(userl);
            FileOutputStream os2 = new FileOutputStream(stats);
            FileOutputStream os3 = new FileOutputStream(funfacts);
            flamesUser.setDiscordId(discordId);
            flamesUser.createData().store(os1, "User Data for " + name);
            flamesUser.getStats().createData().store(os2, "User Stats for " + name);
            flamesUser.getFunFacts().createData().store(os3, "Fun Facts for " + name);
//            Consent.getConsent(user);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Saves data for a FlamesUser to a file in the user directory
     * @throws IOException If flames can't write files for whatever reason.
     */
    public static void save(FlamesUser flamesUser) throws IOException {
        String discordId = flamesUser.getDiscordId();
        String name;
        try {name = Objects.requireNonNull(Flames.api.getUserById(discordId)).getName(); } catch (NullPointerException e) {name = "An Unknown Flames User";}
//        OutputStream outputStream;
        File udir = new File(userDirectory.getAbsolutePath() + "/" + discordId);
        File user = new File(udir.getAbsolutePath() + "/user.fl");
        File stats = new File(udir.getAbsolutePath() + "/stats.fl");
        File funfacts = new File(udir.getAbsolutePath() + "/funfacts.fl");
        // If any of the user data files don't exist, we're just going to assume that the data either doesn't exist or is corrupted and start from scratch because it shouldn't ever happen normally.
        if (udir.mkdir() | user.createNewFile() | stats.createNewFile() | funfacts.createNewFile()) {
            Logger.getGlobal().log(Level.INFO, "User Data for " + discordId + " does not exist. Creating it now.");
        }
        flamesUser.setDiscordId(discordId);
        FileOutputStream os1 = new FileOutputStream(user);
        FileOutputStream os2 = new FileOutputStream(stats);
        FileOutputStream os3 = new FileOutputStream(funfacts);
        flamesUser.createData().store(os1, "User Data for " + name);
        flamesUser.getStats().createData().store(os2, "User Stats for " + name);
        flamesUser.getFunFacts().createData().store(os3, "Fun Facts for " + name);

    }

    /**
     * Reads data for a user from the data directory
     * @param user Discord User to get data for
     * @return A FlamesUser created from the data file
     * @throws ConsentException If the user hasn't consented yet won't return data
     */
    public static FlamesUser readUser(User user) throws IOException, ConsentException {
        FlamesUser fluser = new FlamesUser();
        UserStats stats = new UserStats();
        UserFunFacts funFacts = new UserFunFacts();
        if (newUser(user)) {
            throw new ConsentException(0, user);
        }
        File udir = new File(userDirectory.getAbsolutePath() + "/" + user.getId());
        File userfl = new File(udir.getAbsolutePath() + "/user.fl");
        File stats2 = new File(udir.getAbsolutePath() + "/stats.fl");
        File funfacts = new File(udir.getAbsolutePath() + "/funfacts.fl");
        FileInputStream inputStream1 = new FileInputStream(userfl);
        FileInputStream inputStream2 = new FileInputStream(stats2);
        FileInputStream inputStream3 = new FileInputStream(funfacts);
        Properties data = new Properties();
        Properties statsdata = new Properties();
        Properties funfactsdata = new Properties();
        data.load(inputStream1);
        statsdata.load(inputStream2);
        funfactsdata.load(inputStream3);

        fluser.setScore(Integer.parseInt(data.get("score") + ""));
        fluser.setEmotion(Float.parseFloat(data.get("emotion") + ""));
        fluser.setDiscordId(user.getId());
        fluser.setDataVersion(Double.parseDouble(data.get("version") + ""));
        fluser.setConsent(Integer.parseInt(data.get("consent") + ""));
        fluser.setStreak(Integer.parseInt(data.get("streak") + ""));
        fluser.setLastSeen(Instant.parse(data.get("lastSeen") + ""));

        funFacts.setFrenchToastMentioned(Integer.parseInt(funfactsdata.get("frenchToastScore") + ""));
        funFacts.setBestRank(Rank.valueOf(funfactsdata.get("bestRank") + ""));
        funFacts.setLowestFlamesScore(Integer.parseInt(funfactsdata.get("lowScore") + ""));
        funFacts.setHighestFlamesScore(Integer.parseInt(funfactsdata.get("highScore") + ""));
        funFacts.setSadDay(Instant.parse(funfactsdata.get("sadDay") + ""));
        funFacts.setHappyDay(Instant.parse(funfactsdata.get("happyDay") + ""));
        funFacts.setHighestEmotion(Float.parseFloat(funfactsdata.get("highestEmotion")+ ""));
        funFacts.setLowestEmotion(Float.parseFloat(funfactsdata.get("lowestEmotion") + ""));

        stats = new UserStats(Integer.parseInt(statsdata.get("exp") + ""), Integer.parseInt(statsdata.get("level") + ""), Integer.parseInt(statsdata.get("POW") + ""), Integer.parseInt(statsdata.get("RES") + ""), Integer.parseInt(statsdata.get("LUCK") + ""), Integer.parseInt(statsdata.get("RISE") + ""), Integer.parseInt(statsdata.get("CAR") + ""));

        fluser.setStats(stats);
        fluser.setFunFacts(funFacts);
        if (fluser.getConsent() != 1) throw new ConsentException(fluser.getConsent(), user);
        return fluser;
    }

    /**
     * Reads data for a user from the data directory
     * @param user Discord User to get data for
     * @param skipConsent If true, won't throw an exception if the user hasn't consented. For most cases, leave this false or null.
     * @return A FlamesUser created from the data file
     * @throws ConsentException If the user hasn't consented yet won't return data
     */
    public static FlamesUser readUser(User user, boolean skipConsent) throws IOException, ConsentException {
        FlamesUser fluser = new FlamesUser();
        UserStats stats = new UserStats();
        UserFunFacts funFacts = new UserFunFacts();
        if (newUser(user)) {
            throw new ConsentException(0, user);
        }
        File udir = new File(userDirectory.getAbsolutePath() + "/" + user.getId());
        File userfl = new File(udir.getAbsolutePath() + "/user.fl");
        File stats2 = new File(udir.getAbsolutePath() + "/stats.fl");
        File funfacts = new File(udir.getAbsolutePath() + "/funfacts.fl");
        FileInputStream inputStream1 = new FileInputStream(userfl);
        FileInputStream inputStream2 = new FileInputStream(stats2);
        FileInputStream inputStream3 = new FileInputStream(funfacts);
        Properties data = new Properties();
        Properties statsdata = new Properties();
        Properties funfactsdata = new Properties();
        data.load(inputStream1);
        statsdata.load(inputStream2);
        funfactsdata.load(inputStream3);

        fluser.setScore(Integer.parseInt(data.get("score") + ""));
        fluser.setEmotion(Float.parseFloat(data.get("emotion") + ""));
        fluser.setDiscordId(user.getId());
        fluser.setDataVersion(Double.parseDouble(data.get("version") + ""));
        fluser.setConsent(Integer.parseInt(data.get("consent") + ""));
        fluser.setStreak(Integer.parseInt(data.get("streak") + ""));
        fluser.setLastSeen(Instant.parse(data.get("lastSeen") + ""));

        funFacts.setFrenchToastMentioned(Integer.parseInt(funfactsdata.get("frenchToastScore") + ""));
        funFacts.setBestRank(Rank.valueOf(funfactsdata.get("bestRank") + ""));
        funFacts.setLowestFlamesScore(Integer.parseInt(funfactsdata.get("lowScore") + ""));
        funFacts.setHighestFlamesScore(Integer.parseInt(funfactsdata.get("highScore") + ""));
        funFacts.setSadDay(Instant.parse(funfactsdata.get("sadDay") + ""));
        funFacts.setHappyDay(Instant.parse(funfactsdata.get("happyDay") + ""));
        funFacts.setHighestEmotion(Float.parseFloat(funfactsdata.get("highestEmotion")+ ""));
        funFacts.setLowestEmotion(Float.parseFloat(funfactsdata.get("lowestEmotion") + ""));

        stats = new UserStats(Integer.parseInt(statsdata.get("exp") + ""), Integer.parseInt(statsdata.get("level") + ""), Integer.parseInt(statsdata.get("POW") + ""), Integer.parseInt(statsdata.get("RES") + ""), Integer.parseInt(statsdata.get("LUCK") + ""), Integer.parseInt(statsdata.get("RISE") + ""), Integer.parseInt(statsdata.get("CAR") + ""));

        fluser.setStats(stats);
        fluser.setFunFacts(funFacts);
        if (fluser.getConsent() != 1 && !skipConsent) throw new ConsentException(fluser.getConsent(), user);
        return fluser;
    }

    /**
     * Functions the same as the other readUser function, except skips consent and accepts an ID string instead.
     */
    public static FlamesUser readUser(String id) throws IOException {
        FlamesUser fluser = new FlamesUser();
        UserStats stats = new UserStats();
        UserFunFacts funFacts = new UserFunFacts();
        File udir = new File(userDirectory.getAbsolutePath() + "/" + id);
        File userfl = new File(udir.getAbsolutePath() + "/user.fl");
        File stats2 = new File(udir.getAbsolutePath() + "/stats.fl");
        File funfacts = new File(udir.getAbsolutePath() + "/funfacts.fl");
        FileInputStream inputStream1 = new FileInputStream(userfl);
        FileInputStream inputStream2 = new FileInputStream(stats2);
        FileInputStream inputStream3 = new FileInputStream(funfacts);
        Properties data = new Properties();
        Properties statsdata = new Properties();
        Properties funfactsdata = new Properties();
        data.load(inputStream1);
        statsdata.load(inputStream2);
        funfactsdata.load(inputStream3);

        fluser.setScore(Integer.parseInt(data.get("score") + ""));
        fluser.setEmotion(Float.parseFloat(data.get("emotion") + ""));
        fluser.setDiscordId(id);
        fluser.setDataVersion(Double.parseDouble(data.get("version") + ""));
        fluser.setConsent(Integer.parseInt(data.get("consent") + ""));
        fluser.setStreak(Integer.parseInt(data.get("streak") + ""));
        fluser.setLastSeen(Instant.parse(data.get("lastSeen") + ""));

        funFacts.setFrenchToastMentioned(Integer.parseInt(funfactsdata.get("frenchToastScore") + ""));
        funFacts.setBestRank(Rank.valueOf(funfactsdata.get("bestRank") + ""));
        funFacts.setLowestFlamesScore(Integer.parseInt(funfactsdata.get("lowScore") + ""));
        funFacts.setHighestFlamesScore(Integer.parseInt(funfactsdata.get("highScore") + ""));
        funFacts.setSadDay(Instant.parse(funfactsdata.get("sadDay") + ""));
        funFacts.setHappyDay(Instant.parse(funfactsdata.get("happyDay") + ""));
        funFacts.setHighestEmotion(Float.parseFloat(funfactsdata.get("highestEmotion")+ ""));
        funFacts.setLowestEmotion(Float.parseFloat(funfactsdata.get("lowestEmotion") + ""));

        stats = new UserStats(Integer.parseInt(statsdata.get("exp") + ""), Integer.parseInt(statsdata.get("level") + ""), Integer.parseInt(statsdata.get("POW") + ""), Integer.parseInt(statsdata.get("RES") + ""), Integer.parseInt(statsdata.get("LUCK") + ""), Integer.parseInt(statsdata.get("RISE") + ""), Integer.parseInt(statsdata.get("CAR") + ""));

        fluser.setStats(stats);
        fluser.setFunFacts(funFacts);
        return fluser;
    }

    /**
     * Checks if a guild has been seen before. Behaves otherwise the same as newUser.
     */
    public static boolean newGuild(Guild guild) throws IOException {
        File guildDir = new File(guildDirectory.getAbsolutePath() + "/" + guild.getId());
        File mainFile = new File(guildDir.getAbsolutePath() + "/guild.fl");
        FlamesGuild flGuild;
        if (guildDir.mkdir()) {
            flGuild = new FlamesGuild(guild.getName(), guild.getId(), Instant.now(), 0, 0);
            OutputStream os = new FileOutputStream(mainFile);
            flGuild.createData().store(os, "Flames Guild Data");
            return true;
        } else return false;
    }

    /**
     * Saves a guild to its data file.
     */
    public static void save(FlamesGuild guild) throws IOException {
        File guildDir = new File(guildDirectory.getAbsolutePath() + "/" + guild.getId());
        File mainFile = new File(guildDir.getAbsolutePath() + "/guild.fl");
        OutputStream os = new FileOutputStream(mainFile);
        guild.createData().store(os, "Flames Guild Data");
    }
    public static FlamesGuild readGuild(String id) throws IOException, NewGuildException {
        if (newGuild(Flames.api.getGuildById(id))) throw new NewGuildException();
        File guildDir = new File(guildDirectory.getAbsolutePath() + "/" + id);
        File mainFile = new File(guildDir.getAbsolutePath() + "/guild.fl");
        Properties data = new Properties();
        InputStream is = new FileInputStream(mainFile);
        data.load(is);
        return new FlamesGuild(data.get("name") + "", data.get("discordId") + "", Instant.parse(data.get("joined") + ""), Integer.parseInt(data.get("score") + ""), Float.parseFloat(data.get("emotion") + ""));
    }
}
