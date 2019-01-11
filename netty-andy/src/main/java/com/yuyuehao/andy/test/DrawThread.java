package com.yuyuehao.andy.test;

/**
 * Created by Wang
 * on 2019-01-11
 */

public class DrawThread extends Thread{

    //取钱的用户
    private Account account;

    //当前线程所希望取的钱
    private double drawAmount;

    public DrawThread(String name,Account account,double drawAmount){
        super(name);
        this.account=account;
        this.drawAmount=drawAmount;
    }

    @Override
    public void run() {
        synchronized (account) {
            if (account.getBalance() >= drawAmount) {
                System.out.println(getName() + "取钱成功！ 取出=" + drawAmount);
         /*   try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
                //修改金额
                account.setBalance((account.getBalance() - drawAmount));
                System.out.println("\t 余额为：" + account.getBalance());
            } else {
                System.out.println(getName() + "取钱失败，账号余额不足！");
            }
        }
    }


}
