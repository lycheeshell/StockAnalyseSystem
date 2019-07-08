package entity;

import javax.persistence.*;

/**
 * Created by lienming on 2017/6/4.
 */
@Entity
@Table( name = "user",schema = "",catalog ="")
public class UserEntity {
    private String name ;
    private String password ;

    @Id
    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name ;
    }

    @Basic
    @Column(name = "password" )
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password){
        this.password = password ;
    }

}
