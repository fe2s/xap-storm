package com.gigaspaces.storm.googleanalytics.tools;

import com.maxmind.geoip.LookupService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Converts IP address to country.
 *
 * @author Oleksiy_Dyagilev
 */
public class GeoIpLookupService {
    private static final String DB_FILE_NAME = "GeoIP.dat";

    private static GeoIpLookupService instance = new GeoIpLookupService();

    public static GeoIpLookupService getInstance() {
        return instance;
    }

    LookupService lookupService;

    private GeoIpLookupService() {
        try {
            File db = copyDbToTempDirIfRequired();
            lookupService = new LookupService(db, LookupService.GEOIP_MEMORY_CACHE);
        } catch (Exception e) {
            throw new RuntimeException("Unable to init GeoIp database ", e);
        }
    }

    // maxmind api requires a reference to File, so we have to extract it from jar
    private File copyDbToTempDirIfRequired() throws IOException {
        File tempDirectory = FileUtils.getTempDirectory();
        File dbInTempDir = new File(tempDirectory + File.separator + DB_FILE_NAME);
        if (!dbInTempDir.exists()) {
            InputStream dbStream = this.getClass().getClassLoader().getResourceAsStream(DB_FILE_NAME);
            IOUtils.copy(dbStream, new FileOutputStream(dbInTempDir));

        }
        return dbInTempDir;
    }

    public String getCountry(String ip) {
        return lookupService.getCountry(ip).getName();
    }


}
