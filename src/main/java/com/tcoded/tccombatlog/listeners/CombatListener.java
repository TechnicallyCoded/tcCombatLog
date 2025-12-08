package com.tcoded.tccombatlog.listeners;

import com.tcoded.tccombatlog.manager.CombatManager;
import com.tcoded.tccombatlog.manager.FlyManager;
import com.tcoded.tccombatlog.types.CombatSession;
import org.bukkit.ChatColor;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/*
 * Listens for entity damage events. Uses Paper API 1.21.4's getDamageSource() method (assumed available on the event)
 * to retrieve the attacking entity. If the attacker is a player, it tags the victim using the CombatManager.
 * Also disables flight for both attacker and victim when combat begins.
 */
public class CombatListener implements Listener {

    private final CombatManager combatManager;
    private final FlyManager flyManager;

    public CombatListener(CombatManager combatManager, FlyManager flyManager) {
        this.combatManager = combatManager;
        this.flyManager = flyManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Only process if the damaged entity is a player.
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        // Handle EntityDamageByEntityEvent separately.
        if (event instanceof EntityDamageByEntityEvent damageEvent) {
            handleEntityDamageByEntity(damageEvent);
        } else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            handleBlockRelatedDamage(event);
        }
    }

    private void handleEntityDamageByEntity(EntityDamageByEntityEvent damageEvent) {
        if (damageEvent.getDamageSource() == null) {
            return;
        }

        Entity damageSource = damageEvent.getDamageSource().getCausingEntity();
        if (damageSource instanceof Player attacker) {
            Player victim = (Player) damageEvent.getEntity();

            CombatSession sessionVictim = combatManager.getOrCreateSession(victim);
            Player prevAttackerForVictim = sessionVictim.getAttacker();

            CombatSession sessionAttacker = combatManager.getOrCreateSession(attacker);
            Player prevAttackerForAttacker = sessionAttacker.getAttacker();

            combatManager.tagPlayer(victim, attacker);

            // Disable fly for both players, send message only if they weren't already in combat
            flyManager.disableFly(victim, prevAttackerForVictim == null);
            flyManager.disableFly(attacker, prevAttackerForAttacker == null);

            if (prevAttackerForVictim == null) {
                victim.sendMessage(ChatColor.RED + "You are now in combat with " + attacker.getName() + "!");
            }

            if (prevAttackerForAttacker == null) {
                attacker.sendMessage(ChatColor.RED + "You are now in combat with " + victim.getName() + "!");
            }
        }
    }

    private void handleBlockRelatedDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        DamageSource damageSource = event.getDamageSource();
        Entity attacker = damageSource.getCausingEntity();

        if (!(attacker instanceof Player playerAttacker)) {
            return;
        }

        combatManager.tagPlayer(victim, playerAttacker);
        flyManager.disableFly(victim, false);
        victim.sendMessage(ChatColor.RED + "You are now in combat due to a block-related damage source!");
    }

}