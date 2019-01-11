package com.yuyuehao.andy.test;

/**
 * Created by Wang
 * on 2019-01-11
 */

public class Test {

    public static void main(String[] args) {
        Account account = new Account("123456",1000);
        DrawThread a = new DrawThread("A",account,800);
        a.start();
        DrawThread b = new DrawThread("B",account,800);
        b.start();
    }
}
