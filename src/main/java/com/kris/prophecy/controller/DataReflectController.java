package com.kris.prophecy.controller;

import com.kris.prophecy.enums.ServiceCode;
import com.kris.prophecy.entity.User;
import com.kris.prophecy.reflect.InvokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * @author by Kris on 2018/12/11.
 */
@RestController
@RequestMapping("/reflect")
public class DataReflectController {

    @Autowired
    private InvokeService invokeService;

    /**
     * 用户填充服务
     */
    @GetMapping(value = "/addUser", produces = "application/json;charset=UTF-8")
    public List<User> userReflect(HttpServletRequest request, @RequestHeader("uid") String uid, @RequestParam("frequency") long frequency) throws Throwable {
        String rid = UUID.randomUUID().toString().replace("-", "");
        Object result = invokeService.serviceInvoke(request, ServiceCode.ADD_USER_REFLECT, rid, frequency);
        List<User> userList = (List<User>) result;
        return userList;
    }

}
