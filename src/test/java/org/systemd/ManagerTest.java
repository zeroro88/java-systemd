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

package org.systemd;

import org.freedesktop.dbus.exceptions.DBusException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ManagerTest extends AbstractTestCase {

    @Override
    @BeforeClass
    public void setup() {
        super.setup();

        setupPropertyMocks(Manager.class, Manager.SERVICE_NAME, Manager.Property.getAllNames());
    }

    @Test(description="Tests basic manager accessibility.")
    public void testAccess() {
        Manager manager = null;

        try {
            manager = systemd.getManager();
        }
        catch (DBusException e) {
            Assert.fail(e.getMessage(), e);
        }

        Assert.assertNotNull(manager);
    }

    @Test(description="Tests property access of manager interface.")
    public void testProperties() {
        Manager manager = null;

        try {
            manager = systemd.getManager();
        }
        catch (DBusException e) {
            Assert.fail(e.getMessage(), e);
        }

        for (String propertyName : Manager.Property.getAllNames()) {
            Object value = manager.getProperties().getVariant(propertyName).getValue();

            Assert.assertNotNull(value);
        }
    }

}
