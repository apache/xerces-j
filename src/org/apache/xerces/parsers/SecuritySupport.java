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

package org.apache.xerces.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * This class is duplicated for each subpackage so keep it in sync.
 * It is package private and therefore is not exposed as part of any API.
 *
 * @xerces.internal
 */
final class SecuritySupport {

  static ClassLoader getContextClassLoader() {
    try {
      return Thread.currentThread().getContextClassLoader();
    } catch (SecurityException ex) {
      return null;
    }
  }

  static ClassLoader getSystemClassLoader() {
    try {
      return ClassLoader.getSystemClassLoader();
    } catch (SecurityException ex) {
      return null;
    }
  }

  static ClassLoader getParentClassLoader(final ClassLoader cl) {
    if (cl == null) {
      return null;
    }
    try {
      ClassLoader parent = cl.getParent();
      // eliminate loops in case of the boot
      // ClassLoader returning itself as a parent
      return (parent == cl) ? null : parent;
    } catch (SecurityException ex) {
      return null;
    }
  }

  static String getSystemProperty(final String propName) {
    try {
      return System.getProperty(propName);
    } catch (SecurityException ex) {
      return null;
    }
  }

  static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
    return new FileInputStream(file);
  }

  static InputStream getResourceAsStream(final ClassLoader cl, final String name) {
    try {
      if (cl == null) {
        return ClassLoader.getSystemResourceAsStream(name);
      } else {
        return cl.getResourceAsStream(name);
      }
    } catch (SecurityException ex) {
      return null;
    }
  }

  static boolean getFileExists(final File f) {
    try {
      return f.exists();
    } catch (SecurityException ex) {
      return false;
    }
  }

  static long getLastModified(final File f) {
    try {
      return f.lastModified();
    } catch (SecurityException ex) {
      return 0L;
    }
  }

  private SecuritySupport () {}
}