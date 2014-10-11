package com.example.upload;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class SocketHttpRequester   
{ 
    public static boolean post(String path, Map<String, String> params, FormFile[] files) throws Exception  

    {     
        final String BOUNDARY = "---------------------------7da2137580612";   
        final String endline = "--" + BOUNDARY + "--\r\n";  
        //下面两个for循环都是为了得到数据长度参数，依据表单的类型而定  
        //首先得到文件类型数据的总长度(包括文件分割线)  
        int fileDataLength = 0;  
        for(FormFile uploadFile : files)  
        {  
            StringBuilder fileExplain = new StringBuilder();  
            fileExplain.append("--");  
            fileExplain.append(BOUNDARY);  
            fileExplain.append("\r\n");  
            fileExplain.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() + "\"\r\n");  
            fileExplain.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");  
            fileExplain.append("\r\n");  
            fileDataLength += fileExplain.length();  
            if(uploadFile.getInStream()!=null){  
                fileDataLength += uploadFile.getFile().length();  
           }else{  
                fileDataLength += uploadFile.getData().length;  
            }  
        }  
        //再构造文本类型参数的实体数据  
        StringBuilder textEntity = new StringBuilder();          
        for (Map.Entry<String, String> entry : params.entrySet())   
        {    
            textEntity.append("--");  
            textEntity.append(BOUNDARY);  
            textEntity.append("\r\n");  
            textEntity.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");  
            textEntity.append(entry.getValue());  
            textEntity.append("\r\n");  
        }         
        //计算传输给服务器的实体数据总长度(文本总长度+数据总长度+分隔符)  
        int dataLength = textEntity.toString().getBytes().length + fileDataLength +  endline.getBytes().length;         
        URL url = new URL(path);  
        //默认端口号其实可以不写  
        int port = url.getPort()==-1 ? 80 : url.getPort();  
        //建立一个Socket链接  
        Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);  
        //获得一个输出流（从Android流到web）  
        OutputStream outStream = socket.getOutputStream();  
        //下面完成HTTP请求头的发送  
        String requestmethod = "POST "+ url.getPath()+" HTTP/1.1\r\n"; 
        outStream.write(requestmethod.getBytes());  
        //构建accept  
        String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*/r/n";  
        outStream.write(accept.getBytes());  
        //构建language  
        String language = "Accept-Language: zh-CN\r\n";  
        outStream.write(language.getBytes());  
        //构建contenttype  
        String contenttype = "Content-Type: multipart/form-data; boundary="+ BOUNDARY+ "\r\n";  
        outStream.write(contenttype.getBytes());  
        //构建contentlength  
        String contentlength = "Content-Length: "+ dataLength + "\r\n";  
        outStream.write(contentlength.getBytes());  
        //构建alive  
        String alive = "Connection: Keep-Alive\r\n";          
        outStream.write(alive.getBytes());  
        //构建host  
        String host = "Host: "+ url.getHost() +":"+ port +"\r\n";  
        outStream.write(host.getBytes());  
        //写完HTTP请求头后根据HTTP协议再写一个回车换行  
        outStream.write("\r\n".getBytes());  
        //把所有文本类型的实体数据发送出来  
        outStream.write(textEntity.toString().getBytes());           
        //把所有文件类型的实体数据发送出来  
        for(FormFile uploadFile : files)  
        {  
            StringBuilder fileEntity = new StringBuilder();  
            fileEntity.append("--");  
            fileEntity.append(BOUNDARY);  
            fileEntity.append("\r\n");  
            fileEntity.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() + "\"\r\n");  
            fileEntity.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");  
            outStream.write(fileEntity.toString().getBytes());  
            //边读边写  
            if(uploadFile.getInStream()!=null)  
            {  
                byte[] buffer = new byte[1024];  
                int len = 0;  
                while((len = uploadFile.getInStream().read(buffer, 0, 1024))!=-1)  
                {  
                    outStream.write(buffer, 0, len);  
                }  
                uploadFile.getInStream().close();  
            }  
            else  
            {  
                outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);  
            }  
            outStream.write("\r\n".getBytes());  
        }  
        //下面发送数据结束标志，表示数据已经结束  
        outStream.write(endline.getBytes());          
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
        //读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败  
        if(reader.readLine().indexOf("200")==-1)  
        {  
            return false;  
        }  
        outStream.flush();  
        outStream.close();  
        reader.close();  
        socket.close();  
        return true;  
    }  
    public static boolean post(String path, Map<String, String> params, FormFile file) throws Exception  
    {  
       return post(path, params, new FormFile[]{file});  
    }  
    public static byte[] readStream(InputStream inStream) throws Exception  
    {  
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = -1;  
        while( (len=inStream.read(buffer)) != -1)  
        {  
            outSteam.write(buffer, 0, len);  
        }  
        outSteam.close();  
        inStream.close();  
        return outSteam.toByteArray();  
    }  
}
