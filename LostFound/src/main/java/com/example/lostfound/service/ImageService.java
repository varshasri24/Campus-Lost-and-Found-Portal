package com.example.lostfound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.HexFormat;

@Service
public class ImageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    // save uploaded image and return relative path e.g. "uploads/123_file.jpg"
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String original = Path.of(file.getOriginalFilename()).getFileName().toString();
        String filename = System.currentTimeMillis() + "_" + original;
        Path filePath = uploadPath.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return uploadDir + "/" + filename;
    }

    // compute average hash aHash (8x8) and return hex string
    public String averageHash(String imagePath) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            if (img == null) return null;
            BufferedImage scaled = resize(img, 8, 8);
            int[] gray = new int[64];
            int sum = 0, idx = 0;
            for (int y=0;y<8;y++){
                for (int x=0;x<8;x++){
                    int rgb = scaled.getRGB(x, y);
                    int r = (rgb>>16)&0xff;
                    int g = (rgb>>8)&0xff;
                    int b = rgb & 0xff;
                    int lum = (r + g + b) / 3;
                    gray[idx++] = lum;
                    sum += lum;
                }
            }
            int avg = sum / 64;
            long bits = 0L;
            for (int i=0;i<64;i++){
                if (gray[i] >= avg) bits |= 1L << i;
            }
            String hex = HexFormat.of().formatHex(longToBytes(bits));
            return hex;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage resize(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    private byte[] longToBytes(long x) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte)((x >> (8*i)) & 0xff);
        }
        return b;
    }

    // Hamming distance between two hex hash strings (0..64)
    public int hammingDistanceHex(String hex1, String hex2) {
        if (hex1 == null || hex2 == null) return Integer.MAX_VALUE;
        try {
            byte[] b1 = HexFormat.of().parseHex(hex1);
            byte[] b2 = HexFormat.of().parseHex(hex2);
            int dist = 0;
            int len = Math.min(b1.length, b2.length);
            for (int i=0;i<len;i++) {
                dist += Integer.bitCount((b1[i]^b2[i]) & 0xff);
            }
            for (int i=len;i<Math.max(b1.length,b2.length);i++) dist += 8;
            return dist;
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }
}
