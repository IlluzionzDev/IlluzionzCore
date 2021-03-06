package com.illuzionzstudios.scheduler;

import com.illuzionzstudios.core.IlluzionzCore;
import com.illuzionzstudios.scheduler.bukkit.instance.InstancedEntity;
import com.illuzionzstudios.scheduler.object.Destructible;
import com.illuzionzstudios.scheduler.sync.Async;
import com.illuzionzstudios.scheduler.sync.Rate;
import com.illuzionzstudios.scheduler.sync.Sync;
import com.illuzionzstudios.scheduler.util.PresetCooldown;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public abstract class MinecraftScheduler {

    protected static final long TIME_WARNING_THRESHOLD = 60;

    /**
     * ALL SYNCHRONIZED DATA WILL BE STORED HERE
     * DOES NOT REMOVE DEAD OBJECTS BE SURE TO DISMISS SERVICE WHEN FINISHED
     */
    protected volatile static Set<SynchronizationService> SYNC_SERVICE_REGISTRATION;

    // The amount of synchronized ticks the application has undergone //
    private static volatile long synchronousTicks;
    private static MinecraftScheduler scheduler;

    public static MinecraftScheduler get() {
        return scheduler;
    }

    public static long getCurrentTick() {
        return synchronousTicks;
    }

    public void initialize() {
        scheduler = this;
        SYNC_SERVICE_REGISTRATION = Collections.newSetFromMap(new ConcurrentHashMap<>());
        start();
    }

    /**
     * Stop all tickers
     */
    public void stopInvocation() {
        stop();

        SYNC_SERVICE_REGISTRATION.clear();
    }

    /**
     * Starts scheduler
     */
    protected abstract void start();

    /**
     * Stop scheduler
     */
    protected abstract void stop();

    protected <A extends Annotation> void heartbeat(Class<A> type) {
        for (SynchronizationService service : SYNC_SERVICE_REGISTRATION) {
            for (SynchronizedElement element : service.elements) {
                if (!element.synchronizationClass.equals(type)) {
                    continue;
                }

                // Lets get all the refresh services //

                // Use synchronous ticks to check if rate has elapsed so that if the
                // server thread blocks will still account for the time that we missed

                if ((type.equals(Sync.class) && element.timer.isReady())
                        || (type.equals(Async.class) && element.timer.isReadyRealTime())) {
                    element.timer.go();

                    if (element.object instanceof Method) {
                        Method method = (Method) element.object;

                        // Determine if method should be fired based on the rate of refresh //
                        final long start = System.currentTimeMillis();

                        try {
                            method.invoke(service.source);
                        } catch (Exception e) {
                            IlluzionzCore.LOGGER.severe("Interrupted synchronization invocation: ");
                            e.printStackTrace();
                        }

                        if (System.currentTimeMillis() - start > MinecraftScheduler.TIME_WARNING_THRESHOLD
                                && type.equals(Sync.class)) {
                            IlluzionzCore.LOGGER.severe("WARNING: Synchronization block took way too long to invoke! (" + (System.currentTimeMillis() - start) + "ms)");
                            IlluzionzCore.LOGGER.severe("Block " + method.getName() + "() in " + service.source.getClass());
                        }
                    } else {
                        try {
                            Object object = (element.object instanceof Field) ? ((Field) element.object).get(service.source) : service.source;

                            if (object != null) {
                                if (object instanceof Tickable) {
                                    if (object instanceof Destructible && !((Destructible) object).isValid()) {
                                        continue;
                                    }

                                    safelyTick((Tickable) object);

                                } else if (object instanceof Iterable || object instanceof Map) {
                                    Iterable iterable = null;

                                    if (object instanceof Collection) {
                                        iterable = (Iterable) object;
                                    } else if (object instanceof Map) {
                                        iterable = ((Map) object).values();
                                    }

                                    if (iterable != null) {
                                        iterable.forEach(fieldElement -> {
                                            if (fieldElement != null && fieldElement instanceof Tickable) {
                                                if (fieldElement instanceof Destructible && !((Destructible) fieldElement).isValid()) {
                                                    return;
                                                }

                                                safelyTick((Tickable) fieldElement);
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            IlluzionzCore.LOGGER.severe("Interrupted synchronization invocation: ");
                            IlluzionzCore.LOGGER.severe("in " + service.source.getClass());
                            e.printStackTrace();
                        }
                    }

                }
            }
        }

        if (type.equals(Sync.class)) {
            synchronousTicks++;
        }
    }

    /**
     * Safely runs tick without throwing an exception
     *
     * @param tickable Tickable object
     * @return Returns if tick operation executed successfully
     */
    public boolean safelyTick(Tickable tickable) {
        try {
            if (tickable == null) {
                return false;
            }

            if (tickable instanceof InstancedEntity) {
                if (!((InstancedEntity) tickable).isValid()) {
                    return false;
                }
            }

            tickable.tick();
        } catch (Exception e) {
            IlluzionzCore.LOGGER.severe("Interrupted tick call!:");
            if (tickable instanceof InstancedEntity) {
                e.printStackTrace();
                InstancedEntity instance = (InstancedEntity) tickable;
                if (instance.getPlayer() == null) {
                    return false;
                }

                try {
                    instance.informError("Error occured while ticking player");
                } finally {
                    try {
                        instance.destroy();
                    } catch (Exception anotherException) {
                        try {
                            instance.kickPlayer("Error occured while ticking player");
                        } finally {
                            anotherException.printStackTrace();
                        }
                    }
                }
                return false;
            }
            e.printStackTrace();
        }

        return true;
    }

    /**
     * @param occurrence The time in ticks we want to check
     * @return If time has elapsed
     */
    public boolean hasElapsed(double occurrence) {
        return synchronousTicks % occurrence == 0;
    }

    /**
     * Registers a class as refresh service listener
     *
     * @param source This can be any object you want
     */
    public void registerSynchronizationService(Object source) {
        SYNC_SERVICE_REGISTRATION.add(new SynchronizationService(source));
    }

    /**
     * Removes a synchronized service
     *
     * @param source This can be any object you want
     */
    public void dismissSynchronizationService(Object source) {
        SYNC_SERVICE_REGISTRATION.removeIf(service -> service.source.equals(source));
    }

    /**
     * Checks if method is being ran on server thread
     */
    public abstract void validateMainThread();

    /**
     * Checks if method is not being ran on server thread
     */
    public abstract void validateNotMainThread();

    /**
     * Submits task into sync scheduler
     *
     * @param runnable The task
     */
    public int synchronize(Runnable runnable) {
        return synchronize(runnable, 0);
    }

    /**
     * Submits task into async scheduler
     *
     * @param runnable The task
     */
    public int desynchronize(Runnable runnable) {
        return desynchronize(runnable, 0);
    }

    /**
     * Submits task into sync scheduler
     *
     * @param runnable The task
     * @param time     The delay time
     */
    public abstract int synchronize(Runnable runnable, long time);

    /**
     * Submits task into async scheduler
     *
     * @param runnable The task
     * @param time     The delay time
     */
    public abstract int desynchronize(Runnable runnable, long time);

    /**
     * Asynchronous callback tool
     *
     * @param callable The future task
     * @param consumer The task callback
     * @param <T>      The type returned
     */
    public abstract <T> void desynchronize(Callable<T> callable, Consumer<Future<T>> consumer);

    /**
     * Cancel a running task with certain id
     *
     * @param id The id of the task to cancel
     */
    public abstract void stopTask(int id);

    /**
     * Cached Synchronization Element
     * This contains the method/field data that will be used
     */
    protected static class SynchronizedElement<A extends Annotation> {

        protected final Rate rate;
        protected final Object object;
        protected final Class<A> synchronizationClass;
        protected final PresetCooldown timer;


        protected SynchronizedElement(Rate rate, Object object, Class<A> synchronizationClass) {
            this.rate = rate;
            this.object = object;
            this.synchronizationClass = synchronizationClass;

            this.timer = new PresetCooldown((int) (rate.getTime() / 50));
        }

    }

    /**
     * Cached Synchronization Service
     */
    protected static class SynchronizationService {

        protected Object source;
        protected Set<SynchronizedElement> elements = new HashSet<>();

        private SynchronizationService(Object source) {
            this.source = source;

            try {
                // LOAD ELEMENTS //
                for (Class<? extends Annotation> clazz : getAnnotations()) {

                    if (source.getClass().isAnnotationPresent(clazz)) {
                        Rate rate = getRate(clazz, source.getClass());
                        elements.add(new SynchronizedElement<>(rate, source, clazz));
                    } else if (source.getClass().getSuperclass().isAnnotationPresent(clazz)) {
                        Rate rate = getRate(clazz, source.getClass().getSuperclass());
                        elements.add(new SynchronizedElement<>(rate, source, clazz));
                    }

                    for (Rate rate : Rate.values()) {
                        // LOAD METHODS //
                        elements.addAll(getElements
                                (getAllMethods(source.getClass()), clazz, rate));

                        // LOAD FIELDS //
                        elements.addAll(getElements
                                (getAllFields(source.getClass()), clazz, rate));
                    }

                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public static Field[] getAllFields(Class aClass) {
            List<Field> fields = new ArrayList<>();
            do {
                Collections.addAll(fields, aClass.getDeclaredFields());
                aClass = aClass.getSuperclass();
            } while (aClass != null);
            return fields.toArray(new Field[fields.size()]);
        }

        public static Method[] getAllMethods(Class aClass) {
            List<Method> methods = new ArrayList<>();
            do {
                Collections.addAll(methods, aClass.getDeclaredMethods());
                aClass = aClass.getSuperclass();
            } while (aClass != null);
            return methods.toArray(new Method[methods.size()]);
        }

        private <A extends Annotation> Class<A>[] getAnnotations() {
            return new Class[]{Sync.class, Async.class};
        }

        private <A extends Annotation> Set<SynchronizedElement<A>> getElements(AccessibleObject[] objects, Class<A> synchronizationClass, Rate rate)
                throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Set<SynchronizedElement<A>> elements = new HashSet<>();

            for (AccessibleObject object : objects) {
                // Set them to public if they are private for obvious reasons
                if (!object.isAccessible()) {
                    object.setAccessible(true);
                }

                Rate declaredRate = getRate(synchronizationClass, object);

                if (declaredRate != null && declaredRate.equals(rate)) {
                    elements.add(new SynchronizedElement<>(rate, object, synchronizationClass));
                }
            }

            return elements;
        }

        private <A extends Annotation> Rate getRate(Class<A> synchronizationClass, AnnotatedElement element) throws NoSuchMethodException,
                InvocationTargetException, IllegalAccessException {
            if (!element.isAnnotationPresent(synchronizationClass)) {
                return null;
            }

            // Get the annotation itself //
            A annotation = element.getAnnotation(synchronizationClass);

            // Get declared rate of refresh value is instant by default //
            Method getRate = annotation.annotationType()
                    .getDeclaredMethod("rate");

            if (!getRate.isAccessible()) {
                getRate.setAccessible(true);
            }

            return (Rate) getRate.invoke(annotation);
        }
    }

}
