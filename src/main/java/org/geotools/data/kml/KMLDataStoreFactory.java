/*
 * The MIT License
 *
 * Copyright 2020 Emre Demir.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.geotools.data.kml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;

/**
 * DataStore factory that creates {@linkplain KMLDataStore}s
 *
 * @author NielsCharlier, Scitus Development
 */
public class KMLDataStoreFactory implements DataStoreFactorySpi {

    public static final Param FILE = new Param("file", File.class, "Property file", true);

    public static final Param NAMESPACE =
            new Param("namespace", String.class, "namespace of datastore", false);

    @Override
    public DataStore createDataStore(Map params) throws IOException {
        File file = fileLookup(params);
        String namespaceURI = (String) NAMESPACE.lookUp(params);
        if (file.exists() && !file.isDirectory()) {
            return new KMLDataStore(file, namespaceURI);
        } else {
            throw new IOException("Existing file is required");
        }
    }

    @Override
    public DataStore createNewDataStore(Map params) throws IOException {
        File file = (File) FILE.lookUp(params);

        if (file.exists()) {
            throw new IOException(file + " already exists");
        }

        String namespaceURI = (String) NAMESPACE.lookUp(params);
        return new KMLDataStore(file, namespaceURI);
    }

    @Override
    public String getDisplayName() {
        return "KML Feature Store";
    }

    @Override
    public String getDescription() {
        return "Allows access to KML files containing Feature information (ignores styles)";
    }

    /**
     * @see #DIRECTORY
     * @see KMLDataStoreFactory#NAMESPACE
     */
    @Override
    public Param[] getParametersInfo() {
        return new Param[] {FILE, NAMESPACE};
    }

    /**
     * Test to see if this datastore is available, if it has all the appropriate libraries to
     * construct a datastore. This datastore just returns true for now. This method is used for gui
     * apps, so as to not advertise data store capabilities they don't actually have.
     *
     * @return <tt>true</tt> if and only if this factory is available to create DataStores.
     * @task <code>true</code> property datastore is always available
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Works for a file directory or property file
     *
     * @param params Connection parameters
     * @return true for connection parameters indicating a directory or property file
     */
    @Override
    public boolean canProcess(Map params) {
        File file;
        try {
            file = (File) FILE.lookUp(params);
        } catch (IOException e) {
            return false;
        }
        if (file != null) {
            String name = file.getName();
            int index = name.lastIndexOf('.');
            if (index == -1) {
                return false;
            }
            if (name.substring(index + 1).equalsIgnoreCase("kml")) {
                return true;
            }
        }
        return false;
    }

    /** No implementation hints are provided at this time. */
    @Override
    public Map getImplementationHints() {
        return java.util.Collections.EMPTY_MAP;
    }

    /**
     * Lookups the property file in the params argument, and returns the corresponding <code>
     * java.io.File</code>.
     *
     * <p>The file is first checked for existence as an absolute path in the filesystem. If such a
     * directory is not found, then it is treated as a relative path, taking Java system property
     * <code>"user.dir"</code> as the base.
     *
     * @throws IllegalArgumentException if file is a directory.
     * @throws FileNotFoundException if directory does not exists
     * @throws IOException if {@linkplain #DIRECTORY} doesn't find parameter in <code>params</code>
     *     file does not exists.
     */
    private File fileLookup(Map params)
            throws IOException, FileNotFoundException, IllegalArgumentException {
        File file = (File) FILE.lookUp(params);
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IllegalArgumentException(
                        "Property file is required (not a directory) " + file.getAbsolutePath());
            }
            return file;
        } else {
            File dir = file.getParentFile();
            if (dir == null || !dir.exists()) {
                // quickly check if it exists relative to the user directory
                File currentDir = new File(System.getProperty("user.dir"));

                File file2 = new File(currentDir, file.getPath());
                if (file2.exists()) {
                    return file2;
                }
            }
            return file;
        }
    }
}