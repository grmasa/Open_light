package grmasa.com.open_light.yapi.socket;

import android.util.Log;
import grmasa.com.open_light.yapi.exception.YeelightSocketException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket holder for manage exception, stream, ...
 */
public class YeelightSocketHolder {
    /**
     * Default socket timeout
     */
    private static int SOCKET_TIMEOUT = 1000;
    /**
     * Socket IP
     */
    private String ip;
    /**
     * Socket port
     */
    private int port;
    /**
     * Socket reader
     */
    private BufferedReader socketReader;
    /**
     * Socket writer
     */
    private BufferedWriter socketWriter;

    /**
     * Constructor for socket holder
     * @param ip Socket holder IP
     * @param port Socket holder port
     * @throws YeelightSocketException when socket error occurs
     */
    public YeelightSocketHolder(String ip, int port) throws YeelightSocketException {
        this.ip = ip;
        this.port = port;
        this.initSocketAndStreams();
    }

    /**
     * Create socket and associated streams (reader + writer)
     * @throws YeelightSocketException when socket error occurs
     */
    private void initSocketAndStreams() throws YeelightSocketException {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
            Socket socket = new Socket();
            socket.connect(inetSocketAddress, YeelightSocketHolder.SOCKET_TIMEOUT);
            socket.setSoTimeout(SOCKET_TIMEOUT);
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            throw new YeelightSocketException(e);
        }
    }

    /**
     * Send datas on the socket
     * @param datas Datas to send
     * @throws YeelightSocketException when socket error occurs
     */
    public void send(String datas) throws YeelightSocketException {
        try {
            Log.v("ff1", "{"+datas+"} sent to {"+this.ip+"}:{"+this.port+"}");
            this.socketWriter.write(datas);
            this.socketWriter.flush();
        } catch (Exception e) {
            throw new YeelightSocketException(e);
        }
    }

    /**
     * Read line on the socket (terminated with \r, \n or \r\n)
     * @return The line read
     * @throws YeelightSocketException when socket error occurs
     */
    public String readLine() throws YeelightSocketException {
        try {
            String datas = this.socketReader.readLine();
            Log.v("ff", "{"+datas+"} sent to {"+this.ip+"}:{"+this.port+"}");
            return datas;
        } catch (Exception e) {
            throw new YeelightSocketException(e);
        }
    }
}
