/*
 * MIT License
 * Copyright (c) 2017-2019 nuls.io
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.api.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.parse.JSONUtils;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niels
 */
public class MessageIntfTest {

    private static final String httpurl = "http://127.0.0.1:18003";

    @Test
    public void send() throws JsonProcessingException {
        for (int i = 0; i < 990; i++) {
            Map<String, Object> param = new HashMap<>();
            param.put("jsonrpc", "2.0");
            param.put("method", "commitMsg");
            param.put("id", i);
            List<Object> list = new ArrayList<>();
            list.add("aaaaa" + i);
            list.add("asdfasdfasi;ogupaowiefjao;shfd;lasureoifhgv;afd;lkasdjflsdjfoiweuhfoasjdf");
            param.put("params", list);
            String paramString = JSONUtils.obj2json(param);
            String result = doPost(httpurl, paramString);
            System.out.println(i + "=====" + result);
            if (i % 10 == 0) {
                get(i);
            }
        }
    }

    public void get(int index) throws JsonProcessingException {
        for (int i = index; i > index - 10 && i >= 0; i--) {
            Map<String, Object> param = new HashMap<>();
            param.put("jsonrpc", "2.0");
            param.put("method", "getMsg");
            param.put("id", i);
            List<Object> list = new ArrayList<>();
            list.add("aaaaa" + i);
            param.put("params", list);
            String paramString = JSONUtils.obj2json(param);
            String result = doPost(httpurl, paramString);
            System.out.println(i + "===get==" + result);
        }
    }

    public static String doPost(String httpUrl, String param) {
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // By remoteurlConnect Object Open Connection
            connection = (HttpURLConnection) url.openConnection();
            // Set connection request method
            connection.setRequestMethod("POST");
            // Set the timeout period for connecting to the host server：15000millisecond
            connection.setConnectTimeout(15000);
            // Set the timeout period for reading data returned by the host server：60000millisecond
            connection.setReadTimeout(60000);

            // The default value is：falseWhen transmitting data to a remote server/When writing data, it needs to be set totrue
            connection.setDoOutput(true);
            // The default value is：trueWhen reading data from a remote service, set totrue, this parameter can be optional or optional
            connection.setDoInput(true);
            // Format incoming parameters:The request parameter should be name1=value1&name2=value2 The form of.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // Set authentication information：Authorization: Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0
            connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // Obtain an output stream by connecting objects
            os = connection.getOutputStream();
            // Write parameters out through output stream objects/Transfer out,It is written through a byte array
            os.write(param.getBytes());
            // Obtain an input stream by connecting objects and read it remotely
            if (connection.getResponseCode() == 200) {

                is = connection.getInputStream();
                // Wrapping input stream objects:charsetSet up according to the requirements of the work project team
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                StringBuffer sbf = new StringBuffer();
                String temp = null;
                // Loop through line by line to read data
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close resource
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Disconnect from remote addressurlConnection of
            connection.disconnect();
        }
        return result;
    }
}
