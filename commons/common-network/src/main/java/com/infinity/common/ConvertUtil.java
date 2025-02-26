package com.infinity.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConvertUtil {
    // 字符串形式的二进制流转换为内存形式
    public static byte[] getHexValue(String hexStr) throws RuntimeException {
        if (hexStr == null) return null;

        int len = hexStr.length();

        if (len % 2 != 0) {
            throw new RuntimeException("Invalid hexstr:" + hexStr);
        }

        byte[] addr = new byte[len >> 1];

        int i = 0, j = 0;
        char tmp;
        while (i < len) {
            tmp = (char) Character.digit(hexStr.charAt(i++), 16);
            tmp <<= 4;
            tmp += (char) Character.digit(hexStr.charAt(i++), 16);
            addr[j++] = (byte) tmp;
        }

        return addr;
    }

    static char getValueChar(char bit4v, char a) throws RuntimeException {
        int value4bit = bit4v;
        if (value4bit >= 16) {
            throw new RuntimeException("Invalid hex char:" + value4bit);
        }
        if (value4bit >= 10)
            return (char) (a + (value4bit - 10));
        return (char) ('0' + value4bit);
    }

    // 内存形式的二进制流转换为字符串形式
    public static String getHexStr(byte[] data, boolean toUpperCase) throws RuntimeException {
        if (data == null) return null;

        int newlen = data.length * 2;
        char[] tmpbuf = new char[newlen];
        int i = 0, j = 0;
        char value;
        char a = 'a';
        if (toUpperCase)
            a = 'A';
        for (; i < data.length; ++i) {
            value = (char) data[i];
            tmpbuf[j++] = getValueChar((char) ((value >> 4) & 0xF), a);
            tmpbuf[j++] = getValueChar((char) (value & 0xF), a);
        }
        return new String(tmpbuf);
    }

    // IPV4的整形转为字符串
    public static String ipv4Int2Str(int intIP) {
        int[] parts = new int[4];
        for (int i = 0, shift = 0; i < 4; ++i, shift += 8) {
            parts[i] = (intIP >> shift) & 0xFF;
        }

        StringBuilder buff = new StringBuilder();
        for (int i = 3; i >= 0; --i) {
            buff.append(parts[i]);
            if (i != 0)
                buff.append('.');
        }
        return buff.toString();
    }

    // IPV4的字符串转为整形
    public static int ipv4Str2Int(String strIP) throws RuntimeException {
        strIP = strIP.trim();
        String[] parts = strIP.split("\\.");
        if (parts.length != 4) {
            throw new RuntimeException("Invalid StrIP:" + strIP);
        }
        int ip = 0, part;
        for (int i = 0; i < 4; i++) {
            part = -1;
            try {
                part = Integer.parseInt(parts[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (part < 0 || part > 0xFF) {
                throw new RuntimeException("Invalid StrIP:" + strIP);
            }
            ip <<= 8;
            ip |= part;
        }
        return ip;
    }

    public static long ipv4Str2Long(String strIP) throws RuntimeException {
        strIP = strIP.trim();
        String[] parts = strIP.split("\\.");
        if (parts.length != 4) {
            throw new RuntimeException("Invalid StrIP:" + strIP);
        }
        long ip = 0, part;
        for (int i = 0; i < 4; i++) {
            part = -1;
            try {
                part = Integer.parseInt(parts[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (part < 0 || part > 0xFF) {
                throw new RuntimeException("Invalid StrIP:" + strIP);
            }
            ip <<= 8;
            ip |= part;
        }
        return ip;
    }

    // 从多个路径中寻找文件，pathBuf里面返回寻找到的路径，返回存在的文件名全称
    public static String findFileFullName(List<String> paths, String fileName, StringBuilder pathBuf) {
        for (String path : paths) {
            String fullName = path + fileName;
            File file = new File(fullName);
            if (file.exists()) {
                if (pathBuf != null) pathBuf.append(path);
                return fullName;
            }
        }
        return null;
    }

    public static String parseViaNode(byte[] viaNode, char paraType, boolean ignoreType) {
        byte charlen;
        for (int pos = 0; pos < viaNode.length; ) {
            charlen = viaNode[pos++];
            if ((pos + charlen) > viaNode.length) {
                return null;
            }
            if (viaNode[pos] == paraType) {
                char[] data = new char[charlen];
                int j = 0;
                if (ignoreType) {
                    pos++;
                    charlen--;
                }
                while (j < charlen) {
                    data[j++] = (char) viaNode[pos++];
                }
                return new String(data);
            } else
                pos += charlen;
        }
        return null;
    }

    public static boolean checkTimeout(long lastMS, long curMS, long intervalMS) {
        // 时间异常
        if (lastMS > curMS) {
            log.error(ConvertUtil.whoCalledMe() + " Time is error! lastMS:" + lastMS + " curMS:" + curMS + " curMS-lastMS=" + (curMS - lastMS));
        }

        // 超时
        return curMS - lastMS > intervalMS;
    }

    public static String whoCalledMe() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[3];
        String filename = caller.getFileName();
        int lineNumber = caller.getLineNumber();
        return " at (" + filename + ":" + lineNumber + ") ";
    }

    public static String whoCalledMe(String fileName, int lineNumber) {
        return " at (" + fileName + ":" + lineNumber + ") ";
    }

    public static Byte convertObjectToByte(Object obj) {
        Integer temp = (Integer) obj;
        return temp == null ? null : temp.byteValue();
    }

    public static byte convertObjectToByte(Object obj, byte defaultByte) {
        Integer temp = (Integer) obj;
        return temp == null ? defaultByte : temp.byteValue();
    }

    public static Short convertObjectToShort(Object obj) {
        Integer temp = (Integer) obj;
        return temp == null ? null : temp.shortValue();
    }

    public static short convertObjectToShort(Object obj, short defaultShort) {
        Integer temp = (Integer) obj;
        return temp == null ? defaultShort : temp.shortValue();
    }

    public static int convertObjectToInt(Object obj, int defaultInt) {
        if (obj != null)
            return ((Integer) obj).intValue();
        return defaultInt;
    }

    public static long convertObjectToLong(Object obj, long defaultLong) {
        if (obj != null)
            return ((Long) obj).longValue();
        return defaultLong;
    }

    // 分成多行
    public static String[] splitIntoLines(String str) {
        ArrayList<String> re = new ArrayList<String>();
        int size = str.length();
        char c;
        StringBuffer strbuf = null;
        for (int i = 0; i < size; i++) {
            c = str.charAt(i);
            if (c == '\t' || c == '\r') {
                c = ' ';
            } else if (c == '\n') // split
            {
                if (strbuf != null) // previous substring end
                {
                    re.add(strbuf.toString());
                    strbuf = null; // to start new substring
                }
                continue;
            }

            if (strbuf == null) {
                if (c == ' ') // ignore first empty char
                    continue;
                strbuf = new StringBuffer();
            }
            strbuf.append(c);
        }
        if (strbuf != null)
            re.add(strbuf.toString());

        String[] arr = new String[re.size()];
        return re.toArray(arr);
    }

    // 将一行分成多项
    public static String[] splitLineIntoArray(String line) {
        ArrayList<String> re = new ArrayList<String>();
        int size = line.length();
        char c;
        StringBuffer strbuf = null;
        for (int i = 0; i < size; i++) {
            c = line.charAt(i);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                if (strbuf != null) // previous substring end
                {
                    re.add(strbuf.toString());
                    strbuf = null; // to start new substring
                }
                continue;
            }
            if (strbuf == null)
                strbuf = new StringBuffer();
            strbuf.append(c);
        }
        if (strbuf != null)
            re.add(strbuf.toString());

        String[] arr = new String[re.size()];
        return re.toArray(arr);
    }

    public static byte[] List2Short(List<Short> V) {
        if (V == null || V.isEmpty()) {
            return null;
        }
        ByteBuffer buf = ByteBuffer.allocate(V.size() * 2);
        for (Short v : V) {
            buf.putShort(v);
        }
        return buf.array();
    }

    public static List<Short> Short2List(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        List<Short> list = new ArrayList<>();
        for (int i = 0, size = bytes.length / 2; i < size; i++) {
            if (buf.remaining() > 1)
                list.add(buf.getShort());
        }
        return list;
    }

    public static long convertHexToLong(String str) {
        try {
            return Long.parseLong(str, 16);
        } catch (Exception e) {
            BigInteger bigInt = new BigInteger(str, 16);
            return bigInt.longValue();
        }
    }

//	static List<Pair<Long,Long>> privateIPRanges_ = null;
//	public static boolean inPrivateIpRange(String strIP)
//	{
////		10.0.0.0 ~ 10.255.255.255
////		172.16.0.0 ~ 172.31.255.255
////		192.168.0.0 ~ 192.168.255.255
//		if(privateIPRanges_ == null)
//		{
//			privateIPRanges_ = new ArrayList<>(3);
//			privateIPRanges_.add(new Pair<Long,Long>(ipv4Str2Long("10.0.0.0"),ipv4Str2Long("10.255.255.255")));
//			privateIPRanges_.add(new Pair<Long,Long>(ipv4Str2Long("172.16.0.0"),ipv4Str2Long("172.31.255.255")));
//			privateIPRanges_.add(new Pair<Long,Long>(ipv4Str2Long("192.168.0.0"),ipv4Str2Long("192.168.255.255")));
//		}
//		long ip = ipv4Str2Long(strIP);
//		for(Pair<Long,Long> range : privateIPRanges_)
//		{
//			if(ip >= range.left_ && ip <= range.right_)
//				return true;
//		}
//		return false;
//	}

//	public static byte[] int2bytes(int v)
//	{
//		byte[] bytes = new byte[4];
//		bytes[0] = (byte)(v >> 24);
//		bytes[1] = (byte)(v >> 16);
//		bytes[2] = (byte)(v >> 8);
//		bytes[3] = (byte)(v);
//		return bytes;
//	}
//
//	public static int bytes2int(byte[] bytes)
//	{
//		int re = (((bytes[0]) << 24)
//				| ((bytes[0 + 1] & 0xff) << 16)
//				| ((bytes[0 + 2] & 0xff) <<  8)
//				| ((bytes[0 + 3] & 0xff)));
//		return re;
//	}

}
