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

import java.util.Map;

import org.freedesktop.dbus.Variant;

@FunctionalInterface
public interface UnitStateListener {

    void stateChanged(Unit unit, Map<String, Variant<?>> changedProperties);

}
