package com.acco.life.util;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * description: 邮箱工具类，用于获取收件箱中标题包含“宁波银行”的邮件内容，并提取正文中的URL进行GET请求获取返回结果
 *
 * @date: 2025-07-25 15:30:00
 * @author wensen.zhang
 * @version V1.0.0
 */
public class MailUtil {
    /**
     * 获取邮箱中标题包含“宁波银行”的邮件正文，并提取正文中的URL进行GET请求获取返回内容
     * @param host 邮箱服务器地址（如imap.163.com）
     * @param port 端口（如993）
     * @param username 邮箱账号
     * @param password 邮箱密码或授权码
     * @return 匹配邮件正文和URL请求结果
     */
    public static String fetchNingboBankMailAndUrlContent(String host, String port, String username, String password) {
        StringBuilder result = new StringBuilder();
        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.host", host);
            props.setProperty("mail.imap.port", port);
            props.put("mail.imap.ssl.enable", "true"); // 关键，必须是 true

            Session session = Session.getInstance(props);
            session.setDebug(true); // 打开调试

            Store store = session.getStore("imap");
            store.connect(username, password);
            Folder inbox = store.getFolder("INBOX");

            inbox.open(Folder.READ_ONLY); // 只读模式打开收件箱。
            Message[] messages = inbox.getMessages();
            boolean found = false;
            for (int i = messages.length - 1; i >= 0 && !found; i--) { // 倒序查找最新邮件
                String subject = messages[i].getSubject();
                if (subject != null && subject.contains("宁波银行")) {
                    String content = getTextFromMessage(messages[i]);
                   // result.append("标题: ").append(subject).append("\n");
                    //result.append("内容: ").append(content).append("\n");
                    // 查找正文中的URL
                    String url = extractBillLink(content);
                    String urlContent = getUrlContent(url);

                    found = true; // 只取最新一封
                }
            }
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            result.append("发生异常: ").append(e.getMessage());
        }
        return result.toString();
    }

    // 提取邮件正文内容
    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }
    // 使用 jsoup 抽取目标链接
    private static String extractBillLink(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        for (Element a : doc.select("a[href]")) {
            String href = a.attr("href");
            if (href.contains("cloudbill.nbcb.com.cn/cloud-bill/#/myBill")) {
                return href;
            }
        }
        return null;
    }
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                // 可选：如需处理html内容可用jsoup等库
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    // GET请求获取URL内容
    private static String getUrlContent(String urlStr) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            content.append("[URL请求异常:" + e.getMessage() + "]");
        }
        return content.toString();
    }
}

