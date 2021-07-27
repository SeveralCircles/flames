package com.severalcircles.flames.data.user;

import com.severalcircles.flames.data.base.FlamesDatabase;
import discord4j.common.util.Snowflake;

import java.sql.SQLException;
import java.util.Date;

public class FlamesUser {
    private int score;
    private String firstSeen;
    private int emotion;
    private float multiplier;
    private Date lastSeen;
    private int streak;
    private String discordId;
    private String locale;
    private UserStats stats;
//    private int level;
//    private int exp;
    public FlamesUser(int score, String firstSeen, int emotion, float multiplier, Date lastSeen, int streak, String discordId, String locale, UserStats stats) {
        this.score = score;
        this.firstSeen = firstSeen;
        this.emotion = emotion;
        this.multiplier = multiplier;
        this.lastSeen = lastSeen;
        this.streak = streak;
        this.discordId = discordId;
        this.locale = locale;
        this.stats = stats;
//        this.exp = exp;
    }
    public String getLocale() {
        return locale;
    }
    public UserStats getStats() {return stats;};
    public void setStats(UserStats stats) {this.stats = stats;}
    public void setLocale(String locale) {
        this.locale = locale;
    }
    public FlamesUser() {
        //TODO: Remove this stub
    }
    public void addScore(int amount) {
        score += amount;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(String firstSeen) {
        this.firstSeen = firstSeen;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public static FlamesUser get(Snowflake discordId) throws SQLException {
        FlamesDatabase fd = new FlamesDatabase();
        FlamesUser user = fd.readUser(discordId);
        fd.close();
        return user;
    }
    public static void saveData(FlamesUser user) throws SQLException {
        FlamesDatabase fd = new FlamesDatabase();
        fd.write(user);
    }
    public void saveData() throws SQLException {
        FlamesDatabase fd = new FlamesDatabase();
        fd.write(this);
        fd.close();
    }
}
