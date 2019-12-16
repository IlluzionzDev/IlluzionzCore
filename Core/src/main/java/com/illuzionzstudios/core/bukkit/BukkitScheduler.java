package com.illuzionzstudios.core.bukkit;

import com.illuzionzstudios.core.scheduler.MinecraftScheduler;
import com.illuzionzstudios.core.scheduler.sync.Async;
import com.illuzionzstudios.core.scheduler.sync.Sync;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

/**
 * Created by Illuzionz on 12 2019
 */
@RequiredArgsConstructor
public class BukkitScheduler extends MinecraftScheduler {

    private final Plugin plugin;
    private int SYNC_SCHEDULER = -1, ASYNC_SCHEDULER = -1;

    @Override
    public void start() {
        // The Bukkit SYNC scheduler thread //
        SYNC_SCHEDULER = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> heartbeat(Sync.class), 0L, 0L);

        // The Bukkit ASYNC scheduler //
        ASYNC_SCHEDULER = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, () -> heartbeat(Async.class), 0L, 0L);
    }

    @Override
    public void stop() {
        plugin.getServer().getScheduler().cancelTask(SYNC_SCHEDULER);
        plugin.getServer().getScheduler().cancelTask(ASYNC_SCHEDULER);
    }

    @Override
    public void validateMainThread() {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("This method must be called on main server thread");
        }
    }

    @Override
    public void validateNotMainThread() {
        if (Bukkit.isPrimaryThread()) {
            throw new RuntimeException("This method must not be called on the main server thread");
        }
    }

    @Override
    public int synchronize(Runnable runnable, long time) {
        return plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, time);
    }

    @Override
    public int desynchronize(Runnable runnable, long time) {
        return plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, runnable, time);
    }

    @Override
    public <T> void desynchronize(Callable<T> callable, Consumer<Future<T>> consumer) {
        // FUTURE TASK //
        FutureTask<T> task = new FutureTask<>(callable);

        // BUKKIT'S ASYNC SCHEDULE WORKER
        new BukkitRunnable() {
            @Override
            public void run() {
                // RUN FUTURE TASK ON THREAD //
                task.run();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // ACCEPT CONSUMER //
                        if (consumer != null) {
                            consumer.accept(task);
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }
}
