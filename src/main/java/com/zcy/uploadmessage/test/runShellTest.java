package com.zcy.uploadmessage.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class runShellTest {
    public static void main(String[] args) {
        try {
            String shpath="/home/yk-zcy/Desktop/testShell/t1.sh";
            Process ps = Runtime.getRuntime().exec(shpath);
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String result = sb.toString();
            System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
