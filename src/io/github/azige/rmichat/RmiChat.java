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
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 以Java RMI实现的Chat。
 *
 * @author Azige
 */
public class RmiChat implements Chat{

    private RmiChatListener listener;
    private RmiChatCommunication communication;

    private RmiChat(){
    }

    private RmiChat(RmiChatListener listener){
        this.listener = listener;
    }

    @Override
    public void sendMessage(String message){
        try{
            communication.sendMessage(message);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void disconnect(){
        try{
            communication.disconnect();
        }catch (Exception ex){
            // 对方断开连接会导致连接中断的异常
        }
    }

    /**
     * 启动一个监听程序，在本地9000端口注册RMI对象并等待连接。
     *
     * @param listener 聊天的事件监听器
     * @return 监听中的RmiChat对象
     */
    public static RmiChat startListen(RmiChatListener listener){
        try{
            RmiChat chat = new RmiChat(listener);
            // 在9000端口创建RMI注册表
            LocateRegistry.createRegistry(9000);
            // 将通信对象绑定到RMI注册表里
            Naming.bind("//localhost:9000/RmiChat", chat.new RmiChatCommunicationImpl());
            return chat;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 发起一个连接，连接到指定的地址并开始通信。
     *
     * @param listener 聊天的事件监听器
     * @param address 要连接的地址
     * @return 已连接的RmiChat对象
     */
    public static RmiChat connectTo(RmiChatListener listener, String address){
        try{
            RmiChat chat = new RmiChat(listener);
            RmiChatCommunication localCommu = chat.new RmiChatCommunicationImpl();
            // 获取远程通信对象
            RmiChatCommunication remoteCommu = (RmiChatCommunication)Naming.lookup("//" + address + ":9000/RmiChat");
            remoteCommu.setCommunication(localCommu);
            chat.communication = remoteCommu;
            return chat;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 指定聊天中发生的事件的处理程序的接口，即事件监听器接口。
     */
    public interface RmiChatListener{

        /**
         * 当监听中的RmiChat对象收到连接时会发生这个事件。
         */
        void onConnected();

        /**
         * 当对方发送文本消息时会发生这个事件。
         *
         * @param message 对方所发送的消息
         */
        void onMessage(Serializable message);

        /**
         * 当对方断开连接时会发生这个事件。（自己主动断开并不会发生）
         */
        void onDisconnected();
    }

    /**
     * 用于通信的远程接口。远程接口必须扩展Remote接口，并且所有的方法都要抛出RemoteException异常。
     * 远程接口的方法参数及返回值必须是远程对象或可序列化对象，前者会把远程对象的远程引用传递给 远端，后者则是序列化后传递一个副本给远端。
     */
    private interface RmiChatCommunication extends Remote{

        /**
         * 向远程对象发送一条消息。
         *
         * @param message 要发送的消息
         * @throws RemoteException
         */
        void sendMessage(String message) throws RemoteException;

        /**
         * 向远程对象发送断开连接的消息。
         *
         * @throws RemoteException
         */
        void disconnect() throws RemoteException;

        /**
         * 指定与远程对象连接的本地通信对象。
         *
         * @param communication 本地的通信对象
         * @throws RemoteException
         */
        void setCommunication(RmiChatCommunication communication) throws RemoteException;
    }

    /**
     * 通信接口的实现。直接继承自UnicastRemoteObject，这是一个包含基础功能的RMI类。
     * 对于应用程序来说，使用RMI时只需要扩展此直接继承自UnicastRemoteObject然后专注于实现远程接口的方法即可。
     */
    private class RmiChatCommunicationImpl extends UnicastRemoteObject implements RmiChatCommunication{

        // 由于UnicastRemoteObject的构造方法会抛出RemoteException异常，因此必须声明
        public RmiChatCommunicationImpl() throws RemoteException{
        }

        @Override
        public void sendMessage(String message){
            listener.onMessage(message);
        }

        @Override
        public void disconnect() throws RemoteException{
            listener.onDisconnected();
        }

        @Override
        public void setCommunication(RmiChatCommunication communication){
            listener.onConnected();
            try{
                LocateRegistry.getRegistry(9000).unbind("RmiChat");
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
            RmiChat.this.communication = communication;
        }
    }
}
