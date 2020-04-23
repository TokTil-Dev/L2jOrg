package org.l2j.gameserver.model.holders;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

import java.util.Collections;
import java.util.List;

import static org.l2j.commons.util.Util.falseIfNullOrElse;

/**
 * A static list container of all multisell entries of a given list.
 *
 * @author Nik
 */
public class MultisellListHolder implements IIdentifiable {
    protected final IntSet _npcsAllowed;
    private final int _listId;
    private final boolean _isChanceMultisell;
    private final boolean _applyTaxes;
    private final boolean _maintainEnchantment;
    private final double _ingredientMultiplier;
    private final double _productMultiplier;
    protected List<MultisellEntryHolder> _entries;

    public MultisellListHolder(int listId, boolean isChanceMultisell, boolean applyTaxes, boolean maintainEnchantment, double ingredientMultiplier, double productMultiplier, List<MultisellEntryHolder> entries, IntSet npcsAllowed) {
        _listId = listId;
        _isChanceMultisell = isChanceMultisell;
        _applyTaxes = applyTaxes;
        _maintainEnchantment = maintainEnchantment;
        _ingredientMultiplier = ingredientMultiplier;
        _productMultiplier = productMultiplier;
        _entries = entries;
        _npcsAllowed = npcsAllowed;
    }

    public MultisellListHolder(StatsSet set) {
        _listId = set.getInt("listId");
        _isChanceMultisell = set.getBoolean("isChanceMultisell", false);
        _applyTaxes = set.getBoolean("applyTaxes", false);
        _maintainEnchantment = set.getBoolean("maintainEnchantment", false);
        _ingredientMultiplier = set.getDouble("ingredientMultiplier", 1.0);
        _productMultiplier = set.getDouble("productMultiplier", 1.0);
        _entries = Collections.unmodifiableList(set.getList("entries", MultisellEntryHolder.class, Collections.emptyList()));
        _npcsAllowed = set.getObject("allowNpc", IntSet.class);
    }

    public List<MultisellEntryHolder> getEntries() {
        return _entries;
    }

    @Override
    public final int getId() {
        return _listId;
    }

    public final boolean isChanceMultisell() {
        return _isChanceMultisell;
    }

    public final boolean isApplyTaxes() {
        return _applyTaxes;
    }

    public final boolean isMaintainEnchantment() {
        return _maintainEnchantment;
    }

    public final double getIngredientMultiplier() {
        return _ingredientMultiplier;
    }

    public final double getProductMultiplier() {
        return _productMultiplier;
    }

    public final boolean isNpcAllowed(int npcId) {
        return falseIfNullOrElse(_npcsAllowed, list -> list.contains(npcId));
    }

}