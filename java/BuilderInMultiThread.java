package com.lottery.crawler;

/**
 * Created by doublechen on 16/9/20.
 */
public class BuilderInMultiThread {
    static class MessageBuild {
        int id;
        int content;

        MessageBuild setId(int id) {
            this.id = id;
            return this;
        }

        MessageBuild setContent(int s) {
            content = s;
            return this;
        }

        Message build() {
            Message message = new Message();
            message.content = content;
            message.id = id;
            return message;
        }

        MessageBuild get(){
            return new MessageBuild();
        }

    }

    static class Message {
        int id;
        int content;

        @Override
        public String toString() {
            return "id[" + id + "]" + " " + content;
        }
    }

    // ①通过匿名内部类覆盖ThreadLocal的initialValue()方法，指定初始值
    private static ThreadLocal<MessageBuild> threadSafeBuild = new ThreadLocal<MessageBuild>() {
        @Override
        protected MessageBuild initialValue() {
            return new MessageBuild();
        }

    };

    private static MessageBuild builder = new MessageBuild();


    public static void main(String[] args) {
        BuilderInMultiThread sn = new BuilderInMultiThread();
        // ③ 3个线程共享sn，各自产生序列号
        for (int i = 0; i < 100; i++) {
            TestClient t1 = new TestClient(sn);
            t1.start();
        }
    }

    private static class TestClient extends Thread {
        private BuilderInMultiThread sn;

        public TestClient(BuilderInMultiThread sn) {
            this.sn = sn;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
               // Message message = sn.threadSafeBuild.get().setContent(i).setId(i).build();
               // Message message = sn.builder.get().setContent(i).setId(i).build();
                Message message = sn.builder.setContent(i).setId(i).build();
                if (message.content != message.id) {
                    System.out.println("hint!!" + message);
                }
            }
        }
    }
}