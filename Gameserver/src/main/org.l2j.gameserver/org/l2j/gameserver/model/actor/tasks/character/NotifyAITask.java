package org.l2j.gameserver.model.actor.tasks.character;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.Creature;

/**
 * Task dedicated to notify character's AI
 *
 * @author xban1x
 */
public final class NotifyAITask implements Runnable {
    private final Creature _character;
    private final CtrlEvent _event;

    public NotifyAITask(Creature character, CtrlEvent event) {
        _character = character;
        _event = event;
    }

    @Override
    public void run() {
        if (_character != null) {
            _character.getAI().notifyEvent(_event, null);
        }
    }
}
