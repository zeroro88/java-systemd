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

package de.thjom.java.systemd.types;

class IOPath {

    protected final String filePath;

    protected IOPath(final Object filePath) {
        this.filePath = String.valueOf(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

}
