package com.ecommerce.sbs;

import com.ecommerce.dasient.exceptions.PersistBackupException;
import com.ecommerce.dasient.model.CleanHistory;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

public class BackupUploader {

    private static final Logger logger = Logger.getLogger(BackupUploader.class);
    
    private CleanHistory cleanHistory;

    public BackupUploader(CleanHistory cleanHistory) {
        this.cleanHistory = cleanHistory;
    }

    public void uploadFile(InputStream backupStream, String backupFileName)
            throws PersistBackupException {

        String[] backupPath = {
            cleanHistory.getControlPanel().getName(),
            cleanHistory.getWebServer().getName(),
            cleanHistory.getWebUsername(),
            new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cleanHistory.getStartTime()),
            String.valueOf(cleanHistory.getId())
        };

        try {
            uploadFileViaFtp(backupPath, backupFileName, backupStream);
        } catch (IOException exc) {
            throw new PersistBackupException(
                    "Failed to persist backup to storage via FTP", exc);
        }
    }

    private boolean dirExists(FTPFile[] fileListing, String dirName) {
        for (FTPFile file : fileListing) {
            if (file.isDirectory() && dirName.equals(file.getName())) {
                return true;
            }
        }
        return false;
    }

    private void uploadFileViaFtp(String[] backupPath, String backupFileName, InputStream backupStream)
            throws IOException, PersistBackupException {

        boolean ftpSecure = Boolean.getBoolean("dasient.ftp_secure");
        String ftpHost = System.getProperty("dasient.ftp_host", "localhost");
        int ftpPort = Integer.getInteger("dasient.ftp_port", FTP.DEFAULT_PORT);
        String ftpUser = System.getProperty("dasient.ftp_user", "dasient");
        String ftpPass = System.getProperty("dasient.ftp_pass", "");
        String ftpPath = System.getProperty("dasient.ftp_path", "/");

        FTPClient ftp;

        if (!ftpSecure) {
            ftp = new FTPClient();
        }
        else {
            try {
                ftp = new FTPSClient();
            } catch (NoSuchAlgorithmException exc) {
                throw new PersistBackupException(exc);
            }
        }

        // milliseconds
        ftp.setConnectTimeout(20000);
        ftp.setDefaultTimeout(20000);
        ftp.setDataTimeout(20000);

        try {
            ftp.connect(ftpHost, ftpPort);

            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new PersistBackupException(String.format(
                        "FTP server at '%s:%d' refused the connection (secure = %b): %s",
                        ftpHost, ftpPort, ftpSecure, ftp.getReplyString()));
            }

            if (!ftp.login(ftpUser, ftpPass)) {
                throw new PersistBackupException(String.format(
                        "FTP server at '%s:%d' refused the credentials for user '%s': %s",
                        ftpHost, ftpPort, ftpUser, ftp.getReplyString()));
            }

            if (!ftp.setFileType(FTP.BINARY_FILE_TYPE)) {
                throw new PersistBackupException(String.format(
                        "FTP server refused binary file transfer: %s",
                        ftp.getReplyString()));
            }

            ftp.enterLocalPassiveMode();

            if (!ftp.changeWorkingDirectory(ftpPath)) {
                String replyString = ftp.getReplyString();

                throw new PersistBackupException(String.format(
                        "Failed to chdir to base path '%s' (cwd is '%s'): %s",
                        ftpPath, ftp.printWorkingDirectory(), replyString));
            }

            ftp.setListHiddenFiles(true);

            for (String dirName : backupPath) {
                FTPFile[] fileListing = ftp.listFiles(ftp.printWorkingDirectory());

                if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    String replyString = ftp.getReplyString();

                    throw new PersistBackupException(String.format(
                            "Failed to list directory contents (cwd is '%s'): %s",
                            ftp.printWorkingDirectory(), replyString));
                }

                if (!dirExists(fileListing, dirName) && !ftp.makeDirectory(dirName)) {
                    String replyString = ftp.getReplyString();

                    throw new PersistBackupException(String.format(
                            "Failed to mkdir '%s' (cwd is '%s'): %s",
                            dirName, ftp.printWorkingDirectory(),
                            replyString));
                }

                if (!ftp.changeWorkingDirectory(dirName)) {
                    String replyString = ftp.getReplyString();

                    throw new PersistBackupException(String.format(
                            "Failed to chdir to '%s' (cwd is '%s'): %s",
                            dirName, ftp.printWorkingDirectory(),
                            replyString));
                }
            }

            if (!ftp.storeFile(backupFileName, backupStream)) {
                String replyString = ftp.getReplyString();

                throw new PersistBackupException(String.format(
                        "Failed to upload backup file '%s' (cwd is '%s'): %s",
                        backupFileName, ftp.printWorkingDirectory(),
                        replyString));
            }

            if (!ftp.logout()) {
                logger.debug(String.format("Failed to log out from FTP server: %s",
                        ftp.getReplyString()));
            }
        }
        finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException exc) {
                    logger.debug("Failed to disconnect FTP connection", exc);
                }
            }
        }
    }

}
