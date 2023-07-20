//package com.csm.reggie.generator;
//import com.baomidou.mybatisplus.generator.config.OutputFile;
//import com.baomidou.mybatisplus.generator.config.TemplateType;
//import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
//
//import java.util.Collections;
//
///**
// * @Author 快乐小柴
// * @Date 2022/10/19 10:42
// * @Version 1.0
// */
//public class FastAutoGenerator {
//    public static void main(String[] args) {
//        com.baomidou.mybatisplus.generator.FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/mydb?characterEncoding=utf-8&userSSL=false", "root", "123456")
//                .globalConfig(builder -> {
//                    builder.author("csm") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
//                            .fileOverride() // 覆盖已生成文件
//                            .outputDir("src/main/java"); // 指定输出目录
//                })
//                .packageConfig(builder -> {
//                    builder.parent("com.csm") // 设置父包名
//                            .moduleName("reggie") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "src/main/resources/com/csm/reggie/mapper")); // 设置mapperXml生成路径
//                })
//                .strategyConfig(builder -> {
//                    //用addExclude通过排除空表来生成全部的表
//                    builder.addExclude("")
//                            //.addInclude("m_user","m_role","m_user_role","m_role_menu","m_menu","m_fir_menu","m_sec_menu","m_menu_firmenu","m_firmenu_secmenu") // 设置需要生成的表名
//                            .addTablePrefix("m_", "t_"); // 设置过滤表前缀
//                })
//                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .templateEngine(new FreemarkerTemplateEngine())
//                //禁用模板生成哪些模板
//                .templateConfig(builder -> {
//                    builder.disable(TemplateType.CONTROLLER,TemplateType.SERVICEIMPL,TemplateType.SERVICE,TemplateType.MAPPER);
//                }) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .execute();
//    }
//}