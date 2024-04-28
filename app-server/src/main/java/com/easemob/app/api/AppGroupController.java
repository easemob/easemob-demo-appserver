package com.easemob.app.api;

import com.easemob.app.model.ResCode;
import com.easemob.app.model.ResponseParam;
import com.easemob.app.service.AppGroupService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppGroupController {

    @Value("${application.appkey}")
    private String defaultAppKey;

    @Autowired
    private AppGroupService appGroupService;

    @GetMapping("/inside/app/group/{groupId}/avatarurl")
    public ResponseEntity getAppGroupAvatarUrl(@PathVariable("groupId") String groupId,
            @RequestParam(value = "appkey", required = false) String appKey) {

        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("GroupId cannot be empty.");
        }


        String groupAvatarUrl = appGroupService.getAvatarUrl(defaultAppKey, groupId);

        ResponseParam responseParam = new ResponseParam();
        responseParam.setCode(ResCode.RES_OK.getCode());
        responseParam.setAvatarUrl(groupAvatarUrl);

        return ResponseEntity.ok(responseParam);
    }

}