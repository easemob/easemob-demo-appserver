package com.easemob.app.repository;

import com.easemob.app.model.AppUserInfoNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserInfoNewRepository extends JpaRepository<AppUserInfoNew, Long> {

    @Query(value = "select * from easemob_app_user_info_new where appkey = ? and chat_user_name = ?", nativeQuery = true)
    AppUserInfoNew findByAppkeyAndChatUserName(String appKey, String chatUserName);

    @Query(value = "select * from easemob_app_user_info_new where appkey = ? and phone_number = ?", nativeQuery = true)
    AppUserInfoNew findByAppkeyAndPhoneNumber(String appKey, String phoneNumber);

    @Query(value = "select * from easemob_app_user_info_new where appkey = ? and agora_uid = ?", nativeQuery = true)
    AppUserInfoNew findByAppkeyAndAgoraUid(String appKey, String agoraUid);

}
