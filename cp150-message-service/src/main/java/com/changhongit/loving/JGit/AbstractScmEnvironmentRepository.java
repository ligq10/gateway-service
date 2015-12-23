package com.changhongit.loving.JGit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.UrlResource;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractScmEnvironmentRepository implements JGitRepository,InitializingBean {
    private static Log logger = LogFactory.getLog(AbstractScmEnvironmentRepository.class);

    private File basedir;
    private String uri;
    private String username;
    private String password;
    private String[] searchPaths = new String[0];

    public AbstractScmEnvironmentRepository() {
        this.basedir = createBaseDir();
    }

    private File createBaseDir() {
        try {
            final File basedir = Files.createTempDirectory("config-repo-").toFile();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        FileUtils.delete(basedir, FileUtils.RECURSIVE);
                    }
                    catch (IOException e) {
                        logger.warn("Failed to delete temporary directory on exit: " + e);
                    }
                }
            });
            return basedir;
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot create temp dir", e);
        }
    }

    public void setUri(String uri) {
        while (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        int index = uri.indexOf("://");
        if (index>0 && !uri.substring(index+"://".length()).contains("/")) {
            // If there's no context path add one
            uri = uri + "/";
        }
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir.getAbsoluteFile();
    }

    public File getBasedir() {
        return basedir;
    }

    public void setSearchPaths(String... searchPaths) {
        this.searchPaths = searchPaths;
    }

    public String[] getSearchPaths() {
        return searchPaths;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected File getWorkingDirectory() {
        if (uri.startsWith("file:")) {
            try {
                return new UrlResource(StringUtils.cleanPath(uri)).getFile();
            }
            catch (Exception e) {
                throw new IllegalStateException("Cannot convert uri to file: " + uri);
            }
        }
        return basedir;
    }

    protected String[] getSearchLocations(File dir) {
        List<String> locations = new ArrayList<String>();
        locations.add(dir.toURI().toString());
        String[] list = dir.list();
        if (list!=null) {
            for (String path : list) {
                File file = new File(dir, path);
                if (file.isDirectory() && PatternMatchUtils.simpleMatch(searchPaths, path)) {
                    locations.add(file.toURI().toString());
                }
            }
        }
        return locations.toArray(new String[0]);
    }

}