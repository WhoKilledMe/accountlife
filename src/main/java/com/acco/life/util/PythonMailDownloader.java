package com.acco.life.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PythonMailDownloader {

    public static class ExecResult {
        public final int exitCode;
        public final String stdout;
        public final String stderr;

        public ExecResult(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }

    /**
     * 通过 Python 脚本下载附件。
     * @param pythonExecutable python 命令（如 python3）
     * @param scriptPath 脚本路径
     * @param host IMAP 主机
     * @param port 端口（可空）
     * @param username 邮箱账号
     * @param password 授权码/密码
     * @param subject 主题关键字（可空表示不过滤）
     * @param dateYYYYMMDD 日期（可空表示不过滤）
     * @param destDir 下载目录
     * @param timeoutSeconds 超时时间
     */
    public static ExecResult run(
            String pythonExecutable,
            String scriptPath,
            String host,
            Integer port,
            String username,
            String password,
            String subject,
            String dateYYYYMMDD,
            String destDir,
            long timeoutSeconds
    ) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(Objects.requireNonNullElse(pythonExecutable, "python3"));
        cmd.add(scriptPath);
        cmd.add("--host");
        cmd.add(host);
        if (port != null) {
            cmd.add("--port");
            cmd.add(String.valueOf(port));
        }
        cmd.add("--username");
        cmd.add(username);
        cmd.add("--password");
        cmd.add(password);
        if (subject != null && !subject.isEmpty()) {
            cmd.add("--subject");
            cmd.add(subject);
        }
        if (dateYYYYMMDD != null && !dateYYYYMMDD.isEmpty()) {
            cmd.add("--date");
            cmd.add(dateYYYYMMDD);
        }
        cmd.add("--dest");
        cmd.add(destDir);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);
        Process p = pb.start();

        StringBuilder out = new StringBuilder();
        StringBuilder err = new StringBuilder();
        try (BufferedReader or = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
             BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
            Thread tOut = new Thread(() -> {
                try {
                    String line;
                    while ((line = or.readLine()) != null) {
                        out.append(line).append('\n');
                    }
                } catch (Exception ignored) {}
            }, "python-stdout-gobbler");
            Thread tErr = new Thread(() -> {
                try {
                    String line;
                    while ((line = er.readLine()) != null) {
                        err.append(line).append('\n');
                    }
                } catch (Exception ignored) {}
            }, "python-stderr-gobbler");
            tOut.setDaemon(true);
            tErr.setDaemon(true);
            tOut.start();
            tErr.start();

            boolean finished = p.waitFor(timeoutSeconds > 0 ? timeoutSeconds : 0, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                err.append("Process timeout after ").append(timeoutSeconds).append("s\n");
                return new ExecResult(-1, out.toString(), err.toString());
            }
            // Close streams to unblock gobblers quickly
            try { p.getInputStream().close(); } catch (Exception ignored) {}
            try { p.getErrorStream().close(); } catch (Exception ignored) {}
            // Give gobblers a brief moment, then proceed regardless
            try { tOut.join(100); } catch (InterruptedException ignored) {}
            try { tErr.join(100); } catch (InterruptedException ignored) {}
        } finally {
            try { p.getOutputStream().close(); } catch (Exception ignored) {}
        }

        return new ExecResult(p.exitValue(), out.toString(), err.toString());
    }
}


