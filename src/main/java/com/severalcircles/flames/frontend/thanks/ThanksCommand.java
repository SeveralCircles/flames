/*
 * Copyright (c) 2021 Several Circles.
 */

package com.severalcircles.flames.frontend.thanks;

import com.severalcircles.flames.data.FlamesDataManager;
import com.severalcircles.flames.data.legacy.LegacyFlamesDataManager;
import com.severalcircles.flames.data.legacy.user.LegacyFlamesUser;
import com.severalcircles.flames.data.ConsentException;
import com.severalcircles.flames.data.user.FlamesUser;
import com.severalcircles.flames.exception.handle.ExceptionHandler;
import com.severalcircles.flames.frontend.FlamesCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.Objects;

public class ThanksCommand implements FlamesCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event, FlamesUser sender) {
        User thanked = Objects.requireNonNull(event.getOption("who")).getAsUser();
        String msg;
        try {
            msg = Objects.requireNonNull(event.getOption("msg")).getAsString();
        } catch (NullPointerException e) {
            msg = "";
        }
        FlamesUser flt;
        try {
            flt = FlamesDataManager.getUser(thanked.getId());
        } catch (ConsentException e) {
            event.replyEmbeds(e.getHandler().handleThenGetFrontend()).complete();
            return;
        } catch (IOException e) {
            event.replyEmbeds(new ExceptionHandler(e).handleThenGetFrontend()).complete();
            return;
        }
        event.replyEmbeds(new ThanksEmbed(thanked, event.getUser(), flt, msg).get()).complete();
        if (ThanksEmbed.success.contains(event.getUser().getId())) {
            event.getChannel().sendMessage(thanked.getAsMention()).complete();
            ThanksEmbed.success.remove(event.getUser().getId());
        }
    }
}
