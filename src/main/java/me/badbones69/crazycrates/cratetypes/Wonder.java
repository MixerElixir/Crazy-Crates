package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class Wonder implements Listener {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	
	public static void startWonder(final Player player, Crate crate, KeyType key) {
		final Inventory inv = Bukkit.createInventory(null, 45, Methods.color(crate.getFile().getString("Crate.CrateName")));
		final ArrayList<String> slots = new ArrayList<>();
		for(int i = 0; i < 45; i++) {
			Prize prize = crate.pickPrize(player);
			slots.add(i + "");
			inv.setItem(i, prize.getDisplayItem());
		}
		cc.takeKeys(1, player, crate, key);
		player.openInventory(inv);
		cc.addCrateTask(player, new BukkitRunnable() {
			int fulltime = 0;
			int timer = 0;
			int slot1 = 0;
			int slot2 = 44;
			Random r = new Random();
			ArrayList<Integer> Slots = new ArrayList<>();
			Prize p = null;
			@Override
			public void run() {
				if(timer >= 2 && fulltime <= 65) {
					slots.remove(slot1 + "");
					slots.remove(slot2 + "");
					Slots.add(slot1);
					Slots.add(slot2);
					inv.setItem(slot1, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
					inv.setItem(slot2, new ItemBuilder().setMaterial(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build());
					for(String slot : slots) {
						p = crate.pickPrize(player);
						inv.setItem(Integer.parseInt(slot), p.getDisplayItem());
					}
					slot1++;
					slot2--;
				}
				if(fulltime > 67) {
					Material material = Methods.getRandomPaneColor();
					for(int slot : Slots) {
						inv.setItem(slot, new ItemBuilder().setMaterial(material).setName(" ").build());
						inv.setItem(slot, new ItemBuilder().setMaterial(material).setName(" ").build());
					}
				}
				player.openInventory(inv);
				if(fulltime > 100) {
					cc.endCrate(player);
					player.closeInventory();
					cc.givePrize(player, p);
					if(p.useFireworks()) {
						Methods.fireWork(player.getLocation().add(0, 1, 0));
					}
					Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), p));
					cc.removePlayerFromOpeningList(player);
					return;
				}
				fulltime++;
				timer++;
				if(timer > 2) {
					timer = 0;
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 2));
	}
	
}