package com.poolik.websocket.callback.util;

import com.poolik.classfinder.ClassFinder;
import com.poolik.classfinder.filter.*;
import com.poolik.classfinder.info.ClassInfo;
import org.jboss.vfs.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class ClassUtils {

  private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

  public static Collection<ClassInfo> getImplementingInterface(Class<?> interfaceName) throws Exception {
    ClassFinder classFinder = new ClassFinder();
    addContentRootFolderIfAvailable(classFinder);

    String runningPath = getRunningPath(interfaceName);
    log.info("Running path: " + runningPath);
    if (runningInWar(runningPath)) {
      addIfNotNull(classFinder, getWebInfClassesDir(runningPath));
      addIfNotNull(classFinder, getLibraryDir(runningPath));
    }
    addIfNotNull(classFinder, convertRunningPath(runningPath));
    return classFinder.findClasses(getClassFilter(interfaceName));
  }

  private static File convertRunningPath(String runningPath) {
    try {
      return new File(new URL(runningPath).getPath());
    } catch (Exception e) {
      return null;
    }
  }

  private static void addContentRootFolderIfAvailable(ClassFinder classFinder) {
    URL resource = ClassUtils.class.getResource("/");
    if (resource != null) classFinder.add(new File(resource.getPath()));
  }

  private static void addIfNotNull(ClassFinder classFinder, File file) throws Exception {
    if (file != null) classFinder.add(file);
  }

  private static File getLibraryDir(String runningPath) {
    try {
      if (runningPath.contains("vfs:"))
        return resolveVirtualFileToConcrete(new URL(runningPath).openConnection(), true);
      if (runningPath.contains("jar:file:") && runningPath.contains("!")) {
        String path = new URL(runningPath).getPath();
        return new File(new URL(path.substring(0, path.indexOf("!"))).toURI());
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  private static File getWebInfClassesDir(String runningPath) {
    try {
      if (runningPath.contains("vfs:"))
        return resolveVirtualFileToConcrete(new URL(getWebInfClassesPath(runningPath)).openConnection(), false);

      if (runningPath.contains("jar:file:"))
        return new File(new URL(getWebInfClassesPath(new URL(runningPath).getPath())).toURI());
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  private static String getWebInfClassesPath(String path) {
    return path.substring(0, path.indexOf(File.separator + "WEB-INF")) + File.separator + "WEB-INF" + File.separator + "classes" + File.separator;
  }

  private static File resolveVirtualFileToConcrete(URLConnection conn, boolean getParent) throws IOException {
    VirtualFile vf = (VirtualFile) conn.getContent();
    File contentsFile = vf.getPhysicalFile();
    if (getParent) return new File(contentsFile.getParentFile().getAbsolutePath());
    return new File(contentsFile.getAbsolutePath());
  }

  private static boolean runningInWar(String runningPath) {
    return runningPath.contains("WEB-INF");
  }

  public static String getRunningPath(Class<?> interfaceName) {
    return interfaceName.getProtectionDomain().getCodeSource().getLocation().toString();
  }

  private static ClassFilter getClassFilter(Class<?> interfaceName) {
    return new AndClassFilter(new SubclassClassFilter(interfaceName), new NotClassFilter(new InterfaceOnlyClassFilter()),
        new NotClassFilter(new AbstractClassFilter()));
  }
}