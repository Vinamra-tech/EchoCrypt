package com.vinamra.encrypt_decrypt_backend.util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;


public class QRCodeUtil {

    public static String extractKeyFromQRCode(File qrFile){

        try{

            BufferedImage image =ImageIO.read(qrFile);
            LuminanceSource source =new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
