package dev.snowz.infinitystonesprotection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Hopper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfinityStonesProtection extends JavaPlugin implements Listener {

    private final Map<String, Material> infinityStones = new HashMap<String, Material>() {{
        put("§eMind Stone", Material.YELLOW_DYE);
        put("§cReality Stone", Material.RED_DYE);
        put("§6Soul Stone", Material.ORANGE_DYE);
        put("§aTime Stone", Material.LIME_DYE);
        put("§9Space Stone", Material.BLUE_DYE);
        put("§5Power Stone", Material.PURPLE_DYE);
    }};

    private final ArrayList<Item> droppedItems = new ArrayList<>();
    private final PotionEffect glowingEffect = new PotionEffect(PotionEffectType.GLOWING, 20 * getConfig().getInt("durationInSeconds"), 1);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {
        HumanEntity player = e.getWhoClicked();
        ItemStack craftedItem = e.getCurrentItem();

        if (craftedItem != null &&
                craftedItem.hasItemMeta() &&
                Objects.requireNonNull(craftedItem.getItemMeta()).hasDisplayName() &&
                infinityStones.containsKey(craftedItem.getItemMeta().getDisplayName()) &&
                craftedItem.getType() == infinityStones.get(craftedItem.getItemMeta().getDisplayName())) {

            e.setCancelled(true);
            e.getInventory().clear();
            player.closeInventory();

            World world = player.getWorld();
            Location location = player.getLocation();

            Item droppedItem = world.dropItem(location, craftedItem);
            droppedItem.setPickupDelay(20 * getConfig().getInt("durationInSeconds"));
            droppedItem.setInvulnerable(true);

            droppedItems.add(droppedItem);
            new BukkitRunnable() {
                @Override
                public void run() {
                    droppedItems.remove(droppedItem);
                }
            }.runTaskLater(this, 20L * getConfig().getInt("durationInSeconds"));
            System.out.println(droppedItems);

            if (getConfig().getBoolean("glowing")) {
                player.addPotionEffect(glowingEffect);
            }

            if (getConfig().getBoolean("lightning")) {
                world.strikeLightningEffect(location);
            }

            getServer().broadcastMessage("§c§l" + player.getName() + "§r has crafted a " + craftedItem.getItemMeta().getDisplayName() + "§r at §b" + Math.round(location.getX()) + ", " + Math.round(location.getY()) + ", " + Math.round(location.getZ()) + "§r.");
        }

    }

    @EventHandler()
    public void onInventoryPickupItem(InventoryPickupItemEvent e) {
        if (e.getInventory().getHolder() instanceof Hopper && droppedItems.contains(e.getItem())) {
            e.setCancelled(true);
        }
    }
}
