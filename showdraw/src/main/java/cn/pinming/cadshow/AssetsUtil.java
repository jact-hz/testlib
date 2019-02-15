package cn.pinming.cadshow;

import android.content.res.AssetManager;
import android.util.Log;

import com.weqia.utils.L;
import com.weqia.utils.datastorage.file.FileUtil;
import com.weqia.utils.datastorage.file.NativeFileUtil;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

/**
 * Created by berwin on 2017/3/17.
 */

public class AssetsUtil {

    public static double formetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.000");
        double fileSizeLong = 0.0D;
        switch(sizeType) {
            case 1:
                fileSizeLong = Double.valueOf(df.format((double)fileS)).doubleValue();
                break;
            case 2:
                fileSizeLong = Double.valueOf(df.format((double)fileS / 1024.0D)).doubleValue();
                break;
            case 3:
                fileSizeLong = Double.valueOf(df.format((double)fileS / 1048576.0D)).doubleValue();
                break;
            case 4:
                fileSizeLong = Double.valueOf(df.format((double)fileS / 1.073741824E9D)).doubleValue();
        }

        return fileSizeLong;
    }

    /**
     * Copies file from asset manager to external storage directory
     *
     * @param assetManager      AssetManager containing resources
     * @param fileName          Relative path to file in asset manager
     * @param targetDirName     External storage directory name
     * @param overwriteExisting Pass true to overwrite existing file
     */
    public static void copyAssetFile(AssetManager assetManager, String fileName, String targetDirName, boolean overwriteExisting) {
        File file = new File(targetDirName, fileName);
        if (!overwriteExisting && file.exists())
            return;
        try {
            InputStream istream = assetManager.open(fileName);
            FileUtil.createOrReadFile(file.getAbsolutePath());
            OutputStream ostream = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buffer = new byte[1024];
            int len = istream.read(buffer);
            while (len != -1) {
                ostream.write(buffer, 0, len);
                len = istream.read(buffer);
            }
            ostream.close();
            istream.close();
        } catch (FileNotFoundException e) {
            L.e("File not found in asset manager: " + fileName, e);
        } catch (IOException e) {
            L.e("IOException", e);
        }
    }

    /**
     * Copies file or directory from asset manager to external storage directory.
     *
     * @param assetManager      AssetManager containing resources
     * @param path              Relative path to file or directory in asset manager
     * @param targetDirName     External storage directory name
     * @param overwriteExisting Pass true to overwrite existing file
     */
    public static void copyAsset(AssetManager assetManager, String path, String targetDirName, boolean overwriteExisting) {
        try {
            // If path is a file, assets will be null or 0 length.
            String[] assets = assetManager.list(path);

            if (assets == null || assets.length == 0) {
                copyAssetFile(assetManager, path, targetDirName, overwriteExisting);
            } else {
                File targetDir = new File(targetDirName, path);
                NativeFileUtil.createFolder(targetDir.getAbsolutePath());

                for (String subDirName : assets) {
                    copyAsset(assetManager, path + "/" + subDirName, targetDirName, overwriteExisting);
                }
            }

        } catch (IOException e) {
            Log.e("ViewerUtils", "IOException", e);
        }
    }
    /**
     * 获取文件的MD5校验码
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    public static String getFileMD5ToString(final String filePath) {
        File file = isSpace(filePath) ? null : new File(filePath);
        return getFileMD5ToString(file);
    }


    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    public static String getFileMD5ToString(final File file) {
        return bytes2HexString(getFileMD5(file));
    }
    /**
     * 获取文件的MD5校验码
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    public static byte[] getFileMD5(final File file) {
        if (file == null) return null;
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) break;
            }
            md = dis.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(dis);
        }
        return null;
    }

    /**
     * 关闭IO
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }


}
