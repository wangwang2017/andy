package com.yuyuehao.andy.test;

/**
 * Created by Wang
 * on 2019-01-11
 */

public class Account {

    private String accountNO;
    private double balance;

    public Account(){

    }

    public Account(String accountNO, double balance) {
        this.accountNO = accountNO;
        this.balance = balance;
    }


    public String getAccountNO() {
        return accountNO;
    }

    public void setAccountNO(String accountNO) {
        this.accountNO = accountNO;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public int hashCode(){
        return  accountNO.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj!=null&&obj.getClass()==Account.class){
            Account account= (Account) obj;
            return account.getAccountNO().equals(accountNO);
        }
        return false;
    }

}
