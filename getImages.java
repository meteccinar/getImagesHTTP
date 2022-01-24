import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getImages{

	public static void main(String[] args) throws Exception {
		String address;
		String hostName;
		String httpMessage;
		int imgPath1,imgPath2;
		String imgPathStr;
		String path;
		String newSentence = "";
		int data;
		int data2;

		int i = 0;

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Please Enter the URL address of " + "the web page: ");
		address = inFromUser.readLine();

		if (!address.startsWith("http://")) {
			address = "http://" + address;
		}

		URL url = new URL(address); // 10.2.8.212
		hostName = url.getHost();
		path = url.getPath().isEmpty() ? "/" : url.getPath();

		httpMessage = "GET " + path + " HTTP/1.1\r\n" + "Host: " + hostName + "\r\n" + "Connection: close\r\n\r\n";
		Socket clientSocket = new Socket(hostName, 80);
		DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		System.out.print(httpMessage);
		outToServer.writeBytes(httpMessage);


		//// Take incoming data start ...
		byte[] dataArr = new byte[64000];
		int counter = 0;
		boolean flag = true;

		while ((data = inFromServer.read()) != -1)
		{
			dataArr[counter++] = (byte) data;

			if (flag && dataArr[counter - 1] == '\n' && dataArr[counter - 2] == '\r' && dataArr[counter - 3] == '\n' && dataArr[counter - 4] == '\r')
			{
				counter = 0;
				flag = false;
			}


		}
		//// Take incoming data finish ...

		String incomingDataWebServer = new String(dataArr, 0, counter);


		imgPath1 = incomingDataWebServer.indexOf("<img src=");
		imgPath2 = incomingDataWebServer.indexOf("\"",imgPath1+10);
		imgPathStr = incomingDataWebServer.substring(imgPath1+10,imgPath2);




		System.out.println(incomingDataWebServer);


		clientSocket.close();
		httpMessage = "GET " + imgPathStr + " HTTP/1.1\r\n" + "Host: " + hostName + "\r\n" + "Connection: close\r\n\r\n";
		Socket forImg = new Socket(hostName,80);
		DataInputStream getImage = new DataInputStream(forImg.getInputStream());
		DataOutputStream outImage = new DataOutputStream(forImg.getOutputStream());

		System.out.print(httpMessage);
		outImage.writeBytes(httpMessage);

		byte[] dataArr2 = new byte[64000];
		int counter2 = 0;
		boolean flag2 = true;

		while ((data2 = getImage.read()) != -1)
		{
			dataArr2[counter2++] = (byte) data2;

			if (flag2 && dataArr2[counter2 - 1] == '\n' && dataArr2[counter2 - 2] == '\r' && dataArr2[counter2 - 3] == '\n' && dataArr2[counter2 - 4] == '\r')
			{
				counter2 = 0;
				flag2 = false;
			}
		}
		int name1;
		name1 = imgPathStr.lastIndexOf("/");
		String filename = imgPathStr.substring(name1+1);
		FileOutputStream outputStream = new FileOutputStream(filename);
		outputStream.write(dataArr2,0,counter2);
		System.out.println("Download Success : "+filename);

		outputStream.close();



	}
}
