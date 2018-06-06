package com.yuyuehao.andy.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wang
 * on 2018-05-11
 */

public class IpNetAddress {



    //广域网
    public static String getNetIp(String url) {
        URL infoUrl = null;
        InputStream inStream = null;
        BufferedReader reader = null;
        String ipLine = "undefined";
        HttpURLConnection httpConnection = null;
        try {
            //"http://ip.6655.com/ip.aspx"
            infoUrl = new URL(url);
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            httpConnection.setReadTimeout(2000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                 reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");

                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                if (strber != null && !strber.toString().equals("")){
                    Matcher matcher = pattern.matcher(strber.toString());
                    if (matcher.find()) {
                        ipLine = matcher.group();
                    }
                }
            }
        } catch (MalformedURLException e) {
            return ipLine;
        } catch (IOException e) {
            return ipLine;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (reader != null){
                    reader.close();
                }
                if (httpConnection != null){
                    httpConnection.disconnect();
                }
            } catch (IOException e) {
                return ipLine;
            }
        }
        return ipLine;
    }




    //局域网
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "undefined";
        } catch(Exception e){
            return "undefined";
        }
        return "undefined";
    }

    /**
     * 获取Ethernet的MAC地址
     * @return
     */
    private static String getMacAddress() {
        try {
            String str = loadFileAsString("/sys/class/net/eth0/address").toUpperCase(Locale.ENGLISH).substring(0, 17);
            if (str.contains(":")){
               str = str.replace(":","");

            }

            return str;
        } catch (IOException e) {
            return "undefined";
        }
    }

    private static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024]; int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static String getIpAddressAndSubnettest() {
        String mac = getMacAddress();
        String hostAddress = "undefined";
        String subnetMaskAddress = "undefined";
        String broadcastAddress = "undefined";

        try {
            Enumeration<NetworkInterface> eni = NetworkInterface
                    .getNetworkInterfaces();
            while (eni.hasMoreElements()) {
                NetworkInterface networkCard = eni.nextElement();
                List<InterfaceAddress> ncAddrList = networkCard
                        .getInterfaceAddresses();
                Iterator<InterfaceAddress> ncAddrIterator = ncAddrList.iterator();
                while (ncAddrIterator.hasNext()) {
                    InterfaceAddress networkCardAddress = ncAddrIterator.next();
                    InetAddress address = networkCardAddress.getAddress();
                    if (!address.isLoopbackAddress()) {
                        hostAddress = address.getHostAddress();
                        if (hostAddress.indexOf(":") > 0) {
                            // case : ipv6
                            continue;
                        } else {
                            // case : ipv4
                            subnetMaskAddress = calcMaskByPrefixLength(networkCardAddress.getNetworkPrefixLength());
                            broadcastAddress = networkCardAddress.getBroadcast().getHostAddress();
                        }
                    }
                }
            }
            return  createData3(hostAddress,mac,subnetMaskAddress,broadcastAddress);
        } catch (Exception e) {
            return  createData3(hostAddress,mac,subnetMaskAddress,broadcastAddress);
        }
    }

    public static String createData3(String hostAddress,String mac,String subnetMaskAddress,String broadcastAddress){
        StringBuffer sb = new StringBuffer();
        sb.append(hostAddress);
        sb.append("_");
        sb.append(mac);
        sb.append("_");
        sb.append(subnetMaskAddress);
        sb.append("_");
        sb.append(broadcastAddress);
        return sb.toString();
    }


    public static String calcMaskByPrefixLength(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

    public static String calcSubnetAddress(String ip, String mask) {
        String result = "";
        try {
            // calc sub-net IP
            InetAddress ipAddress = InetAddress.getByName(ip);
            InetAddress maskAddress = InetAddress.getByName(mask);

            byte[] ipRaw = ipAddress.getAddress();
            byte[] maskRaw = maskAddress.getAddress();

            int unsignedByteFilter = 0x000000ff;
            int[] resultRaw = new int[ipRaw.length];
            for (int i = 0; i < resultRaw.length; i++) {
                resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
            }

            // make result string
            result = result + resultRaw[0];
            for (int i = 1; i < resultRaw.length; i++) {
                result = result + "." + resultRaw[i];
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return result;
    }


}
