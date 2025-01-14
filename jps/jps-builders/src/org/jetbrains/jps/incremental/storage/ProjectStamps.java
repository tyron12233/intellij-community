/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.jps.incremental.storage;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.jps.incremental.relativizer.PathRelativizerService;

import java.io.File;
import java.io.IOException;

/**
 * @author Eugene Zhuravlev
 */
public class ProjectStamps {
  public static final String PORTABLE_CACHES_PROPERTY = "org.jetbrains.jps.portable.caches";
  public static final boolean PORTABLE_CACHES = Boolean.getBoolean(PORTABLE_CACHES_PROPERTY);
  public static final boolean FORCE_DOWNLOAD_PORTABLE_CACHES = Boolean.getBoolean("org.jetbrains.jps.portable.caches.force.download");

  private static final Logger LOG = Logger.getInstance(ProjectStamps.class);

  private final StampsStorage<? extends StampsStorage.Stamp> myStampsStorage;

  public ProjectStamps(File dataStorageRoot,
                       BuildTargetsState targetsState,
                       PathRelativizerService relativizer) throws IOException {
    myStampsStorage = PORTABLE_CACHES
                      ? new FileStampStorage(dataStorageRoot, relativizer, targetsState)
                      : new FileTimestampStorage(dataStorageRoot, targetsState);
  }

  public StampsStorage<? extends StampsStorage.Stamp> getStampStorage() {
    return myStampsStorage;
  }

  public void clean() {
    myStampsStorage.wipe();
  }

  public void close() {
    try {
      myStampsStorage.close();
    }
    catch (IOException e) {
      LOG.error(e);
      FileUtil.delete(myStampsStorage.getStorageRoot());
    }
  }
}
