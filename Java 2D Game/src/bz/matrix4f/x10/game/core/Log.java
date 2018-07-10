package bz.matrix4f.x10.game.core;

/**
 * Created by Matrix4f on 6/3/2016.
 */
public class Log {

    public static void print(Object text) {
        String threadName = Thread.currentThread().getName();
        StackTraceElement[] elements = new RuntimeException().getStackTrace();
        StackTraceElement element = null;
        for(int i = 2; i >= 0; i--)
        	try {
        		element = elements[i];
        		break;
        	} catch (ArrayIndexOutOfBoundsException e) {}
        String classname = element.getClassName();
        Class<?> cname = null;
        try {
            cname = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            System.out.println("[Thread:" + threadName + "] [" + Log.class.getSimpleName() + "] Invalid Reflector " +
                    "method: Class.forName(" + classname + ");");
            return;
        }

        System.out.println("[Thread:" + threadName + "][" + cname.getSimpleName() + "] " + text);
    }

    public static void err(String text) {
        String threadName = Thread.currentThread().getName();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[2];
        String classname = element.getClassName();
        Class<?> cname = null;
        try {
            cname = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            System.out.println("[Thread:" + threadName + "] [" + Log.class.getSimpleName() + "] Invalid Reflector " +
                    "method: Class.forName(" + classname + ");");
            return;
        }

        System.err.println("[ERROR][Thread:" + threadName + "][" + cname.getSimpleName() + "] " + text);
    }
}
