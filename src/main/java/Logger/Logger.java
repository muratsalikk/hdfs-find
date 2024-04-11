package Logger;

public class Logger {
    public  final String className;

    public Logger(Class<?> clazz) {
        this.className = clazz.getName();
    }

    public void debug(String message) {
        if ("true".equals(System.getProperty("debug", "false"))) {
            System.out.println("[DEBUG] [" + className + "] " + message);
        }
    }

    public void info(String message) {
        System.out.println("[INFO] [" + className + "] " + message);
    }

    public void error(String message) {
        System.err.println("[ERROR] [" + className + "] " + message);
    }

    // You can add more methods for other log levels if needed.
}
