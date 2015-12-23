package com.changhongit.loving.JGit;


public interface JGitRepository {

    String getDefaultLabel();

    String findOne(String application, String label);

    boolean fetchRepos();

    boolean pullRepos(String label);
}
