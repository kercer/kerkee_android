package com.kercer.kerkee.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

/**
 * 
 * @author zihong
 *
 */
public class KCUtilNet
{
    public String getLocalIpAddress()
    {
        try
        {
            Enumeration<NetworkInterface> infos = NetworkInterface.getNetworkInterfaces();
            while (infos.hasMoreElements())
            {
                NetworkInterface niFace = infos.nextElement();
                Enumeration<InetAddress> enumIpAddr = niFace.getInetAddresses();
                while (enumIpAddr.hasMoreElements())
                {
                    InetAddress mInetAddress = enumIpAddr.nextElement();
                    if (!mInetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(mInetAddress.getHostAddress()))
                    {
                        return mInetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException e)
        {

        }
        return null;
    }
}
