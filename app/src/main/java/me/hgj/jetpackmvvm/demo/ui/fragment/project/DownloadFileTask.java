package me.hgj.jetpackmvvm.demo.ui.fragment.project;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileTask extends AsyncTask<String, Void, Void> {


    @Override
    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];
        String fileName = strings[2]; // 可以根据需要修改文件名
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(6000); // 设置连接超时时间
            connection.setReadTimeout(6000); // 设置读取超时时间
            // 获取文件长度
            int fileLength = connection.getContentLength();

            // 创建输入流
            InputStream input = connection.getInputStream();

            // 创建输出流，并指定保存路径
            String pngfilePath = strings[1] + fileName;

            FileOutputStream output = new FileOutputStream(pngfilePath);

            // 缓冲区大小
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;

            // 逐个读取数据并写入到文件中
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                // 可以在此处更新进度条
            }

            // 关闭流
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


