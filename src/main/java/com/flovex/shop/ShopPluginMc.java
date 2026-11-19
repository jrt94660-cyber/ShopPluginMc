package com.flovex.shop;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public final class ShopPluginMc extends JavaPlugin implements Listener {
 private static final String MAIN="§8Shop";
 @Override public void onEnable(){saveDefaultConfig();Bukkit.getPluginManager().registerEvents(this,this);}
 @Override public boolean onCommand(CommandSender s,Command c,String l,String[] a){
  if(c.getName().equalsIgnoreCase("flovexshop")){if(!s.hasPermission("flovexshop.admin")){s.sendMessage("§cNo permission.");return true;}if(a.length==1&&a[0].equalsIgnoreCase("reload")){reloadConfig();s.sendMessage("§aConfig reloaded.");}else s.sendMessage("§e/flovexshop reload");return true;}
  if(!(s instanceof Player p))return true;if(!p.hasPermission("flovexshop.use")){p.sendMessage("§cNo permission.");return true;}
  if(c.getName().equalsIgnoreCase("shardshop")){p.performCommand(getConfig().getString("shard-command","shardshop"));return true;}
  if(a.length>0)openCat(p,a[0]);else openMain(p);return true;
 }
 private void openMain(Player p){Inventory v=Bukkit.createInventory(null,27,MAIN);fill(v,Material.GRAY_STAINED_GLASS_PANE);ConfigurationSection cats=getConfig().getConfigurationSection("categories");if(cats!=null)for(String id:cats.getKeys(false)){ConfigurationSection cat=cats.getConfigurationSection(id);if(cat==null||!cat.getBoolean("enabled",true))continue;int slot=cat.getInt("slot",13);if(slot>=0&&slot<27)v.setItem(slot,item(cat.getString("icon","CHEST"),cat.getString("name",id),cat.getString("lore","")));}p.openInventory(v);}
 private void openCat(Player p,String id){ConfigurationSection cat=getConfig().getConfigurationSection("categories."+id);if(cat==null){openMain(p);return;}if(id.equalsIgnoreCase(getConfig().getString("shard-category","shards"))){p.performCommand(getConfig().getString("shard-command","shardshop"));return;}int size=Math.max(9,Math.min(54,cat.getInt("size",54)));size=size/9*9;Inventory v=Bukkit.createInventory(null,size,cat.getString("title",id));fill(v,Material.GRAY_STAINED_GLASS_PANE);ConfigurationSection items=cat.getConfigurationSection("items");if(items!=null)for(String key:items.getKeys(false)){ConfigurationSection it=items.getConfigurationSection(key);if(it==null)continue;int slot=it.getInt("slot",0);if(slot>=0&&slot<size)v.setItem(slot,item(it.getString("material","STONE"),it.getString("name",key),it.getString("lore","")));}v.setItem(size-9,item("RED_DYE","§cBack","§7Back to categories"));p.openInventory(v);}
 @EventHandler public void click(InventoryClickEvent e){if(!(e.getWhoClicked() instanceof Player p)||e.getClickedInventory()!=e.getView().getTopInventory())return;String title=e.getView().getTitle();if(!title.equals(MAIN)&&!catTitle(title))return;e.setCancelled(true);ItemStack clicked=e.getCurrentItem();if(clicked==null||clicked.getType().isAir())return;if(title.equals(MAIN)){ConfigurationSection cats=getConfig().getConfigurationSection("categories");if(cats!=null)for(String id:cats.getKeys(false)){String name=color(getConfig().getString("categories."+id+".name",id));if(clicked.hasItemMeta()&&clicked.getItemMeta().hasDisplayName()&&name.equals(clicked.getItemMeta().getDisplayName())){if(id.equalsIgnoreCase(getConfig().getString("shard-category","shards")))p.performCommand(getConfig().getString("shard-command","shardshop"));else openCat(p,id);return;}}}else if(clicked.getType()==Material.RED_DYE)openMain(p);}
 private boolean catTitle(String t){ConfigurationSection cats=getConfig().getConfigurationSection("categories");if(cats==null)return false;for(String id:cats.getKeys(false))if(t.equals(getConfig().getString("categories."+id+".title",id)))return true;return false;}
 private ItemStack item(String mat,String name,String lore){Material m;try{m=Material.valueOf(mat.toUpperCase(Locale.ROOT));}catch(Exception ex){m=Material.BARRIER;}ItemStack i=new ItemStack(m);ItemMeta meta=i.getItemMeta();if(meta!=null){meta.setDisplayName(color(name));if(lore!=null&&!lore.isBlank())meta.setLore(Arrays.asList(lore.split("\\n")).stream().map(this::color).toList());i.setItemMeta(meta);}return i;}
 private String color(String s){return s==null?"":s.replace('&','§');}
 private void fill(Inventory v,Material pane){ItemStack f=item(pane.name()," ","");for(int n=0;n<v.getSize();n++)if(v.getItem(n)==null)v.setItem(n,f);}
}
