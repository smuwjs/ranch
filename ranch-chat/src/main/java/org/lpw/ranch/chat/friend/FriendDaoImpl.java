package org.lpw.ranch.chat.friend;

import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.dao.orm.lite.LiteOrm;
import org.lpw.tephra.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository(FriendModel.NAME + ".dao")
class FriendDaoImpl implements FriendDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<FriendModel> query(String owner) {
        return liteOrm.query(new LiteQuery(FriendModel.class).where("c_owner=?"), new Object[]{owner});
    }

    @Override
    public FriendModel findById(String id) {
        return liteOrm.findById(FriendModel.class, id);
    }

    @Override
    public void save(FriendModel friend) {
        liteOrm.save(friend);
    }
}