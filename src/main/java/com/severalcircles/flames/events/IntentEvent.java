/*
 * Copyright (c) 2022 Several Circles.
 */

package com.severalcircles.flames.events;

import com.severalcircles.flames.frontend.FlamesIntentResponse;
import com.severalcircles.flames.frontend.data.user.intents.MyDataIntent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class IntentEvent {
    static Map<String, FlamesIntentResponse> responseMap = new HashMap<>();
    public void register() {
        responseMap.put("&myData", new MyDataIntent());
    }
    public void execute(String[] response, MessageReceivedEvent messageEvent) {
        responseMap.forEach((key, value) -> {
            if (response[1].contains(key)) value.execute(messageEvent);
        });
    }
}
