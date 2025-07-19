package com.monkey.mobattack.commands.manager;

import com.monkey.mobattack.commands.manager.reflection.ReflectionMobCommand;
import com.monkey.mobattack.commands.manager.reflection.ReflectionNoMobCommand;
import com.monkey.mobattack.utils.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public class CommandRegister {

    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public CommandRegister(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    public void registerAll() {
        int count = 0;

        Reflections mobReflections = new Reflections("com.monkey.mobattack.commands");
        Set<Class<? extends ReflectionMobCommand>> mobCommands = mobReflections.getSubTypesOf(ReflectionMobCommand.class);

        for (Class<? extends ReflectionMobCommand> clazz : mobCommands) {
            try {
                String simpleName = clazz.getSimpleName();
                if (!simpleName.endsWith("Command")) continue;

                String entityName = simpleName.replace("Command", "");

                if (!isEntitySupported(entityName)) {
                    Bukkit.getLogger().info("[MobAttack] Entity '" + entityName + "' is not supported in this version. Skipping command.");
                    continue;
                }

                Constructor<? extends ReflectionMobCommand> constructor = clazz.getConstructor(JavaPlugin.class, CooldownManager.class);
                ReflectionMobCommand command = constructor.newInstance(plugin, cooldownManager);
                String name = command.getCommandName();

                plugin.getCommand(name).setExecutor(command);
                Bukkit.getLogger().info("[MobAttack] Command /" + name + " successfully registered.");
                count++;

            } catch (Exception e) {
                Bukkit.getLogger().warning("[MobAttack] Error while registering mob command: " + clazz.getSimpleName());
                e.printStackTrace();
            }
        }

        Reflections noMobReflections = new Reflections("com.monkey.mobattack.commands");
        Set<Class<? extends ReflectionNoMobCommand>> noMobCommands = noMobReflections.getSubTypesOf(ReflectionNoMobCommand.class);

        for (Class<? extends ReflectionNoMobCommand> clazz : noMobCommands) {
            try {
                Constructor<? extends ReflectionNoMobCommand> constructor = clazz.getConstructor(JavaPlugin.class, CooldownManager.class);
                ReflectionNoMobCommand command = constructor.newInstance(plugin, cooldownManager);
                String name = command.getCommandName();

                plugin.getCommand(name).setExecutor(command);
                Bukkit.getLogger().info("[MobAttack] Command /" + name + " successfully registered (non-mob).");
                count++;

            } catch (Exception e) {
                Bukkit.getLogger().warning("[MobAttack] Error while registering non-mob command: " + clazz.getSimpleName());
                e.printStackTrace();
            }
        }

        Bukkit.getLogger().info("[MobAttack] Total commands loaded: " + count);
    }

    private boolean isEntitySupported(String entityName) {
        try {
            Class.forName("org.bukkit.entity." + entityName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
