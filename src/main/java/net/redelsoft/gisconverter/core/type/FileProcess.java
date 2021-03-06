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
package net.redelsoft.gisconverter.core.type;

import net.redelsoft.gisconverter.core.FeatureFactory;
import org.geotools.data.FeatureSource;

import java.io.File;

/**
 * File Formats Abstract class for read and write features
 * <p>
 * <ul>
 * <li>readFile reads file and returns the FeatureSource
 * <li>writeFile writes the given FeatureSource to given file 
 * </ul>
 *
 * @author Emre Demir
 */
public abstract class FileProcess {
    
    protected FeatureFactory ff = null;

    public FileProcess() {
        ff = FeatureFactory.getInstance();
    }
    
    /**
     *
     * @param file the file parameter will be read, not null
     * @return the FeatureSource, null if not processed
     */
    public abstract FeatureSource readFile(File file);

     /**
     *
     * @param fs the FeatureSouce where the features and properties have, not null
     * @param file the file where the features will be written, not null
     */
    public abstract void writeFile(FeatureSource fs, File file);
    
    public FeatureFactory getFeatureFactory(){
        return ff;
    }
}
