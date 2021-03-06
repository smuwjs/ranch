package org.lpw.ranch.group.member;

import org.lpw.tephra.dao.model.Jsonable;
import org.lpw.tephra.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.sql.Timestamp;

/**
 * @author lpw
 */
@Component(MemberModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = MemberModel.NAME)
@Table(name = "t_group_member")
public class MemberModel extends ModelSupport {
    static final String NAME = "ranch.group.member";

    private String group; // 群组ID
    private String user; // 用户ID
    private String nick; // 群组昵称
    private String reason; // 申请加入理由
    private int type; // 类型：0-待审核；1-普通成员；2-管理员；3-所有者
    private String introducer; // 介绍人ID
    private Timestamp join; // 加入时间

    @Jsonable
    @Column(name = "c_group")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Jsonable
    @Column(name = "c_user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Jsonable
    @Column(name = "c_nick")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Jsonable
    @Column(name = "c_reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Jsonable
    @Column(name = "c_type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Jsonable
    @Column(name = "c_introducer")
    public String getIntroducer() {
        return introducer;
    }

    public void setIntroducer(String introducer) {
        this.introducer = introducer;
    }

    @Jsonable
    @Column(name = "c_join")
    public Timestamp getJoin() {
        return join;
    }

    public void setJoin(Timestamp join) {
        this.join = join;
    }
}
