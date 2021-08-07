package com.natymere;

import java.io.*;

/**
 * Singleton with eager initialization
 * <p>
 * Instance is created before any class actually asked for its instance or not.
 * <p>
 * Drawback:
 * waste memory, instance still live without being used.
 */
class EagerSingleton {
    private static volatile EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() {
    }

    public static EagerSingleton getInstance() {
        return instance;
    }
}

/**
 * Single with lazy initialization
 * <p>
 * Restrict the creation of the instance until it is requested for the first time.
 * <p>
 * Drawback:
 * If two threads read the instance == null, both threads will assume they must create an instance. They sequentially
 * go into synchronized block and create the instance. In the end we have two instances in our applicaiton.
 */
class LazySingleton {
    private static volatile LazySingleton instance = null;

    private LazySingleton() {
    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (LazySingleton.class) {
                instance = new LazySingleton();
            }
        }
        return instance;
    }
}

/**
 * Bill Pugh suggests to use static inner class.
 * <p>
 * The LazyHolder class will not be initialized until required.
 */
class BillPughSingleton {
    private BillPughSingleton() {
    }

    private static class LazyHolder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return LazyHolder.INSTANCE;
    }
}

/**
 * Conclusion
 * 1. Use BillPugh advice which is to use static inner class to hold the instance
 * 2. Add readResolve() to Singleton Objects to return value comes from the existing instance to ensure single instance
 * application wide.
 */
class DemoSingleton implements Serializable {
    private static final long serialVersionUID = 1L;
    private int i = 10;

    private DemoSingleton() {
    }

    private static class DemoSingletonHolder {
        public static final DemoSingleton INSTANCE = new DemoSingleton();
    }

    public static DemoSingleton getInstance() {
        return DemoSingletonHolder.INSTANCE;
    }

    protected Object readResolve() {
        return getInstance();
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}

public class Singleton {
    static DemoSingleton instanceOne = DemoSingleton.getInstance();

    public static void main(String[] args) {

        LazySingleton lazySingleton = LazySingleton.getInstance();
        EagerSingleton eagerSingleton = EagerSingleton.getInstance();
        BillPughSingleton billPughSingleton = BillPughSingleton.getInstance();

        try {
            // serialize to a file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("filename.ser"));
            out.writeObject(instanceOne);
            out.close();

            System.out.println(instanceOne);
            instanceOne.setI(20);

            // serialize to a file
            ObjectInput in = new ObjectInputStream(new FileInputStream("./filename.ser"));
            DemoSingleton instanceTwo = (DemoSingleton) in.readObject();
            in.close();

            System.out.println(instanceOne.getI());
            System.out.println(instanceTwo.getI());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
