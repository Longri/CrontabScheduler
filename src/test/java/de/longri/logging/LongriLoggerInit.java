package de.longri.logging;



import de.longri.crontab.Main;
import de.longri.utils.SystemType;
import org.slf4j.LoggerFactory;

import static de.longri.logging.LongriLogger.CONFIG_PARAMS;


public class LongriLoggerInit {

    static {
        //initial Logger
        try {

            if (SystemType.isWindows()) {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/LongriLoggerWin.properties"));
            } else if (SystemType.isLinux() || SystemType.getSystemType() == SystemType.UNKNOWN) {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/LongriLoggerLinux.properties"));
            } else {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/LongriLogger.properties"));
            }
            LongriLoggerFactory factory = ((LongriLoggerFactory) LoggerFactory.getILoggerFactory());
            factory.reset();
            LongriLoggerInit.init();


            //Exclude some Classes from debug Logging
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.share.FileInputStream", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.packet.SMB2CreditGrantingPacketHandler", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.packet.SMB2SignatureVerificationPacketHandler", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.packet.SMB3DecryptingPacketHandler", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.transport.tcp.direct.DirectTcpPacketReader", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.transport.tcp.direct.DirectTcpTransport", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.Connection", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.protocol.commons.concurrent.Promise", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.protocol.commons.socket.ProxySocketFactory", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.SMBProtocolNegotiator", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.PacketEncryptor", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.auth.NtlmAuthenticator", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.connection.SMBSessionBuilder", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.asn1.ASN1InputStream", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.ntlm.messages.NtlmChallenge", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.hierynomus.smbj.session.Session", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.botiss.fx.file_handle_tree_view.FileHandleTreeItem", "error");
            CONFIG_PARAMS.setProperty("LongriLogger.logLevel:com.botiss.prtg.backup.crontab.Main", "debug");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void init() {
        LongriLogger.resetLazyInit();
        LongriLogger.lazyInit();
    }

}
