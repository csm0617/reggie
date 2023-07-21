package com.csm.reggie.controller;

import com.csm.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("common")
public class CommonController {

    //导包问题
    //这个地方需要注意可能报错，我们用的是spring的框架应该导入的是spring相关的包，
    // 但是我们引入了lombok相关的包，可能引入的是import lombok.Value;导致报错
    //正确的导包应该是org.springframework.beans.factory.annotation.Value;
    @Value("${reggie.path}")
    private String bashPath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除

        //打断点或者日志输出检测到是否接收到了文件，并且还可以查看到文件的一些相关属性
        log.info("接收到的文件为:{}", file.toString());
        //transferTo文件传输至
        //1.优化 获取原始文件名
        String originalFilename = file.getOriginalFilename();//abc.jpg
        //截取获得文件后缀，如果不了解String类的方法可以去搜索一下复习一下
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //2.使用UUID重新生成文件名，防止文件名重复造成覆盖（这里按业务需求来，不想保存那么多文件占用硬盘大小就直接覆盖，需要记录就保存）
        String fileName = UUID.randomUUID().toString() + suffix;
        //3.
        //创建文件目录对象
        File dir = new File(bashPath);
        //判断当前文件夹是否存在
        if (!dir.exists()) {
            //当前文件夹不存在时自动创建
            dir.mkdir();
        }
        try {
            //将临时文件转存至保存的文件夹
//            file.transferTo(new File("D:\\hello.jpg"));
            //优化为文件路径从环境变量中读入
//            file.transferTo(new File(bashPath+"hello.jpg"));
            //优化保存文件名使用UUID
            file.transferTo(new File(bashPath + fileName));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //返回文件名给前端
        return R.success(fileName);
    }
}
