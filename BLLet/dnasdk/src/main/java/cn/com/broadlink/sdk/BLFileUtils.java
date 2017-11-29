package cn.com.broadlink.sdk;

import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *     
 * 项目名称：BLEControlAppV4    
 * 类名称：BLFileUtils    
 * 类描述：  文件处理工具类
 * 创建人：YeJing 
 * 创建时间：2015-3-23 上午9:19:50    
 * 修改人：Administrator    
 * 修改时间：2015-3-23 上午9:19:50    
 * 修改备注：    
 * @version     
 *
 */
public class BLFileUtils {

    /**
     * 保存字节流至文件
     * 
     * @param bytes
     *            字节流
     * @param file
     *            目标文件
     */
    public static final boolean saveBytesToFile(byte[] bytes, File file) {
        if (bytes == null) {
            return false;
        }

        ByteArrayInputStream bais = null;
        BufferedOutputStream bos = null;
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            bais = new ByteArrayInputStream(bytes);
            bos = new BufferedOutputStream(new FileOutputStream(file));

            int size;
            byte[] temp = new byte[1024];
            while ((size = bais.read(temp, 0, temp.length)) != -1) {
                bos.write(temp, 0, size);
            }

            bos.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bos = null;
            }
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bais = null;
            }
        }
        return false;
    }

    public static String getFileKeyByUrlNew(String url) {
        String result = null;

        Pattern pattern = Pattern.compile("mkey=(\\w+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            result = matcher.group(1);
            result = result.replaceAll("mkey=", "");
        }

        return result;
    }

    public static String getFileNameByUrlNew(String url) {
        if (isNewUrl(url)) {
            String result = null;

            Pattern pattern = Pattern.compile("fixedid=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                result = matcher.group(1);
                result = result.replaceAll("fixedid=", "");
                result = result + ".gz";
            }

            return result;
        } else {
            if (url.contains("interimid=")) {
                String result = null;

                Pattern pattern = Pattern.compile("interimid=(\\d+)");
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    result = matcher.group(1);
                    result = result.replaceAll("interimid=", "");
                    result = result + ".gz";
                }

                return result;
            } else {
                return getFileNameByUrl(url);
            }
        }
    }

    /**
     * 根据URL获取到下载的文件名称
     *
             * @param url 下载路径
     * @return 下载文件名称
     */
    public static String getFileNameByUrl(String url) {
        int postion = url.lastIndexOf("/");
        return url.substring(postion + 1);
    }

    public static boolean isNewUrl(String url) {
        return url.contains("fixedid=");
    }


    public static final byte[] readFileBytes(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // 把文件路径和文件名作为参数 告诉读取流
            fis = new FileInputStream(file);

            // 把文件读取流对象传递给缓存读取流对象
            bis = new BufferedInputStream(fis);

            // 获得缓存读取流开始的位置
//            int len = bis.read();
//            System.out.println("len=" + len);
            int len = 0;

            // 定义一个容量来盛放数据
            byte[] buf = new byte[4 * 1024];

            while ((len = bis.read(buf)) != -1) {
                // 如果有数据的话，就把数据添加到输出流
                //这里直接用字符串StringBuffer的append方法也可以接收
                baos.write(buf, 0, len);
            }

            // 把文件输出流的数据，放到字节数组
            buffer = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭所有的流
                baos.close();
                bis.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return buffer;
    }

    /**
     * 复制文件夹
     * 
     * @param sourceDir
     *            源文件
     * 
     * @param targetDir
     *            目标文件
     * 
     */
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * 复制文件
     * 
     * @param srcFile
     *            源文件
     * @param destFile
     *            目标文件
     */
    public static final boolean copyFile(File srcFile, File destFile) {
        if (!srcFile.exists()) {
            return false;
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            destFile.getParentFile().mkdirs();
            destFile.createNewFile();

            bis = new BufferedInputStream(new FileInputStream(srcFile));
            bos = new BufferedOutputStream(new FileOutputStream(destFile));

            int size;
            byte[] temp = new byte[1024];
            while ((size = bis.read(temp, 0, temp.length)) != -1) {
                bos.write(temp, 0, size);
            }

            bos.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bos = null;
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bis = null;
            }
        }
        return false;
    }

    /**
     * 将图片保存成文件
     *
     * @param  bitmap    
     *          位图
     *          
     *  @param  savePath    
     *          保存的路径      
     *
     */
    public static final void saveBitmapToFile(Bitmap bitmap, String savePath, String fileName) {
        try {
            File f = new File(savePath + File.separator + fileName);

            //创建文件的目录
            File filePath = new File(savePath);
            if(!filePath.exists()){
                filePath.mkdirs();
            }
      
            f.createNewFile();

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static final void saveStringToFile(String value, String fileName) {
        File file = new File(fileName);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(value);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹及下面的子文件
     * 
     * @param deletedFile
     * 
     */
    public static void deleteFile(File deletedFile) {
        if (deletedFile.exists()) { // 判断文件是否存在
            if (deletedFile.isFile()) { // 判断是否是文件
                deletedFile.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (deletedFile.isDirectory()) { // 否则如果它是一个目录
                File files[] = deletedFile.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
                //删除文件夹
            	deletedFile.delete();
            }
        }
    }

    /**
     * 读取文本文件内容
     * 
     * @param path
     * 			文件路径
     * @return
     */
	public static String readTextFileContent(String path) {
        if(path == null) return  null;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			StringBuffer result = new StringBuffer();
			String line = null;
		
			while ((line = br.readLine()) != null) {
				result.append(line).append("\r\n");
			}
			
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			br = null;
		}

		return null;
	}
}
