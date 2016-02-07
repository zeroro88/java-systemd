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

package de.mnx.java.systemd;

import static de.mnx.java.systemd.Systemd.SYSTEMD_DBUS_NAME;

import java.util.Vector;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.Path;
import org.freedesktop.dbus.exceptions.DBusException;

import de.mnx.java.systemd.interfaces.UnitInterface;

public abstract class Unit extends InterfaceAdapter {

    public static final String SERVICE_NAME = SYSTEMD_DBUS_NAME + ".Unit";

    public enum Who {
        MAIN("main"),
        CONTROL("control"),
        ALL("all");

        private final String value;

        private Who(final String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public enum Mode {
        REPLACE("replace"),
        FAIL("fail"),
        ISOLATE("isolate"),
        IGNORE_DEPENDENCIES("ignore-dependencies"),
        IGNORE_REQUIREMENTS("ignore-requirements");

        private final String value;

        private Mode(final String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    private final Properties properties;

    protected Unit(final DBusConnection dbus, final UnitInterface iface) throws DBusException {
        super(dbus, iface);

        this.properties = Properties.create(dbus, iface.getObjectPath(), SERVICE_NAME);
    }

    @Override
    public UnitInterface getInterface() {
        return (UnitInterface) super.getInterface();
    }

    public void kill(final Who who, final int signal) {
        getInterface().kill(who.getValue(), signal);
    }

    public Path reload(final Mode mode) {
        return reload(mode.getValue());
    }

    public Path reload(final String mode) {
        return getInterface().reload(mode);
    }

    public Path reloadOrRestart(final Mode mode) {
        return reloadOrRestart(mode.getValue());
    }

    public Path reloadOrRestart(final String mode) {
        return getInterface().reloadOrRestart(mode);
    }

    public Path reloadOrTryRestart(final Mode mode) {
        return reloadOrTryRestart(mode.getValue());
    }

    public Path reloadOrTryRestart(final String mode) {
        return getInterface().reloadOrTryRestart(mode);
    }

    public void resetFailed() {
        getInterface().resetFailed();
    }

    public Path restart(final Mode mode) {
        return restart(mode.getValue());
    }

    public Path restart(final String mode) {
        return getInterface().restart(mode);
    }

    public Path start(final Mode mode) {
        return start(mode.getValue());
    }

    public Path start(final String mode) {
        return getInterface().start(mode);
    }

    public Path stop(final Mode mode) {
        return stop(mode.getValue());
    }

    public Path stop(final String mode) {
        return getInterface().stop(mode);
    }

    public Path tryRestart(final Mode mode) {
        return tryRestart(mode.getValue());
    }

    public Path tryRestart(final String mode) {
        return getInterface().tryRestart(mode);
    }

    public long getActiveEnterTimestamp() {
        return properties.getLong("ActiveEnterTimestamp");
    }

    public long getActiveEnterTimestampMonotonic() {
        return properties.getLong("ActiveEnterTimestampMonotonic");
    }

    public long getActiveExitTimestamp() {
        return properties.getLong("ActiveExitTimestamp");
    }

    public long getActiveExitTimestampMonotonic() {
        return properties.getLong("ActiveExitTimestampMonotonic");
    }

    public String getActiveState() {
        return properties.getString("ActiveState");
    }

    public Vector<String> getAfter() {
        return properties.getVector("After");
    }

    public Vector<String> getBefore() {
        return properties.getVector("Before");
    }

    public Vector<String> getBindsTo() {
        return properties.getVector("BindsTo");
    }

    public Vector<String> getBoundBy() {
        return properties.getVector("BoundBy");
    }

    public boolean isCanIsolate() {
        return properties.getBoolean("CanIsolate");
    }

    public boolean isCanReload() {
        return properties.getBoolean("CanReload");
    }

    public boolean isCanStart() {
        return properties.getBoolean("CanStart");
    }

    public boolean isCanStop() {
        return properties.getBoolean("CanStop");
    }

    public Vector<String> getConflicts() {
        return properties.getVector("Conflicts");
    }

    public String getDescription() {
        return properties.getString("Description");
    }

    public String getFragmentPath() {
        return properties.getString("FragmentPath");
    }

    public String getId() {
        return properties.getString("Id");
    }

    public String getLoadState() {
        return properties.getString("LoadState");
    }

    public Vector<String> getNames() {
        return properties.getVector("Names");
    }

    public boolean isNeedDaemonReload() {
        return properties.getBoolean("NeedDaemonReload");
    }

    public long getNetClass() {
        return properties.getLong("NetClass");
    }

    public Vector<String> getOnFailure() {
        return properties.getVector("OnFailure");
    }

    public String getOnFailureJobMode() {
        return properties.getString("OnFailureJobMode");
    }

    public Vector<String> getPartOf() {
        return properties.getVector("PartOf");
    }

    public Vector<String> getPropagatesReloadTo() {
        return properties.getVector("PropagatesReloadTo");
    }

    public boolean isRefuseManualStart() {
        return properties.getBoolean("RefuseManualStart");
    }

    public boolean isRefuseManualStop() {
        return properties.getBoolean("RefuseManualStop");
    }

    public Vector<String> getReloadPropagatedFrom() {
        return properties.getVector("ReloadPropagatedFrom");
    }

    public Vector<String> getRequiredBy() {
        return properties.getVector("RequiredBy");
    }

    public Vector<String> getRequires() {
        return properties.getVector("Requires");
    }

    public Vector<String> getRequiresMountsFor() {
        return properties.getVector("RequiresMountsFor");
    }

    public Vector<String> getRequisite() {
        return properties.getVector("Requisite");
    }

    public Vector<String> getRequisiteOf() {
        return properties.getVector("RequisiteOf");
    }

    public String getSourcePath() {
        return properties.getString("SourcePath");
    }

    public boolean isStopWhenUnneeded() {
        return properties.getBoolean("StopWhenUnneeded");
    }

    public String getSubState() {
        return properties.getString("SubState");
    }

    public boolean isTransient() {
        return properties.getBoolean("Transient");
    }

    public Vector<String> getTriggeredBy() {
        return properties.getVector("TriggeredBy");
    }

    public Vector<String> getTriggers() {
        return properties.getVector("Triggers");
    }

    public Vector<String> getWantedBy() {
        return properties.getVector("WantedBy");
    }

    public Vector<String> getWants() {
        return properties.getVector("Wants");
    }

    /**
     *
NAME                             TYPE      SIGNATURE RESULT/VALUE                             FLAGS
  .Kill                            method    si        -                                        -
  .Reload                          method    s         o                                        -
  .ReloadOrRestart                 method    s         o                                        -
  .ReloadOrTryRestart              method    s         o                                        -
  .ResetFailed                     method    -         -                                        -
  .Restart                         method    s         o                                        -
.SetProperties                   method    ba(sv)    -                                        -
  .Start                           method    s         o                                        -
  .Stop                            method    s         o                                        -
  .TryRestart                      method    s         o                                        -

  .ActiveEnterTimestamp            property  t         1454763448044889                         emits-change
  .ActiveEnterTimestampMonotonic   property  t         7533431                                  emits-change
  .ActiveExitTimestamp             property  t         0                                        emits-change
  .ActiveExitTimestampMonotonic    property  t         0                                        emits-change
  .ActiveState                     property  s         "active"                                 emits-change
  .After                           property  as        4 "systemd-journald.socket" "sysinit.... const
.AllowIsolate                    property  b         false                                    const
.AssertResult                    property  b         true                                     emits-change
.AssertTimestamp                 property  t         1454763448044462                         emits-change
.AssertTimestampMonotonic        property  t         7533004                                  emits-change
.Asserts                         property  a(sbbsi)  0                                        -
  .Before                          property  as        2 "shutdown.target" "multi-user.target"  const
  .BindsTo                         property  as        0                                        const
  .BoundBy                         property  as        0                                        const
  .CanIsolate                      property  b         false                                    const
  .CanReload                       property  b         true                                     const
  .CanStart                        property  b         true                                     const
  .CanStop                         property  b         true                                     const
.ConditionResult                 property  b         true                                     emits-change
.ConditionTimestamp              property  t         1454763448044461                         emits-change
.ConditionTimestampMonotonic     property  t         7533004                                  emits-change
.Conditions                      property  a(sbbsi)  0                                        -
.ConflictedBy                    property  as        0                                        const
  .Conflicts                       property  as        1 "shutdown.target"                      const
.ConsistsOf                      property  as        0                                        const
.DefaultDependencies             property  b         true                                     const
  .Description                     property  s         "Periodic Command Scheduler"             const
.Documentation                   property  as        0                                        const
.DropInPaths                     property  as        0                                        const
.Following                       property  s         ""                                       -
  .FragmentPath                    property  s         "/usr/lib/systemd/system/cronie.service" const
  .Id                              property  s         "cronie.service"                         const
.IgnoreOnIsolate                 property  b         false                                    const
.InactiveEnterTimestamp          property  t         0                                        emits-change
.InactiveEnterTimestampMonotonic property  t         0                                        emits-change
.InactiveExitTimestamp           property  t         1454763448044889                         emits-change
.InactiveExitTimestampMonotonic  property  t         7533431                                  emits-change
.Job                             property  (uo)      0 "/"                                    emits-change
.JobTimeoutAction                property  s         "none"                                   const
.JobTimeoutRebootArgument        property  s         ""                                       const
.JobTimeoutUSec                  property  t         0                                        const
.JoinsNamespaceOf                property  as        0                                        const
.LoadError                       property  (ss)      "" ""                                    const
  .LoadState                       property  s         "loaded"                                 const
  .Names                           property  as        1 "cronie.service"                       const
  .NeedDaemonReload                property  b         false                                    const
  .NetClass                        property  u         0                                        -
  .OnFailure                       property  as        0                                        const
  .OnFailureJobMode                property  s         "replace"                                const
  .PartOf                          property  as        0                                        const
  .PropagatesReloadTo              property  as        0                                        const
  .RefuseManualStart               property  b         false                                    const
  .RefuseManualStop                property  b         false                                    const
  .ReloadPropagatedFrom            property  as        0                                        const
  .RequiredBy                      property  as        0                                        const
  .Requires                        property  as        2 "sysinit.target" "system.slice"        const
  .RequiresMountsFor               property  as        0                                        const
  .Requisite                       property  as        0                                        const
  .RequisiteOf                     property  as        0                                        const
  .SourcePath                      property  s         ""                                       const
  .StopWhenUnneeded                property  b         false                                    const
  .SubState                        property  s         "running"                                emits-change
  .Transient                       property  b         false                                    const
  .TriggeredBy                     property  as        0                                        const
  .Triggers                        property  as        0                                        const
  .UnitFilePreset                  property  s         "disabled"                               -
  .UnitFileState                   property  s         "enabled"                                -
  .WantedBy                        property  as        1 "multi-user.target"                    const
  .Wants                           property  as        0                                        const

     */

}