/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

/**
 * Verifies that classes belonging to the Xerces distribution are loaded by
 * the ClassLoader that loaded Xerces, not by a context ClassLoader which may
 * see a different copy of Xerces.
 */
public class ContextClassLoaderTest extends TestCase {

    private Thread fThread;
    private ClassLoader fOriginalClassLoader;
    private final List<URLClassLoader> fClassLoaders = new ArrayList<URLClassLoader>();
    private final List<File> fTempDirs = new ArrayList<File>();

    @Override
    protected void setUp() throws Exception {
        fThread = Thread.currentThread();
        fOriginalClassLoader = fThread.getContextClassLoader();
    }

    @Override
    protected void tearDown() throws Exception {
        fThread.setContextClassLoader(fOriginalClassLoader);
        for (URLClassLoader loader : fClassLoaders) {
            loader.close();
        }
        for (File dir : fTempDirs) {
            delete(dir);
        }
    }

    public void testFallbackClassUsesXercesClassLoader() throws Exception {
        assertNotNull(newDocumentBuilder(false));
    }

    public void testServiceProviderUsesXercesClassLoader() throws Exception {
        assertNotNull(newDocumentBuilder(true));
    }

    private Object newDocumentBuilder(boolean withServiceProvider) throws Exception {
        URL classes = DocumentBuilderFactoryImpl.class.getProtectionDomain().getCodeSource().getLocation();
        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();

        URLClassLoader xercesLoader = new URLClassLoader(new URL[] { classes }, parent);
        fClassLoaders.add(xercesLoader);

        URL[] contextUrls;
        if (withServiceProvider) {
            File dir = createTempDir();
            File services = new File(dir, "META-INF/services");
            assertTrue(services.mkdirs());
            File service = new File(services, "org.apache.xerces.xni.parser.XMLParserConfiguration");
            Files.write(service.toPath(),
                    "org.apache.xerces.parsers.XML11Configuration\n".getBytes(StandardCharsets.UTF_8));
            contextUrls = new URL[] { classes, dir.toURI().toURL() };
        } else {
            contextUrls = new URL[] { classes };
        }

        URLClassLoader contextLoader = new URLClassLoader(contextUrls, parent);
        fClassLoaders.add(contextLoader);
        fThread.setContextClassLoader(contextLoader);

        Class<?> factoryClass = Class.forName(DocumentBuilderFactoryImpl.class.getName(), true, xercesLoader);
        Object factory = factoryClass.newInstance();
        return factoryClass.getMethod("newDocumentBuilder").invoke(factory);
    }

    private File createTempDir() throws Exception {
        File dir = File.createTempFile("xerces-814-", "");
        assertTrue(dir.delete());
        assertTrue(dir.mkdirs());
        fTempDirs.add(dir);
        return dir;
    }

    private static void delete(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                delete(child);
            }
        }
        file.delete();
    }
}
