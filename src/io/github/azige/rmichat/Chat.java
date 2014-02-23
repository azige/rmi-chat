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

/**
 * 用于聊天程序的基础接口，指定了发送消息及断开连接的方法。
 * @author Azige
 */
public interface Chat{

    /**
     * 发送一条消息。
     * @param message 要发送的消息
     */
    void sendMessage(String message);

    /**
     * 断开连接，结束通信。
     */
    void disconnect();
}
