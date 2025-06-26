package dom;

public class ClassPathDebugger {
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.err.println("Usage: java ClassPathDebugger <fully.qualified.ClassName>");
      System.exit(1);
    }
    String className = args[0];
    try {
      Class<?> clazz = Class.forName(className);
      if (clazz.getProtectionDomain().getCodeSource() != null) {
        System.out.println("[DEBUG] Class '" + className + "' is loaded from: " +
            clazz.getProtectionDomain().getCodeSource().getLocation());
      } else {
        System.out.println("[DEBUG] Class '" + className + "' is loaded from the bootstrap classpath (part of the JDK/JRE).");
      }
    } catch (ClassNotFoundException e) {
      System.err.println("[DEBUG] Class '" + className + "' not found on the classpath.");
    }
  }
}
