package org.example;

import java.io.*;

public class Test {
    public static void main(String[] args) throws IOException {
        String file1 = "";
        String file2 = "";

        String folderPath = "src/main/resources/Temp"; // 替换为你要检查的文件夹路径

        String targetName = "MergeTemp";

        File folder = new File(folderPath);

        // If mergeTemp already exits, merge it with current lib
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().equals(targetName)) {
                    BufferedReader reader1 = new BufferedReader(new FileReader(folderPath + "/" + targetName));
                    // 创建输入流读取第二个文件
                    BufferedReader reader2 = new BufferedReader(new FileReader(file2));
                    // 创建输出流写入合并后的文件
                    BufferedWriter writer = new BufferedWriter(new FileWriter(folderPath + "/" + targetName));

                }
            }
        }else {
            BufferedReader reader1 = new BufferedReader(new FileReader(folderPath + "/" + targetName));
            // 创建输入流读取第二个文件
            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            // 创建输出流写入合并后的文件
            BufferedWriter writer = new BufferedWriter(new FileWriter(folderPath + "/" + targetName));
        }


    }
}
