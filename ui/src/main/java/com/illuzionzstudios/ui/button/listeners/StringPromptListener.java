package com.illuzionzstudios.ui.button.listeners;

import com.illuzionzstudios.core.locale.player.Message;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.ui.button.InterfaceClickListener;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public abstract class StringPromptListener implements InterfaceClickListener {

    protected ConversationFactory factory;
    protected Message message;

    public StringPromptListener() {
        this.factory = new ConversationFactory(IlluzionzPlugin.getInstance());
        this.factory = factory.withLocalEcho(false).withModality(false);
    }

    public StringPromptListener(Message message) {
        this();
        this.message = message;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        player.closeInventory();
        ConversationFactory f = factory.withFirstPrompt(new MessagePrompt(player));
        Conversation c = f.buildConversation(player);
        c.addConversationAbandonedListener(e -> conversationAbandoned(player, e));
        c.begin();
    }

    public void conversationAbandoned(Player player, ConversationAbandonedEvent event) {
    }

    public abstract Prompt acceptInput(ConversationContext context, Player player, String message, MessagePrompt prompt);

    public class MessagePrompt extends StringPrompt {
        private Player player;

        public MessagePrompt(Player player) {
            this.player = player;
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return message.getMessage();
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String message) {
            return StringPromptListener.this.acceptInput(conversationContext, player, message, this);
        }
    }
}


