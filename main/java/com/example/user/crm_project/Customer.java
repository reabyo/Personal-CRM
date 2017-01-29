package com.example.user.crm_project;

import java.io.Serializable;

/**
 * Created by user on 3/26/2016.
 */
public class Customer implements Serializable{

    private String name, tel, mail, dynamic1, dynamic2, dynamic3, dynamic4, dynamic5;

    public Customer(String name, String tel, String mail, String dynamic1, String dynamic2, String dynamic3, String dynamic4,
                    String dynamic5){
        this.name = name;
        this.tel = tel;
        this.mail = mail;
        this.dynamic1 = dynamic1;
        this.dynamic2 = dynamic2;
        this.dynamic3 = dynamic3;
        this.dynamic4 = dynamic4;
        this.dynamic5 = dynamic5;
    }

    public Customer(String name, String tel, String mail) {
        this.tel = tel;
        this.name = name;
        this.mail = mail;
    }

    public String getName() {return name;}

    public String getTel() {
        return tel;
    }

    public String getMail() {
        return mail;
    }

    public String getDynamic1() {
        return dynamic1;
    }

    public String getDynamic2() {
        return dynamic2;
    }

    public String getDynamic3() {
        return dynamic3;
    }

    public String getDynamic4() {
        return dynamic4;
    }

    public String getDynamic5() {
        return dynamic5;
    }
}
