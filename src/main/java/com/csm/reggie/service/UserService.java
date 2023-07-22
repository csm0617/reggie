package com.csm.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csm.reggie.entity.User;

public interface UserService extends IService<User> {
    void sendMail(String to,String subject ,String text);
}
