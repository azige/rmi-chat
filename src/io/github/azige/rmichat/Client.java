/*
 *        DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                    Version 2, December 2004
 *
 * Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 *            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package io.github.azige.rmichat;

import java.io.Serializable;
import java.util.Scanner;
import io.github.azige.rmichat.RmiChat.RmiChatListener;

/**
 * 演示用的Client端程序，必须先启动Server。
 * @author Azige
 */
public class Client{

    public static void main(String[] args){
        Chat chat = RmiChat.connectTo(new RmiChatListener(){

            @Override
            public void onConnected(){
                System.out.println("# connected.");
            }

            @Override
            public void onMessage(Serializable message){
                System.out.println("> " + message);
            }

            @Override
            public void onDisconnected(){
                System.out.println("# disconnected.");
                System.exit(0);
            }
        }, "localhost");
        System.out.println("# connected.");
        Scanner scanner = new Scanner(System.in);
        while (true){
            String line = scanner.nextLine();
            if (line.equals("exit")){
                chat.disconnect();
                System.out.println("# disconnected.");
                System.exit(0);
            }else{
                chat.sendMessage(line);
            }
        }
    }
}
