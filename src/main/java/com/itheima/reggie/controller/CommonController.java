package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String BasePath;
    /**
     *文件上传
     * @param file
     * @return
     */

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        // file是一个临时文件，需要转存到其他位置，否则本次请求后文件会删除
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();
        //截取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUId重新设置文件名，防止文件名重复造成文件覆盖
        String fileName  = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(BasePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //创建一个目录
            dir.mkdirs();
        }
        try {
            //将临时文件转存到其他位置
            file.transferTo(new File(BasePath + fileName));
        }catch (IOException e){
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(BasePath + name));

            //输出流，通过输出流写回浏览器,在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            // 关闭资源流
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
