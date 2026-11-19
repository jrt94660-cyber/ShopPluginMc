package com.flovex.shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
public final class ShopPluginMc extends JavaPlugin implements Listener {
 private static final String MAIN_TITLE="§8Shop";
 @Override public void onEnable(){saveDefaultConfig();Bukkit.getPluginManager().registerEvents(this,this);getLogger().info("ShopPluginMc enabled");}
 @Override public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
  if(cmd.getName().equalsIgnoreCase("flovexshop")){if(!sender.hasPermission("flovexshop.admin")){sender.sendMessage("§cNo permission.");return true;}if(args.length==1&&args[0].equalsIgnoreCase("reload")){reloadConfig();sender.sendMessage("§aShop config reloaded.");return true;}sender.sendMessage("§e/flovexshop reload");return true;}
  if(!(sender instanceof Player p)){sender.sendMessage("Players only.");return true;}if(!p.hasPermission("flovexshop.use")){p.sendMessage("§cNo permission.");return true;}
  if(cmd.getName().equalsIgnoreCase("shardshop")){p.performCommand(getConfig().getString("shard-command","shardshop"));return true;}
  if(args.length>0)openCategory(p,args[0]);else openMain(p);return true;
 }
 private void openMain(Player p){Inventory inv=Bukkit.createInventory(null,27,MAIN_TITLE);fill(inv,Material.GRAY_STAINED_GLASS_PANE," ");var cats=getConfig().getConfigurationSection("categories");if(cats!=null)for(String id:cats.getKeys(false)){var c=cats.getConfigurationSection(id);if(c==null||!c.getBoolean("enabled",true))continue;int slot=c.getInt("slot",13);if(slot>=0&&slot<27)inv.setItem(slot,make(c.getString("icon","CHEST"),c.getString("name",id),c.getString("lore","")));}p.openInventory(inv);}
 private void openCategory(Player p,String id){var cat=getConfig().getConfigurationSection("categories."+id);if(cat==null||!cat.getBoolean("enabled",true)){openMain(p);return;}if(id.equalsIgnoreCase(getConfig().getString("shard-category","shards"))){p.performCommand(getConfig().getString("shard-command","shardshop"));return;}int size=Math.max(9,Math.min(54,cat.getInt("size",54)));size=(size/9)*9;Inventory inv=Bukkit.createInventory(null,size,cat.getString("title",id));fill(inv,Material.GRAY_STAINED_GLASS_PANE," ");var entries=cat.getConfigurationSection("items");if(entries!=null)for(String key:entries.getKeys(false)){var entry=entries.getConfigurationSection(key);if(entry==null)continue;int slot=entry.getInt("slot",0);if(slot>=0&&slot<size)inv.setItem(slot,make(entry.getString("material","STONE"),entry.getString("name",key),entry.getString("lore","")));}inv.setItem(size-9,make("RED_DYE","§cBack","§7Return to categories"));p.openInventory(inv);}
 @EventHandler public void click(InventoryClickEvent e){if(!(e.getWhoClicked() instanceof Player p)||e.getClickedInventory()!=e.getView().getTopInventory())return;String title=e.getView().getTitle();if(!title.equals(MAIN_TITLE)&&!isCategoryTitle(title))return;e.setCancelled(true);ItemStack cur=e.getCurrentItem();if(cur==null||cur.getType().isAir())return;if(title.equals(MAIN_TITLE)){var cats=getConfig().getConfigurationSection("categories");if(cats!=null)for(String id:cats.getKeys(false)){String display=translate(getConfig().getString("categories."+id+".name",id));if(cur.hasItemMeta()&&cur.getItemMeta().hasDisplayName()&&cur.getItemMeta().getDisplayName().equals(display)){if(id.equalsIgnoreCase(getConfig().getString("shard-category","shards"))){p.performCommand(getConfig().getString("shard-command","shardshop"));return;}openCategory(p,id);return;}}}else if(cur.getType()==Material.RED_DYE)openMain(p);}
 private boolean isCategoryTitle(String title){var cats=getConfig().getConfigurationSection("categories");if(cats==null)return false;for(String id:cats.getKeys(false))if(title.equals(getConfig().getString("categories."+id+".title",id)))return true;return false;}
 private ItemStack make(String material,String name,String lore){Material m;try{m=Material.valueOf(material.toUpperCase(Locale.ROOT);}catch(Exception ex){m=Material.BARRIER;}ItemStack item=new ItemStack(m);ItemMeta meta=item.getItemMeta();if(meta!=null){meta.setDisplayName(translate(name));if(lore!=null&&!lore.isBlank())meta.setLore(Arrays.stream(lore.split("\\n")).map(this::translate).toList());item.setItemMeta(meta);}return item;}
 private String translate(String s){return s==null?"":s.replace('&','§');}
 private void fill(Inventory inv,Material mat,String name){ItemStack pane=make(mat.name(),name,"");for(int i=0;i<inv.getSize();i++)if(inv.getItem(i)==null)inv.setItem(i,pane);}
}
