package me.wertiko.elytraBlocker;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElytraBlocker extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("elyblock")).setExecutor(this);
        Objects.requireNonNull(getCommand("elyblock")).setTabCompleter(this);

        getLogger().info("ElytraBlocker включен!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("elyblock")) return false;

        if (!sender.hasPermission("elytrablocker.admin")) {
            sendMiniMessage(sender, config.getString("noPermission", "<red>Недостаточно прав!"));
            return true;
        }

        if (args.length == 0) {
            sendMiniMessage(sender, "<yellow>Используйте: /elyblock reload или /elyblock toggle");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            config = getConfig();
            sendMiniMessage(sender, config.getString("reloadConfig", "<green>Конфигурация перезагружена!"));
        } else if (args[0].equalsIgnoreCase("toggle")) {
            boolean isEnabled = config.getBoolean("isEnable", true);
            config.set("isEnable", !isEnabled);
            saveConfig();
            sendMiniMessage(sender, isEnabled ? "<red>Блокировка элитр отключена!" : "<green>Блокировка элитр включена!");
        } else {
            sendMiniMessage(sender, "<yellow>Неизвестная команда. Используйте: reload или toggle.");
        }

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!config.getBoolean("isEnable", true)) return;

        if (player.getGameMode() != GameMode.SURVIVAL) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (player.getWorld().getEnvironment() == World.Environment.THE_END && player.isGliding()) {
            cancelElytraUsage(player);
        }
    }

    @EventHandler
    public void onElytraToggle(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) return;

        if (!config.getBoolean("isEnable", true)) return;

        if (player.getGameMode() != GameMode.SURVIVAL) return;

        if (event.isGliding() && player.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled(true);
            cancelElytraUsage(player);
        }
    }

    private void cancelElytraUsage(Player player) {
        if (config.getBoolean("isDropElytra", true)) {
            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
                player.getInventory().setChestplate(null);
                player.getWorld().dropItemNaturally(player.getLocation(), chestplate);
            }
        }

        player.setVelocity(new Vector(0, 0, 0));

        String message = config.getString("elytraUseMessage", "<red>Элитры отключены!");
        sendMiniMessage(player, message);
    }

    public void sendMiniMessage(CommandSender sender, String message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("elyblock")) {
            List<String> completions = new ArrayList<>();

            if (args.length == 1) {
                completions.add("reload");
                completions.add("toggle");
            }

            return completions;
        }

        return null;
    }
}
