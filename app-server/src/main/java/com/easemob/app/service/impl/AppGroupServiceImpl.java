package com.easemob.app.service.impl;

import com.easemob.app.model.AppUserInfoNew;
import com.easemob.app.service.AppGroupService;
import com.easemob.app.service.AssemblyService;
import com.easemob.app.service.RestService;
import com.easemob.app.utils.GenerateGroupAvatarUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AppGroupServiceImpl implements AppGroupService {

    @Autowired
    private AssemblyService assemblyService;

    @Autowired
    private RestService restService;

    @Override public String getAvatarUrl(String appKey, String groupId) {
        String chatGroupCustom = restService.getChatGroupCustom(appKey, groupId);
        if (StringUtils.isNotBlank(chatGroupCustom)) {
            return chatGroupCustom;
        }

        List<String> groupMembers = restService.getChatGroupMembers(appKey, groupId);
        if (groupMembers != null && groupMembers.size() > 0) {
            List<String> groupMemberAvatarUrlList = new ArrayList<>();
            groupMembers.forEach(chatUserName -> {
                AppUserInfoNew appUserInfoNew = assemblyService.getAppUserInfoNewByChatUserName(appKey, chatUserName);
                if (appUserInfoNew == null) {
                    groupMemberAvatarUrlList.add("https://a1.easemob.com/default_avatar");
                } else {
                    String avatarUrl = appUserInfoNew.getAvatarUrl();
                    if (avatarUrl == null) {
                        groupMemberAvatarUrlList.add("https://a1.easemob.com/default_avatar");
                    } else {
                        groupMemberAvatarUrlList.add(avatarUrl);
                    }
                }
            });

            BufferedImage outImage;
            try {
                outImage = GenerateGroupAvatarUtil.getCombinationOfHead(restService, appKey, groupId, groupMemberAvatarUrlList);
            } catch (Exception e) {
                log.error("Generate group avatar failed, appKey: {}, groupId: {}", appKey, groupId, e);
                throw new IllegalArgumentException("Generate group avatar failed.");
            }


            String groupAvatarUrl;
            File tempFile;
            try {
                // 创建临时文件
                tempFile = File.createTempFile("tempImage", ".jpg");

                // 将BufferedImage对象写入临时文件
                ImageIO.write(outImage, "jpg", tempFile);
            } catch (IOException e) {
                log.error("Create temp file failed, appKey: {}, groupId: {}", appKey, groupId, e);
                throw new IllegalArgumentException("Upload group avatar failed.");
            }

            groupAvatarUrl = restService.uploadFile(appKey, groupId, tempFile);
            tempFile.delete();

            restService.updateGroupCustom(appKey, groupId, groupAvatarUrl);

            return groupAvatarUrl;
        } else {
            log.error("Group members is empty, appKey: {}, groupId: {}", appKey, groupId);
            throw new IllegalArgumentException("Group members is empty.");
        }
    }

}
