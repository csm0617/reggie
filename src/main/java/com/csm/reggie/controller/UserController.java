package com.csm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csm.reggie.common.R;
import com.csm.reggie.entity.User;
import com.csm.reggie.service.UserService;
import com.csm.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user) {
        log.info(user.toString());
        //获取邮箱号
        String email = user.getPhone();
        String subject = "瑞吉外卖";

        if (StringUtils.isNotEmpty(email)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String text = "【瑞吉外卖】你好，您的登录验证码为：【{}】" + code + ",请尽快登录";
            log.info("验证码为：" + code);
            userService.sendMail(email, subject, text);

            //将生成的验证码保存到session中，在登录校验的时候，取出
            session.setAttribute(email, code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送异常，请重新发送");
    }


    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map) {

        //可以通过打断点或者是日志输出看能不能用map接收到传过来phone和code
        log.info("登录的手机号和验证码为：{}",map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        String sessionCode = (String) session.getAttribute(phone);

        //取session中保存验证码code和提交过来的Map
        if (StringUtils.isNotEmpty(sessionCode) && sessionCode.equals(code)) {
            //先查数据库，如果数据中不存在这个邮箱就自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StringUtils.isNotEmpty(phone), User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                //如果不存在这个用户就
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                //截取邮箱前五位作为用户名
                user.setName(phone.substring(0,6));
                userService.save(user);
            }

            session.setAttribute("user", Long.valueOf(user.getName()));
            return R.success(user);
        }
        return R.error("验证码错误，登录失败");

    }
}
