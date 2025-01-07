package me.hgj.jetpackmvvm.demo.ui.fragment.project;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtil {
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        FileInputStream fis = new FileInputStream(zipFilePath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();

        byte[] buffer = new byte[1024];
        int count;

        while (ze != null) {
            String fileName = ze.getName();
            String path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toString();
            String filePath = path + File.separator + fileName;
            File pngfilePath = new File(filePath);
            if (pngfilePath.exists()) {
                // 如果文件已存在，则先删除旧文件
                pngfilePath.delete();
            }
            FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
            while ((count = zis.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            zis.closeEntry();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        fis.close();
    }
}
