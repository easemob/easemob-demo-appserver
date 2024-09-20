package com.easemob.app.repository;

import com.easemob.app.model.AppUserOneToOneVideoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserOneToOneVideoInfoRepository extends JpaRepository<AppUserOneToOneVideoInfo, Long> {

    @Query(value = "select * from easemob_app_user_1v1_video_info where appkey = ? and id > ? order by id desc limit ?", nativeQuery = true)
    List<AppUserOneToOneVideoInfo> findAppUsers(String appKey, Long id, Integer limit);

    @Query(value = "select * from easemob_app_user_1v1_video_info where appkey = ? and chat_user_name = ?", nativeQuery = true)
    AppUserOneToOneVideoInfo findByAppkeyAndChatUserName(String appKey, String chatUserName);

    @Query(value = "select * from easemob_app_user_1v1_video_info where appkey = ? and agora_uid = ?", nativeQuery = true)
    AppUserOneToOneVideoInfo findByAppkeyAndAgoraUid(String appKey, String agoraUid);

    @Query(value = "select * from easemob_app_user_1v1_video_info where appkey = ? and phone_number = ?", nativeQuery = true)
    AppUserOneToOneVideoInfo findByAppkeyAndPhoneNumber(String appKey, String phoneNumber);

}
