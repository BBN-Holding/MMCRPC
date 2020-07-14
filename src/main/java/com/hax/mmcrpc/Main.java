package com.hax.mmcrpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Arrays;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class Main {

    public static void main(String[] args) {
        if (args.length != 0) {
            String content = args[0].replace("\"", "");
            String path = args[1].replaceFirst("\"", "");

            try {
                FileWriter myWriter = null;
                myWriter = new FileWriter(path);
                myWriter.write(content);
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new Main().main();
        }
    }

    public void main() {
        // Listen on File Change

        WatchService watcher = null;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path dir = Paths.get(jar.getParent());

            System.out.println("Started watching directory: " + dir.toString());

            try {
                WatchKey key = dir.register(watcher, ENTRY_MODIFY);
            } catch (IOException x) {
                System.err.println(x);
            }

            for (; ; ) {

                // wait for key to be signaled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (filename.toFile().getName().equals("currentcfg.txt")) {
                        try {
                            System.out.println("New Change, reading " + jar.getParent() + "/currentcfg.txt");
                            this.setRPC(new String(Files.readAllBytes(Paths.get(jar.getParent() + "/currentcfg.txt"))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Reset the key -- this step is critical if you want to
                // receive further watch events.  If the key is no longer valid,
                // the directory is inaccessible so exit the loop.
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    DiscordRPC lib;
    boolean initialized = false;

    public void startRPC() {
        System.out.println("Starting RPC");
        this.initialized = true;
        lib = DiscordRPC.INSTANCE;
        String applicationId = "703315329476198440";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(applicationId, handlers, true, null);

        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();
        System.out.println("Started RPC");
    }

    public void setRPC(String name) {
        if (!initialized)
            this.startRPC();

        if (name.equals(" ")) {
            System.out.println("Clearing RPC");
            lib.Discord_ClearPresence();
        } else {
            System.out.println("Setting RPC to: "+name);
            DiscordRichPresence presence = new DiscordRichPresence();
            presence.startTimestamp = System.currentTimeMillis() / 1000;
            presence.details = "Profile: " + name;
            presence.largeImageKey = "mmc";
            presence.largeImageText = "Using MultiMC";
            presence.smallImageKey = "bbn_logo";
            presence.smallImageText = "Coded by BBN";
            lib.Discord_UpdatePresence(presence);
        }
    }

}
