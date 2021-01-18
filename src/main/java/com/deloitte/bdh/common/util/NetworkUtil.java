package com.deloitte.bdh.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 网络接口工具类。 用于获取IP地址、MAC地址。
 *
 * @author dahpeng
 */
public class NetworkUtil {

	private NetworkUtil() {
	}

	/**
	 * <pre>
	 *   获取本机的IPv4地址。
	 *   示例：192.168.100.43。
	 * </pre>
	 *
	 * @return IPv4地址
	 * @throws SocketException 发生 I/O 错误时抛出。
	 */
	public static String getIPv4() throws SocketException {
		List<String> address = getIPv4List();
		if (address.isEmpty()) {
			return null;
		}
		return address.get(0);
	}

	/**
	 * <pre>
	 *   获取本机的IPv4地址列表。
	 *   不包含回送地址127.0.0.1。
	 * </pre>
	 *
	 * @return 不会返回null
	 * @throws SocketException 发生 I/O 错误时抛出。
	 */
	public static List<String> getIPv4List() throws SocketException {
		List<String> list = new ArrayList<>();
		for (InetAddress address : getInet4AddressList()) {
			list.add(address.getHostAddress());
		}
		return list;
	}

	/**
	 * <pre>
	 *   获取本机MAC地址。
	 *   示例：00-AB-C1-3D-48-2F。
	 * </pre>
	 *
	 * @param address 本地IP地址
	 * @return mac地址
	 * @throws SocketException 发生 I/O 错误时抛出。
	 */
	public static String getMac(InetAddress address) throws SocketException {
		if (address == null) {
			return null;
		}
		byte[] mac = NetworkInterface.getByInetAddress(address).getHardwareAddress();
		StringBuilder builder = new StringBuilder(17);
		for (int i = 0; mac != null && i < mac.length; i++) {
			if (i != 0) {
				builder.append("-");
			}
			//字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp).toUpperCase();
			if (str.length() == 1) {
				builder.append("0" + str);
			} else {
				builder.append(str);
			}
		}
		return builder.toString();
	}

	/**
	 * <pre>
	 *   获取本机的IPv4地址列表。
	 *   不包含回送地址127.0.0.1。
	 * </pre>
	 *
	 * @return 不会返回null
	 * @throws SocketException 发生 I/O 错误时抛出。
	 */
	public static List<InetAddress> getInet4AddressList() throws SocketException {
		List<InetAddress> list = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces != null && interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || networkInterface.isPointToPoint()) {
				continue;
			}
			Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
			while (addresses != null && addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (address.isLoopbackAddress() || !(address instanceof Inet4Address)) {
					continue;
				}
				list.add(address);
			}
		}
		return list;
	}

}
