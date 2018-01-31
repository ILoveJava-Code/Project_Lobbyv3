package de.unknown.titleAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.unknown.main.main;
import net.md_5.bungee.api.ChatColor;

public class CraftTitleAPI implements Listener
{
    private main main;

	public CraftTitleAPI(main main) {
		this.main = main;
	}

@Deprecated
public static void sendTitle(Player player, Integer einblenden, Integer bleiben, Integer hiden, String message)
{
  sendTitle(player, einblenden, bleiben, hiden, message, null);
}

@Deprecated
public static void sendSubtitle(Player player, Integer einblenden, Integer bleiben, Integer hiden, String message) {
  sendTitle(player, einblenden, bleiben, hiden, null, message);
}

@Deprecated
public static void sendFullTitle(Player player, Integer einblenden, Integer bleiben, Integer hiden, String title, String texttitle) {
  sendTitle(player, einblenden, bleiben, hiden, title, texttitle);
}

public static void sendPacket(Player player, Object packet) {
  try {
    Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
    Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
    playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
  } catch (Exception e) {
    e.printStackTrace();
  }
}

public static Class<?> getNMSClass(String name) {
  String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  try {
    return Class.forName("net.minecraft.server." + version + "." + name);
  } catch (ClassNotFoundException e) {
    e.printStackTrace();
  }return null;
}

@SuppressWarnings("rawtypes")
public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle)
{
  TitleSendEvent titleSendEvent = new TitleSendEvent(player, title, subtitle);
  Bukkit.getPluginManager().callEvent(titleSendEvent);
  if (titleSendEvent.isCancelled()) {
    return;
  }

  try
  {
    if (title != null) {
      title = ChatColor.translateAlternateColorCodes('&', title);
      title = title.replaceAll("%player%", player.getDisplayName());

      Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
      Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
      Constructor subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
      Object titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle, fadeIn, stay, fadeOut });
      sendPacket(player, titlePacket);

      e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
      chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
      subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent") });
      titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle });
      sendPacket(player, titlePacket);
    }

    if (subtitle != null) {
      subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
      subtitle = subtitle.replaceAll("%player%", player.getDisplayName());

      Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
      Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
      Constructor subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
      Object subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
      sendPacket(player, subtitlePacket);

      e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
      chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
      subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
      subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
      sendPacket(player, subtitlePacket);
    }
  } catch (Exception var11) {
    var11.printStackTrace();
  }
}

public static void clearTitle(Player player) {
  sendTitle(player, Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), "", "");
}

@SuppressWarnings("rawtypes")
public static void sendTabTitle(Player player, String header, String footer) {
  if (header == null) header = "";
  header = ChatColor.translateAlternateColorCodes('&', header);

  if (footer == null) footer = "";
  footer = ChatColor.translateAlternateColorCodes('&', footer);

  TitleSendEvent tabTitleSendEvent = new TitleSendEvent(player, header, footer);
  Bukkit.getPluginManager().callEvent(tabTitleSendEvent);
  if (tabTitleSendEvent.isCancelled()) {
    return;
  }
  header = header.replaceAll("%player%", player.getDisplayName());
  footer = footer.replaceAll("%player%", player.getDisplayName());
  try
  {
    Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + header + "\"}" });
    Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
    Constructor titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]);
    Object packet = titleConstructor.newInstance(new Object[0]);
    Field aField = packet.getClass().getDeclaredField("a");
    aField.setAccessible(true);
    aField.set(packet, tabHeader);
    Field bField = packet.getClass().getDeclaredField("b");
    bField.setAccessible(true);
    bField.set(packet, tabFooter);
    sendPacket(player, packet);
  } catch (Exception ex) {
    ex.printStackTrace();
  }
}
  /*Server server = main.getServer();
  main.getConfig().options().copyDefaults(true);
  main.saveConfig();*/

@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
  if (main.getConfig().getBoolean("Title On Join")) {
    sendTitle(event.getPlayer(), Integer.valueOf(20), Integer.valueOf(50), Integer.valueOf(20), main.getConfig().getString("Title Message"), main.getConfig().getString("Subtitle Message"));
  }

  if (main.getConfig().getBoolean("Tab Header Enabled"))
    sendTabTitle(event.getPlayer(), main.getConfig().getString("Tab Header Message"), main.getConfig().getString("Tab Footer Message"));
}
}
