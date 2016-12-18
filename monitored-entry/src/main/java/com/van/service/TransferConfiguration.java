package com.van.service;

import com.van.common.kafka.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by rabit on 2016/6/2.
 */
public class TransferConfiguration {
    private final Logger logger= LoggerFactory.getLogger(TransferConfiguration.class);

    private String path;
    private Properties ps;
    private final String filename = "entry.xml";


    public TransferConfiguration() {
        path = System.getProperty("app.home");
        ps = new Properties();
        try {
            if (path == null) {
                ps.loadFromXML(
                        KafkaConfig.class.getClassLoader().getResourceAsStream(filename));
            } else {
                File home = new File(path);
                File psf = new File(home, "conf" + File.separator + filename);
                ps.loadFromXML(new FileInputStream(psf));
            }
        } catch (IOException e) {
            logger.error("error loading from file "+(path==null?"classpath":"conf/"+filename)+". check if exists or is damaged or jvm param:app.home is not set", e);
        }
    }

    public Object get(String key) {
        return ps.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key).toString());
    }

    public String getString(String key) {
        return get(key).toString();
    }


    public static void main(String[] args) {
        System.out.println(System.getProperties());
        System.out.println("\0");
        System.out.print(new TransferConfiguration().ps);
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransferConfiguration)) {
            return false;
        } else if (this.filename == null) {
            return false;
        } else {
            return this.filename.equals(((TransferConfiguration) obj).filename);
        }
    }


}
