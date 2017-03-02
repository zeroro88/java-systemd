/*
 * Java-systemd implementation
 * Copyright (c) 2016 Markus Enax
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of either the GNU Lesser General Public License Version 2 or the
 * Academic Free Licence Version 2.1.
 *
 * Full licence texts are included in the COPYING file with this program.
 */

package de.thjom.java.systemd;

import java.util.EnumSet;

import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.exceptions.DBusException;

import de.thjom.java.systemd.interfaces.ManagerInterface.Reloading;
import de.thjom.java.systemd.interfaces.ManagerInterface.UnitFilesChanged;
import de.thjom.java.systemd.interfaces.ManagerInterface.UnitNew;
import de.thjom.java.systemd.interfaces.ManagerInterface.UnitRemoved;
import de.thjom.java.systemd.types.UnitType;

public class UnitTypeMonitor extends UnitMonitor {

    public enum MonitoredType {
        AUTOMOUNT,
        BUSNAME,
        DEVICE,
        MOUNT,
        PATH,
        SCOPE,
        SERVICE,
        SLICE,
        SNAPSHOT,
        SOCKET,
        SWAP,
        TARGET,
        TIMER
    }

    protected final EnumSet<MonitoredType> monitoredTypes = EnumSet.noneOf(MonitoredType.class);

    private ReloadingHandler reloadingHandler;
    private UnitFilesChangedHandler unitFilesChangedHandler;
    private UnitNewHandler unitNewHandler;
    private UnitRemovedHandler unitRemovedHandler;

    public UnitTypeMonitor(final Manager manager) {
        super(manager);
    }

    @Override
    public final void addDefaultHandlers() throws DBusException {
        manager.subscribe();

        reloadingHandler = new ReloadingHandler();
        manager.addHandler(Reloading.class, reloadingHandler);

        unitFilesChangedHandler = new UnitFilesChangedHandler();
        manager.addHandler(UnitFilesChanged.class, unitFilesChangedHandler);

        unitNewHandler = new UnitNewHandler();
        manager.addHandler(UnitNew.class, unitNewHandler);

        unitRemovedHandler = new UnitRemovedHandler();
        manager.addHandler(UnitRemoved.class, unitRemovedHandler);
    }

    @Override
    public final void removeDefaultHandlers() throws DBusException {
        manager.removeHandler(Reloading.class, reloadingHandler);
        manager.removeHandler(UnitFilesChanged.class, unitFilesChangedHandler);
        manager.removeHandler(UnitNew.class, unitNewHandler);
        manager.removeHandler(UnitRemoved.class, unitRemovedHandler);
    }

    @Override
    public void refresh() throws DBusException {
        mapUnits();
    }

    public final void addMonitoredTypes(final MonitoredType... monitoredTypes) throws DBusException {
        for (MonitoredType monitoredType : monitoredTypes) {
            this.monitoredTypes.add(monitoredType);
        }

        mapUnits();
    }

    public final void removeMonitoredTypes(final MonitoredType... monitoredTypes) throws DBusException {
        for (MonitoredType monitoredType : monitoredTypes) {
            this.monitoredTypes.remove(monitoredType);
        }

        mapUnits();
    }

    protected final synchronized void mapUnits() throws DBusException {
        monitoredUnits.clear();

        for (UnitType unit : manager.listUnits()) {
            String name = unit.getUnitName();

            if (unit.isAutomount() && monitoredTypes.contains(MonitoredType.AUTOMOUNT)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getAutomount(name));
            }
            else if (unit.isBusName() && monitoredTypes.contains(MonitoredType.BUSNAME)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getBusName(name));
            }
            else if (unit.isDevice() && monitoredTypes.contains(MonitoredType.DEVICE)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getDevice(name));
            }
            else if (unit.isMount() && monitoredTypes.contains(MonitoredType.MOUNT)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getMount(name));
            }
            else if (unit.isPath() && monitoredTypes.contains(MonitoredType.PATH)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getPath(name));
            }
            else if (unit.isScope() && monitoredTypes.contains(MonitoredType.SCOPE)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getScope(name));
            }
            else if (unit.isService() && monitoredTypes.contains(MonitoredType.SERVICE)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getService(name));
            }
            else if (unit.isSlice() && monitoredTypes.contains(MonitoredType.SLICE)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getSlice(name));
            }
            else if (unit.isSnapshot() && monitoredTypes.contains(MonitoredType.SNAPSHOT)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getSnapshot(name));
            }
            else if (unit.isSocket() && monitoredTypes.contains(MonitoredType.SOCKET)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getSocket(name));
            }
            else if (unit.isSwap() && monitoredTypes.contains(MonitoredType.SWAP)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getSwap(name));
            }
            else if (unit.isTarget() && monitoredTypes.contains(MonitoredType.TARGET)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getTarget(name));
            }
            else if (unit.isTimer() && monitoredTypes.contains(MonitoredType.TIMER)) {
                monitoredUnits.put(Systemd.escapePath(name), manager.getTimer(name));
            }
        }
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
                    mapUnits();
                }
                catch (final DBusException e) {
                    log.error("Unable to map units", e);
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
                mapUnits();
            }
            catch (final DBusException e) {
                log.error("Unable to map units", e);
            }
        }

    }

    public class UnitNewHandler implements DBusSigHandler<UnitNew> {

        @Override
        public void handle(final UnitNew signal) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Signal received (unit new: %s)", signal));
            }

            try {
                mapUnits();
            }
            catch (final DBusException e) {
                log.error("Unable to map units", e);
            }
        }

    }

    public class UnitRemovedHandler implements DBusSigHandler<UnitRemoved> {

        @Override
        public void handle(final UnitRemoved signal) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Signal received (unit removed: %s)", signal));
            }

            try {
                mapUnits();
            }
            catch (final DBusException e) {
                log.error("Unable to map units", e);
            }
        }

    }

}