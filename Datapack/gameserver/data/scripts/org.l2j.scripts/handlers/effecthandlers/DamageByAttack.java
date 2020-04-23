package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.DamageByAttackType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * An effect that changes damage taken from an attack.<br>
 * The retail implementation seems to be altering whatever damage is taken after the attack has been done and not when attack is being done. <br>
 * Exceptions for this effect appears to be DOT effects and terrain damage, they are unaffected by this stat.<br>
 * As for example in retail this effect does reduce reflected damage taken (because it is received damage), as well as it does not decrease reflected damage done,<br>
 * because reflected damage is being calculated with the original attack damage and not this altered one.<br>
 * Multiple values of this effect add-up to each other rather than multiplying with each other. Be careful, there were cases in retail where damage is deacreased to 0.
 *
 * @author Nik
 * @author JoeAlisson
 */
public class DamageByAttack extends AbstractEffect {

    private final double power;
    private final DamageByAttackType type;

    private DamageByAttack(StatsSet params) {
        power = params.getDouble("power");
        type = params.getEnum("type", DamageByAttackType.class, DamageByAttackType.NONE);
    }

    @Override
    public void pump(Creature target, Skill skill) {
        switch (type) {
            case PK -> target.getStats().mergeAdd(Stat.PVP_DAMAGE_TAKEN, power);
            case ENEMY_ALL -> target.getStats().mergeAdd(Stat.PVE_DAMAGE_TAKEN, power);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DamageByAttack(data);
        }

        @Override
        public String effectName() {
            return "damage-by-attack";
        }
    }
}
