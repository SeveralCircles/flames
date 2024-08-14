/*
 * Copyright (c) 2023 Several Circles.
 */

package com.severalcircles.flames.frontend.info;

import com.severalcircles.flames.Flames;
import com.severalcircles.flames.data.legacy.global.GlobalData;
import com.severalcircles.flames.data.legacy.user.LegacyFlamesUser;
import com.severalcircles.flames.external.FlamesAssets;
import com.severalcircles.flames.frontend.FlamesEmbed;
import com.severalcircles.flames.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ResourceBundle;

public class AboutEmbed implements FlamesEmbed {
    private final ResourceBundle local;

    public AboutEmbed(LegacyFlamesUser user) {
        this.local = Flames.local(user.getConfig().getLocale());
    }
    public MessageEmbed get() {
        return new EmbedBuilder()
                .setTitle(Flames.api.getSelfUser().getName() + " " + Flames.version)
                .setAuthor(local.getString("author"), null, Flames.api.getSelfUser().getAvatarUrl())
                .setDescription(String.format(local.getString("description"), Flames.version))
                .setImage(FlamesAssets.getVersionIcon())
                .addField(local.getString("created"), StringUtil.prettifyDate(Flames.api.getSelfUser().getTimeCreated().toInstant()), true)
                .addField(local.getString("users"), String.valueOf(GlobalData.participants), true)
                .setFooter(Flames.api.getSelfUser().getGlobalName(), Flames.api.getSelfUser().getAvatarUrl())
                .setColor(Color.decode("#D9581C"))
                .build();
    }
}
