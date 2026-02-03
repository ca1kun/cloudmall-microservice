public class NoThreadLocalDemo {
    
    // 普通的共享变量
    private static Integer sharedCounter = 0;
    
    public static void main(String[] args) {
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    sharedCounter = sharedCounter + 1;
                    System.out.println(Thread.currentThread().getName() + 
                                     " 看到的共享计数器: " + sharedCounter);
                }
            }, "线程-" + i).start();
        }
    }
}