package com.wx.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
public class QrCodeUtil {

    // 默认二维码尺寸
    private static final int DEFAULT_SIZE = 300;

    // 二维码前景色（黑色）
    private static final int BLACK = 0xFF000000;

    // 二维码背景色（白色）
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成二维码图片为 BufferedImage
     *
     * @param content 二维码内容（URL、文本等）
     * @param size    图片大小（默认 300x300）
     * @return BufferedImage 对象
     * @throws WriterException
     */
    public static BufferedImage generateQrCode(String content, int size) throws WriterException {
        // 设置二维码参数
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                getDecodeHints()
        );

        return toBufferedImage(bitMatrix);
    }

    /**
     * 生成二维码图片并保存到文件
     *
     * @param content 二维码内容
     * @param size    图片大小
     * @param file    输出文件
     * @throws IOException
     * @throws WriterException
     */
    public static void generateAndSaveQrCode(String content, int size, File file) throws IOException, WriterException {
        BufferedImage image = generateQrCode(content, size);
        ImageIO.write(image, "PNG", file);
    }

    /**
     * 生成二维码并返回 Base64 编码的图片数据（PNG格式）
     *
     * @param content 二维码内容
     * @param size    图片大小
     * @return Base64编码的图片字符串
     * @throws IOException
     * @throws WriterException
     */
    public static String generateQrCodeBase64(String content, int size) throws IOException, WriterException {
        BufferedImage image = generateQrCode(content, size);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * 将 BitMatrix 转换为 BufferedImage
     */
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }

        return image;
    }

    /**
     * 获取二维码解码配置
     */
    private static Map<EncodeHintType, Object> getDecodeHints() {
//        return ErrorCorrectionLevel.L; // 容错级别
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // 容错级别 L
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 编码格式
        return hints;
    }

    public static void main(String[] args) {
        try {
            String content = "weixin://wxpay/bizpayurl?pr=4bxkzdsz3";
            File outputFile = new File("D:\\qrcode.png");

            generateAndSaveQrCode(content, DEFAULT_SIZE, outputFile);
            System.out.println("二维码已生成并保存到：" + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
