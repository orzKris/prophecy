package com.kris.prophecy.controller;

import com.kris.prophecy.enums.UserErrorCode;
import com.kris.prophecy.model.common.util.Response;
import com.kris.prophecy.service.AttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 我的关注列表接口
     */
    @GetMapping(value = "/myConcerned", produces = "application/json;charset=UTF-8")
    public Response myConcerned(@RequestHeader("uid") String uid) {
        return attentionService.getMyConcerned(uid);
    }

}
