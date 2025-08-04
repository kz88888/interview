package com.qsystem.clientlib;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SFTP客户端类
 */
public class SFTP {
    public ChannelSftp sftp;
    public Session session;
    public static final Logger logClientLib = LoggerFactory.getLogger(SFTP.class);
    
    public ChannelSftp getSftp() {
        return sftp;
    }
    
    public SFTP(String ip) {
        // 使用Settings单例获取配置，而不是重复读取app.config
        Settings settings = Settings.getInstance();
        
        int sfpPort = Integer.parseInt(settings.getProperty("sftp_port", "22"));
        String sfpUser = settings.getProperty("sftp_user", "");
        String sfpPwd = settings.getProperty("sftp_pwd", "");
        
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(sfpUser, ip, sfpPort);
            session.setPassword(sfpPwd);
            
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            session.connect();
            
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception ex) {
            throw new RuntimeException(String.format("连接SFTP失败，原因：%s", ex.getMessage()));
        }
    }
    
    protected void finalize() throws Throwable {
        try {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        } catch (Exception ex) {
            throw new RuntimeException(String.format("断开SFTP失败，原因：%s", ex.getMessage()));
        } finally {
            super.finalize();
        }
    }
    
    public void Download(String remotePath, String localPath) {
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(localPath);
            sftp.get(remotePath, fs);
            fs.flush();
        } catch (Exception ex) {
            throw new RuntimeException(String.format("SFTP文件获取失败，原因：%s", ex.getMessage()));
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (localPath.contains(".zip")) {
                ZipUtil.unZipSingleFile(localPath);
            }
            if (localPath.endsWith(File.separator)) {
                ZipUtil.unZip(localPath);
            }
        }
    }
    
    public List<String> getFileNameList(String remotePath) {
        List<String> filenames = new ArrayList<>();
        try {
            List<ChannelSftp.LsEntry> files = sftp.ls(remotePath);
            for (ChannelSftp.LsEntry file : files) {
                String fileName = file.getFilename();
                if ((!fileName.equals(".")) && (!fileName.equals("..")) && 
                    (!fileName.contains(".json")) && (!fileName.contains("log"))) {
                    filenames.add(fileName);
                }
            }
        } catch (Exception e) {
            logClientLib.error("获取文件列表失败", e);
        }
        return filenames;
    }
    
    public void DownloadAllFilesExceptLog(String remotePath, String localPath) {
        File localDir = new File(localPath);
        if (!localDir.exists()) {
            localDir.mkdirs();
        }
        
        try {
            List<ChannelSftp.LsEntry> files = sftp.ls(remotePath);
            for (ChannelSftp.LsEntry file : files) {
                String fileName = file.getFilename();
                if ((!fileName.equals(".")) && (!fileName.equals("..")) && 
                    (!fileName.contains(".json")) && (!fileName.contains("log"))) {
                    String sourceFilePath = remotePath + File.separator + fileName;
                    String destFilePath = new File(localPath, fileName).getPath();
                    try (FileOutputStream fileStream = new FileOutputStream(destFilePath)) {
                        logClientLib.info("正在下载文件" + fileName);
                        sftp.get(sourceFilePath, fileStream);
                    }
                }
            }
            ZipUtil.unZip(localPath);
        } catch (Exception e) {
            logClientLib.error("下载文件失败", e);
        }
    }
} 