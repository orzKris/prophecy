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
     * 关注用户接口
     */
    @GetMapping(value = "/concern",produces = "application/json;charset=UTF-8")
    public Response attention(@RequestParam("pid") String pid,@RequestHeader("uid") String uid){
        if (!attentionService.selectUid(pid)){
            return Response.error(UserErrorCode.USER_NOT_EXIST);
        }
        return attentionService.insertAttention(uid,pid);
    }


}
