package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author Mobius
 */
public class NotFearedSkillCondition implements SkillCondition {

    private NotFearedSkillCondition() {
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target)
    {
        return isCreature(target) && !((Creature) target).isAffected(EffectFlag.FEAR);
    }

    public static final class Factory extends SkillConditionFactory {

        private static final NotFearedSkillCondition INSTANCE = new NotFearedSkillCondition();

        @Override
        public SkillCondition create(Node xmlNode) {
            return INSTANCE;
        }

        @Override
        public String conditionName() {
            return "NotFeared";
        }
    }
}
