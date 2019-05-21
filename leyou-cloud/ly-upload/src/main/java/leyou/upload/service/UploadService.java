package leyou.upload.service;

import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UploadService {

    private static final List<String> allowTypes = Arrays.asList("image/jpeg","image/png","image/bmp");
    public String uploadImage(MultipartFile file) {

        try {
            //校验文件类型
            String contentType = file.getContentType();
            System.out.println(contentType);
            if(!allowTypes.contains(contentType)){
                throw new LyException(ExceptionEnum.UPLOAD_TYPE_ERROR);
            }

            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null){
                throw new LyException(ExceptionEnum.UPLOAD_TYPE_ERROR);
            }

            //获取文件名
            String fileName = file.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成文件名
            fileName = UUID.randomUUID()+suffixName;
            //指定本地文件夹存储图片
            String filePath = "E:/code/java_oncedemo/leyou/leyou-cloud/ly-upload/src/main/resources/static/upload/";

            //准备目标路径
            File dest = new File(filePath+fileName);
            //保存文件到本地
            file.transferTo(dest);

            //返回路径
            return "http://127.0.0.1:8082/static/upload/"+fileName;
        } catch (IOException e) {
            //上传失败
            log.error("上传失败");
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }

    }
}
