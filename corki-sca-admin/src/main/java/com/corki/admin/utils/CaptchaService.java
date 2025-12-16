package com.corki.admin.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.corki.common.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类
 *
 * @author Corki
 * @since 2025-12-16
 */
@Slf4j
@Component
public class CaptchaService {

    @Resource
    private RedisUtil redisUtil;
    /**
     * 验证码Redis key前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码过期时间（分钟）
     */
    private static final long CAPTCHA_EXPIRE_TIME = 5;

    /**
     * 验证码图片宽度
     */
    private static final int CAPTCHA_WIDTH = 200;

    /**
     * 验证码图片高度
     */
    private static final int CAPTCHA_HEIGHT = 60;

    /**
     * 验证码字符数
     */
    private static final int CAPTCHA_CODE_COUNT = 4;

    /**
     * 验证码干扰线数量
     */
    private static final int CAPTCHA_LINE_COUNT = 50;

    /**
     * 生成验证码
     *
     * @return CaptchaResponse 验证码响应对象
     */
    public CaptchaResponse generateCaptcha(String uuid) {
        // 创建线段干扰的验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(
                CAPTCHA_WIDTH, CAPTCHA_HEIGHT, CAPTCHA_CODE_COUNT, CAPTCHA_LINE_COUNT);

        // 获取验证码文本
        String code = lineCaptcha.getCode();

        // 存储到Redis
        String redisKey = CAPTCHA_KEY_PREFIX + uuid;
        redisUtil.set(redisKey, code, CAPTCHA_EXPIRE_TIME, TimeUnit.MINUTES);
//        stringRedisTemplate.opsForValue().set(redisKey, code, CAPTCHA_EXPIRE_TIME, TimeUnit.MINUTES);

        log.debug("生成验证码成功，UUID: {}, 验证码: {}", uuid, code);

        // 获取验证码图片
        BufferedImage image = lineCaptcha.getImage();

        return new CaptchaResponse(uuid, image, CAPTCHA_EXPIRE_TIME);
    }

    /**
     * 校验验证码
     *
     * @param uuid 验证码唯一标识
     * @param code 用户输入的验证码
     * @return true-验证成功，false-验证失败
     */
    public boolean verifyCaptcha(String uuid, String code) {
        if (uuid == null || uuid.isEmpty() || code == null || code.isEmpty()) {
            log.warn("验证码参数为空，UUID: {}, Code: {}", uuid, code);
            return false;
        }

        String redisKey = CAPTCHA_KEY_PREFIX + uuid;
        String storedCode = redisUtil.getString(redisKey);

        if (storedCode == null) {
            log.warn("验证码不存在或已过期，UUID: {}", uuid);
            return false;
        }

        // 验证码忽略大小写
        boolean isValid = storedCode.equalsIgnoreCase(code);

        if (isValid) {
            // 验证成功后删除验证码
            redisUtil.del(redisKey);
            log.debug("验证码校验成功，UUID: {}", uuid);
        } else {
            log.warn("验证码校验失败，UUID: {}, 输入: {}, 正确: {}", uuid, code, storedCode);
        }

        return isValid;
    }

    /**
     * 删除验证码
     *
     * @param uuid 验证码唯一标识
     */
    public void deleteCaptcha(String uuid) {
        if (uuid != null && !uuid.isEmpty()) {
            String redisKey = CAPTCHA_KEY_PREFIX + uuid;
            redisUtil.del(redisKey);
            log.debug("删除验证码，UUID: {}", uuid);
        }
    }

    /**
     * 验证码响应对象
     */
    @Getter
    public static class CaptchaResponse {
        private final String uuid;
        private final BufferedImage image;
        private final long expireTime;

        public CaptchaResponse(String uuid, BufferedImage image, long expireTime) {
            this.uuid = uuid;
            this.image = image;
            this.expireTime = expireTime;
        }

    }
}