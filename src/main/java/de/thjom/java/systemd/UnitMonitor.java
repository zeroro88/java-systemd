/*
 * Java-systemd implementation
 * Copyright (c) 2016 Markus Enax
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of either the GNU Lesser General Public License Version 2 or the
 * Academic Free Licence Version 3.0.
 *
 * Full licence texts are included in the COPYING file with this program.
 */

package de.thjom.java.systemd;

import static de.thjom.java.systemd.Unit.Property.ACTIVE_STATE;
import static de.thjom.java.systemd.Unit.Property.LOAD_STATE;
import static de.thjom.java.systemd.Unit.Property.SUB_STATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.freedesktop.DBus.Properties.PropertiesChanged;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thjom.java.systemd.interfaces.ManagerInterface.Reloading;
import de.thjom.java.systemd.interfaces.ManagerInterface.UnitFilesChanged;

abstract class UnitMonitor extends AbstractAdapter implements UnitStateNotifier {

    protected static final String ERROR_MSG_MONITOR_REFRESH = "Error while refreshing internal monitor state";

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Manager manager;
    protected final ConcurrentMap<String, Unit> monitoredUnits = new ConcurrentHashMap<>();

    protected final List<UnitMonitorListener> unitMonitorListeners = new ArrayList<>();

    protected ReloadingHandler reloadingHandler;
    protected UnitFilesChangedHandler unitFilesChangedHandler;

    private Timer pollingTimer;

    protected UnitMonitor(final Manager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @Override
    public <T extends DBusSignal> void addHandler(final Class<T> type, final DBusSigHandler<T> handler) throws DBusException {
        manager.subscribe();
        manager.addHandler(type, handler);
    }

    @Override
    public <T extends DBusSignal> void removeHandler(final Class<T> type, final DBusSigHandler<T> handler) throws DBusException {
        manager.removeHandler(type, handler);
    }

    public void addDefaultHandlers() throws DBusException {
        manager.subscribe();

        reloadingHandler = new ReloadingHandler();
        manager.addConsumer(Reloading.class, reloadingHandler);

        unitFilesChangedHandler = new UnitFilesChangedHandler();
        manager.addConsumer(UnitFilesChanged.class, unitFilesChangedHandler);
    }

    public void removeDefaultHandlers() throws DBusException {
        manager.removeConsumer(Reloading.class, reloadingHandler);
        manager.removeConsumer(UnitFilesChanged.class, unitFilesChangedHandler);
    }

    @Override
    protected SignalConsumer<PropertiesChanged> createStateConsumer() {
        return new SignalConsumer<>(s -> {
            Optional<Unit> unit = getMonitoredUnit(Unit.extractName(s.getPath()));

            if (unit.isPresent()) {
                Map<String, Variant<?>> properties = s.changedProperties;

                if (properties.containsKey(ACTIVE_STATE) || properties.containsKey(LOAD_STATE) || properties.containsKey(SUB_STATE)) {
                    synchronized (unitStateListeners) {
                        unitStateListeners.forEach(l -> l.stateChanged(unit.get(), properties));
                    }
                }
            }
        });
    }

    public synchronized void addListener(final UnitMonitorListener listener) {
        unitMonitorListeners.add(listener);
    }

    public synchronized void removeListener(final UnitMonitorListener listener) {
        unitMonitorListeners.remove(listener);
    }

    public abstract void reset();

    public abstract void refresh() throws DBusException;

    public synchronized void startPolling(final long delay, final long period) {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                try {
                    refresh();
                }
                catch (final DBusException e) {
                    log.error(ERROR_MSG_MONITOR_REFRESH, e);
                }
            }

        };

        if (pollingTimer != null) {
            pollingTimer.cancel();
        }

        pollingTimer = createTimer();
        pollingTimer.schedule(task, delay, period);
    }

    public synchronized void stopPolling() {
        if (pollingTimer != null) {
            pollingTimer.cancel();
        }
    }

    protected Timer createTimer() {
        return new Timer(getClass().getSimpleName() + "-pollingTimer", true);
    }

    public boolean monitorsUnit(final String unitName) {
        return monitoredUnits.containsKey(Systemd.escapePath(unitName));
    }

    public Optional<Unit> getMonitoredUnit(final String unitName) {
        return Optional.ofNullable(monitoredUnits.get(Systemd.escapePath(unitName)));
    }

    public Collection<Unit> getMonitoredUnits() {
        return monitoredUnits.values();
    }

    public class ReloadingHandler implements DBusSigHandler<Reloading> {

        @Override
        public void handle(final Reloading signal) {
            if (signal.isActive()) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Signal received ('daemon-reload' started: %s)", signal));
                }
            }
            else {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Signal received ('daemon-reload' finished: %s)", signal));
                }

                try {
                    refresh();
                }
                catch (final DBusException e) {
                    log.error(ERROR_MSG_MONITOR_REFRESH, e);
                }
            }
        }

    }

    public class UnitFilesChangedHandler implements DBusSigHandler<UnitFilesChanged> {

        @Override
        public void handle(final UnitFilesChanged signal) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Signal received (unit files changed: %s)", signal));
            }

            try {
                refresh();
            }
            catch (final DBusException e) {
                log.error(ERROR_MSG_MONITOR_REFRESH, e);
            }
        }

    }

}
