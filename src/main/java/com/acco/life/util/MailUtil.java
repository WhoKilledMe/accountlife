package com.acco.life.util;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.search.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;

/**
 * description: 邮箱工具类，用于获取收件箱中标题包含“宁波银行”的邮件内容，并提取正文中的URL进行GET请求获取返回结果
 *
 * @author wensen.zhang
 * @version V1.0.0
 * @date: 2025-07-25 15:30:00
 */
public class MailUtil {

    /**
     * 基于发件人与日期（单日或范围）搜索并下载附件到指定目录
     */
    public static String searchAndDownloadAttachmentsBySender(
            String host,
            String port,
            String username,
            String password,
            String senderContains,
            String startDate,
            String endDate,
            String destDirectory
    ) {
        StringBuilder result = new StringBuilder();
        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imaps.host", host);
            if (port != null) props.setProperty("mail.imaps.port", port);
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.ssl.trust", "*");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            LocalDate start = parseLocalDateOrNull(startDate);
            LocalDate end = parseLocalDateOrNull(endDate);
            if (end != null) {
                end = end.plusDays(1);
            }

            SearchTerm term = null;
            if (start != null) {
                Date sd = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
                SearchTerm dateTerm = new OrTerm(new SentDateTerm(ComparisonTerm.GE, sd), new ReceivedDateTerm(ComparisonTerm.GE, sd));
                term =  new AndTerm(term, dateTerm);
            }
            if (end != null) {
                Date ed = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());
                SearchTerm dateTerm = new OrTerm(new SentDateTerm(ComparisonTerm.LE, ed), new ReceivedDateTerm(ComparisonTerm.LE, ed));
                term = term == null ? dateTerm : new AndTerm(term, dateTerm);
            }
            if (CharSequenceUtil.isNotBlank(senderContains)) {
                SearchTerm fromTerm = new FromStringTerm(senderContains);
                term = term == null ? fromTerm : new AndTerm(term, fromTerm);
            }

            Message[] messages = term == null ? inbox.getMessages() : inbox.search(term);

            int downloaded = 0;
            Path destDir = Path.of(destDirectory);
            if (!Files.exists(destDir)) {
                Files.createDirectories(destDir);
            }
            if (messages != null) {
                for (Message m : messages) {
                    downloaded += saveAttachments(m, destDir);
                }
            }
            result.append("下载附件数量: ").append(downloaded);
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            return "发生异常: " + e.getMessage();
        }
        return result.toString();
    }

    private static int saveAttachments(Message message, Path destDir) throws Exception {
        int count = 0;
        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                String filename = bodyPart.getFileName();
                boolean isAttachment = Part.ATTACHMENT.equalsIgnoreCase(disp) || (filename != null && !filename.isEmpty());
                if (isAttachment) {
                    String safeName = MimeUtility.decodeText(filename != null ? filename : ("attachment-" + i));
                    Path target = destDir.resolve(safeName);
                    try (var in = bodyPart.getInputStream()) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static LocalDate parseLocalDateOrNull(String s) {
        try {
            if (s == null || s.isEmpty()) return null;
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
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

