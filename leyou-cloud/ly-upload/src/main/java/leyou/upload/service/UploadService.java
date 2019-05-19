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

            //准备目标路径
            File dest = new File("E:\\upload/",file.getOriginalFilename());
            //保存文件到本地
            file.transferTo(dest);

            //返回路径
            return "http://img.leyou.com/"+file.getOriginalFilename();
        } catch (IOException e) {
            //上传失败
            log.error("上传失败");
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }

    }
}
