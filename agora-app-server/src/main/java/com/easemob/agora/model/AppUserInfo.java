package com.easemob.agora.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "APP_USER_INFO")
public class AppUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_account")
    private String userAccount;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "easemob_user_name")
    private String easemobUserName;

    @Column(name = "easemob_user_id")
    private String easemobUserId;

    @Column(name = "agora_uid")
    private String agoraUid;
}
