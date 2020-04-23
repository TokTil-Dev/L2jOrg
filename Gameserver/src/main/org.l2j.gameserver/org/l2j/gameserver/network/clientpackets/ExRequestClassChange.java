package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;

/**
 * @author Mobius
 */
public class ExRequestClassChange extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExRequestClassChange.class);

    private int classId;

    @Override
    public void readImpl() {
        classId = readInt();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();

        if (isNull(player)) {
            return;
        }

        boolean canChange = false;
        for (var cId : player.getClassId().getNextClassIds()) {
            if (cId.getId() == classId) {
                canChange = true;
                break;
            }
        }

        if (!canChange) {
            LOGGER.warn("{} tried to change class from {}  to {}!", player, player.getClassId(), ClassId.getClassId(classId));
            return;
        }

        canChange = false;
        final int playerLevel = player.getLevel();
        if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && (playerLevel >= 18))
        {
            canChange = CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId);
        }
        else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) && (playerLevel >= 38))
        {
            canChange = CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId);
        }
        else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && (playerLevel >= 76))
        {
            canChange = CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId);
        }

        if (canChange)
        {
            player.setClassId(classId);
            if (player.isSubClassActive())
            {
                player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
            }
            else
            {
                player.setBaseClass(player.getActiveClass());
            }

            if (Config.AUTO_LEARN_SKILLS)
            {
                player.giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
            }
            player.store(false); // Save player cause if server crashes before this char is saved, he will lose class.
            player.broadcastUserInfo();
            player.sendSkillList();
            player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
        }
    }
}
