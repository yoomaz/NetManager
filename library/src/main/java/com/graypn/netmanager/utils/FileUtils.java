package com.graypn.netmanager.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created by graypn on 16/8/23.
 */
public final class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    /**
     * 获取文件后缀名
     */
    public static String getExtension(final String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (dot > 0) {
            return fileName.substring(dot + 1);
        } else {
            return null;
        }
    }

    /**
     * 获取文件名
     */
    public static String getBaseName(final String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (dot > 0) {
            return fileName.substring(0, dot);
        } else {
            return fileName;
        }
    }

    /**
     * 复制文件
     */
    public static void copyFile(File src, File dest) throws IOException {
        if (!dest.exists()) {
            dest.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(src).getChannel();
            destination = new FileOutputStream(dest).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * 删除文件，支持文件和文件夹，支持递归删除
     */
    public static boolean deleteFile(File file) {
        boolean result = false;
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File subFile : files) {
                    deleteFile(subFile);
                }
            }
        } else {
            result = file.delete();
        }
        return result;
    }

    /**
     * 保存文件
     */
    public static File saveFile(String filePath, String fileName, InputStream inputStream) {
        if (inputStream == null || TextUtils.isEmpty(filePath) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        File dirFile = new File(filePath);
        boolean mkdirsResult;
        if (!dirFile.exists()) {
            mkdirsResult = dirFile.mkdirs();
        } else {
            mkdirsResult = true;
        }
        if (mkdirsResult) {
            File file = new File(dirFile, fileName);
            FileOutputStream fos = null;
            byte[] buff = new byte[2048];
            int len;
            try {
                fos = new FileOutputStream(file);
                while ((len = inputStream.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
                fos.flush();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 文件转换为二进制数组
     */
    public static byte[] readFileIntoByte(File file) {
        if (file.exists()) {
            FileInputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new ByteArrayOutputStream(2048);
                byte[] cache = new byte[DEFAULT_BUFFER_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                }
                out.flush();
                return out.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new byte[0];
    }

    /**
     * 文件转换为二进制数组
     */
    public static byte[] readFileIntoByte(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            return readFileIntoByte(file);
        }
        return null;
    }

}
