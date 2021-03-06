package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * @author DS
 */
public class ConditionTargetPlayable extends Condition {
    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return isPlayable(effected);
    }
}
