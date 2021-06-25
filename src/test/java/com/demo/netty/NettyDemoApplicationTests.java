package com.demo.netty;

import com.demo.netty.utils.HexUtil;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


@SpringBootTest
class NettyDemoApplicationTests {

    public static void main(String[] args) {
        try {
            Socket s = new Socket("127.0.0.1", 8081);

            OutputStream os = s.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            while (true) {
                Scanner sc = new Scanner(System.in);
                String str = sc.next();
                if ("!".equals(str)) {
                    break;
                }
                dos.write(HexUtil.dumpHex(str));
            }
            dos.close();
            s.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
