public class SimpleThreadLocalDemo {
    
    // 创建一个ThreadLocal，每个线程都有自己的计数器
    private static ThreadLocal<Integer> counter = new ThreadLocal<>();
    
    public static void main(String[] args) {
        // 创建3个线程
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                // 每个线程设置自己的初始值
                counter.set(0);
                
                // 每个线程给自己的计数器加5次
                for (int j = 0; j < 5; j++) {
                    Integer current = counter.get();
                    counter.set(current + 1);
                    System.out.println(Thread.currentThread().getName() + 
                                     " 的计数器: " + counter.get());
                }
                
                // 最后清理
                counter.remove();
            }, "线程-" + i).start();
        }
    }
}