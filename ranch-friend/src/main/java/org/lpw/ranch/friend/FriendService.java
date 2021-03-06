package org.lpw.ranch.friend;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author lpw
 */
public interface FriendService {
    /**
     * 好友信息是否存在验证器Bean名称。
     */
    String VALIDATOR_EXISTS = FriendModel.NAME + ".validator.exists";

    /**
     * 检索当前用户好友列表。
     *
     * @param state 状态：0-待对方确认；1-待己方确认；2-已通过；3-已拒绝/拉黑。
     * @return 当前用户好友列表；如果未找到则返回空集。
     */
    JSONArray query(int state);

    /**
     * 获取已验证通过的好友信息集。
     *
     * @param ids 好友ID集。
     * @return 好友信息集。
     */
    JSONObject get(String[] ids);

    /**
     * 添加好友。
     *
     * @param user 好友ID。
     * @param memo 请求备注。
     */
    void create(String user, String memo);

    /**
     * 通过好友审核。
     *
     * @param user 好友ID。
     * @param memo 备注。
     */
    void pass(String user, String memo);

    /**
     * 设置好友备注。
     *
     * @param user 好友ID。
     * @param memo 备注。
     */
    void memo(String user, String memo);

    /**
     * 拒绝好友申请。
     *
     * @param user 好友ID。
     */
    void refuse(String user);

    /**
     * 将好友加入黑名单（拉黑）。
     *
     * @param user 好友ID。
     */
    void blacklist(String user);

    /**
     * 查找好友信息。
     *
     * @param user 用户ID。
     * @return 好友信息；如果不存在则返回null。
     */
    FriendModel find(String user);
}
