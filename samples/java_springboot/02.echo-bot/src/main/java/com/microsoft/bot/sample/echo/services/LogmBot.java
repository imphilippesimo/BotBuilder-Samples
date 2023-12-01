// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.bot.sample.echo.services;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ChannelAccount;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation participants.
 * </p>
 */
public class LogmBot extends ActivityHandler {
    ApiService apiService;

    public LogmBot(ApiService apiService) {
        this.apiService = apiService;
    }


    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        String question = turnContext.getActivity().getText();
        String response = apiService.consumeApi(question);
        return turnContext.sendActivity(
                MessageFactory.text(response)
        ).thenApply(sendResult -> null);
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(
            List<ChannelAccount> membersAdded,
            TurnContext turnContext
    ) {
        String welcomeText = "Bonjour, que souhaitez-vous savoir sur Logm ?";
        return membersAdded.stream()
                .filter(
                        member -> !StringUtils
                                .equals(member.getId(), turnContext.getActivity().getRecipient().getId())
                ).map(channel -> turnContext.sendActivity(MessageFactory.text(welcomeText, welcomeText, null)))
                .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
    }
}
