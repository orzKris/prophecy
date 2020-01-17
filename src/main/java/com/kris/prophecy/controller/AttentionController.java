package com.kris.prophecy.controller;

import com.github.pagehelper.PageHelper;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.AttentionService;
import com.kris.prophecy.utils.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kris
 * @date 2020/1/12
 */
@RestController
@RequestMapping("/attention")
public class AttentionController {

    @Autowired
    private AttentionService attentionService;

    /**
     * 关注用户操作接口, flag: 1-关注操作，0-取关操作
     */
    @GetMapping(value = "/concern", produces = "application/json;charset=UTF-8")
    public Response attention(@RequestParam("pid") String pid, @RequestParam("flag") int flag, @RequestHeader("uid") String uid) {
        if (flag != 1 && flag != 0) {
            return Response.error(UserErrorCode.PARAM_ERROR);
        }
        if (!attentionService.selectUid(pid)) {
            return Response.error(UserErrorCode.USER_NOT_EXIST);
        }
        return attentionService.attentionOperation(uid, pid, flag);
    }

    /**
     * 获取我的关注或我的粉丝列表接口,flag：1-我的关注，0-我的粉丝
     */
    @GetMapping(value = "/myConcernedOrFans", produces = "application/json;charset=UTF-8")
    public Response myConcernedOrFans(@RequestHeader("uid") String uid, @RequestParam("flag") int flag,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer pageSize) {
        if (flag != 1 && flag != 0) {
            return Response.error(UserErrorCode.PARAM_ERROR);
        }
        pageSize = (pageSize == null || pageSize < 0 ? 10 : pageSize);
        page = (page == null || page < 1 ? 1 : page);
        PageHelper.startPage(page, pageSize);
        List<User> users = attentionService.getMyConcernedOrFans(uid, flag);
        PageData<User> userPageData = new PageData<>(users, pageSize);
        return Response.ok(userPageData);
    }

}
