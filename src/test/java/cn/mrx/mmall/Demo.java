package cn.mrx.mmall;

import cn.mrx.mmall.util.BigDecimalUtil;
import cn.mrx.mmall.util.FTPUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
//import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author：Mr.X
 * Date：2017/11/20 14:09
 * Description：
 */
public class Demo {

    @Test
    public void test() {
        File file = new File("C:\\Users\\Administrator\\Desktop\\TODO.txt");
        try {
            boolean result = FTPUtil.uploadFile(Lists.newArrayList(file));
            if (result) System.out.println("上传成功");
            else System.out.println("上传失败");
        } catch (IOException e) {
            System.out.println("上传发生异常");
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        System.out.println(0.05 + 0.01);
        System.out.println(1.0 - 0.42);
        System.out.println(4.015 * 100);
        System.out.println(123.3 / 100);
    }

    @Test
    public void test3() {
        System.out.println(BigDecimalUtil.add(0.05666, 0.01));
        System.out.println(BigDecimalUtil.sub(1.0, 0.42));
        System.out.println(BigDecimalUtil.mul(2.3366, 100));
        System.out.println(BigDecimalUtil.div(10, 3));
    }

    @Test
    public void test4() {
        List<String> strList1 = new ArrayList<>();
        List<String> strList2 = Lists.newArrayList();

        List<List<Map<String, String>>> list1 = new ArrayList<>();
        List<List<Map<String, String>>> list2 = Lists.newArrayList();

        Map<String, Map<String, String>> map1 = new HashMap<>();
        Map<String, Map<String, String>> map2 = Maps.newHashMap();
    }

//    @Test
//    public void test5() {
//        String pwd = "111111";
//        System.out.println(DigestUtils.md5DigestAsHex(pwd.getBytes()));
//    }

    @Test
    public void test6() {
        String pwd = "111111";
        System.out.println(DigestUtils.md5Hex(pwd));
        System.out.println(DigestUtils.sha1Hex(pwd));
        System.out.println(DigestUtils.sha256(pwd));

        // Base64加密
        String encodeBase64 = new String(Base64.encodeBase64(pwd.getBytes()));
        System.out.println(encodeBase64);
        // Base64解密
        String decodeBase64 = new String(Base64.decodeBase64(encodeBase64));
        System.out.println(decodeBase64);
    }
}
