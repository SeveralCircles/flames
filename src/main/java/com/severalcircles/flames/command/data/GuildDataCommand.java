/*
 * Copyright (c) 2021 Several Circles.
 */

package com.severalcircles.flames.command.data;

import com.severalcircles.flames.command.FlamesCommand;
import com.severalcircles.flames.data.base.ConsentException;
import com.severalcircles.flames.data.base.FlamesDataManager;
import com.severalcircles.flames.data.guild.FlamesGuild;
import com.severalcircles.flames.data.guild.NewGuildException;
import com.severalcircles.flames.data.user.FlamesUser;
import com.severalcircles.flames.features.Emotion;
import com.severalcircles.flames.features.StringUtils;
import com.severalcircles.flames.features.rank.Ranking;
import com.severalcircles.flames.system.Flames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;

public class GuildDataCommand implements FlamesCommand {
//    ResourceBundle resources = ResourceBundle.getBundle("commands/MyDataCommand", Locale.ENGLISH);
    @Override
    public void execute(SlashCommandEvent event, FlamesUser sender) {
        Guild guild = event.getGuild();
        FlamesGuild gdata;
        try {
            gdata = FlamesDataManager.readGuild(guild.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (NewGuildException e) {
            event.reply("Something went wrong. Mind trying that again?");
            return;
        }
        FlamesUser owner;
        try {
            owner = FlamesDataManager.readUser(guild.getOwner().getUser());
        } catch (IOException | ConsentException | NullPointerException e) {
            owner = new FlamesUser();
        }
        float emotion = gdata.getEmotion();
        String emotionString = Emotion.getEmotionString(emotion, sender.getConfig().getLocale());
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Guild Data", null, event.getUser().getAvatarUrl())
                .setTitle(guild.getName())
                .setDescription("Using Flames since " + StringUtils.prettifyDate(gdata.getJoined()))
                .setThumbnail(guild.getIconUrl())
                .addField("Flames Score", StringUtils.formatScore(gdata.getFlamesScore()), true)
                .addField("Rank", Ranking.getRank((int) Math.round(gdata.getFlamesScore() / guild.getMemberCount())).toString(), true)
//                .addField("Owner", guild.getOwner().getEffectiveName() + " (" + Ranking.getRank(owner.getScore()).toString() + ")", true)
                .addField("Feeling", emotionString, true)
                .setTimestamp(Instant.now())
                .setColor(Color.magenta.darker())
                .setFooter(Flames.api.getSelfUser().getName(), Flames.api.getSelfUser().getAvatarUrl())
                .build();
        event.replyEmbeds(embed).complete();
    }
}
